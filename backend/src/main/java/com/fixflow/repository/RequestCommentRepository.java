package com.fixflow.repository;

import com.fixflow.model.RequestComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestCommentRepository extends JpaRepository<RequestComment, Long> {

    // Returns comments oldest-first so the thread reads chronologically
    List<RequestComment> findByRequestIdOrderByCreatedAtAsc(Long requestId);
}
