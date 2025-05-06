package aibe.hosik.service;

import aibe.hosik.model.entity.GithubUser;
import aibe.hosik.model.entity.KakaoUser;
import aibe.hosik.model.repository.GithubUserRepository;
import aibe.hosik.model.repository.KakaoUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final KakaoUserRepository kakaoUserRepository;
    private final GithubUserRepository githubUserRepository;

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String regId = userRequest.getClientRegistration().getRegistrationId();
        if ("kakao".equals(regId)) {
            Map<String, Object> attrs = oAuth2User.getAttributes();
            Map<String, Object> kakaoAccount = (Map<String, Object>) attrs.get("kakao_account");
            Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
            String kakaoId = String.valueOf(attrs.get("id"));
            String nickname = (String) kakaoProfile.get("nickname");
            String username = "kakao_" + kakaoId;

            KakaoUser user = kakaoUserRepository.findByUsername(username)
                    .orElseGet(() -> {
                        KakaoUser u = new KakaoUser();
                        u.setUsername(username);
                        u.setName(nickname);
                        return kakaoUserRepository.save(u);
                    });
            log.info("Kakao login: " + user);

        } else if ("github".equals(regId)) {
            Map<String, Object> attrs = oAuth2User.getAttributes();
            String login = (String) attrs.get("login");
            String name  = (String) attrs.get("name");
            String username = "github_" + login;

            GithubUser user = githubUserRepository.findByUsername(username)
                    .orElseGet(() -> {
                        GithubUser u = new GithubUser();
                        u.setUsername(username);
                        u.setName(name);
                        return githubUserRepository.save(u);
                    });
            log.info("GitHub login: " + user);
        }

        return oAuth2User;
    }
}
