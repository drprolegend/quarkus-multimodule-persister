package it.abs.ms.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperBuilder {
    private ObjectMapperBuilder() {
    }

    public static ObjectMapper getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final ObjectMapper INSTANCE = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        public LazyHolder() {
            INSTANCE.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        }
    }
}
