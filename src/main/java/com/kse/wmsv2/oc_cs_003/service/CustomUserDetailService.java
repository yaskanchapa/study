package com.kse.wmsv2.oc_cs_003.service;

import com.kse.wmsv2.oc_cs_003.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * SpringSecurityConfigに関わる
 * UserDetailService
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userCd) throws UsernameNotFoundException {
        return userRepository.findById(userCd)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザー情報が見つかりません"));
    }

}
