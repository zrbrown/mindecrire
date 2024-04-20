package net.eightlives.mindecrire.dao.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private String urlName;

    @NotBlank
    private String title;

    @Column
    private String content;

    @Column
    private LocalDateTime createdDateTime;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "posts_to_tags",
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private Set<Tag> tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private AuthorDetails authorDetails;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "post")
    private Set<PostUpdate> postUpdates;

    public Post() {
    }

    public Post(UUID id, String urlName, String title, String content, LocalDateTime createdDateTime, Set<Tag> tags,
                AuthorDetails authorDetails) {
        this.id = id;
        this.urlName = urlName;
        this.title = title;
        this.content = content;
        this.createdDateTime = createdDateTime;
        this.tags = tags;
        this.authorDetails = authorDetails;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<PostUpdate> getPostUpdates() {
        return postUpdates;
    }

    public void setPostUpdates(Set<PostUpdate> postUpdates) {
        this.postUpdates = postUpdates;
    }

    public AuthorDetails getAuthorDetails() {
        return authorDetails;
    }

    public void setAuthorDetails(AuthorDetails authorDetails) {
        this.authorDetails = authorDetails;
    }
}
