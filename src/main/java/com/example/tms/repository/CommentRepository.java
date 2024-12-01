package com.example.tms.repository;

import com.example.tms.repository.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Override
    <S extends Comment> S save(S entity);
}
