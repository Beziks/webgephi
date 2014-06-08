package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.ranking;

import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunction;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunctionProperty;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingModel;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractColorTransformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 17. 5. 2014
 * Time: 18:51
 */
public class RankingWrapper implements GraphFunction {
    private String id;
    private String name;
    private String desc;

    private List<GraphFunctionProperty> properties = new ArrayList<GraphFunctionProperty>(3);

    //  RENDERABLE_SIZE / RENDERABLE_COLOR / LABEL_SIZE / LABEL_COLOR
    private String transformerType = Transformer.RENDERABLE_SIZE;

    // Ranking.EDGE_ELEMENT /  Ranking.NODE_ELEMENT
    private String rankingType = Ranking.NODE_ELEMENT;


    public RankingWrapper(String id, String name, String desc, String transformerType, String rankingType) {
        this.id = id;
        this.name = name;
        this.desc = desc;

        if (!transformerType.equals(Transformer.RENDERABLE_SIZE) && !transformerType.equals(Transformer.RENDERABLE_COLOR)
                && !transformerType.equals(Transformer.LABEL_SIZE) && !transformerType.equals(Transformer.LABEL_COLOR)) {
            throw new IllegalArgumentException("This value is not supported: " + transformerType);
        }

        if (!rankingType.equals(Ranking.NODE_ELEMENT) && !rankingType.equals(Ranking.EDGE_ELEMENT)) {
            throw new IllegalArgumentException("This value is not supported: " + rankingType);
        }

        this.rankingType = rankingType;
        this.transformerType = transformerType;

        if (isNodeRanking()) {
            properties.add(RankingProperty.createNodeAttributeProperty());
        } else {
            properties.add(RankingProperty.createEdgeAttributeProperty());
        }

        if (isSizeRanking()) {
            properties.add(RankingProperty.createStartSizeProperty());
            properties.add(RankingProperty.createEndSizeProperty());
        } else {
            properties.add(RankingProperty.createStartColorProperty());
            properties.add(RankingProperty.createEndColorProperty());
        }
    }

    private boolean isNodeRanking() {
        return rankingType.equals(Ranking.NODE_ELEMENT);
    }

    private boolean isSizeRanking() {
        if (transformerType.toLowerCase().contains("size")) {
            return true;
        } else if (transformerType.toLowerCase().contains("color")) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid transformer type: " + transformerType);
        }
    }

    public GraphFunctionProperty getProperty(String idPart) {
        for (GraphFunctionProperty p : getProperties()) {
            if (p.getId().toLowerCase().contains(idPart.toLowerCase())) {
                return p;
            }
        }
        throw new IllegalArgumentException("No such property containing " + idPart);
    }

    public RankingSetting getSetting(WorkspaceWrapper ww, RankingModel rankingModel) {
        RankingSetting sett = new RankingSetting();

        ww.getWorkspace().add(rankingModel);
        RankingAttributeProperty attr = (RankingAttributeProperty) getProperty("attribute");
        sett.setRanking(rankingModel.getRanking(rankingType, attr.getValue().getId()));

        if (isSizeRanking()) {
            AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingModel.getTransformer(rankingType, transformerType);
            sizeTransformer.setMinSize(((RankingSizeProperty) getProperty("start")).getValue());
            sizeTransformer.setMaxSize(((RankingSizeProperty) getProperty("end")).getValue());
            sett.setTransformer(sizeTransformer);
        } else {
            AbstractColorTransformer colorTransformer = (AbstractColorTransformer) rankingModel.getTransformer(rankingType, transformerType);
            colorTransformer.setColors(new Color[]{
                    ((RankingColorProperty) getProperty("start")).getValue(),
                    ((RankingColorProperty) getProperty("end")).getValue()
            });
            sett.setTransformer(colorTransformer);
        }

        return sett;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public List<GraphFunctionProperty> getProperties() {
        return properties;
    }

    @Override
    public RankingWrapper copy() {
        return new RankingWrapper(id, name, desc, transformerType, rankingType);
    }

    public static class RankingProperty<T> implements GraphFunctionProperty<T> {
        private String id;
        private String name;
        private String description;
        private T value;
        private Class<T> valueClass;

        public static RankingProperty<?> createStartColorProperty() {
            return new RankingColorProperty("startColor", "Start color", "Color of node with lowest value. In hexadecimal format: RRGGBB, e.g. FEF0D9", new Color(0xFEF0D9));
        }

        public static RankingProperty<?> createEndColorProperty() {
            return new RankingColorProperty("endColor", "End color", "Color of node with highest value. In hexadecimal format: RRGGBB, e.g. B30000", new Color(0xB30000));
        }

        public static RankingProperty<?> createStartSizeProperty() {
            return new RankingSizeProperty("startSize", "Start size", "Size of element (node/edge/label) with lowest value.", 1f);
        }

        public static RankingProperty<?> createEndSizeProperty() {
            return new RankingSizeProperty("endSize", "End size", "Size of element (node/edge/label) with highest value.", 10f);
        }

        public static RankingProperty<?> createNodeAttributeProperty() {
            return new RankingAttributeProperty("nodeAttribute", "Node attribute", "Id of attribute which will be used for ranking. It has to be one of already calculated node attributes (see GEXF format)");
        }

        public static RankingProperty<?> createEdgeAttributeProperty() {
            return new RankingAttributeProperty("edgeAttribute", "Edge attribute", "Id of attribute which will be used for ranking. It has to be one of already calculated edge attributes (see GEXF format)");
        }

        public RankingProperty(String id, String name, String description, Class<T> valueClass, T value) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.value = value;
            this.valueClass = valueClass;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Class<T> getValueType() {
            return valueClass;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public void setValue(T value) {
            this.value = value;
        }
    }

    public static class RankingColorProperty extends RankingProperty<Color> {
        public RankingColorProperty(String id, String name, String description, Color value) {
            super(id, name, description, Color.class, value);
        }
    }

    public static class RankingSizeProperty extends RankingProperty<Float> {
        public RankingSizeProperty(String id, String name, String description, float value) {
            super(id, name, description, Float.class, value);
        }
    }

    public static class RankingAttributeProperty extends RankingProperty<AttributeColumn> {
        public RankingAttributeProperty(String id, String name, String description) {
            super(id, name, description, AttributeColumn.class, null);
        }
    }

    public static class RankingSetting {
        private Ranking ranking;
        private Transformer transformer;

        public Ranking getRanking() {
            return ranking;
        }

        public void setRanking(Ranking ranking) {
            this.ranking = ranking;
        }

        public Transformer getTransformer() {
            return transformer;
        }

        public void setTransformer(Transformer transformer) {
            this.transformer = transformer;
        }
    }
}
