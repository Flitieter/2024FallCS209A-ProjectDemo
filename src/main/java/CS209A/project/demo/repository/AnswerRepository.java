package CS209A.project.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import CS209A.project.demo.model.Answer;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAllByThreadId(Long threadId);

    List<Answer> findByBodyContaining(String word);
}

