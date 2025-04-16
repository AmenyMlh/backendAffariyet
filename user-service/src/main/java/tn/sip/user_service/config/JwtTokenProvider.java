package tn.sip.user_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import tn.sip.user_service.dto.UserDTO;
import tn.sip.user_service.entities.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    private final String jwtSecret = "c54e9fc5cfd0a0fe7bed9dfbc07dd0800f9957b80bbdf21dea0b61aecfe2fc3012d87c1c6e54a7a0de6722383cee1b0a4f084eac9aa4c22ddf8cf982f0241dc73f7072d9a5c12197b3df3ffde9805e3864e5fdc64c9efd6da21dc2ca5b5b2dc1fc01cf9e3fb24ec2d9fa26763dc19358e2f2d6932bc59875ae06b6b1781c30fbe04136483c375ae35201d6926a97193edb95477faa2d73db744b3a3520145c82265124add21b95152333fb58c264a9b3b60f20252cf9ad82ff531a98c7fe3d5a43953feea9ae973a852308abe4649a028243e18aafe0d9279e926af98d8766d359138cc29ee73b1e0bb7f62bf357df05e4a3ed270eb8ad35912dc32b869813aa";
    private final int jwtExpiration = 86400000; // 1 day
    private final int refreshExpiration = 604800000; // 7 days

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDTO user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("firstName", user.getFirstName());
        claims.put("role", user.getRole());
        claims.put("approved", user.isApproved());

        if (user.getAgencyDTO() != null) {
            claims.put("subscriptionId", user.getAgencyDTO().getSubscriptionId());
            claims.put("agencyId", user.getAgencyDTO().getId());
        }

        return buildToken(claims, user, jwtExpiration);
    }


    public String generateRefreshToken(UserDTO user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        return buildToken(claims, user, refreshExpiration);
    }

    public String extractEmailFromRefreshToken(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public boolean isRefreshTokenValid(String token, User user) {
        try {
            String email = extractUsername(token); 
            return (email.equals(user.getEmail())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }




    private String buildToken(Map<String, Object> extraClaims, UserDTO user, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
