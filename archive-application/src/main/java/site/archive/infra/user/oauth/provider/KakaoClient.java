package site.archive.infra.user.oauth.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import site.archive.common.exception.user.OAuthRegisterFailException;
import site.archive.domain.user.OAuthProvider;
import site.archive.dto.v1.auth.OAuthRegisterCommandV1;
import site.archive.dto.v1.user.OAuthRegisterRequestDtoV1;
import site.archive.infra.user.oauth.provider.dto.KakaoProviderRequirements;
import site.archive.infra.user.oauth.provider.dto.KakaoUserInfo;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoClient implements OAuthProviderClient {

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoUrl;

    @Override
    public String support() {
        return OAuthProvider.KAKAO.getRegistrationId();
    }

    @Override
    public OAuthRegisterCommandV1 getOAuthRegisterInfo(OAuthRegisterRequestDtoV1 oAuthRegisterRequestDtoV1) {
        var userEmail = getUserEmail(KakaoProviderRequirements.from(oAuthRegisterRequestDtoV1));
        return new OAuthRegisterCommandV1(userEmail, OAuthProvider.KAKAO);
    }

    private String getUserEmail(KakaoProviderRequirements requirements) {
        var entity = userInfoRequestEntity(requirements.getKakaoAccessToken());
        var response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, KakaoUserInfo.class);
        var kakaoUserInfo = response.getBody();

        if (response.getStatusCode() != HttpStatus.OK || kakaoUserInfo == null || kakaoUserInfo.getEmail() == null) {
            log.error("Kakao getUserEmail process - get user info error: status code {}, user info {}",
                      response.getStatusCodeValue(), userInfoUrl);
            throw new OAuthRegisterFailException(OAuthProvider.KAKAO.getRegistrationId(), "UserInfoUrl Response error");
        }

        return kakaoUserInfo.getEmail();
    }

    private HttpEntity<Object> userInfoRequestEntity(String accessToken) {
        var authHeader = new HttpHeaders();
        authHeader.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        return new HttpEntity<>(authHeader);
    }

}
