package ru.effective.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effective.tms.model.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
