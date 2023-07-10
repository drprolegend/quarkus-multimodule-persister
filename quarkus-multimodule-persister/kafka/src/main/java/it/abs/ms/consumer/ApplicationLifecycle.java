package it.abs.ms.consumer;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationLifecycle {

    @Inject
    KafkaStreamInitializer kafkaStreamInitializer;

    @ConfigProperty(name = "enable.kafka-streams")
    Boolean kafkaStreamsEnabled;

    void onStart(@Observes StartupEvent event) {
        if (kafkaStreamsEnabled)
            kafkaStreamInitializer.startKafkaStream();
    }

    void onStop(@Observes ShutdownEvent event) {
        kafkaStreamInitializer.stopKafkaStream();
    }
}
