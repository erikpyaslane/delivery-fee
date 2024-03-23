package com.delivery.deliveryfee.enums;

public enum PhenomenonType {
    SNOW("Light snow shower", "Moderate snow shower",
            "Heavy snow shower", "Light snowfall", "Moderate snowfall", "Heavy snowfall",
            "Blowing snow", "Drifting snow"),
    SLEET("Light sleet", "Moderate sleet"),
    RAIN("Light shower", "Moderate shower", "Heavy shower", "Light rain",
            "Moderate rain", "Heavy rain"),
    GLAZE ("Glaze"),
    HAIL ("Hail"),
    THUNDER("Thunder", "Thunderstorm"),
    NOPE;

    private final String[] descriptions;

    PhenomenonType(String... descriptions) {
        this.descriptions = descriptions;
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public static PhenomenonType getPhenomenonType(String phenomenon) {
        for (PhenomenonType phenomenonType : values()) {
            for (String description : phenomenonType.getDescriptions())
                if (description.equals(phenomenon))
                    return phenomenonType;
        }
        return NOPE;
    }
}
