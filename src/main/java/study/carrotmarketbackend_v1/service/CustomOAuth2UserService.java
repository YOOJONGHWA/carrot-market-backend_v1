package study.carrotmarketbackend_v1.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import study.carrotmarketbackend_v1.dto.*;
import study.carrotmarketbackend_v1.entity.User;
import study.carrotmarketbackend_v1.repository.UserRepository;
import study.carrotmarketbackend_v1.status.AccountStatus;
import study.carrotmarketbackend_v1.status.UserType;

import java.util.Date;
import java.util.Optional;

@Service
public class CustomOAuth2UserService  extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("Unsupported registration ID: " + registrationId);
        }

        String oauthProvider = oAuth2Response.getProvider();
        String oauthId = oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();

        Optional<User> existingUser = userRepository.findByOauthId(oauthId);

        if (existingUser.isEmpty()) {
            // 신규 OAuth 사용자 등록
            User newUser = User.builder()
                    .role("ROLE_USER")
                    .userType(UserType.OAUTH)
                    .status(AccountStatus.ACTIVE)
                    .username(oAuth2Response.getName())
                    .email(email)
                    .phone("오어스에서 적용")
                    .password("오어스에서 적용")
                    .oauthProvider(oauthProvider)
                    .oauthId(oauthId)
                    .createDate(new Date())
                    .build();

            userRepository.save(newUser);

            return new CustomUser(newUser);
        } else {
            // 기존 유저가 존재할 경우 업데이트
            User existing = existingUser.get();

            User updatedUser = User.builder()
                        .id(existing.getId())
                        .role(existing.getRole())
                        .role(existing.getRole())
                        .userType(existing.getUserType())
                        .status(existing.getStatus())
                        .username(oAuth2Response.getName())
                        .email(email)
                        .phone(existing.getPhone())
                        .password(existing.getPassword())
                        .oauthProvider(oauthProvider)
                        .oauthId(oauthId)
                        .createDate(existing.getCreateDate())
                        .build();

                userRepository.save(updatedUser);

                return new CustomUser(updatedUser);
        }
    }
}
