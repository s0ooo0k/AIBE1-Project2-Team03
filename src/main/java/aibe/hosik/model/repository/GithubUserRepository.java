package aibe.hosik.model.repository;

import aibe.hosik.model.entity.GithubUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GithubUserRepository extends JpaRepository<GithubUser, Long> {
    Optional<GithubUser> findByUsername(String username);
}
