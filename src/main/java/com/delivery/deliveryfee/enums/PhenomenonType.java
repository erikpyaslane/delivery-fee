package com.delivery.deliveryfee.enums;

public enum PhenomenonType {
    SNOW, SLEET, RAIN, GLAZE, HAIL, THUNDER, NOPE;

    public static PhenomenonType getPhenomenonType(String phenomenon) {
        for (PhenomenonType phenomenonType : values()) {
            if (phenomenon.toLowerCase().contains(phenomenonType.name().toLowerCase()))
                return phenomenonType;
        }
        return NOPE;
    }
}
