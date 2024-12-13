package CS209A.project.demo.model;

import jakarta.persistence.*;
import java.util.List;

@Table(name = "threads")
@Entity
public class Thread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Integer questionId;

    private String title;
    private String body;

    // 使用 List<String> 来存储标签
    @ElementCollection
    @CollectionTable(name = "thread_tags", joinColumns = @JoinColumn(name = "thread_id"))
    @Column(name = "tag")
    private List<String> tags;  // 使用 List<String> 存储标签

    private Integer score;

    @Column(name = "owner_id")
    private Integer ownerId;

    @Column(name = "owner_reputation")
    private Integer ownerReputation;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getOwnerReputation() {
        return ownerReputation;
    }

    public void setOwnerReputation(Integer ownerReputation) {
        this.ownerReputation = ownerReputation;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
