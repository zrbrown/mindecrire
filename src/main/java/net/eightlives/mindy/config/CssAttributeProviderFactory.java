package net.eightlives.mindy.config;

import org.commonmark.node.*;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class CssAttributeProviderFactory implements AttributeProviderFactory {

    @Override
    public AttributeProvider create(AttributeProviderContext attributeProviderContext) {
        return (node, tagName, attributes) -> {
            Consumer<String> setClass = className -> attributes.put("class", className);

            if (node instanceof BlockQuote) {
                setClass.accept("mindy-md-block-quote");
            } else if (node instanceof CustomBlock) {
                setClass.accept("mindy-md-custom-block");
            } else if (node instanceof Document) {
                setClass.accept("mindy-md-document");
            } else if (node instanceof FencedCodeBlock) {
                setClass.accept("mindy-md-fenced-code-block");
            } else if (node instanceof Heading) {
                setClass.accept("mindy-md-heading");
            } else if (node instanceof HtmlBlock) {
                setClass.accept("mindy-md-html-block");
            } else if (node instanceof IndentedCodeBlock) {
                setClass.accept("mindy-md-indented-code-block");
            } else if (node instanceof BulletList) {
                setClass.accept("mindy-md-bullet-list");
            } else if (node instanceof OrderedList) {
                setClass.accept("mindy-md-ordered-list");
            } else if (node instanceof ListItem) {
                setClass.accept("mindy-md-list-item");
            } else if (node instanceof Paragraph) {
                setClass.accept("mindy-md-paragraph");
            } else if (node instanceof ThematicBreak) {
                setClass.accept("mindy-md-thematic-break");
            } else if (node instanceof Code) {
                setClass.accept("mindy-md-code");
            } else if (node instanceof CustomNode) {
                setClass.accept("mindy-md-custom-node");
            } else if (node instanceof Emphasis) {
                setClass.accept("mindy-md-emphasis");
            } else if (node instanceof HardLineBreak) {
                setClass.accept("mindy-md-hard-line-break");
            } else if (node instanceof HtmlInline) {
                setClass.accept("mindy-md-html-inline");
            } else if (node instanceof Image) {
                setClass.accept("mindy-md-image");
            } else if (node instanceof Link) {
                setClass.accept("mindy-md-link");
            } else if (node instanceof SoftLineBreak) {
                setClass.accept("mindy-md-soft-line-break");
            } else if (node instanceof StrongEmphasis) {
                setClass.accept("mindy-md-strong-emphasis");
            } else if (node instanceof Text) {
                setClass.accept("mindy-md-text");
            }
        };
    }
}
