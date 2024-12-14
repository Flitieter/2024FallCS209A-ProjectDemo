package CS209A.project.demo.service;

import CS209A.project.demo.controller.JavatopicsImpl;
import CS209A.project.demo.repository.AnswerRepository;
import CS209A.project.demo.repository.CommentRepository;
import CS209A.project.demo.repository.ThreadRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import CS209A.project.demo.model.Answer;
import CS209A.project.demo.model.Comment;
import CS209A.project.demo.model.Thread;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    public Map<Integer,Integer> getUserReputation(){
        List<Thread> threads=threadRepository.findAll();
        List<Answer> answers=answerRepository.findAll();
        List<Comment> comments=commentRepository.findAll();
        Map<Integer, Integer> userReputation=new HashMap<>();
        threads.forEach(thread -> {
            if(thread.getOwnerId()!=null){
                userReputation.put(thread.getOwnerId(),thread.getOwnerReputation());
            }

        });
        answers.forEach(answer -> {
            if(answer.getOwnerId()!=null){
                userReputation.put(answer.getOwnerId(),answer.getOwnerReputation());
            }
        });
        comments.forEach(comment -> {
            if(comment.getOwnerId()!=null){
                userReputation.put(comment.getOwnerId(),comment.getOwnerReputation());
            }
        });
        List<Map.Entry<Integer, Integer>> sortedList = userReputation.entrySet()
                .stream()  // 将 Map 转换为 Stream
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))  // 按值升序排序
                .toList();  // 收集成 List
        System.out.println(sortedList.size()+" "+sortedList.get(sortedList.size()/2));
        System.out.println(sortedList);
        return userReputation;
    }
    public List<Answer> findHigherReputationUser(Integer score){
        List<Answer> answers= answerRepository.findHigherReputationUser(score);
        System.out.println("DONE");
        answers.forEach((answer)->{System.out.println(answer.getAnswerId()+" "+answer.getOwnerReputation());});
        return answers;
    }
    private List<String> cleanString(String input){
        String cleanedInput = input.replaceAll("[{}]", "");  // 去掉花括号
        String[] items = cleanedInput.split(",");  // 使用逗号分割

        // 将数组转换为 List
        List<String> list = Arrays.asList(items);

        // 打印结果
        return list;
    }
    private <T> void addMap(Map<T,Integer> map,T key,int add){
        if(map.containsKey(key)){
            map.put(key,map.get(key)+add);
        }
        else{
            map.put(key,add);
        }
    }
    public List<Map<Long,Integer> > analyzeGoodAnswerByDuration(){
        List<Map<Long,Integer> > ans=new ArrayList<>();
        List<Answer> answers=answerRepository.findAll();
        Map<Long,Integer> tot_ans=new HashMap<>();
        Map<Long,Integer> good_ans=new HashMap<>();

        for(Answer answer:answers){
            Duration duration = Duration.between(answer.getThread().getCreationDate(), answer.getCreationDate());
            long hours = duration.toHours();
            addMap(tot_ans,hours,1);
            if(answer.getIsAccepted()==1||answer.getScore()>=50){
                addMap(good_ans,hours,1);
            }
        }
        ans.add(tot_ans);
        ans.add(good_ans);
        return ans;
    }

    public List<Map<Integer,Integer> > analyzeGoodAnswerByReputation(){
        List<Map<Integer,Integer> > ans=new ArrayList<>();
        List<Answer> answers=answerRepository.findAll();
        Map<Integer,Integer> tot_ans=new HashMap<>();
        Map<Integer,Integer> good_ans=new HashMap<>();

        for(Answer answer:answers){
            if(answer.getOwnerReputation()==null)continue;
            int reputation=answer.getOwnerReputation();
            addMap(tot_ans,reputation,1);
            if(answer.getIsAccepted()==1||answer.getScore()>=50){
                addMap(good_ans,reputation,1);
            }
        }
        ans.add(tot_ans);
        ans.add(good_ans);
        return ans;
    }

    public List<Map<Integer,Integer> > analyzeGoodAnswerByComments(){
        List<Map<Integer,Integer> > ans=new ArrayList<>();
        List<Answer> answers=answerRepository.findAll();
        Map<Integer,Integer> tot_ans=new HashMap<>();
        Map<Integer,Integer> good_ans=new HashMap<>();

        for(Answer answer:answers){
            int commentNumber=answer.getComments().size();
            addMap(tot_ans,commentNumber,1);
            if(answer.getIsAccepted()==1||answer.getScore()>=50){
                addMap(good_ans,commentNumber,1);
            }
        }
        ans.add(tot_ans);
        ans.add(good_ans);
        return ans;
    }
    public List<Map<String, Object>> getHotEngagementTopics(int score) {
        // 获取所有相关的数据
        List<Answer> answers = answerRepository.findHigherReputationUser(score);
        List<Comment> comments = commentRepository.findHigherReputationUser(score);
        List<Thread> threads = threadRepository.findAll();

        // 统计每个线程的互动次数
        Map<Long, Integer> count = new HashMap<>();
        for (Answer answer : answers) {
            long threadId = answer.getThread().getId();
            count.put(threadId, count.getOrDefault(threadId, 0) + 1);
        }

        for (Comment comment : comments) {
            long threadId = comment.getAnswer().getThread().getId();
            count.put(threadId, count.getOrDefault(threadId, 0) + 1);
        }

        // 统计每个标签的频次
        Map<String, Integer> tagsFreq = new HashMap<>();
        for (Thread thread : threads) {
            long threadId = thread.getId();
            if (count.containsKey(threadId)) {
                int times = count.get(threadId);
                List<String> tags = cleanString(thread.getTags());
                for (String tag : tags) {
                    tagsFreq.put(tag, tagsFreq.getOrDefault(tag, 0) + times);
                }
            }
        }

        // 转换为 [{ "tag": "标签名", "count": 频次 }] 格式
        // 按照 count 降序排序

        return tagsFreq.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> tagData = new HashMap<>();
                    tagData.put("tag", entry.getKey());
                    tagData.put("count", entry.getValue());
                    return tagData;
                })
                // 按照 count 降序排序
                .sorted((map1, map2) -> Integer.compare((int) map2.get("count"), (int) map1.get("count")))
                .collect(Collectors.toList());
    }
    public List<Map<String, Object>> getAllTags() {
        // 获取所有线程
        List<Thread> threads = threadRepository.findAll();

        // 使用一个列表来存储所有标签
        List<String> tags = new ArrayList<>();

        // 提取所有标签并去除大括号和空格
        threads.forEach(thread -> {
            Arrays.stream(thread.getTags().split(","))
                    .map(tag -> tag.replaceAll("[{}]", "").trim())  // 去除标签中的大括号和多余的空格
                    .forEach(tags::add);
        });

        // 统计每个标签出现的次数
        Map<String, Integer> tagCountMap = tags.stream()
                .collect(Collectors.groupingBy(tag -> tag, Collectors.summingInt(tag -> 1)));

        // 转换为结果格式 [{ "tag": "标签名", "count": 频次 }]
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tagCountMap.entrySet()) {
            Map<String, Object> tagData = new HashMap<>();
            tagData.put("tag", entry.getKey());
            tagData.put("count", entry.getValue());
            result.add(tagData);
        }

        // 按照 count 降序排序
        result.sort((map1, map2) -> Integer.compare((int) map2.get("count"), (int) map1.get("count")));

        return result;
    }
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

}
