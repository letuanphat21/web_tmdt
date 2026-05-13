package com.group2.web_tmdt.service;

import java.util.Date;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {

    String generateToken(String email);

    String createRefreshToken(String email);

    <T> T extractClaim(String token, Function<Claims, T> claimsTFunction);

    Date extractExpiration(String token);

    String extractEmail(String token);

    Boolean validateToken(String token, UserDetails userDetails);

}
