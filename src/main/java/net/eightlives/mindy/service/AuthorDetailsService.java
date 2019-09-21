package net.eightlives.mindy.service;

import net.eightlives.mindy.dao.AuthorDetailsRepository;
import net.eightlives.mindy.dao.model.AuthorDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AuthorDetailsService {

    private final AuthorDetailsRepository authorDetailsRepository;

    public AuthorDetailsService(AuthorDetailsRepository authorDetailsRepository) {
        this.authorDetailsRepository = authorDetailsRepository;
    }

    public AuthorDetails getOrCreateAuthorDetails(OAuth2Authentication authentication) {
        return authorDetailsRepository.findById(authentication.getName())
                .orElseGet(() -> {
                    String displayName;
                    try {
                        displayName = (String) ((HashMap) authentication.getUserAuthentication().getDetails()).get("name");
                    } catch (Exception e) {
                        displayName = authentication.getName();
                        // TODO log error
                    }

                    return authorDetailsRepository.save(new AuthorDetails(authentication.getName(), displayName));
                });
    }
}