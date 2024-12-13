package CS209A.project.demo.service;

import CS209A.project.demo.repository.AnswerRepository;
import CS209A.project.demo.repository.CommentRepository;
import CS209A.project.demo.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import CS209A.project.demo.model.Answer;
import CS209A.project.demo.model.Comment;
import CS209A.project.demo.model.Thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ForumService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ThreadRepository threadRepository;

    public List<Thread> getAllThreads() {
        return threadRepository.findAll();
    }

    public List<Answer> getAnswersByThread(Long threadId) {
        return answerRepository.findAllByThreadId(threadId);
    }

    public List<Comment> getCommentsByAnswer(Long answerId) {
        return commentRepository.findAllByAnswerId(answerId);
    }


    public Map<String, Integer> getWordFrequencyInTitles(String word) {
        // 查找标题中包含 "Error" 的所有线程
        List<Thread> threads = threadRepository.findByTitleContaining(word);

        // 用来存储单词频率
        Map<String, Integer> wordCount = new HashMap<>();

        // 正则表达式匹配单词
        Pattern pattern = Pattern.compile("\\b\\w+\\b");

        for (Thread thread : threads) {
            String title = thread.getTitle();
            Matcher matcher = pattern.matcher(title);

            // 对每个标题中的单词进行统计
            while (matcher.find()) {
                String matchedWord = matcher.group().toLowerCase();  // 将单词转换为小写，避免大小写混淆
                if (matchedWord.contains("error")) {  // 只统计包含 "error" 的单词
                    wordCount.put(matchedWord, wordCount.getOrDefault(matchedWord, 0) + 1);
                }
            }
        }

        return wordCount;
    }

    // 获取按频率排序的单词和对应次数的 List
    public List<String> getSortedWordsByFrequency(String word) {
        Map<String, Integer> wordCount = getWordFrequencyInTitles(word);

        // 将 Map 按照值（即出现次数）降序排序
        List<Map.Entry<String, Integer>> wordList = new ArrayList<>(wordCount.entrySet());
        wordList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // 将排序后的单词和次数分别提取到 List 中
        List<String> sortedWords = new ArrayList<>();
        List<Integer> sortedCounts = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : wordList) {
            sortedWords.add(entry.getKey());
            sortedCounts.add(entry.getValue());
        }

        // 输出结果
        System.out.println("Sorted Words: " + sortedWords);
        System.out.println("Sorted Counts: " + sortedCounts);

        return sortedWords;  // 或者返回 sortedWords 和 sortedCounts，取决于需求
    }
}
