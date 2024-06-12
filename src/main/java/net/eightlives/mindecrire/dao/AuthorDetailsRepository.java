package net.eightlives.mindecrire.dao;

import net.eightlives.mindecrire.dao.model.AuthorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorDetailsRepository extends JpaRepository<AuthorDetails, UUID> {

    Optional<AuthorDetails> findByAuthor(String author);
}
