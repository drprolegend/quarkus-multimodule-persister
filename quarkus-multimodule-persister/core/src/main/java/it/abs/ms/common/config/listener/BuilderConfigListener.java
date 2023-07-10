package it.abs.ms.common.config.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import it.abs.ms.common.config.model.BuilderConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@ApplicationScoped
public class BuilderConfigListener {

    private BuilderConfig builderConfig;

    @ConfigProperty(name = "builder.config.filename")
    String builderConfigFilename;

    @ConfigProperty(name = "builder.config.path")
    String builderConfigPath;

    public void init(@Observes StartupEvent startupEvent) throws IOException {

        try {
            if (builderConfigPath == null) {
                throw new RuntimeException("Unable to read builder.config.path from application.properties");
            }

            if (builderConfigFilename == null) {
                throw new RuntimeException("Unable to read builder.config.filename from application.properties");
            }

            Optional<Path> pathOpt = Files.walk(Paths.get(builderConfigPath))
                    .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().equals(builderConfigFilename))
                    .findFirst();

            InputStream inputStream = Files.newInputStream(pathOpt.get());
            ObjectMapper objectMapper = new ObjectMapper();
            builderConfig = objectMapper.readValue(inputStream, BuilderConfig.class);

        } catch (Exception e) {
            throw new RuntimeException("Unable to read config file for error: " + e.getMessage());
        }

    }

    public BuilderConfig getBuilderConfig() {
        return builderConfig;
    }
}
