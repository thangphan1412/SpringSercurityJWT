package org.example.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "90c20da0964470b1a84208d0f8c47a1ce9afa6f0e85ebebad4598c2af1e16e6e1f2e3039a15dda04e76f8e2bea310b41fbddfc7cb1f9307ff664fd71a9648435";
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractClaims(token);
        return claimsTFunction.apply(claims);
    }
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // tao ra 1 mat ma thong bao
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*24))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpriration(token).before(new Date());
    }

    private Date extractExpriration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .build()
                // giai ma token va kiem tra chu ky
                .parseClaimsJws(token)
                // lay phan payload
                .getBody();


    }

    // giai ma key tu dang 64 -> byte array
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // tao 1 hmacsha key phu hop de dung trong jwt
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
