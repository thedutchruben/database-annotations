package nl.thedutchruben.databaseAnotations.demo;


import nl.thedutchruben.databaseAnotations.orm.annotations.*;

import java.sql.Timestamp;

/**
 * Example Comment entity demonstrating ManyToOne relationships.
 */
@Entity
@Table(name = "comments")
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "content", nullable = false, length = 500)
    private String content;
    
    @Column(name = "author_name", nullable = false, length = 100)
    private String authorName;
    
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    // Default constructor required for ORM
    public Comment() {}
    
    public Comment(String content, String authorName, Post post) {
        this.content = content;
        this.authorName = authorName;
        this.post = post;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", authorName='" + authorName + '\'' +
                ", createdAt=" + createdAt +
                ", post=" + (post != null ? post.getTitle() : null) +
                '}';
    }
}
