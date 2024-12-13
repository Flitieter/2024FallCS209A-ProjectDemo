package CS209A.project.demo.repository;

import CS209A.project.demo.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAnswerId(Long answerId);
}
