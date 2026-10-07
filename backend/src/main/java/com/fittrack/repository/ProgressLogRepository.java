package com.fittrack.repository;

import com.fittrack.entity.ProgressLog;
import com.fittrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {
    List<ProgressLog> findByUserId(Long userId);
    List<ProgressLog> findByUserIdOrderByDateDesc(Long userId);
    List<ProgressLog> findByUserOrderByDateDesc(User user);
}
