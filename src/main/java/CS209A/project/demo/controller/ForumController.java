package CS209A.project.demo.controller;

import CS209A.project.demo.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import CS209A.project.demo.model.Answer;
import CS209A.project.demo.model.Comment;
import CS209A.project.demo.model.Thread;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @GetMapping("/threads")
    public List<Thread> getAllThreads() {
        return forumService.getAllThreads();
    }

    @GetMapping("/answers/{threadId}")
    public List<Answer> getAnswersByThread(@PathVariable Long threadId) {
        return forumService.getAnswersByThread(threadId);
    }

    @GetMapping("/comments/{answerId}")
    public List<Comment> getCommentsByAnswer(@PathVariable Long answerId) {
        return forumService.getCommentsByAnswer(answerId);
    }
}
