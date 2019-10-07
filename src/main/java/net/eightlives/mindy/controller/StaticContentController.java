package net.eightlives.mindy.controller;

import net.eightlives.mindy.config.custom.StaticContentConfig;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class StaticContentController {

    private final StaticContentConfig staticContentConfig;
    private final AttributeProviderFactory attributeProviderFactory;

    public StaticContentController(StaticContentConfig staticContentConfig,
                                   AttributeProviderFactory attributeProviderFactory) {
        this.staticContentConfig = staticContentConfig;
        this.attributeProviderFactory = attributeProviderFactory;
    }

    @GetMapping("/{markdown}")
    public String content(@PathVariable String markdown, Model model) {
        if (staticContentConfig.getMarkdownToName() != null &&
                staticContentConfig.getMarkdownToName().containsKey(markdown)) {
            try {
                Path path = Paths.get(getClass().getResource("/static/markdown/" + markdown + ".md").toURI());

                try (Stream<String> fileLines = Files.lines(path)) {
                    String content = fileLines.collect(Collectors.joining("\n"));

                    Parser parser = Parser.builder().build();
                    Node document = parser.parse(content);
                    HtmlRenderer renderer = HtmlRenderer.builder()
                            .attributeProviderFactory(attributeProviderFactory)
                            .build();
                    String renderedContent = renderer.render(document);

                    model.addAttribute("content", renderedContent);
                }
            } catch (URISyntaxException | IOException e) {
                //TODO handle
            }

            model.addAttribute("title", staticContentConfig.getMarkdownToName().get(markdown));

            return "static_content";
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found");
    }
}
