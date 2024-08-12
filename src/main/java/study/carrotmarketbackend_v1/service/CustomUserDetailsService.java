package study.carrotmarketbackend_v1.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import study.carrotmarketbackend_v1.dto.CustomUser;
import study.carrotmarketbackend_v1.entity.User;
import study.carrotmarketbackend_v1.repository.UserRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository memberRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository memberRepository) {

        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> userData = memberRepository.findByEmail(email);

        User normalUser = userData.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new CustomUser(normalUser);

    }
}
