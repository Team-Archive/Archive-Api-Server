package com.depromeet.archive.security.common;

import com.depromeet.archive.domain.user.entity.UserRole;
import com.depromeet.archive.domain.user.info.UserInfo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

@Getter
public class UserPrincipal implements OAuth2User {

    private final UserInfo userInfo;
    private final Map<String, Object> attributes;
    private final Collection<GrantedAuthority> authorities = new LinkedList<>();

    @Builder
    public UserPrincipal(UserInfo userInfo, Map<String, Object> attributes) {
        this.userInfo = userInfo;
        this.attributes = attributes;
        authorities.add(new SimpleGrantedAuthority(userInfo.getUserRole().toString()));
    }

    public void setUserId(long userId) {
        this.userInfo.setUserId(userId);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return userInfo.getMailAddress();
    }

}
