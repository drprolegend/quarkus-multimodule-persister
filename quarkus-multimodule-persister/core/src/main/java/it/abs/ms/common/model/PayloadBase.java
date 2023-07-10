package it.abs.ms.common.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@RegisterForReflection
public class PayloadBase implements Serializable {

    public String target;
    public String operation;

    public Map<String, Object> filters;

    public String uuid;

    public Object data;
    public List<String> attributesToRemove;
}
