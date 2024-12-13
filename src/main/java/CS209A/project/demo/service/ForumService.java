package CS209A.project.demo.service;

import CS209A.project.demo.controller.JavatopicsImpl;
import CS209A.project.demo.repository.AnswerRepository;
import CS209A.project.demo.repository.CommentRepository;
import CS209A.project.demo.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import CS209A.project.demo.model.Answer;
import CS209A.project.demo.model.Comment;
import CS209A.project.demo.model.Thread;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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


    // 查询包含指定单词的线程和回答，并统计所有相关的单词频率
    public Map<String, Integer> getWordFrequencyInTitlesAndAnswers(String word) {
        // 查找包含指定单词的线程标题和正文，或者包含指定单词的回答
        List<Thread> threads = threadRepository.findByTitleContainingOrBodyContaining(word, word);
        List<Answer> answers = answerRepository.findByBodyContaining(word);

        // 用来存储单词频率
        Map<String, Integer> wordCount = new HashMap<>();

        // 正则表达式匹配单词
        Pattern pattern = Pattern.compile("\\b\\w+\\b");

        // 统计线程标题和正文中的单词频率
        for (Thread thread : threads) {
            String title = thread.getTitle();
            Matcher matcher = pattern.matcher(title);

            // 对每个标题中的单词进行统计
            while (matcher.find()) {
                String matchedWord = matcher.group().toLowerCase();  // 将单词转换为小写，避免大小写混淆
                if (matchedWord.contains(word)) {  // 只统计包含指定单词的单词
                    wordCount.put(matchedWord, wordCount.getOrDefault(matchedWord, 0) + 1);
                }
            }

            String body = thread.getBody();
            matcher = pattern.matcher(body);

            // 对每个正文中的单词进行统计
            while (matcher.find()) {
                String matchedWord = matcher.group().toLowerCase();  // 将单词转换为小写，避免大小写混淆
                if (matchedWord.contains(word)) {  // 只统计包含指定单词的单词
                    wordCount.put(matchedWord, wordCount.getOrDefault(matchedWord, 0) + 1);
                }
            }
        }

        // 统计包含指定单词的所有回答的单词频率
        for (Answer answer : answers) {
            String body = answer.getBody();
            Matcher matcher = pattern.matcher(body);

            // 对每个回答的 body 部分中的单词进行统计
            while (matcher.find()) {
                String matchedWord = matcher.group().toLowerCase();  // 将单词转换为小写，避免大小写混淆
                if (matchedWord.contains(word)) {  // 只统计包含指定单词的单词
                    wordCount.put(matchedWord, wordCount.getOrDefault(matchedWord, 0) + 1);
                }
            }
        }

        return wordCount;
    }

    // 获取按频率排序的单词和对应次数的 List，并返回 JSON 格式的结果
    public List<Map<String, Object>> getSortedWordsByFrequency(String word) {
        // 获取单词频率，包括线程标题、正文和回答
        Map<String, Integer> wordCount = getWordFrequencyInTitlesAndAnswers(word);

        // 将 Map 按照值（即出现次数）降序排序
        List<Map.Entry<String, Integer>> wordList = new ArrayList<>(wordCount.entrySet());
        wordList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // 创建结果列表，用于存储每个单词和其出现次数的 Map
        List<Map<String, Object>> sortedWordsWithCount = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : wordList) {
            Map<String, Object> wordMap = new HashMap<>();
            wordMap.put("word", entry.getKey());
            wordMap.put("count", entry.getValue());
            sortedWordsWithCount.add(wordMap);
        }

        // 输出排序后的结果
        System.out.println("Sorted Words with Count: " + sortedWordsWithCount);

        // 返回 JSON 格式的字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(sortedWordsWithCount);
            System.out.println("JSON Output: " + json);
            return sortedWordsWithCount;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Map.Entry<String, Integer>> getAllTags(){
        List<Thread> threads=threadRepository.findAll();
        List<String> tags=new ArrayList<>();
        threads.forEach(thread -> {tags.add(thread.getTags());});
        JavatopicsImpl javatopics=new JavatopicsImpl();
        return javatopics.Query(tags);
    }
}
