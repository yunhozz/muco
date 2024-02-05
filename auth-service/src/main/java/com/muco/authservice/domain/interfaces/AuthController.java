package com.muco.authservice.domain.interfaces;

import com.muco.authservice.domain.application.AuthService;
import com.muco.authservice.domain.interfaces.dto.ResponseDTO;
import com.muco.authservice.global.auth.security.UserDetailsImpl;
import com.muco.authservice.global.dto.req.SignInRequestDTO;
import com.muco.authservice.global.dto.res.TokenResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO login(@Valid @RequestBody SignInRequestDTO dto, HttpServletRequest request, HttpServletResponse response) {
        TokenResponseDTO data = authService.login(dto);
        String referer = request.getHeader(HttpHeaders.REFERER);
        URI prevPage = ServletUriComponentsBuilder
                .fromUriString(referer != null ? referer : "/")
                .build().toUri();

        response.addHeader(HttpHeaders.LOCATION, String.valueOf(prevPage));
        response.addHeader(HttpHeaders.AUTHORIZATION, data.getTokenType() + " " + data.getAccessToken());

        return ResponseDTO.of("이메일 로그인에 성공하였습니다.", data.getAccessToken(), String.class);
    }

    @GetMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getAccessTokenBySocialLogin(@RequestParam String token, @RequestParam String error, HttpServletResponse response) {
        if (StringUtils.isNotBlank(token)) {
            response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return ResponseDTO.of("소셜 로그인에 성공하였습니다.", token, String.class);
        } else {
            return ResponseDTO.of(error);
        }
    }

    @GetMapping("/token/reissue")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO refreshToken(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response) {
        if (authService.refreshJwtTokens(userDetails.getUsername()).isPresent()) {
            TokenResponseDTO data = authService.refreshJwtTokens(userDetails.getUsername()).get();
            response.addHeader(HttpHeaders.AUTHORIZATION, data.getTokenType() + " " + data.getAccessToken());

            return ResponseDTO.of("JWT 토큰이 재발행 되었습니다.", data, TokenResponseDTO.class);

        } else {
            return ResponseDTO.of("로그인을 다시 진행해주세요.");
        }
    }

    @DeleteMapping("/sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseDTO logout(
            @AuthenticationPrincipal Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        authService.logout(userDetails.getUsername(), accessToken.split(" ")[1]);

        new SecurityContextLogoutHandler().logout(request, response, authentication);

        return ResponseDTO.of("로그아웃에 성공하였습니다.");
    }
}