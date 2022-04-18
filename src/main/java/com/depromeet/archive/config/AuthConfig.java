package com.depromeet.archive.config;

import com.depromeet.archive.domain.archive.ArchiveRepository;
import com.depromeet.archive.domain.user.UserAuthService;
import com.depromeet.archive.domain.user.UserRegisterService;
import com.depromeet.archive.domain.user.UserService;
import com.depromeet.archive.security.authorization.permissionhandler.ArchiveAdminOrAuthorChecker;
import com.depromeet.archive.security.general.UserNamePasswordAuthenticationProvider;
import com.depromeet.archive.security.oauth.OAuthUserService;
import com.depromeet.archive.security.result.LoginFailureHandler;
import com.depromeet.archive.security.result.LoginSuccessHandler;
import com.depromeet.archive.security.token.HttpAuthTokenSupport;
import com.depromeet.archive.security.token.TokenProvider;
import com.depromeet.archive.security.token.jwt.JwtTokenProvider;
import com.depromeet.archive.security.token.jwt.JwtTokenSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class AuthConfig {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Bean
    public UserNamePasswordAuthenticationProvider directLoginProvider(UserAuthService userAuthService) {
        return new UserNamePasswordAuthenticationProvider(userAuthService);
    }

    @Bean
    public OAuthUserService oAuthUserService(UserRegisterService userRegisterService) {
        return new OAuthUserService(userRegisterService);
    }

    @Bean
    public TokenProvider tokenProvider(ObjectMapper mapper) {
        return new JwtTokenProvider(secretKey, mapper);
    }

    @Bean
    public HttpAuthTokenSupport tokenSupport() {
        return new JwtTokenSupport();
    }

    @Bean
    public LoginSuccessHandler successHandler(TokenProvider tokenProvider,
                                              HttpAuthTokenSupport authTokenSupport,
                                              UserService userService) {
        return new LoginSuccessHandler(tokenProvider, authTokenSupport, userService);
    }

    @Bean
    public LoginFailureHandler failureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ArchiveAdminOrAuthorChecker checker(ArchiveRepository repository) {
        return new ArchiveAdminOrAuthorChecker(repository);
    }
}
