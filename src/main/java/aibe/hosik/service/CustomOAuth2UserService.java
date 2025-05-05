package aibe.hosik.service;

import aibe.hosik.auth.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import aibe.hosik.model.entity.KakaoUser;
import aibe.hosik.model.entity.GithubUser;
import aibe.hosik.model.repository.KakaoUserRepository;
import aibe.hosik.model.repository.GithubUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final GithubUserRepository githubUserRepository;
    private final KakaoUserRepository kakaoUserRepository;

    @SuppressWarnings("unchecked")
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 실제 OAuth2 프로바이더에서 유저 정보를 가져오는 기본 서비스 호출
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // (kakao or github) 로그인했는지 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("kakao".equals(registrationId)) {
            //  기존 카카오 로직
            Map<String, Object> attributes = oAuth2User.getAttributes();
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

            String kakaoId = attributes.get("id").toString();
            String nickname = kakaoProfile.get("nickname").toString();
            String kakaoUsername = "kakao_" + kakaoId;

            // 카카오 유저 저장 또는 조회
            KakaoUser kakaoUser = kakaoUserRepository.findByUsername(kakaoUsername)
                    .orElseGet(() -> {
                        KakaoUser u = new KakaoUser();
                        u.setUsername(kakaoUsername);
                        u.setName(nickname);
                        return kakaoUserRepository.save(u);
                    });

            log.info("Kakao login: " + kakaoUser);
        } else if ("github".equals(registrationId)) {
            // GitHub 로직
            Map<String, Object> attrs = oAuth2User.getAttributes();

            String githubLogin = (String) attrs.get("login");
            String githubName = (String) attrs.get("name");
            String githubUsername = "github_" + githubLogin;

            // GitHub 유저 저장 또는 조회
            GithubUser githubUser = githubUserRepository.findByUsername(githubUsername)
                    .orElseGet(() -> {
                        GithubUser u = new GithubUser();
                        u.setUsername(githubUsername);
                        u.setName(githubName);
                     //   u.setRole("GITHUB");
                        return githubUserRepository.save(u);
                    });

            log.info("GitHub login: " + githubUser);
        }

        // 스프링 시큐리티가 사용할 OAuth2User 반환
        return oAuth2User;
    }

    @Service
    @RequiredArgsConstructor
    public static class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
        private final JwtTokenProvider jwtTokenProvider;

        @Value("${front-end.redirect}")
        private String frontEndRedirect;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest req,
                                            HttpServletResponse res,
                                            Authentication authentication)
                throws IOException, ServletException {
            // 1) OAuth2AuthenticationToken으로 캐스팅해서 registrationId 가져오기
            var oauthToken = (OAuth2AuthenticationToken) authentication;
            String regId = oauthToken.getAuthorizedClientRegistrationId(); // "kakao" 또는 "github"

            // 2) OAuth2User 정보
            OAuth2User oAuth2User = oauthToken.getPrincipal();

            String username;

            if ("github".equals(regId)) {
                //  GitHub 로그인
                // GitHub API 응답에서 주요 속성 꺼내기
                String githubLogin = oAuth2User.getAttribute("login");
                username = "github_" + githubLogin;
            } else {
                // Kakao 로그인
                String id = oAuth2User.getAttribute("id").toString();
                username = "kakao_" + id;
            }

            // 3) JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(
                    new UsernamePasswordAuthenticationToken(username, "")
            );

            // 4) 프론트엔드로 토큰 전달 (쿼리 파라미터)
            String redirectUrl = UriComponentsBuilder
                    .fromUriString(frontEndRedirect)
                    .queryParam("token", token)
                    .build().toUriString();

            res.sendRedirect(redirectUrl);
        }
    }
}