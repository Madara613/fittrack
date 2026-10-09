package com.fittrack.repository;

import com.fittrack.entity.ProgressLog;
import com.fittrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ProgressLog} entity database operations.
 */
@Repository
public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {

    /**
     * Finds all progress logs belonging to a user ID.
     *
     * @param userId user identifier
     * @return list of progress logs
     */
    List<ProgressLog> findByUserId(Long userId);

    /**
     * Finds all progress logs belonging to a user ID ordered by log date descending.
     *
     * @param userId user identifier
     * @return sorted list of progress logs
     */
    List<ProgressLog> findByUserIdOrderByDateDesc(Long userId);

    /**
     * Finds all progress logs belonging to a user entity ordered by log date descending.
     *
     * @param user user entity
     * @return sorted list of progress logs
     */
    List<ProgressLog> findByUserOrderByDateDesc(User user);
}
