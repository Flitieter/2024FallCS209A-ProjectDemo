package CS209A.project.demo.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;


public class JavatopicsImpl {
    Map<String,Integer> Count=new HashMap<>();
    private List<String> cleanString(String input){
        String cleanedInput = input.replaceAll("[{}]", "");  // 去掉花括号
        String[] items = cleanedInput.split(",");  // 使用逗号分割

        // 将数组转换为 List
        List<String> list = Arrays.asList(items);

        // 打印结果
        return list;
    }
    private void addTag(String tag){
        if(Count.containsKey(tag)){
            Count.put(tag,Count.get(tag)+1);
        }
        else{
            Count.put(tag,1);
        }
    }
    public void Query(Connection connection){
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM threads";  // 假设有一个 users 表
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String tags = resultSet.getString("tags");
                List<String> tag_list=cleanString(tags);
                tag_list.forEach(this::addTag);
            }


            List<Map.Entry<String, Integer>> sortedList = Count.entrySet()
                    .stream()  // 将 Map 转换为 Stream
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))  // 按值升序排序
                    .toList();  // 收集成 List

            // 打印排序后的结果
            sortedList.forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
