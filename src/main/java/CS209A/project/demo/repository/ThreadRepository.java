package CS209A.project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import CS209A.project.demo.model.Thread;

import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findByTitleContaining(String title);
}
