package dk.ek.adtoolbackend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtUtil jwtUtil;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expirationMs}") long expirationMs
    ) {
        this.jwtUtil = new JwtUtil(secret, expirationMs);
    }

    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }

    public String validateAndGetUsername(String token) {
        return jwtUtil.validateAndGetUsername(token);
    }
}