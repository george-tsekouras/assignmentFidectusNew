package com.fidectus.eventlog.repository;

import com.fidectus.eventlog.domain.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {

    Optional<EventLog> findFirstByUserIdOrderByIdDesc(Long userId);

    List<EventLog> findAllByUserIdOrderByIdDesc(long userId);
    
}
