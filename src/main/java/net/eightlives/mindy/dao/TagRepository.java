package net.eightlives.mindy.dao;

import net.eightlives.mindy.dao.model.Post;
import net.eightlives.mindy.dao.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    Set<Tag> getAllByPosts(Set<Post> posts);
}
