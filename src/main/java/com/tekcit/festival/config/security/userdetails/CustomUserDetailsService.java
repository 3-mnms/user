package com.tekcit.festival.config.security.userdetails;

import com.tekcit.festival.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    //로컬 로그인 용도
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException{
        return userRepository.findByLoginId(loginId)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다(loginId: " + loginId+")"));
    }

    //accesstoken
    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        return userRepository.findById(userId)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다(userId: " + userId + ")"));
    }

}
