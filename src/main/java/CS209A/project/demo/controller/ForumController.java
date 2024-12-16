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
    public List<Map<String, Object>> findAllTags() {
        return forumService.getAllTags();
    }

    @GetMapping("/threads/tags/{topN}")
    public List<Map<String, Object>> findAllTagsTopN(@PathVariable Integer topN) {
        List<Map<String, Object>> list = forumService.getAllTags();
        return list.subList(0, Math.min(topN, list.size()));
    }

    @GetMapping("/threads/tags/search/{name}")
    public Integer findAllTagsByName(@PathVariable String name) {
        List<Map<String, Object>> list = forumService.getAllTags();
        for (Map<String, Object> map : list) {
            if (map.get("tag").equals(name)) {
                return (Integer) map.get("count");
            }
        }
        return null;
    }

    @GetMapping("/threads/HigherReputationTags/{score}")
    public List<Map<String, Object>> getHigherReputationTags(@PathVariable Integer score) {
        return forumService.getHotEngagementTopics(score);
    }

    @GetMapping("/users")
    public Map<Integer, Integer> getUserReputation() {
        return forumService.getUserReputation();
    }

    @GetMapping("/answers/reputation/{score}")
    public List<Answer> findHigherReputationAnswer(@PathVariable Integer score) {
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

    @GetMapping("/threads/mistake/{topN}")
    public List<Map<String, Object>> getThreadsMistakesTopN(@PathVariable Integer topN) {
        List<Map<String, Object>> list_error = forumService.getSortedWordsByFrequency("error");
        List<Map<String, Object>> list_exception = forumService.getSortedWordsByFrequency("exception");
        list_error.addAll(list_exception);
        list_error.sort((map1, map2) -> Integer.compare((Integer) map2.get("count"), (Integer) map1.get("count")));
        return list_error.subList(0, Math.min(topN, list_error.size()));
    }

    @GetMapping("/threads/mistake/search/{name}")
    public Integer getThreadsMistakesByName(@PathVariable String name) {
        List<Map<String, Object>> list_error = forumService.getSortedWordsByFrequency("error");
        List<Map<String, Object>> list_exception = forumService.getSortedWordsByFrequency("exception");
        //list_error.addAll(list_exception);
        //list_error.sort((map1, map2) -> Integer.compare((Integer) map2.get("count"), (Integer) map1.get("count")));
        for (Map<String, Object> map : list_error) {
            if (map.get("word").equals(name)) {
                System.out.println("YES" + map.get("count"));
                return (Integer) map.get("count");
            }
        }
        for (Map<String, Object> map : list_exception) {
            if (map.get("word").equals(name)) {
                System.out.println("YES" + map.get("count"));
                return (Integer) map.get("count");
            }
        }
        return null;
    }

    @GetMapping("/analyze/duration/total")
    public List<Map<String, Object>> getAnalyzeTotalByDuration() {
        return forumService.analyzeTotalByDuration();
    }

    @GetMapping("/analyze/duration/good")
    public List<Map<String, Object>> getAnalyzeGoodByDuration() {
        return forumService.analyzeGoodByDuration();
    }

    @GetMapping("/analyze/reputation/total")
    public List<Map<String, Object>> getAnalyzeTotalByReputation() {
        return forumService.analyzeTotalByReputation();
    }

    @GetMapping("/analyze/reputation/good")
    public List<Map<String, Object>> getAnalyzeGoodByReputation() {
        return forumService.analyzeGoodByReputation();
    }

    @GetMapping("/analyze/comments/total")
    public List<Map<String, Object>> getAnalyzeTotalByComment() {
        return forumService.analyzeTotalByComments();
    }

    @GetMapping("/analyze/comments/good")
    public List<Map<String, Object>> getAnalyzeGoodByComment() {
        return forumService.analyzeGoodByComments();
    }
}
