package org.elestya.bedrock.interfaces.recompenses;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.DailyRewardDay;
import org.elestya.bedrock.api.sources.DailyRewardSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class DailyRewardForm {

    private final FormService formService;
    private final DailyRewardSource rewardSource;
    private final Consumer<Player> parentOpener;

    public DailyRewardForm(
            FormService formService,
            DailyRewardSource rewardSource,
            Consumer<Player> parentOpener
    ) {
        this.formService = Objects.requireNonNull(formService);
        this.rewardSource = Objects.requireNonNull(rewardSource);
        this.parentOpener = Objects.requireNonNull(parentOpener);
    }

    public void open(Player player) {
        open(player, null);
    }

    private void open(Player player, String resultMessage) {
        Objects.requireNonNull(player);
        List<DailyRewardDay> days = List.copyOf(Objects.requireNonNull(
                rewardSource.getDays(player),
                "daily reward days cannot be null"
        ));
        if (days.size() != 7) {
            throw new IllegalStateException("Daily reward source must provide exactly seven days");
        }

        DailyRewardDay availableDay = days.stream()
                .filter(DailyRewardDay::available)
                .findFirst()
                .orElse(null);
        StringBuilder content = new StringBuilder();
        if (resultMessage != null) {
            content.append(resultMessage).append("\n\n");
        }
        content.append(FormStyle.TEXT).append("Tes récompenses de la semaine\n");
        for (DailyRewardDay day : days) {
            content.append(day.claimed() ? FormStyle.SUCCESS + "✔ " : FormStyle.MUTED + "· ")
                    .append(FormStyle.TEXT).append("Jour ").append(FormStyle.VALUE).append(day.day())
                    .append(FormStyle.TEXT).append(" — ")
                    .append(day.claimed() ? FormStyle.MUTED + "Réclamé" :
                            day.available() ? FormStyle.PRIMARY + "Disponible" : FormStyle.MUTED + "À venir")
                    .append('\n').append(FormStyle.MUTED).append("  ").append(day.rewardDescription());
            if (day != days.getLast()) {
                content.append("\n\n");
            }
        }

        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Récompenses journalières"))
                .content(content.toString());
        if (availableDay != null) {
            builder.button(FormStyle.PRIMARY + "Réclamer le jour " + availableDay.day());
        }
        int backButtonId = availableDay == null ? 0 : 1;
        int claimButtonId = 0;
        builder.button(FormStyle.RETURN + "← Retour au menu")
                .closedOrInvalidResultHandler(formService.sync(player, () -> parentOpener.accept(player)))
                .validResultHandler(formService.sync(player, response -> {
                    int buttonId = response.clickedButtonId();
                    if (buttonId == backButtonId) {
                        parentOpener.accept(player);
                        return;
                    }
                    if (buttonId != claimButtonId || availableDay == null) {
                        return;
                    }

                    ActionResult result = rewardSource.claim(player, availableDay.day());
                    open(player, (result.success() ? FormStyle.SUCCESS : FormStyle.DANGER) + result.message());
                }));

        formService.sendFormSync(player, builder.build());
    }
}
