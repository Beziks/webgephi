package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 21.1.14
 * Time: 15:58
 */
@Entity
@Bindable
@Portable
public class UserEntity {
    @Id
    private String username;

    @OneToOne(mappedBy = "user")
    private OAuthConsumerEntity clientAppEntity;

    @OneToMany(mappedBy = "owner")
    private List<GraphEntity> graphs;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<OAuthRequestTokenEntity> requestTokens;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<OAuthAccessTokenEntity> accessTokens;

    public UserEntity() {
    }

    public UserEntity(String usename) {
        this.username = usename;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String usename) {
        this.username = usename;
    }

    public List<GraphEntity> getGraphs() {
        if (graphs == null) {
            graphs = new ArrayList<GraphEntity>();
        }
        return graphs;
    }

    public void setGraphs(List<GraphEntity> graphs) {
        this.graphs = graphs;
    }

    public OAuthConsumerEntity getClientAppEntity() {
        return clientAppEntity;
    }

    public void setClientAppEntity(OAuthConsumerEntity clientAppEntity) {
        this.clientAppEntity = clientAppEntity;
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
