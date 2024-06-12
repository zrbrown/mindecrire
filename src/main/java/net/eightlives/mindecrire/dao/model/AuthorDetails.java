package net.eightlives.mindecrire.dao.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "author_details")
public class AuthorDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private String author;

    @Column
    private String displayName;

    public AuthorDetails() {
    }

    public AuthorDetails(UUID id, String author, String displayName) {
        this.id = id;
        this.author = author;
        this.displayName = displayName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
