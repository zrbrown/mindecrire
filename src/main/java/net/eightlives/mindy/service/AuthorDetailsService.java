package net.eightlives.mindy.service;

import lombok.extern.slf4j.Slf4j;
import net.eightlives.mindy.dao.AuthorDetailsRepository;
import net.eightlives.mindy.dao.model.AuthorDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorDetailsService {

    private final AuthorDetailsRepository authorDetailsRepository;

    public AuthorDetailsService(AuthorDetailsRepository authorDetailsRepository) {
        this.authorDetailsRepository = authorDetailsRepository;
    }

    public AuthorDetails getOrCreateAuthorDetails(OAuth2AuthenticationToken authentication) {
        String authorId = authentication.getAuthorizedClientRegistrationId() + "-" + authentication.getName();
        return authorDetailsRepository.findById(authorId)
                .orElseGet(() -> {
                    String displayName = authentication.getPrincipal().getAttribute("name");
                    if (displayName == null) {
                        log.error("OAuth authentication is missing the 'name' attribute. Using username instead for author " + authorId);
                        displayName = authentication.getPrincipal().getAttribute("login");
                        if (displayName == null) {
                            throw new IllegalStateException("OAuth authentication is missing the 'login' attribute. This is either a bug from a standard provider, or your custom provider does not provide this attribute.");
                        }
                    }

                    return authorDetailsRepository.save(new AuthorDetails(authorId, displayName));
                });
    }
}
