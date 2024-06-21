package net.eightlives.mindecrire.controller;

import net.eightlives.mindecrire.config.custom.StaticContentConfig;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class StaticContentController {

    private static final Logger LOG = LoggerFactory.getLogger(StaticContentController.class);

    private final StaticContentConfig staticContentConfig;
    private final AttributeProviderFactory attributeProviderFactory;

    public StaticContentController(StaticContentConfig staticContentConfig,
                                   AttributeProviderFactory attributeProviderFactory) {
        this.staticContentConfig = staticContentConfig;
        this.attributeProviderFactory = attributeProviderFactory;
    }

    @GetMapping("/{markdown:^(?!favicon\\.ico$).*}")
    public String content(@PathVariable String markdown, Model model) {
        if (staticContentConfig.getMarkdownToName() != null &&
                staticContentConfig.getMarkdownToName().containsKey(markdown)) {
            try {
                URL url = getClass().getResource("/static/markdown/" + markdown + ".md");
                if (url == null) {
                    LOG.error("Markdown file /static/markdown/{}.md cannot be found", markdown);
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server misconfigured");
                }

                try (Stream<String> fileLines = Files.lines(Paths.get(url.toURI()))) {
                    String content = fileLines.collect(Collectors.joining("\n"));

                    Parser parser = Parser.builder().build();
                    Node document = parser.parse(content);
                    HtmlRenderer renderer = HtmlRenderer.builder()
                            .attributeProviderFactory(attributeProviderFactory)
                            .build();
                    String renderedContent = renderer.render(document);

                    model.addAttribute("content", renderedContent);
                }
            } catch (URISyntaxException e) {
                LOG.error("Error while constructing path for markdown file /static/markdown/{}.md", markdown, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server misconfigured");
            } catch (IOException e) {
                LOG.error("Error while reading markdown file /static/markdown/{}.md", markdown, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error loading page");
            }

            model.addAttribute("title", staticContentConfig.getMarkdownToName().get(markdown));

            return "static_content";
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found");
    }
}
