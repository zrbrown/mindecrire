package net.eightlives.mindecrire.service;

import net.eightlives.mindecrire.dao.model.Post;
import net.eightlives.mindecrire.dao.PostRepository;
import net.eightlives.mindecrire.dao.model.PostUpdate;
import net.eightlives.mindecrire.dao.PostUpdateRepository;
import net.eightlives.mindecrire.model.FormattedPostUpdate;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostUpdateService {

    private final PostUpdateRepository postUpdateRepository;
    private final PostRepository postRepository;

    public PostUpdateService(PostUpdateRepository postUpdateRepository, PostRepository postRepository) {
        this.postUpdateRepository = postUpdateRepository;
        this.postRepository = postRepository;
    }

    public List<FormattedPostUpdate> getFormattedPostUpdates(Post post) {
        List<PostUpdate> postUpdates = postUpdateRepository.findAllByPost(post,
                Sort.by(Sort.Direction.DESC, "updatedDateTime"));

        return postUpdates.stream()
                .map(postUpdate -> {
                    FormattedPostUpdate formattedPostUpdate = new FormattedPostUpdate();

                    Parser parser = Parser.builder().build();
                    Node document = parser.parse(postUpdate.getContent());
                    HtmlRenderer renderer = HtmlRenderer.builder().build();
                    String renderedContent = renderer.render(document);
                    formattedPostUpdate.setContent(renderedContent);

                    formattedPostUpdate.setDate(postUpdate.getUpdatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));

                    return formattedPostUpdate;
                }).collect(Collectors.toList());
    }

    public void addPostUpdate(Post post, String content, LocalDateTime updatedDateTime) {
        PostUpdate postUpdate = new PostUpdate();
        postUpdate.setPost(post);
        postUpdate.setContent(content);
        postUpdate.setUpdatedDateTime(updatedDateTime);
        postUpdateRepository.save(postUpdate);

        post.getPostUpdates().add(postUpdate);
        postRepository.save(post);
    }
}
