package CS209A.project.demo.controller;

import CS209A.project.demo.service.ForumService;
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

    @GetMapping("/analyze/duration/total")
    public List<Map<String, Object>> getAnalyzeTotalByDuration(){
        return forumService.analyzeTotalByDuration();
    }

    @GetMapping("/analyze/duration/good")
    public List<Map<String, Object>> getAnalyzeGoodByDuration(){
        return forumService.analyzeGoodByDuration();
    }

    @GetMapping("/analyze/reputation/total")
    public List<Map<String, Object>> getAnalyzeTotalByReputation(){
        return forumService.analyzeTotalByReputation();
    }

    @GetMapping("/analyze/reputation/good")
    public List<Map<String, Object>> getAnalyzeGoodByReputation(){
        return forumService.analyzeGoodByReputation();
    }

    @GetMapping("/analyze/comments/total")
    public List<Map<String, Object>> getAnalyzeTotalByComment(){
        return forumService.analyzeTotalByComments();
    }

    @GetMapping("/analyze/comments/good")
    public List<Map<String, Object>> getAnalyzeGoodByComment(){
        return forumService.analyzeGoodByComments();
    }
}
