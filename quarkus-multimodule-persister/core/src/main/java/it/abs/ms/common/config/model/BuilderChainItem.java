package it.abs.ms.common.config.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class BuilderChainItem {
    private String clazz;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
