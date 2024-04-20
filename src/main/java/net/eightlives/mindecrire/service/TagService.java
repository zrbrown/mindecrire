package net.eightlives.mindecrire.service;

import net.eightlives.mindecrire.dao.model.Post;
import net.eightlives.mindecrire.dao.model.Tag;
import net.eightlives.mindecrire.dao.TagRepository;
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
        return tagRepository.getAllByPostsIn(Collections.singleton(post)).stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }
}
