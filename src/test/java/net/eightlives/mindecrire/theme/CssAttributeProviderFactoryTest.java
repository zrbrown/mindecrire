package net.eightlives.mindecrire.theme;

import org.commonmark.node.*;
import org.commonmark.renderer.html.AttributeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CssAttributeProviderFactoryTest {

    CssAttributeProviderFactory cssFactory = new CssAttributeProviderFactory();
    AttributeProvider provider = cssFactory.create(null);

    @DisplayName("Correct CSS class names are assigned")
    @Test
    void create() {
        Map<String, String> attributes = new HashMap<>();
        provider.setAttributes(new BlockQuote(), null, attributes);

        assertClass(new BlockQuote(), "mindecrire-md-block-quote");
        assertClass(new CustomBlock() {}, "mindecrire-md-custom-block");
        assertClass(new Document(), "mindecrire-md-document");
        assertClass(new FencedCodeBlock(), "mindecrire-md-fenced-code-block");
        assertClass(new Heading(), "mindecrire-md-heading");
        assertClass(new HtmlBlock(), "mindecrire-md-html-block");
        assertClass(new IndentedCodeBlock(), "mindecrire-md-indented-code-block");
        assertClass(new BulletList(), "mindecrire-md-bullet-list");
        assertClass(new OrderedList(), "mindecrire-md-ordered-list");
        assertClass(new ListItem(), "mindecrire-md-list-item");
        assertClass(new Paragraph(), "mindecrire-md-paragraph");
        assertClass(new ThematicBreak(), "mindecrire-md-thematic-break");
        assertClass(new Code(), "mindecrire-md-code");
        assertClass(new CustomNode() {}, "mindecrire-md-custom-node");
        assertClass(new Emphasis(), "mindecrire-md-emphasis");
        assertClass(new HardLineBreak(), "mindecrire-md-hard-line-break");
        assertClass(new HtmlInline(), "mindecrire-md-html-inline");
        assertClass(new Image(), "mindecrire-md-image");
        assertClass(new Link(), "mindecrire-md-link");
        assertClass(new SoftLineBreak(), "mindecrire-md-soft-line-break");
        assertClass(new StrongEmphasis(), "mindecrire-md-strong-emphasis");
        assertClass(new Text(), "mindecrire-md-text");
    }

    private void assertClass(Node node, String className) {
        Map<String, String> attributes = new HashMap<>();
        provider.setAttributes(node, null, attributes);
        assertEquals(attributes.get("class"), className);
    }
}
