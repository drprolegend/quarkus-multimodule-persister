package it.abs.ms.common.builder;

import io.smallrye.mutiny.Uni;
import it.abs.ms.common.enums.Operation;
import it.abs.ms.common.model.ResponseDataBase;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface BaseBuilder {

    Uni<ResponseDataBase> bulk(
            Stream<Map<String, Object>> data,
            String uuid,
            String target,
            Operation operation,
            String operation_id,
            Map<String, Object> filters,
            List<String> attributesToRemove);
}
