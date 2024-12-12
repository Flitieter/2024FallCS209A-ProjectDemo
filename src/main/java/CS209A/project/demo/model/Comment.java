package CS209A.project.demo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @Column(unique = true)
    private Integer commentId;

    private String body;

    @Column(name = "owner_id")
    private Integer ownerId;

    @Column(name = "owner_reputation")
    private Integer ownerReputation;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
}
