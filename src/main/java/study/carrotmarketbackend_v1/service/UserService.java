package study.carrotmarketbackend_v1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import study.carrotmarketbackend_v1.dto.*;
import study.carrotmarketbackend_v1.entity.Address;
import study.carrotmarketbackend_v1.entity.User;
import study.carrotmarketbackend_v1.exception.UserErrorCode;
import study.carrotmarketbackend_v1.exception.UserException;
import study.carrotmarketbackend_v1.repository.UserRepository;
import study.carrotmarketbackend_v1.status.AccountStatus;
import study.carrotmarketbackend_v1.status.UserStatusEnum;
import study.carrotmarketbackend_v1.status.UserType;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponse<CreateUser.Response> createMember(CreateUser.Request request) {

        validateCreateMemberRequest(request);

        // DTO를 엔티티로 변환
        Address address = Address.builder()
                .city(request.getAddress().getCity())
                .street(request.getAddress().getStreet())
                .zipcode(request.getAddress().getZipcode())
                .build();

        String userRole = "ROLE_USER";
        if(request.getEmail().equals("admin@admin.com")) {
            userRole = "ROLE_ADMIN";
        }

        Date now = new Date();
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.info("Encoded password: {}", encodedPassword);  // 인코딩된 비밀번호를 로그에 출력에 출력
        User user = User.builder()
                .role(userRole)
                .userType(UserType.REGULAR)
                .status(AccountStatus.ACTIVE)
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(address)
                .oauthProvider("제공되지 않음")
                .oauthId("제공되지 않음")
                .createDate(now)
                .updatedDate(now)
                .build();
        log.info("password {}", passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        CreateUser.Response response = CreateUser.Response.fromEntity(user);
        return ApiResponse.<CreateUser.Response>builder()
                .status(UserStatusEnum.OK.getStatusCode())
                .message(UserStatusEnum.OK.getMessage())
                .body(response)
                .build();
    }

    private void validateCreateMemberRequest(CreateUser.Request request) {

        // 이메일 중복 검사
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 사용자 이름 중복 검사
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserException(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }

    }

    public ApiResponse<User> getUserDetails(Authentication authentication) {

        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getName();

        Optional<User> userData = userRepository.findByUsername(username);
        User user = userData.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return ApiResponse.<User>builder()
                .status(UserStatusEnum.OK.getStatusCode())
                .message(UserStatusEnum.OK.getMessage())
                .body(user)
                .build();
    }

    public ApiResponse<UpdateUser.Response> updateUser(UpdateUser.Request request, Authentication authentication) {

        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername();
        log.info("updateMember: " + username);

        User existingUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Address address = Address.builder()
                .city(request.getAddress().getCity())
                .street(request.getAddress().getStreet())
                .zipcode(request.getAddress().getZipcode())
                .build();

        Date now = new Date();
        User updatedUser = User.builder()
                .id(existingUser.getId())
                .role(existingUser.getRole())
                .userType(existingUser.getUserType())
                .status(existingUser.getStatus())
                .username(request.getUsername())
                .email(existingUser.getEmail())
                .password(existingUser.getPassword())
                .phone(request.getPhone())
                .address(address)
                .oauthProvider(existingUser.getOauthProvider())
                .oauthId(existingUser.getOauthId())
                .createDate(existingUser.getCreateDate())
                .updatedDate(now)
                .build();

        userRepository.save(updatedUser);

        UpdateUser.Response response = UpdateUser.Response.fromEntity(updatedUser);

        return ApiResponse.<UpdateUser.Response>builder()
                .status(UserStatusEnum.OK.getStatusCode())
                .message(UserStatusEnum.OK.getMessage())
                .body(response)
                .build();
    }

    public ApiResponse<Void> changePassword(ChangePassword.Request request, Authentication authentication) {

        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername();

        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        if (passwordEncoder.matches(request.getNewPassword(), existingUser.getPassword())) {
            throw new UserException(UserErrorCode.SAME_PASSWORD);
        }

        Date now = new Date();
        User updatedUser = User.builder()
                .id(existingUser.getId())
                .role(existingUser.getRole())
                .userType(existingUser.getUserType())
                .status(existingUser.getStatus())
                .username(existingUser.getUsername())
                .email(existingUser.getEmail())
                .password(passwordEncoder.encode(request.getNewPassword()))
                .phone(existingUser.getPhone())
                .address(existingUser.getAddress())
                .oauthProvider(existingUser.getOauthProvider())
                .oauthId(existingUser.getOauthId())
                .createDate(existingUser.getCreateDate())
                .updatedDate(now)
                .build();

        userRepository.save(updatedUser);

        return ApiResponse.<Void>builder()
                .status(UserStatusEnum.OK.getStatusCode())
                .message("비밀번호가 성공적으로 변경되었습니다.")
                .build();
    }
}
