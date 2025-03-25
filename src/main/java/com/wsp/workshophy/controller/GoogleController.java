package com.wsp.workshophy.controller;

import com.wsp.workshophy.dto.response.AuthenticationResponse;
import com.wsp.workshophy.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GoogleController {
    AuthenticationService authenticationService;

    @GetMapping("/login/google")
    ResponseEntity<String> loginGoogleAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
        return ResponseEntity.ok("Redirecting to Google login page");
    }

    @GetMapping("/loginSuccess")
    ResponseEntity<AuthenticationResponse> handleGoogleSuccess(OAuth2AuthenticationToken oAuth2AuthenticationToken) throws IOException {
        log.info("Handling successful Google login for user: {}", Optional.ofNullable(oAuth2AuthenticationToken.getPrincipal().getAttribute("email")));
        AuthenticationResponse response = authenticationService.loginRegisterByGoogleOAuth2(oAuth2AuthenticationToken);
        log.info("Redirecting to success URL after Google login");
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://www.youtube.com/watch?v=Axi6twDnjqw")).build();
    }

    @GetMapping("/loginFailure")
    public ResponseEntity<AuthenticationResponse> handleGoogleFailure(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        log.error("Google login failed for user: {}", Optional.ofNullable(oAuth2AuthenticationToken.getPrincipal().getAttribute("email")));
        AuthenticationResponse response = authenticationService.loginRegisterByGoogleOAuth2(oAuth2AuthenticationToken);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).location(URI.create("https://www.google.com.vn/")).build();
    }
}
