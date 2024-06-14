package net.eightlives.mindecrire.model;

import javax.validation.constraints.NotBlank;

public class FormattedPostUpdate {

    @NotBlank
    private String content;
    private String date;

    public FormattedPostUpdate(String content, String date) {
        this.content = content;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
