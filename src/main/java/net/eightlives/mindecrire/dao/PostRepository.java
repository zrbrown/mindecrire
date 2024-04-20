package net.eightlives.mindy.dao;

import net.eightlives.mindy.dao.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    Optional<Post> getByUrlName(String urlName);

    Page<Post> findByCreatedDateTimeBefore(LocalDateTime createdDateTime, Pageable pageable);

    Page<Post> findByCreatedDateTimeAfter(LocalDateTime createdDateTime, Pageable pageable);
}
