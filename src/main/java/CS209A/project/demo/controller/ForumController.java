package CS209A.project.demo.controller;

import CS209A.project.demo.service.ForumService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import CS209A.project.demo.model.Answer;
import CS209A.project.demo.model.Comment;
import CS209A.project.demo.model.Thread;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @GetMapping("/threads")
    public List<Thread> getAllThreads() {
        return forumService.getAllThreads();
    }

    @GetMapping("/threads/tags")
    public List<Map<String, Object>> findAllTags(){
        return forumService.getAllTags();
    }

    @GetMapping("/threads/HigherReputationTags/{score}")
    public List<Map<String, Object>> getHigherReputationTags(@PathVariable Integer score){
        return forumService.getHotEngagementTopics(score);
    }
    @GetMapping("/users")
    public Map<Integer,Integer> getUserReputation(){
        return forumService.getUserReputation();
    }

    @GetMapping("/answers/reputation/{score}")
    public List<Answer> findHigherReputationAnswer(@PathVariable Integer score){
        return forumService.findHigherReputationUser(score);
    }
    @GetMapping("/answers/{threadId}")
    public List<Answer> getAnswersByThread(@PathVariable Long threadId) {
        return forumService.getAnswersByThread(threadId);
    }

    @GetMapping("/comments/{answerId}")
    public List<Comment> getCommentsByAnswer(@PathVariable Long answerId) {
        return forumService.getCommentsByAnswer(answerId);
    }

    @GetMapping("/threads/search/{words}")
    public List<Map<String, Object>> getThreadsByWords(@PathVariable String words) {
        return forumService.getSortedWordsByFrequency(words);
    }

    @GetMapping("/analyze/duration")
    public List<Map<Long, Integer>> getAnalyzeByDuration(){
        return forumService.analyzeGoodAnswerByDuration();
    }

    @GetMapping("/analyze/reputation")
    public List<Map<Integer, Integer>> getAnalyzeByReputation(){
        return forumService.analyzeGoodAnswerByReputation();
    }

    @GetMapping("/analyze/comments")
    public List<Map<Integer, Integer>> getAnalyzeByComment(){
        return forumService.analyzeGoodAnswerByComments();
    }
}
