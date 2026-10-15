package org.elestya.bedrock.interfaces.quetes;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.DailyQuest;
import org.elestya.bedrock.api.sources.DailyQuestCategory;
import org.elestya.bedrock.api.sources.DailyQuestSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class DailyQuestForm {

    private static final int PROGRESS_BAR_WIDTH = 10;

    private final FormService formService;
    private final DailyQuestSource questSource;
    private final Consumer<Player> parentOpener;

    public DailyQuestForm(
            FormService formService,
            DailyQuestSource questSource,
            Consumer<Player> parentOpener
    ) {
        this.formService = Objects.requireNonNull(formService);
        this.questSource = Objects.requireNonNull(questSource);
        this.parentOpener = Objects.requireNonNull(parentOpener);
    }

    public void open(Player player) {
        Objects.requireNonNull(player);

        int totalQuests = 0;
        int completedQuests = 0;
        for (DailyQuestCategory category : DailyQuestCategory.values()) {
            List<DailyQuest> quests = List.copyOf(Objects.requireNonNull(
                    questSource.getQuests(player, category),
                    "quest list cannot be null"
            ));
            totalQuests += quests.size();
            completedQuests += quests.stream().filter(DailyQuestForm::isCompleted).count();
        }

        int completionPercent = totalQuests == 0 ? 0 : completedQuests * 100 / totalQuests;
        SimpleForm form = SimpleForm.builder()
                .title(FormStyle.title("Quêtes"))
                .content(FormStyle.TEXT + "Progression globale\n"
                        + progressBar(completedQuests, totalQuests)
                        + "\n" + FormStyle.VALUE + completedQuests
                        + FormStyle.TEXT + " / " + FormStyle.VALUE + totalQuests
                        + FormStyle.TEXT + " quêtes terminées · "
                        + FormStyle.PRIMARY + completionPercent + "%")
                .button(FormStyle.PRIMARY + "Quêtes faciles")
                .button(FormStyle.PRIMARY + "Quêtes normales")
                .button(FormStyle.PRIMARY + "Quêtes difficiles")
                .button(FormStyle.PRIMARY + "Quêtes de la semaine")
                .button(FormStyle.RETURN + "← Retour au menu")
                .closedOrInvalidResultHandler(formService.sync(player, () -> parentOpener.accept(player)))
                .validResultHandler(formService.sync(player, response -> {
                    int buttonId = response.clickedButtonId();
                    if (buttonId == DailyQuestCategory.values().length) {
                        parentOpener.accept(player);
                        return;
                    }
                    if (buttonId < 0 || buttonId >= DailyQuestCategory.values().length) {
                        return;
                    }
                    openCategory(player, DailyQuestCategory.values()[buttonId]);
                }))
                .build();

        formService.sendFormSync(player, form);
    }

    private void openCategory(Player player, DailyQuestCategory category) {
        List<DailyQuest> quests = List.copyOf(Objects.requireNonNull(
                questSource.getQuests(player, category),
                "quest list cannot be null"
        ));
        StringBuilder content = new StringBuilder(FormStyle.PRIMARY)
                .append("◆ ")
                .append(category.displayName());

        if (quests.isEmpty()) {
            content.append('\n').append(FormStyle.MUTED).append("Aucune quête dans cette catégorie.");
        } else {
            for (DailyQuest quest : quests) {
                content.append("\n\n")
                        .append(FormStyle.ITEM_TITLE).append(quest.name())
                        .append('\n').append(FormStyle.TEXT).append(quest.description())
                        .append('\n').append(FormStyle.MUTED).append("Avancement : ")
                        .append(FormStyle.VALUE).append(quest.completed())
                        .append(FormStyle.TEXT).append(" / ")
                        .append(FormStyle.VALUE).append(quest.required())
                        .append('\n').append(progressBar(quest));
            }
        }

        SimpleForm form = SimpleForm.builder()
                .title(FormStyle.title("Quêtes · " + category.displayName()))
                .content(content.toString())
                .button(FormStyle.RETURN + "← Retour aux quêtes")
                .closedOrInvalidResultHandler(formService.sync(player, () -> open(player)))
                .validResultHandler(formService.sync(player, response -> open(player)))
                .build();

        formService.sendFormSync(player, form);
    }

    private static boolean isCompleted(DailyQuest quest) {
        return quest.completed() >= quest.required();
    }

    private String progressBar(DailyQuest quest) {
        return progressBar(quest.completed(), quest.required());
    }

    private String progressBar(int completed, int required) {
        int filled = required == 0 ? 0 : (int) Math.round((double) completed / required * PROGRESS_BAR_WIDTH);
        return FormStyle.PRIMARY + "■".repeat(filled)
                + FormStyle.MUTED + "□".repeat(PROGRESS_BAR_WIDTH - filled)
                + FormStyle.TEXT + "  " + (required == 0 ? 0 : completed * 100 / required) + "%";
    }
}
