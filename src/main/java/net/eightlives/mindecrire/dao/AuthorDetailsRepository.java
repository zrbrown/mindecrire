package net.eightlives.mindecrire.dao;

import net.eightlives.mindecrire.dao.model.AuthorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorDetailsRepository extends JpaRepository<AuthorDetails, String> {
}
