package com.fittrack.repository;

import com.fittrack.entity.Profile;
import com.fittrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Profile} entity database operations.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * Finds a profile by the associated user's ID.
     *
     * @param userId user identifier
     * @return Optional containing the profile or empty
     */
    Optional<Profile> findByUserId(Long userId);

    /**
     * Finds a profile by the associated {@link User} entity reference.
     *
     * @param user user entity
     * @return Optional containing the profile or empty
     */
    Optional<Profile> findByUser(User user);
}
