package com.muco.authservice.interfaces;

import com.muco.authservice.application.AuthService;
import com.muco.authservice.global.auth.security.UserDetailsImpl;
import com.muco.authservice.global.dto.req.SignInRequestDTO;
import com.muco.authservice.global.dto.res.TokenResponseDTO;
import com.muco.authservice.interfaces.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
    public ResponseEntity<ResponseDTO<String>> login(@Valid @RequestBody SignInRequestDTO dto, HttpServletRequest request) {
        TokenResponseDTO tokenResponseDTO = authService.login(dto);
        String referer = request.getHeader(HttpHeaders.REFERER);
        URI prevPage = ServletUriComponentsBuilder
                .fromUriString(referer != null ? referer : "/")
                .build().toUri();

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(h -> {
                    h.setLocation(prevPage);
                    h.setBearerAuth(tokenResponseDTO.getAccessToken());
                })
                .body(ResponseDTO.of("이메일 로그인에 성공하였습니다.", tokenResponseDTO.getAccessToken()));
    }

    @GetMapping("/token")
    public ResponseEntity<ResponseDTO<Object>> getAccessTokenBySocialLogin(
            @RequestParam String token,
            @RequestParam String error,
            HttpServletRequest request
    ) {
        if (StringUtils.isNotBlank(token)) {
            return ResponseEntity.ok()
                    .headers(h -> h.setBearerAuth(token))
                    .body(ResponseDTO.of("소셜 로그인에 성공하였습니다.", token));
        } else {
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.of(error));
        }
    }

    @GetMapping("/token/reissue")
    public ResponseEntity<ResponseDTO<Object>> refreshToken(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (authService.refreshJwtTokens(userDetails.getUsername()).isPresent()) {
            TokenResponseDTO tokenResponseDTO = authService.refreshJwtTokens(userDetails.getUsername()).get();
            return ResponseEntity.ok()
                    .headers(h -> h.setBearerAuth(tokenResponseDTO.getAccessToken()))
                    .body(ResponseDTO.of("JWT 토큰이 재발행 되었습니다.", tokenResponseDTO));

        } else {
            return ResponseEntity.ok()
                    .body(ResponseDTO.of("로그인을 다시 진행해주세요."));
        }
    }

    @DeleteMapping("/sign-out")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        authService.logout(userDetails.getUsername(), accessToken.split(" ")[1]);

        new SecurityContextLogoutHandler().logout(request, response, authentication);

        return ResponseEntity.noContent()
                .build();
    }
}