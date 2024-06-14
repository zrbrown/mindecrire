package net.eightlives.mindecrire.model;

import javax.validation.constraints.NotBlank;

public class FormBlogPostUpdate {

    @NotBlank
    private String postContent;

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }
}
