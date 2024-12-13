package CS209A.project.demo.repository;

import CS209A.project.demo.model.Answer;
import CS209A.project.demo.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAnswerId(Long answerId);
    @Query("SELECT a FROM Comment a WHERE a.ownerReputation >= :reputationThreshold")
    List<Comment> findHigherReputationUser(@Param("reputationThreshold") Integer reputationThreshold);
}
