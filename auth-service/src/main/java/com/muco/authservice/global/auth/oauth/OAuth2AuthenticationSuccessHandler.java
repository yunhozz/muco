package com.muco.authservice.global.auth.oauth;

import com.muco.authservice.global.auth.jwt.JwtProvider;
import com.muco.authservice.global.auth.security.UserDetailsImpl;
import com.muco.authservice.global.dto.res.TokenResponseDTO;
import com.muco.authservice.global.util.CookieUtils;
import com.muco.authservice.global.util.RedisUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import static com.muco.authservice.global.auth.oauth.OAuth2AuthorizationRequestCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.muco.authservice.global.config.PropertyConfig.MucoProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RedisUtils redisUtils;
    private final MucoProperties mucoProperties;
    private final OAuth2AuthorizationRequestCookieRepository oAuth2AuthorizationRequestCookieRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = this.determineTargetUrl(request, response, authentication);
        log.info("Target URL : " + targetUrl);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String[] redirectUris = {null};
        CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .ifPresentOrElse(cookie -> {
                    String redirectUri = cookie.getValue();
                    if (!isAuthorizedRedirectUri(redirectUri))
                        throw new RuntimeException("Unauthorized Redirect URI");
                    redirectUris[0] = redirectUri;

                }, () -> redirectUris[0] = getDefaultTargetUrl());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        TokenResponseDTO tokenResponseDTO = jwtProvider.createJwtTokenDTO(userDetails.getUsername(), userDetails.getRoles());
        redisUtils.saveValue(userDetails.getUsername(), tokenResponseDTO.getRefreshToken(), Duration.ofMillis(tokenResponseDTO.getRtkValidTime()));

        return ServletUriComponentsBuilder
                .fromUriString(redirectUris[0])
                .queryParam("token", tokenResponseDTO.getAccessToken())
                .queryParam("error", "")
                .toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestCookieRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return mucoProperties.getOAuth2().getAuthorizedRedirectUris().stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost()
                            .equalsIgnoreCase(clientRedirectUri.getHost()) && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}