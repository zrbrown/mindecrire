package net.eightlives.mindecrire.model;

import jakarta.validation.constraints.NotBlank;

public record FormattedPostUpdate(@NotBlank String content, String date) {
}
