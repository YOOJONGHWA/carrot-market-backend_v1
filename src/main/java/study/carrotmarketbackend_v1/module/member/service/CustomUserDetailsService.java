package study.carrotmarketbackend_v1.module.member.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import study.carrotmarketbackend_v1.module.member.dto.CustomUserDetails;
import study.carrotmarketbackend_v1.module.member.entity.Member;
import study.carrotmarketbackend_v1.module.member.repository.MemberRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {

        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Member> member = memberRepository.findByEmail(email);
        return member.map(CustomUserDetails::new).orElse(null);

    }
}
