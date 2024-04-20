package net.eightlives.mindecrire.config.custom;

import net.eightlives.mindecrire.security.Permission;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "user-authorization")
public class UserAuthorizationConfig {

    private Map<String, List<Permission>> userPermissions;

    public Map<String, List<Permission>> getUserPermissions() {
        return userPermissions;
    }

    public void setUserPermissions(Map<String, List<Permission>> userPermissions) {
        this.userPermissions = userPermissions;
    }
}
