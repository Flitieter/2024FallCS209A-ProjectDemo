package CS209A.project.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import CS209A.project.demo.model.Answer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAllByThreadId(Long threadId);

    List<Answer> findByBodyContaining(String word);

    @Query("SELECT a FROM Answer a WHERE a.ownerReputation >= :reputationThreshold")
    List<Answer> findHigherReputationUser(@Param("reputationThreshold") Integer reputationThreshold);

}

