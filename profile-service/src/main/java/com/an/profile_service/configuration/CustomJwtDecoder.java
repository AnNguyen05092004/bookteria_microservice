package com.an.identityservice.configuration;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.an.identityservice.Service.AuthenticationService;
import com.nimbusds.jwt.SignedJWT;

// CustomJwtDecoder là một lớp tùy chỉnh để giải mã token JWT. Nó sử dụng AuthenticationService để kiểm tra tính hợp lệ
// của token thông qua phương thức introspect.
// kiểm tra token khi các request đến server
@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        //
        //        try {
        //            var response = authenticationService.introspect(
        //                    IntrospectRequest.builder().token(token).build());
        //
        //            if (!response.isValid()) throw new JwtException("Token invalid");
        //        } catch (JOSEException | ParseException e) {
        //            throw new JwtException(e.getMessage());
        //        }
        //
        //        if (Objects.isNull(nimbusJwtDecoder)) {
        //            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
        //            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
        //                    .macAlgorithm(MacAlgorithm.HS512)
        //                    .build();
        //        }
        //
        //        return nimbusJwtDecoder.decode(token);
        // Do ở api gateway đã verify rồi nên ko cần verify lại nữa

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return new Jwt(
                    token,
                    signedJWT.getJWTClaimsSet().getIssueTime().toInstant(),
                    signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(),
                    signedJWT.getHeader().toJSONObject(),
                    signedJWT.getJWTClaimsSet().getClaims());
        } catch (ParseException e) {
            throw new JwtException("Invalid JWT token", e);
        }
    }
}
