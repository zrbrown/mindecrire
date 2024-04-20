package net.eightlives.mindy.model;

import java.util.ArrayList;
import java.util.List;

public class ImageUploadResponse {

    private List<ImageUploadResult> successful = new ArrayList<>();
    private List<ImageUploadResult> failed = new ArrayList<>();

    public List<ImageUploadResult> getSuccessful() {
        return successful;
    }

    public void setSuccessful(List<ImageUploadResult> successful) {
        this.successful = successful;
    }

    public List<ImageUploadResult> getFailed() {
        return failed;
    }

    public void setFailed(List<ImageUploadResult> failed) {
        this.failed = failed;
    }

    public static class ImageUploadResult {
        private String filename;
        private String result;

        public ImageUploadResult(String filename, String result) {
            this.filename = filename;
            this.result = result;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}
