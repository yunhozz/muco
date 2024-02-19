package com.muco.authservice.global.auth.jwt;

import com.muco.authservice.global.auth.security.UserDetailsServiceImpl;
import com.muco.authservice.global.dto.res.TokenResponseDTO;
import com.muco.authservice.global.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static com.muco.authservice.global.config.PropertyConfig.MucoProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final UserDetailsServiceImpl userDetailsService;
    private final MucoProperties mucoProperties;

    private String secretKey;

    public TokenResponseDTO createJwtTokenDTO(String userId, Set<Role> roles) {
        MucoProperties.Jwt jwtProperties = mucoProperties.getJwt();
        secretKey = jwtProperties.getSecretKey();
        Long accessTokenValidTime = jwtProperties.getAccessTokenValidTime();
        Long refreshTokenValidTime = jwtProperties.getRefreshTokenValidTime();

        Claims claims = Jwts.claims().setSubject(userId);
        String auth = roles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.joining(","));

        claims.put("auth", auth);
        String accessToken = createToken(claims, "atk", accessTokenValidTime);
        String refreshToken = createToken(claims, "rtk", refreshTokenValidTime);

        return TokenResponseDTO.builder()
                .id(userId)
                .tokenType(jwtProperties.getTokenType())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .atkValidTime(accessTokenValidTime)
                .rtkValidTime(refreshTokenValidTime)
                .build();
    }

    @Transactional(readOnly = true)
    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String createToken(Claims claims, String type, Long validTime) {
        claims.put("type", type);
        return Jwts.builder()
                .setClaims(claims)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + validTime))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}