package CS209A.project.demo.model;

import jakarta.persistence.*;
import java.util.List;

@Table(name = "threads")
@Entity
public class Thread {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "thread_id_seq")
    @SequenceGenerator(name = "thread_id_seq", sequenceName = "threads_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "question_id", unique = true)
    private Integer questionId;

    private String title;

    @Column(name = "body", columnDefinition = "text")
    private String body;

    // 使用 List<String> 来存储标签

    private String tags;

    @Column(name = "creation_date")
    private java.time.OffsetDateTime creationDate;

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
    public String getTags(){
        return tags;
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


    public java.time.OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.time.OffsetDateTime creationDate) {
        this.creationDate = creationDate;
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
