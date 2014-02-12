package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 19:07
 */
@Entity
@Bindable
@Portable
public class GraphEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private UserEntity owner;

    @NotNull
    @NotEmpty
    @Lob
    private String xml;

    @Lob
    private String statisticsReport;

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    @ManyToOne
    private GraphEntity parent;
    @OneToMany(mappedBy = "parent")
    private List<GraphEntity> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getStatisticsReport() {
        return statisticsReport;
    }

    public void setStatisticsReport(String statisticsReport) {
        this.statisticsReport = statisticsReport;
    }

    public GraphEntity getParent() {
        return parent;
    }

    public void setParent(GraphEntity parent) {
        this.parent = parent;
    }

    public List<GraphEntity> getChildren() {
        if(children == null){
            children = new ArrayList<GraphEntity>();
        }
        return children;
    }

    public void setChildren(List<GraphEntity> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphEntity that = (GraphEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
