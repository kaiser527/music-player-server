package com.kaiser.messenger_server.modules.auth;

import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.kaiser.messenger_server.enums.TokenType;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import com.kaiser.messenger_server.modules.auth.dto.AuthRequest;
import com.kaiser.messenger_server.modules.auth.dto.AuthResponse;
import com.kaiser.messenger_server.modules.auth.dto.ForgotPasswordRequest;
import com.kaiser.messenger_server.modules.auth.dto.VerifyUserRequest;
import com.kaiser.messenger_server.modules.auth.entity.BlacklistToken;
import com.kaiser.messenger_server.modules.mail.EmailService;
import com.kaiser.messenger_server.modules.role.RoleRepository;
import com.kaiser.messenger_server.modules.role.entity.Role;
import com.kaiser.messenger_server.modules.user.UserMapper;
import com.kaiser.messenger_server.modules.user.UserRepository;
import com.kaiser.messenger_server.modules.user.dto.CreateUserRequest;
import com.kaiser.messenger_server.modules.user.dto.UserResponse;
import com.kaiser.messenger_server.modules.user.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    BlacklistTokenRepository blacklistTokenRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    EmailService emailService;
    
    @NonFinal
    @Value("${jwt.signerKey-access}")
    String SIGNER_KEY_ACCESS;

    @NonFinal
    @Value("${jwt.signerKey-refresh}")
    String SIGNER_KEY_REFRESH;

    @NonFinal
    @Value("${jwt.valid-duration}")
    long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${role.user}")
    String USER_ROLE;

    private static final SecureRandom secureRandom = new SecureRandom();

    public boolean introspect (String token, String path) throws JOSEException, ParseException {
        boolean isValid = true;
        try {
            SignedJWT signedJWT = verifyToken(token);
            String type = signedJWT.getJWTClaimsSet().getStringClaim("type");

            if (path.endsWith("/refresh")) {
                if (!(TokenType.ACCESS.toString().equals(type) || TokenType.REFRESH.toString().equals(type))) {
                    isValid = false;
                }
            } else {
                if (!TokenType.ACCESS.toString().equals(type)) {
                    isValid = false;
                }
            }
        } catch (AppException e) {
            isValid = false;
        }
        return isValid;
    }

    public UserResponse getAccount(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));     
        
        return userMapper.toUserResponse(user);
    }

    public AuthResponse login(AuthRequest request, HttpServletResponse response){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if(!user.getIsActive() || !user.getRole().getIsActive()){
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVATED);
        }

        String access_token = generateToken(user, TokenType.ACCESS);
        String refresh_token = generateToken(user, TokenType.REFRESH);

        return AuthResponse.builder()
            .access_token(access_token)
            .refresh_token(refresh_token)
            .isAuthenticated(authenticated)
            .user(userMapper.toUserResponse(user))
            .build();
    }

    public UserResponse register(CreateUserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = roleRepository.findById(USER_ROLE).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
        
        User user = userMapper.toCreateUser(request);

        user.setRole(role);
        user.setCreatedBy("system");
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCodeId(generateCode());

        emailService.sendTemplateEmail(user.getEmail(), user.getCodeId(), "Welcome to Kaiser Music App! Confirm your Email");

        user.setCodeExpire(new Date(Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()));

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public UserResponse verifyUser(VerifyUserRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        boolean check = 
            user.getCodeId().equals(request.getCodeId()) &&
            user.getCodeExpire().after(new Date());

        if(!check){
            throw new AppException(ErrorCode.CODE_INVALID);
        }

        user.setIsActive(true);

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public void resendCode(VerifyUserRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        if(user.getIsActive() && !request.getIsForgot()){
            throw new AppException(ErrorCode.ACCOUNT_ACTIVATED);
        }

        user.setCodeId(generateCode());
        
        emailService.sendTemplateEmail(user.getEmail(), user.getCodeId(), "Welcome to Kaiser Musi App! Confirm your Email");

        user.setCodeExpire(new Date(Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()));

        userRepository.save(user);
    }

    public UserResponse forgotPassword(ForgotPasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        if(!user.getIsActive()){
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVATED);
        }

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        boolean check = 
            user.getCodeId().equals(request.getCodeId()) &&
            user.getCodeExpire().after(new Date());

        if(!check){
            throw new AppException(ErrorCode.CODE_INVALID);
        }

        user.setPassword(passwordEncoder.encode(request.getConfirmPassword()));

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public void logout(String access_token, String refresh_token) throws JOSEException, ParseException {
       blacklistSafely(access_token);
       blacklistSafely(refresh_token);
    }

    public AuthResponse refreshToken(String token,  HttpServletResponse response) throws JOSEException, ParseException {
        SignedJWT signedJWT = verifyToken(token);

        String email = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if(!user.getIsActive() || !user.getRole().getIsActive()){
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVATED);
        }

        String access_token = generateToken(user, TokenType.ACCESS);

        return AuthResponse.builder()
            .access_token(access_token)
            .isAuthenticated(true)
            .user(userMapper.toUserResponse(user))
            .build();
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        var claims = signedJWT.getJWTClaimsSet();

        String type = claims.getStringClaim("type");
        if (type == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String signerKey =
            TokenType.ACCESS.toString().equals(type)
                ? SIGNER_KEY_ACCESS
                : SIGNER_KEY_REFRESH;

        if (!signedJWT.verify(new MACVerifier(signerKey.getBytes()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Date exp = claims.getExpirationTime();
        if (exp == null || exp.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (claims.getJWTID() != null &&
            blacklistTokenRepository.existsById(claims.getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user, TokenType type) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .subject(user.getEmail())
            .issuer("kaiser.com")
            .issueTime(new Date())
            .expirationTime(new Date(
                Instant.now()
                    .plus(type == TokenType.ACCESS ? VALID_DURATION : REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                    .toEpochMilli()
            ))
            .jwtID(UUID.randomUUID().toString())
            .claim("role", user.getRole().getName())
            .claim("type", type)
            .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        
        String signerKey = type == TokenType.REFRESH ? SIGNER_KEY_REFRESH : SIGNER_KEY_ACCESS;
        try{
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        }catch(JOSEException e){
            throw new RuntimeException(e);
        }
    }

    private String generateCode(){
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    private void blacklistSafely(String token) {
        if (token == null || token.isBlank()) return;
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            String jti = jwt.getJWTClaimsSet().getJWTID();
            Date exp = jwt.getJWTClaimsSet().getExpirationTime();

            if (jti == null || exp == null) return;

            if (!blacklistTokenRepository.existsById(jti)) {
                blacklistTokenRepository.save(
                    BlacklistToken.builder()
                        .id(jti)
                        .expiryTime(exp)
                        .build()
                );
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORZIED_EXCEPTION);
        }
    }
}
