package net.eightlives.mindy.service;

import net.eightlives.mindy.dao.model.Post;
import net.eightlives.mindy.dao.model.Tag;
import net.eightlives.mindy.dao.TagRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Set<String> getTags(Post post) {
        return tagRepository.getAllByPosts(Collections.singleton(post)).stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }
}
