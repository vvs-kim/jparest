package jparest.practice.user.repository;

import jparest.practice.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findBySocialUserId(String socialUserId);

    Optional<User> findByNickname(String nickname);
}