package site.archive.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.archive.security.authn.CustomAuthenticationEntryPoint;
import site.archive.security.authz.CustomAccessDeniedHandler;
import site.archive.security.general.BodyCredentialAuthenticationFilter;
import site.archive.security.general.UserNamePasswordAuthenticationProvider;
import site.archive.security.oauth.OAuthUserService;
import site.archive.security.result.LoginFailureHandler;
import site.archive.security.result.LoginSuccessHandler;
import site.archive.security.token.HttpAuthTokenSupport;
import site.archive.security.token.TokenProvider;
import site.archive.security.token.jwt.JwtTokenPersistFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginSuccessHandler successHandler;
    private final LoginFailureHandler failureHandler;
    private final OAuthUserService userService;
    private final UserNamePasswordAuthenticationProvider provider;
    private final TokenProvider tokenProvider;
    private final HttpAuthTokenSupport tokenSupport;
    private final ObjectMapper mapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager)
        throws Exception {
        // @formatter:off
        return http.csrf().disable()
                   .formLogin().disable()
                   .httpBasic().disable()
                   .logout().disable()
                   .headers().frameOptions().sameOrigin().and()
                   .authorizeRequests()
                        .antMatchers("/h2-console/**").permitAll()
                        .antMatchers("/api/v1/auth/unregister").authenticated()
                        .antMatchers("/api/v1/auth/info").authenticated()
                        .antMatchers("/api/v1/auth/**").permitAll()
                        .antMatchers("/login/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/exception/**").permitAll()
                        .anyRequest().authenticated().and()
                   .exceptionHandling()
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()).and()
                   .addFilterBefore(bodyCredentialAuthenticationFilter(authenticationManager, mapper),
                                    UsernamePasswordAuthenticationFilter.class)
                   .addFilterBefore(tokenPersistFilter(),
                                    UsernamePasswordAuthenticationFilter.class)
                   // TODO: OAuth는 따로 지원 중. 추후 안드로이드는 Security에서 지원해야할 수 있으니 주석처리.
                   // 적용 시, Access denied (JWT Filter)에서 redirection이 일어나지 않도록 확인이 필요.
                   // .oauth2Login().userInfoEndpoint().userService(userService).and()
                   // .successHandler(successHandler).failureHandler(failureHandler).and()
                   .build();
        // @formatter:on
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    private BodyCredentialAuthenticationFilter bodyCredentialAuthenticationFilter(AuthenticationManager manager, ObjectMapper mapper) {
        var filter = new BodyCredentialAuthenticationFilter("/api/v1/auth/login", manager, mapper);
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);
        return filter;
    }

    private JwtTokenPersistFilter tokenPersistFilter() {
        return new JwtTokenPersistFilter(tokenSupport, tokenProvider);
    }

}
