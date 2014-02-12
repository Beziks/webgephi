package cz.cokrtvac.webgephi.webgephiserver.core.gephi.statistics;

import cz.cokrtvac.webgephi.api.util.Log;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphModel;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.gephi.utils.progress.ProgressTicket;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StatisticsWrapper {
    private StatisticsBuilder statisticsBuilder;
    private Statistics statistics;

    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        statistics.execute(graphModel, attributeModel);
    }

    public String getReport() {
        return statistics.getReport();
    }

    private List<StatisticsProperty> properties;
    private static Logger log = Log.get(StatisticsWrapper.class);

    public StatisticsWrapper(StatisticsBuilder sb) {
        this.statisticsBuilder = sb;
        this.statistics = this.statisticsBuilder.getStatistics();
        properties = loadProperties();
    }

    public String getName() {
        return statisticsBuilder.getName();
    }

    public List<StatisticsProperty> getProperties() {
        return properties;
    }

    private List<StatisticsProperty> loadProperties() {
        List<StatisticsProperty> out = new ArrayList<StatisticsProperty>();

        Method[] ms = statistics.getClass().getDeclaredMethods();
        for (Method m : ms) {
            if (m.getName().startsWith("set")) {
                if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0] != ProgressTicket.class) {
                    out.add(new StatisticsProperty(m));
                }
            }
        }
        return out;
    }

    public class StatisticsProperty<T> {
        private String name;
        private Method setter;
        private Method getter;
        private Class<T> type;

        public StatisticsProperty(Method setter) {
            this.name = setter.getName().substring(3);
            this.setter = setter;
            String getterName = "get" + name;
            try {
                log.debug("Searching getter " + getterName);
                this.getter = this.setter.getDeclaringClass().getDeclaredMethod(getterName);
            } catch (Exception e) {
                log.debug("Getter " + getterName + " not found");
                getterName = "is" + name;
                log.debug("Searching getter " + getterName);
                try {
                    getter = this.setter.getDeclaringClass().getDeclaredMethod(getterName);
                } catch (Exception e1) {
                    log.error("Getter not found", e1);
                }
            }
            this.type = (Class<T>) setter.getParameterTypes()[0];
        }

        public String getName() {
            return name;
        }

        public Class<?> getValueType() {
            return type;
        }

        public T getValue() {
            try {
                return (T) getter.invoke(statistics, null);
            } catch (Exception e) {
                log.error("Getter could not be invoked", e);
                return null;
            }
        }

        public void setValue(T value) {
            try {
                setter.invoke(statistics, value);
            } catch (Exception e) {
                log.error("Setter could not be invoked", e);
            }
        }

        @Override
        public String toString() {
            return "StatisticsProperty [name=" + name + ", methodName=" + setter.getName() + "|" + getter.getName() + ", type=" + type + "]";
        }

    }

}
