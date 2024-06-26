package net.eightlives.mindecrire.endpointextension;

import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@EndpointWebExtension(endpoint = RefreshEndpoint.class)
public class RefreshEndpointExtension {

    private final RefreshEndpoint delegate;

    public RefreshEndpointExtension(RefreshEndpoint delegate) {
        this.delegate = delegate;
    }

    @WriteOperation
    @PreAuthorize("hasPermission(null, T(net.eightlives.mindecrire.security.Permission).ADMIN)")
    public Collection<String> refresh() {
        return delegate.refresh();
    }
}
