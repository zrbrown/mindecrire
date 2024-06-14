package net.eightlives.mindecrire.model;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;

public class FormBlogPost {

    @NotBlank
    private String postTitle;
    @NotBlank
    private String postContent;
    private List<String> addedTags;

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public List<String> getAddedTags() {
        return addedTags == null ? Collections.emptyList() : addedTags;
    }

    public void setAddedTags(List<String> addedTags) {
        this.addedTags = addedTags;
    }
}
