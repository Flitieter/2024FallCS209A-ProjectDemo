package CS209A.project.demo.model;

import jakarta.persistence.*;
import java.util.List;

@Table(name = "answers")
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @Column(unique = true)
    private Integer answerId;

    private String body;
    private Integer score;

    @Column(name = "is_accepted")
    private Integer isAccepted;

    @Column(name = "owner_id")
    private Integer ownerId;

    @Column(name = "owner_reputation")
    private Integer ownerReputation;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Integer isAccepted) {
        this.isAccepted = isAccepted;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
