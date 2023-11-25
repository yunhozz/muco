package com.muco.authservice.global.auth.jwt;

import com.muco.authservice.global.auth.security.UserDetailsServiceImpl;
import com.muco.authservice.global.dto.res.TokenResponseDTO;
import com.muco.authservice.global.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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

    public TokenResponseDTO createJwtTokenDTO(String userId, Set<Role> roles) {
        MucoProperties.Jwt jwtProperties = mucoProperties.getJwt();
        String secretKey = jwtProperties.getSecretKey();
        Long accessTokenValidTime = jwtProperties.getAccessTokenValidTime();
        Long refreshTokenValidTime = jwtProperties.getRefreshTokenValidTime();

        Claims claims = Jwts.claims().setSubject(userId);
        String auth = roles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.joining(","));

        claims.put("auth", auth);
        String accessToken = createToken(claims, secretKey, "atk", accessTokenValidTime);
        String refreshToken = createToken(claims, secretKey, "rtk", refreshTokenValidTime);

        return TokenResponseDTO.builder()
                .tokenType(jwtProperties.getTokenType())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .atkValidTime(accessTokenValidTime)
                .rtkValidTime(refreshTokenValidTime)
                .build();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean isValidToken(String token) {
        try {
            parseToken(token);
            return true;

        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 Jwt 서명입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 토큰입니다.");
        }

        return false;
    }

    private String createToken(Claims claims, String secretKey, String type, Long validTime) {
        claims.put("type", type);
        return Jwts.builder()
                .setClaims(claims)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + validTime))
                .signWith(getSecretKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSecretKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}