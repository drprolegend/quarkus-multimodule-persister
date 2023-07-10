package it.abs.ms.utils;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class ErrorResponse implements Serializable {

    public String message;
    public String operation_id;

}
