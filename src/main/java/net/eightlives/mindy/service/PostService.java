package net.eightlives.mindy.service;

import net.eightlives.mindy.dao.PostRepository;
import net.eightlives.mindy.dao.TagRepository;
import net.eightlives.mindy.dao.model.AuthorDetails;
import net.eightlives.mindy.dao.model.Post;
import net.eightlives.mindy.dao.model.Tag;
import net.eightlives.mindy.exception.DuplicatePostUrlNameException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostService {

    private final AuthorDetailsService authorDetailsService;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(AuthorDetailsService authorDetailsService, PostRepository postRepository,
                       TagRepository tagRepository) {
        this.authorDetailsService = authorDetailsService;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public Optional<Post> getPostByUrlName(String urlName) {
        return postRepository.getByUrlName(urlName);
    }

    public Optional<Post> getLatestPost() {
        return postRepository.findAll(
                PageRequest.of(0, 1, Sort.Direction.DESC, "createdDateTime"))
                .get().findFirst();
    }

    public Optional<Post> getPreviousPost(Post post) {
        return postRepository.findByCreatedDateTimeBefore(
                post.getCreatedDateTime(),
                PageRequest.of(0, 1, Sort.Direction.DESC, "createdDateTime"))
                .get().findFirst();
    }

    public Optional<Post> getNextPost(Post post) {
        return postRepository.findByCreatedDateTimeAfter(
                post.getCreatedDateTime(),
                PageRequest.of(0, 1, Sort.Direction.ASC, "createdDateTime"))
                .get().findFirst();
    }

    public void addPost(String title, String content, LocalDateTime addedDateTime, List<String> tags,
                        OAuth2Authentication authentication) {
        String urlName = title.replaceAll("\\s", "-");
        if (postRepository.getByUrlName(urlName).isPresent()) {
            throw new DuplicatePostUrlNameException();
        }

        AuthorDetails authorDetails = authorDetailsService.getOrCreateAuthorDetails(authentication);

        Set<Tag> addedTags = tags.stream().map(this::getOrAddTag).collect(Collectors.toSet());

        Post post = new Post(
                UUID.randomUUID(),
                urlName,
                title,
                content,
                addedDateTime,
                addedTags,
                authorDetails
        );
        postRepository.save(post);
    }

    public void editPost(Post post, String title, String content, List<String> tags) {
        post.setTitle(title);
        post.setContent(content);
        post.getTags().addAll(tags.stream().map(this::getOrAddTag).collect(Collectors.toSet()));
        postRepository.save(post);
    }

    private Tag getOrAddTag(String tagName) {
        Tag tag = new Tag();
        tag.setName(tagName);

        Optional<Tag> existingTag = tagRepository.findOne(Example.of(tag));
        return existingTag.orElseGet(() -> tagRepository.save(tag));
    }
}
