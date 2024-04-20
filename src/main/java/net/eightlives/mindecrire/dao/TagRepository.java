package net.eightlives.mindecrire.dao;

import net.eightlives.mindecrire.dao.model.Post;
import net.eightlives.mindecrire.dao.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    Set<Tag> getAllByPostsIn(Set<Post> posts);
}
