package net.eightlives.mindy.config;

import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@EndpointWebExtension(endpoint = RefreshEndpoint.class)
public class RefreshEndpointExtension {

    private RefreshEndpoint delegate;

    public RefreshEndpointExtension(RefreshEndpoint delegate) {
        this.delegate = delegate;
    }

    @WriteOperation
    @PreAuthorize("hasPermission(null, T(net.eightlives.mindy.config.Permission).ADMIN)")
    public Collection<String> refresh() {
        return delegate.refresh();
    }
}
