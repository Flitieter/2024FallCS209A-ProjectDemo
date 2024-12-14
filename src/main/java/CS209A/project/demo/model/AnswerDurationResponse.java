package CS209A.project.demo.model;

public class AnswerDurationResponse {
    private long time;  // 时间（小时）
    private int count;  // 数量

    // 构造函数
    public AnswerDurationResponse(long time, int count) {
        this.time = time;
        this.count = count;
    }

    // Getter 和 Setter
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
