package com.ssafy.doit.service.user;

import com.ssafy.doit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).map(u -> new UserDetailsImpl(u,
                Collections.singleton(new SimpleGrantedAuthority(u.getRoleKey()))))
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
