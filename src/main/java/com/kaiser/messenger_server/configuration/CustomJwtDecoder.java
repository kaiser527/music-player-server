package com.kaiser.messenger_server.configuration;

import java.text.ParseException;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.kaiser.messenger_server.dto.request.IntrospectRequest;
import com.kaiser.messenger_server.dto.response.IntrospectResponse;
import com.kaiser.messenger_server.services.AuthService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey-access}")
    private String SIGNER_KEY_ACCESS;

    @Value("${jwt.signerKey-refresh}")
    private String SIGNER_KEY_REFRESH;

    @Autowired
    private AuthService authService;

    @Override
    public Jwt decode(String token) throws JwtException {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String path = httpRequest.getRequestURI();
        try {
            IntrospectResponse response = authService.introspect(IntrospectRequest.builder().token(token).build(), path);
            if (!response.isValid()) throw new JwtException("Token invalid");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String type = signedJWT.getJWTClaimsSet().getStringClaim("type");

            String signerKey = "REFRESH".equals(type) ? SIGNER_KEY_REFRESH : SIGNER_KEY_ACCESS;
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");

            NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();

            return decoder.decode(token);

        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage(), e);
        }
    }
}
