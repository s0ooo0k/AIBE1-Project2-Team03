package aibe.hosik.service;

import aibe.hosik.model.entity.GithubUser;
import aibe.hosik.model.entity.KakaoUser;
import aibe.hosik.model.entity.LocalUser;
import aibe.hosik.model.repository.GithubUserRepository;
import aibe.hosik.model.repository.KakaoUserRepository;
import aibe.hosik.model.repository.LocalUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log
public class CustomUserDetailsService implements UserDetailsService {
    private final KakaoUserRepository kakaoUserRepository;
    private final GithubUserRepository GithubUserRepository;
    private final LocalUserRepository localUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.startsWith("kakao_")) {
            //  카카오 로그인 유저
            KakaoUser account = kakaoUserRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("유저가 없습니다: " + username));
            return User.builder()
                    .username(account.getUsername())
                    .password("")  // OAuth2 로그인은 패스워드 미사용
                    .build();
        }else if (username.startsWith("github_")) {
            //  깃허브 로그인 유저
            GithubUser account = GithubUserRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("유저가 없습니다: " + username));
            return User.builder()
                    .username(account.getUsername())
                    .password("")  // OAuth2 로그인은 패스워드 미사용
                    .build();
        }
    }
}
