package it.abs.ms.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.abs.ms.common.enums.Operation;
import it.abs.ms.common.model.PayloadBase;
import it.abs.ms.common.persister.Persister;
import it.abs.ms.common.service.CommonService;
import it.abs.ms.common.utils.ObjectMapperBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class KafkaConsumer extends CommonService {

    private static final ObjectMapper om = ObjectMapperBuilder.getInstance();

    private static final String serviceName = "mongo";
    @Inject
    Persister persister;

    public void consume(String message) {
        try {
            PayloadBase payload = new PayloadBase();
            payload.target = "target-collection";
            payload.operation = Operation.UPSERT.getValue();
            payload.uuid = "_id";
            payload.data = om.readValue(message, Map.class);

            persister.persist(null, null, null, serviceName, payload).subscribe().with(response -> {
                LOG.info("Persist request completed successfully");
                // Handle the response or perform further processing
            });
        } catch (Exception e) {
            LOG.error("Error during persist", e);
        }

    }

}
