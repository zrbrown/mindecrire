package net.eightlives.mindecrire.theme;

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
                setClass.accept("mindecrire-md-block-quote");
            } else if (node instanceof CustomBlock) {
                setClass.accept("mindecrire-md-custom-block");
            } else if (node instanceof Document) {
                setClass.accept("mindecrire-md-document");
            } else if (node instanceof FencedCodeBlock) {
                setClass.accept("mindecrire-md-fenced-code-block");
            } else if (node instanceof Heading) {
                setClass.accept("mindecrire-md-heading");
            } else if (node instanceof HtmlBlock) {
                setClass.accept("mindecrire-md-html-block");
            } else if (node instanceof IndentedCodeBlock) {
                setClass.accept("mindecrire-md-indented-code-block");
            } else if (node instanceof BulletList) {
                setClass.accept("mindecrire-md-bullet-list");
            } else if (node instanceof OrderedList) {
                setClass.accept("mindecrire-md-ordered-list");
            } else if (node instanceof ListItem) {
                setClass.accept("mindecrire-md-list-item");
            } else if (node instanceof Paragraph) {
                setClass.accept("mindecrire-md-paragraph");
            } else if (node instanceof ThematicBreak) {
                setClass.accept("mindecrire-md-thematic-break");
            } else if (node instanceof Code) {
                setClass.accept("mindecrire-md-code");
            } else if (node instanceof CustomNode) {
                setClass.accept("mindecrire-md-custom-node");
            } else if (node instanceof Emphasis) {
                setClass.accept("mindecrire-md-emphasis");
            } else if (node instanceof HardLineBreak) {
                setClass.accept("mindecrire-md-hard-line-break");
            } else if (node instanceof HtmlInline) {
                setClass.accept("mindecrire-md-html-inline");
            } else if (node instanceof Image) {
                setClass.accept("mindecrire-md-image");
            } else if (node instanceof Link) {
                setClass.accept("mindecrire-md-link");
            } else if (node instanceof SoftLineBreak) {
                setClass.accept("mindecrire-md-soft-line-break");
            } else if (node instanceof StrongEmphasis) {
                setClass.accept("mindecrire-md-strong-emphasis");
            } else if (node instanceof Text) {
                setClass.accept("mindecrire-md-text");
            }
        };
    }
}
