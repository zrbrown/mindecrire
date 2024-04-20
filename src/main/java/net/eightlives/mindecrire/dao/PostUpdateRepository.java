package net.eightlives.mindy.dao;

import net.eightlives.mindy.dao.model.Post;
import net.eightlives.mindy.dao.model.PostUpdate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostUpdateRepository extends JpaRepository<PostUpdate, UUID> {

    List<PostUpdate> findAllByPost(Post post, Sort sort);
}
