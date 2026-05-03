package net.eightlives.mindecrire.model;

import jakarta.validation.constraints.NotBlank;

import java.util.Collections;
import java.util.List;

public record FormBlogPost(@NotBlank String postTitle, @NotBlank String postContent, List<String> addedTags) {

    public List<String> addedTags() {
        return addedTags == null ? Collections.emptyList() : addedTags;
    }
}
