package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model;

import org.hibernate.validator.constraints.Length;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 23.1.14
 * Time: 15:44
 */
@Portable
@Bindable
@Entity
public class OAuthConsumerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Length(min = 5, max = 64, message = "Key (unique application name) has to be 5-64 characters long")
    @Column(name = "consumer_key", unique = true)
    private String key;

    @NotNull
    @Length(min = 36, max = 256, message = "Key (unique application name) has to be 36-256 characters long")
    private String secret;

    @Length(min = 5, max = 64, message = "Application name (name visible to end user) has to be 5-64 characters long. If not set, key will be used")
    private String applicationName;

    @NotNull
    @Pattern(regexp = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", message = "URL of your application nas to be in format http(s)://xxxxx")
    @Column(unique = true)
    private String connectUri;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> scopes;

    @OneToOne
    private UserEntity user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "consumer")
    private Set<OAuthAccessTokenEntity> accessTokens;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "consumer")
    private Set<OAuthRequestTokenEntity> requestTokens;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getApplicationName() {
        if (applicationName == null) {
            return getKey();
        }
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getConnectUri() {
        return connectUri;
    }

    public void setConnectUri(String connectUri) {
        this.connectUri = connectUri;
    }

    public Set<String> getScopes() {
        if (scopes == null) {
            scopes = new HashSet<String>();
        }
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Set<OAuthAccessTokenEntity> getAccessTokens() {
        if (accessTokens == null) {
            accessTokens = new HashSet<OAuthAccessTokenEntity>();
        }
        return accessTokens;
    }

    public void setAccessTokens(Set<OAuthAccessTokenEntity> accessTokens) {
        this.accessTokens = accessTokens;
    }

    public Set<OAuthRequestTokenEntity> getRequestTokens() {
        if (requestTokens == null) {
            requestTokens = new HashSet<OAuthRequestTokenEntity>();
        }
        return requestTokens;
    }

    public void setRequestTokens(Set<OAuthRequestTokenEntity> requestTokens) {
        this.requestTokens = requestTokens;
    }
}
