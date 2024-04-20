package net.eightlives.mindecrire.service;

import net.eightlives.mindecrire.dao.AuthorDetailsRepository;
import net.eightlives.mindecrire.dao.model.AuthorDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthorDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorDetailsService.class);

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
                        LOG.error("OAuth authentication is missing the 'name' attribute. Using username instead for author " + authorId);
                        displayName = authentication.getPrincipal().getAttribute("login");
                        if (displayName == null) {
                            throw new IllegalStateException("OAuth authentication is missing the 'login' attribute. This is either a bug from a standard provider, or your custom provider does not provide this attribute.");
                        }
                    }

                    return authorDetailsRepository.save(new AuthorDetails(authorId, displayName));
                });
    }
}
