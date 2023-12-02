package com.muco.authservice.interfaces;

import com.muco.authservice.application.AuthService;
import com.muco.authservice.global.auth.security.UserDetailsImpl;
import com.muco.authservice.global.dto.req.SignInRequestDTO;
import com.muco.authservice.global.dto.res.TokenResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<String> login(@Valid @RequestBody SignInRequestDTO dto, HttpServletRequest request) {
        TokenResponseDTO tokenResponseDTO = authService.login(dto);
        String referer = request.getHeader("Referer");
        URI location = ServletUriComponentsBuilder
                .fromUriString(referer != null ? referer : "/")
                .build().toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(httpHeaders -> {
                    httpHeaders.setLocation(location); // 로그인 직전 페이지 명시
                    httpHeaders.setBearerAuth(tokenResponseDTO.getAccessToken());
                })
                .body("이메일 로그인 성공!! Access Token : " + tokenResponseDTO.getAccessToken());
    }

    @GetMapping("/token")
    public ResponseEntity<String> getAccessTokenBySocialLogin(@RequestParam String token, @RequestParam String error, HttpServletRequest request) {
        if (StringUtils.isNotBlank(token)) {
            String referer = request.getHeader("Referer");
            URI location = ServletUriComponentsBuilder
                    .fromUriString(referer != null ? referer : "/")
                    .build().toUri();

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .headers(httpHeaders -> {
                        httpHeaders.setLocation(location); // 로그인 직전 페이지 명시
                        httpHeaders.setBearerAuth(token);
                    })
                    .body("소셜 로그인 성공!! Access Token : " + token);
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(error);
        }
    }

    @GetMapping("/token/reissue")
    public ResponseEntity<Object> refreshToken(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (authService.refreshJwtTokens(userDetails.getUsername()).isPresent()) {
            TokenResponseDTO tokenResponseDTO = authService.refreshJwtTokens(userDetails.getUsername()).get();
            return ResponseEntity
                    .ok()
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponseDTO.getAccessToken()))
                    .body(tokenResponseDTO);

        } else {
            return ResponseEntity
                    .ok()
                    .headers(httpHeaders -> {
                        URI loginPage = ServletUriComponentsBuilder
                                .fromUriString("/sign-in")
                                .build().toUri();
                        httpHeaders.setLocation(loginPage);
                    })
                    .body("로그인을 다시 진행해주세요.");
        }
    }

    @DeleteMapping("/sign-out")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        authService.logout(userDetails.getUsername(), accessToken.split(" ")[1]);
        URI location = ServletUriComponentsBuilder
                .fromUriString("/")
                .build().toUri();

        return ResponseEntity
                .noContent()
                .headers(httpHeaders -> httpHeaders.setLocation(location))
                .build();
    }
}