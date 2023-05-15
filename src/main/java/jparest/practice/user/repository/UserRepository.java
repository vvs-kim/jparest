package jparest.practice.user.repository;

import jparest.practice.user.domain.User;
import jparest.practice.user.dto.SocialJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialUserId(String socialUserId);

    User save(User user);
}