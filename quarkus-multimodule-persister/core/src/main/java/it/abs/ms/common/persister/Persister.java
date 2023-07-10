package it.abs.ms.common.persister;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.smallrye.mutiny.Uni;
import it.abs.ms.common.builder.BaseBuilder;
import it.abs.ms.common.builder.BuilderInstances;
import it.abs.ms.common.config.listener.BuilderConfigListener;
import it.abs.ms.common.config.model.BuilderConfig;
import it.abs.ms.common.enums.Operation;
import it.abs.ms.common.model.PayloadBase;
import it.abs.ms.common.model.ResponseDataBase;
import it.abs.ms.common.service.CommonService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@ApplicationScoped
public class Persister extends CommonService {

    @ConfigProperty(name = "mongodb.database")
    String mongodbDatabase;

    @Inject
    ReactiveMongoClient reactiveMongoClient;

    @Inject
    BuilderConfigListener builderConfigListener;

    @Inject
    BuilderInstances builderInstances;

    public Uni<ResponseDataBase> persist(
            String request_id,
            String session_id,
            String operation_id,
            String servicename,
            PayloadBase payload) {
        addLoggingValues(request_id, session_id, operation_id, payload, servicename);
        LOG.info("START persist");

        Operation operation;
        try {
            operation = Operation.valueOf(payload.operation);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(String.format("operation %s not permitted", payload.operation));
        }

        Stream<Map<String, Object>> stream = null;
        if (!operation.equals(Operation.REMOVE)) {
            Objects.requireNonNull(payload.data, "Field data can not be null");
            if (payload.data instanceof Map) {
                Map<String, Object> mapObject = (Map<String, Object>) payload.data;
                // Convert mapObject to a stream with a single element
                stream = Stream.of(mapObject);
            } else if (payload.data instanceof List) {
                List<Map<String, Object>> listObject = (List<Map<String, Object>>) payload.data;
                // Flatten the list of maps into a stream of maps
                stream = listObject.stream();
            } else {
                throw new RuntimeException(String.format("Could not processing data"));
            }
        }

        LOG.info(String.format("Working with %s operation", operation.getValue()));

        final BuilderConfig builderConfig = builderConfigListener.getBuilderConfig();

        final BaseBuilder baseBuilder = builderInstances.getInstance(builderConfig.getBuilderChainItem(servicename)
                .getClazz());

        return baseBuilder.bulk(stream, payload.uuid, payload.target, operation, operation_id, payload.filters, payload.attributesToRemove);

    }

}
