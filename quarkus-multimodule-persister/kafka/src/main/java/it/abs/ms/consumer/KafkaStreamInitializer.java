package it.abs.ms.consumer;

import it.abs.ms.common.service.CommonService;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Properties;

@ApplicationScoped
public class KafkaStreamInitializer extends CommonService {


    @ConfigProperty(name = "kafka-streams.bootstrap-servers")
    String kafkaServerHost;
    @ConfigProperty(name = "kafka-streams.topic")
    String kafkaServerTopic;

    @Inject
    KafkaConsumer kafkaConsumer;
    protected final StreamsBuilder builder = new StreamsBuilder();
    private KafkaStreams kafkaStreams;
    private static final Serde<String> STRING_SERDE = Serdes.String();


    public void startKafkaStream() {

        KStream<String, String> stream = builder.stream(kafkaServerTopic, Consumed.with(STRING_SERDE, STRING_SERDE));
        stream.foreach((key, value) -> {
            // Logic to consume the message
            kafkaConsumer.consume(value);
        });

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerHost);

        final Topology topology = builder.build();

        kafkaStreams = new KafkaStreams(topology, config);
        kafkaStreams.start();
    }

    public void stopKafkaStream() {
        if (kafkaStreams != null) {
            kafkaStreams.close();
        }
    }
}

