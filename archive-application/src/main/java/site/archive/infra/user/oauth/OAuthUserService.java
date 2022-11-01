package site.archive.infra.user.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.stereotype.Service;
import site.archive.dto.v1.auth.OAuthRegisterCommandV1;
import site.archive.dto.v1.user.OAuthRegisterRequestDtoV1;
import site.archive.infra.user.oauth.provider.OAuthProviderClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthUserService {

    private final List<OAuthProviderClient> oAuthProviderClients;

    public OAuthRegisterCommandV1 getOAuthRegisterInfo(OAuthRegisterRequestDtoV1 oAuthRegisterRequestDtoV1) {
        var provider = oAuthRegisterRequestDtoV1.getProvider();
        var oAuthProviderClient = oAuthProviderClients.stream()
                                                      .filter(client -> client.support().equals(provider))
                                                      .findFirst()
                                                      .orElseThrow(() ->
                                                                       new ProviderNotFoundException(
                                                                           "There is no suitable register provider client for " + provider));
        log.debug("oauth provider access token: {}", oAuthRegisterRequestDtoV1);
        return oAuthProviderClient.getOAuthRegisterInfo(oAuthRegisterRequestDtoV1);
    }

}
