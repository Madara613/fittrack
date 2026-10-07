package com.fittrack.repository;

import com.fittrack.entity.Workout;
import com.fittrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUserId(Long userId);
    List<Workout> findByUserIdOrderByDateDesc(Long userId);
    List<Workout> findByUserOrderByDateDesc(User user);
}
