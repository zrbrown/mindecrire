package net.eightlives.mindy.dao;

import net.eightlives.mindy.dao.model.AuthorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorDetailsRepository extends JpaRepository<AuthorDetails, String> {
}
