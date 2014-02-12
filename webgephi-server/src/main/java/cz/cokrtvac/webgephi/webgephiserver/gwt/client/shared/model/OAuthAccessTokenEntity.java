package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 23.1.14
 * Time: 22:56
 */
@Entity
@Bindable
@Portable
public class OAuthAccessTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne
    private OAuthConsumerEntity consumer;
    private String token;
    private String secret;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> scopes;

    @ManyToOne
    private UserEntity user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OAuthConsumerEntity getConsumer() {
        return consumer;
    }

    public void setConsumer(OAuthConsumerEntity consumer) {
        this.consumer = consumer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Set<String> getScopes() {
        if(scopes == null){
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
}
