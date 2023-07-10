package it.abs.ms.common.enums;

public enum Operation {
    UPDATE("UPDATE"),
    UPSERT("UPSERT"),
    REMOVE("REMOVE");
    private final String value;

    private Operation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

