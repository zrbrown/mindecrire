package net.eightlives.mindecrire.model;

import java.util.ArrayList;
import java.util.List;

public record ImageUploadResponse(List<ImageUploadResult> successful, List<ImageUploadResult> failed) {

    public ImageUploadResponse {
        successful = new ArrayList<>();
        failed = new ArrayList<>();
    }

    public ImageUploadResponse() {
        this(null, null);
    }

    public record ImageUploadResult(String filename, String result) {
    }
}
