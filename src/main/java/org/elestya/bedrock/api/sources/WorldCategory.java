package org.elestya.bedrock.api.sources;

public enum WorldCategory {
    RESOURCES("Ressources"),
    SURVIVAL("Monde survie"),
    COMBAT("Combat");

    private final String displayName;

    WorldCategory(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
