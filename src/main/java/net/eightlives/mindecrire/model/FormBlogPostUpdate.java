package net.eightlives.mindecrire.model;

import jakarta.validation.constraints.NotBlank;

public record FormBlogPostUpdate(@NotBlank String postContent) {
}
