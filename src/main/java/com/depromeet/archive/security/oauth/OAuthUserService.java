package com.depromeet.archive.security.oauth;

import com.depromeet.archive.domain.user.command.OAuthRegisterCommand;
import com.depromeet.archive.domain.user.entity.OAuthProvider;
import com.depromeet.archive.exception.common.ResourceNotFoundException;
import com.depromeet.archive.domain.user.UserService;
import com.depromeet.archive.security.common.UserPrincipal;
import com.depromeet.archive.exception.security.WrappingAuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

public class OAuthUserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public OAuthUserService(UserService service) {
        this.userService = service;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(oAuth2UserRequest);
        OAuthProvider provider = getProvider(oAuth2UserRequest);
        UserPrincipal principal = provider.convert(user);
        assert principal != null;
        registerOrUpdateUser(principal, provider);
        return principal;
    }

    private OAuthProvider getProvider(OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        return OAuthProvider.getByRegistrationId(registrationId);
    }

    private void registerOrUpdateUser(UserPrincipal principal, OAuthProvider provider) {
        OAuthRegisterCommand command = new OAuthRegisterCommand(principal.getName(), provider);
        long userId = userService.getOrRegisterUser(command);
        principal.setUserId(userId);
    }
}
