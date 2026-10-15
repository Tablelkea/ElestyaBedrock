package org.elestya.bedrock.api.sources;

public enum DailyQuestCategory {
    EASY("Facile"),
    NORMAL("Normale"),
    HARD("Difficile"),
    WEEKLY("Semaine");

    private final String displayName;

    DailyQuestCategory(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
