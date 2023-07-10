package it.abs.ms.common.builder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class BuilderInstances {
    public static ConcurrentHashMap<String, BaseBuilder> map = new ConcurrentHashMap<>();

    @Inject
    Instance<BaseBuilder> builderInstances;

    public BaseBuilder getInstance(String clazz) {
        return map.computeIfAbsent(clazz, (clazzName) -> {
            try {
                Class o = Class.forName(clazzName);
                BaseBuilder baseBuilder = (BaseBuilder) builderInstances.select(o).get();
                return baseBuilder;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("unable to load class " + clazzName, e);
            }
        });
    }
}
