package net.eightlives.mindecrire.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "static-content")
@RefreshScope
public class StaticContentConfig {

    private Map<String, String> markdownToName;

    public Map<String, String> getMarkdownToName() {
        return markdownToName;
    }

    public void setMarkdownToName(Map<String, String> markdownToName) {
        this.markdownToName = markdownToName;
    }
}
