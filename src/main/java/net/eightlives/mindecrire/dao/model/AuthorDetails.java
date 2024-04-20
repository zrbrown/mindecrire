package net.eightlives.mindy.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "author_details")
public class AuthorDetails {

    @Id
    private String author;

    @Column
    private String displayName;

    public AuthorDetails() {
    }

    public AuthorDetails(String author, String displayName) {
        this.author = author;
        this.displayName = displayName;
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
