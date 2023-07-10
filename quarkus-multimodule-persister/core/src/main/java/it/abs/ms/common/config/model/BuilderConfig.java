package it.abs.ms.common.config.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
public class BuilderConfig {
    private Map<String, BuilderChainItem> builderChain;

    public Map<String, BuilderChainItem> getBuilderChain() {
        return builderChain;
    }

    public void setBuilderChain(Map<String, BuilderChainItem> builderChain) {
        this.builderChain = builderChain;
    }

    public BuilderChainItem getBuilderChainItem(String key) {
        return builderChain.get(key);
    }
}

