package com.kaiser.messenger_server.services;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.kaiser.messenger_server.dto.request.AuthRequest;
import com.kaiser.messenger_server.dto.request.CreateUserRequest;
import com.kaiser.messenger_server.dto.request.ForgotPasswordRequest;
import com.kaiser.messenger_server.dto.request.IntrospectRequest;
import com.kaiser.messenger_server.dto.request.LogoutRequest;
import com.kaiser.messenger_server.dto.request.VerifyUserRequest;
import com.kaiser.messenger_server.dto.response.AuthResponse;
import com.kaiser.messenger_server.dto.response.IntrospectResponse;
import com.kaiser.messenger_server.dto.response.UserResponse;
import com.kaiser.messenger_server.entities.BlacklistToken;
import com.kaiser.messenger_server.entities.Role;
import com.kaiser.messenger_server.entities.User;
import com.kaiser.messenger_server.enums.TokenType;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import com.kaiser.messenger_server.mapper.UserMapper;
import com.kaiser.messenger_server.repositories.BlacklistTokenRepository;
import com.kaiser.messenger_server.repositories.RoleRepository;
import com.kaiser.messenger_server.repositories.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
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

    private Set<String> generatedCodes = new HashSet<>();

    private Random random = new Random();

    public IntrospectResponse introspect (IntrospectRequest request, String path) throws JOSEException, ParseException {
        String token = request.getToken();
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
        return IntrospectResponse.builder()
            .valid(isValid)
            .build();
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

        if(user.getIsActive() == false){
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVATED);
        }

        String access_token = generateToken(user, TokenType.ACCESS);
        String refresh_token = generateToken(user, TokenType.REFRESH);

        Cookie cookie = new Cookie("refresh_token", refresh_token);
        cookie.setMaxAge((int)REFRESHABLE_DURATION);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

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

    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        SignedJWT accessToken = verifyToken(request.getAccessToken());
        SignedJWT refreshToken = verifyToken(request.getRefreshToken());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean check = authentication.getName().equals(accessToken.getJWTClaimsSet().getSubject()) && 
            authentication.getName().equals(refreshToken.getJWTClaimsSet().getSubject());
        if(!check){
            throw new AppException(ErrorCode.LOGOUT_OTHER);
        }

        List<BlacklistToken> savedTokens = List.of(
            BlacklistToken.builder()
                .id(accessToken.getJWTClaimsSet().getJWTID())
                .expiryTime(accessToken.getJWTClaimsSet().getExpirationTime())
                .build(),
            BlacklistToken.builder()
                .id(refreshToken.getJWTClaimsSet().getJWTID())
                .expiryTime(new Date(
                    refreshToken.getJWTClaimsSet().getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli()
                ))
                .build()
        );
                
        blacklistTokenRepository.saveAll(savedTokens);     
    }

    public AuthResponse refreshToken(String token,  HttpServletResponse response) throws JOSEException, ParseException {
        SignedJWT signedJWT = verifyToken(token);

        String jit = signedJWT.getJWTClaimsSet().getJWTID();

        BlacklistToken blacklistToken = BlacklistToken.builder()
            .id(jit)
            .expiryTime(signedJWT.getJWTClaimsSet().getExpirationTime())
            .build();
        blacklistTokenRepository.save(blacklistToken);

        String email = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        String access_token = generateToken(user, TokenType.ACCESS);
        String refresh_token = generateToken(user, TokenType.REFRESH);

        Cookie deleteCookie = new Cookie("refresh_token", null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setHttpOnly(true);

        response.addCookie(deleteCookie);

        Cookie cookie = new Cookie("refresh_token", refresh_token);
        cookie.setMaxAge((int)REFRESHABLE_DURATION);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return AuthResponse.builder()
            .access_token(access_token)
            .isAuthenticated(true)
            .user(userMapper.toUserResponse(user))
            .build();
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        String type = (String)signedJWT.getJWTClaimsSet().getClaim("type");

        String signerKey = TokenType.REFRESH.toString().equals(type) ? SIGNER_KEY_REFRESH : SIGNER_KEY_ACCESS;
        JWSVerifier jwsVerifier = new MACVerifier(signerKey.getBytes());

        Date expireTime = TokenType.REFRESH.toString().equals(type) 
            ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
            : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(jwsVerifier);

        if (!(verified && expireTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (blacklistTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
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

    private synchronized String generateCode(){
        if (generatedCodes.size() >= 1_000_000) {
            throw new IllegalStateException("All 6-digit codes exhausted.");
        }

        String code;
        do {
            code = String.format("%06d", random.nextInt(1_000_000)); // "000000" to "999999"
        } while (generatedCodes.contains(code));

        generatedCodes.add(code);

        return code;
    }
}
