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
        return (node, _, attributes) -> attributes.put("class", switch (node) {
            case BlockQuote _ -> "mindecrire-md-block-quote";
            case CustomBlock _ -> "mindecrire-md-custom-block";
            case Document _ -> "mindecrire-md-document";
            case Heading _ -> "mindecrire-md-heading";
            case HtmlBlock _ -> "mindecrire-md-html-block";
            case FencedCodeBlock _ -> "mindecrire-md-fenced-code-block";
            case IndentedCodeBlock _ -> "mindecrire-md-indented-code-block";
            case BulletList _ -> "mindecrire-md-bullet-list";
            case OrderedList _ -> "mindecrire-md-ordered-list";
            case ListItem _ -> "mindecrire-md-list-item";
            case Paragraph _ -> "mindecrire-md-paragraph";
            case ThematicBreak _ -> "mindecrire-md-thematic-break";
            case Code _ -> "mindecrire-md-code";
            case CustomNode _ -> "mindecrire-md-custom-node";
            case Emphasis _ -> "mindecrire-md-emphasis";
            case StrongEmphasis _ -> "mindecrire-md-strong-emphasis";
            case SoftLineBreak _ -> "mindecrire-md-soft-line-break";
            case HardLineBreak _ -> "mindecrire-md-hard-line-break";
            case HtmlInline _ -> "mindecrire-md-html-inline";
            case Image _ -> "mindecrire-md-image";
            case Link _ -> "mindecrire-md-link";
            case LinkReferenceDefinition _ -> "mindecrire-md-link-reference-definition";
            case Text _ -> "mindecrire-md-text";
            default -> throw new IllegalStateException(node.getClass().getName() + " does not have a css class");
        });
    }
}
