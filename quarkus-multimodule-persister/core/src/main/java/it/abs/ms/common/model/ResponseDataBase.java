package it.abs.ms.common.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ResponseDataBase implements Serializable {
    public String operation;
    public String operation_id;
    public List<String> id = new ArrayList<>();

}
