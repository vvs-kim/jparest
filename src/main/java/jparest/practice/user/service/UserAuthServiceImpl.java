package jparest.practice.user.service;

import jparest.practice.auth.jwt.JwtService;
import jparest.practice.user.domain.LoginType;
import jparest.practice.user.domain.User;
import jparest.practice.user.dto.SocialJoinRequest;
import jparest.practice.user.dto.SocialJoinResponse;
import jparest.practice.user.dto.SocialLoginResponse;
import jparest.practice.user.dto.SocialUserInfoDto;
import jparest.practice.user.exception.LoginFailException;
import jparest.practice.user.feign.KakaoFeignClient;
import jparest.practice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jparest.practice.user.domain.UserType;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private static final String BEARER = "Bearer ";
    private static final String grantType = "authorization_code";

    private final KakaoFeignClient kakaoFeignClient;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final JwtService jwtService;

    @Value("${app.kakao.client-id}")
    private String clientId;

    @Value("${app.kakao.redirect-uri}")
    private String redirectUri;

    @Override
    @Transactional
    public User join(User user) {
//        User alreadyUser = userRepository.findFirstUserByLoginIdOrderByIdAsc(user.getLoginId()).orElse(null);
//        if (alreadyUser != null) throw new ExistLoginIdException(alreadyUser.getLoginId());

        User saveUser = User.builder()
                .socialUserId(user.getSocialUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .loginType(user.getLoginType())
                .userType(UserType.ROLE_GENERAL)
                .build();

        userRepository.save(saveUser);

        return saveUser;
    }

    @Override
    @Transactional
    public SocialJoinResponse socialJoin(SocialJoinRequest socialJoinRequest) {
        Optional<User> findSocialUser = userRepository.findBySocialUserId(socialJoinRequest.getSocialUserId());
        User user = findSocialUser.orElseGet(() -> join(socialJoinRequest));
        return new SocialJoinResponse(user.getNickname());
    }

    private User join(SocialJoinRequest socialJoinRequest) {
        System.out.println("join call!");

        User user = User.builder()
                .socialUserId(socialJoinRequest.getSocialUserId())
                .email(socialJoinRequest.getEmail())
                .nickname(socialJoinRequest.getNickname())
                .loginType(socialJoinRequest.getLoginType())
                .userType(UserType.ROLE_GENERAL)
                .build()
                ;

        System.out.println("user = " + user);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public SocialLoginResponse kakaoLogin(String code) {
        Map<String, Object> token = getKakaoToken(grantType, clientId, code, redirectUri);

        String accessToken = BEARER + token.get("access_token");

        ResponseEntity<SocialUserInfoDto> kakaoUserInfo = getKakaoUserInfo(accessToken);

        SocialUserInfoDto userInfo = kakaoUserInfo.getBody();

        String socialUserId = String.valueOf(userInfo.getId());
        String email = userInfo.getKakao_account().getEmail();
        String nickname = userInfo.getKakao_account().getProfile().getNickname();

        System.out.println("socialUserId = " + socialUserId);

        Optional<User> findSocialUser = userRepository.findBySocialUserId(socialUserId);

        System.out.println("findSocialUser = " + findSocialUser);
        findSocialUser.orElseGet(() -> join(new SocialJoinRequest(socialUserId, email, nickname, LoginType.KAKAO)));

        SocialLoginResponse socialLoginResponse = new SocialLoginResponse();
//        Optional<User> findSocialUser = userRepository.findBySocialUserId(socialUserId);

        return new SocialLoginResponse(socialUserId, email, nickname, LoginType.KAKAO);
    }

    private Map<String, Object> getKakaoToken(String grantType, String clientId, String code, String redirectUri) {
        try {
            return kakaoFeignClient.createToken(grantType, clientId, code, redirectUri);
        } catch (Exception e) {
            log.error("KAKAO CREATE TOKEN ERROR - {} ", e.getMessage());
            throw new LoginFailException("카카오 로그인 실패 code = " + code);
        }
    }

    private ResponseEntity<SocialUserInfoDto> getKakaoUserInfo(String token) {
        try {
            ResponseEntity<SocialUserInfoDto> userInfo = kakaoFeignClient.getUserInfo(new URI("https://kapi.kakao.com/v2/user/me"), token);
            return userInfo;
        } catch (Exception e) {
            log.error("KAKAO USER INFO ERROR - {} ", e.getMessage());
            throw new LoginFailException("카카오 정보 조회 실패 token = " + token);
        }
    }


//    아이디, 비밀번호 로그인 시
//    @Transactional
//    public UserLoginResponse login(String loginId, String password) {
//        User user = userRepository.findByLoginId(loginId)
//                .orElseThrow(() -> new LoginFailException("존재하지 않는 유저 loginID = " + loginId));
//        if(!passwordEncoder.matches(password, user.getPassword())) {
//            throw new LoginFailException("잘못된 비밀번호 loginID = " + loginId);
//        }
//
//        TokenDto tokenDto = new TokenDto();
//        tokenDto.setAccessToken(jwtService.createAccessToken(loginId, UserType.ROLE_GENERAL.name()));
//        tokenDto.setRefreshToken(jwtService.createRefreshToken(loginId));
//        return new UserLoginResponse(user.getEmail(), user.getName(), tokenDto);
//    }

//@Override
//@Transactional
//public User join(User user) {
//    User alreadyUser = userRepository.findFirstUserByLoginIdOrderByIdAsc(user.getLoginId()).orElse(null);
//    if (alreadyUser != null) throw new ExistLoginIdException(alreadyUser.getLoginId());
//
//    User saveUser = User.builder()
//            .loginId(user.getLoginId())
//            .password(passwordEncoder.encode(user.getPassword()))
//            .email(user.getEmail())
//            .name(user.getName())
//            .userType(UserType.ROLE_GENERAL)
//            .build();
//
//    userRepository.save(saveUser);
//
//    return saveUser;
//}
}
