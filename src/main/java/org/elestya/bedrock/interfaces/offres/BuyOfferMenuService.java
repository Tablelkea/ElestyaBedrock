package org.elestya.bedrock.interfaces.offres;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.BuyOffer;
import org.elestya.bedrock.api.sources.BuyOfferSource;
import org.elestya.bedrock.api.sources.OfferItem;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormPages;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.elestya.bedrock.interfaces.PriceFormatter;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class BuyOfferMenuService {

    private final FormService forms;
    private final BuyOfferSource source;

    public BuyOfferMenuService(FormService forms, BuyOfferSource source) {
        this.forms = Objects.requireNonNull(forms);
        this.source = Objects.requireNonNull(source);
    }

    public void open(Player player) {
        open(player, () -> {});
    }

    public void open(Player player, Runnable parent) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(parent);
        forms.runSync(player, () -> openRoot(player, parent, null));
    }

    private void openRoot(Player player, Runnable parent, String result) {
        openRoot(player, parent, result, 0);
    }

    private void openRoot(Player player, Runnable parent, String result, int page) {
        List<BuyOffer> allOffers = source.getOffers(player).stream()
                .filter(offer -> offer.remainingQuantity() > 0)
                .toList();
        FormPages.Page<BuyOffer> offerPage = FormPages.of(allOffers, page);
        List<BuyOffer> offers = offerPage.values();
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Offres d'achat"))
                .content((result == null ? "" : result + "\n\n")
                        + "Choisissez une offre, livrez vos objets ou créez une offre."
                        + (offerPage.count() > 1 ? "\nPage " + (offerPage.number() + 1)
                        + " / " + offerPage.count() : ""));
        for (BuyOffer offer : offers) {
            builder.button(FormStyle.PRIMARY + offer.itemName() + " · " + offer.remainingQuantity()
                            + " restants\n" + FormStyle.PRICE + PriceFormatter.format(offer.unitPrice()) + " / unité",
                    FormImage.Type.PATH, FormImagePaths.forMaterial(offer.material()));
        }
        boolean hasPrevious = offerPage.hasPrevious();
        if (hasPrevious) builder.button(FormStyle.RETURN + "← Page précédente");
        boolean hasNext = offerPage.hasNext();
        if (hasNext) builder.button(FormStyle.PRIMARY + "Page suivante →");
        int createIndex = offers.size() + (hasPrevious ? 1 : 0) + (hasNext ? 1 : 0);
        builder.button(FormStyle.PRIMARY + "Créer une offre", FormImage.Type.PATH,
                FormImagePaths.forMaterial(org.bukkit.Material.WRITABLE_BOOK));
        int mineIndex = createIndex + 1;
        builder.button(FormStyle.PRIMARY + "Mes offres", FormImage.Type.PATH,
                FormImagePaths.forMaterial(org.bukkit.Material.CHEST));
        int backIndex = mineIndex + 1;
        builder.button(FormStyle.RETURN + "← Retour");
        int previousIndex = hasPrevious ? offers.size() : -1;
        int nextIndex = hasNext ? offers.size() + (hasPrevious ? 1 : 0) : -1;
        builder.closedOrInvalidResultHandler(forms.sync(player, parent));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index >= 0 && index < offers.size()) {
                openDeliver(player, parent, offers.get(index));
            } else if (index == previousIndex) {
                openRoot(player, parent, null, offerPage.number() - 1);
            } else if (index == nextIndex) {
                openRoot(player, parent, null, offerPage.number() + 1);
            } else if (index == createIndex) {
                openCreateItems(player, parent);
            } else if (index == mineIndex) {
                openMine(player, parent);
            } else if (index == backIndex) {
                parent.run();
            }
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openDeliver(Player player, Runnable parent, BuyOffer offer) {
        int maximum = offer.remainingQuantity();
        CustomForm form = CustomForm.builder().title(FormStyle.title("Livrer · " + offer.itemName()))
                .label("Restant : " + offer.remainingQuantity() + " · " + FormStyle.PRICE
                        + PriceFormatter.format(offer.unitPrice()) + " par objet")
                .slider("Quantité à livrer", 1, maximum, 1, 1)
                .closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response ->
                        confirmDelivery(player, parent, offer, (int) response.asSlider(1)))))
                .build();
        forms.sendFormSync(player, form);
    }

    private void confirmDelivery(Player player, Runnable parent, BuyOffer offer, int quantity) {
        ModalForm form = ModalForm.builder().title(FormStyle.title("Confirmer la livraison"))
                .content("Livrer " + quantity + " × " + offer.itemName() + " ?\n"
                        + "Gain : " + PriceFormatter.format(source.getDeliveryTotal(player, offer, quantity)))
                .button1(FormStyle.CONFIRM).button2(FormStyle.DANGER + "Annuler")
                .closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (!response.clickedFirst()) {
                        openRoot(player, parent, null);
                        return;
                    }
                    ActionResult result = source.deliver(player, offer, quantity);
                    openRoot(player, parent, (result.success() ? FormStyle.SUCCESS : FormStyle.DANGER)
                            + result.message());
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void openCreateItems(Player player, Runnable parent) {
        List<OfferItem> items = source.getOfferableItems(player);
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Créer une offre"))
                .content(items.isEmpty() ? "Vous n'avez aucun objet à proposer."
                        : "Choisissez l'objet de votre inventaire à vendre.");
        for (OfferItem item : items) {
            builder.button(FormStyle.PRIMARY + item.name() + " × " + item.available(),
                    FormImage.Type.PATH, FormImagePaths.forMaterial(item.material()));
        }
        builder.button(FormStyle.RETURN + "← Retour");
        builder.closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index >= 0 && index < items.size()) openCreateDetails(player, parent, items.get(index));
            else openRoot(player, parent, null);
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openCreateDetails(Player player, Runnable parent, OfferItem item) {
        openCreateDetails(player, parent, item, 1, "", null);
    }

    private void openCreateDetails(Player player, Runnable parent, OfferItem item, int previousQuantity,
                                   String previousPrice, String error) {
        CustomForm form = CustomForm.builder().title(FormStyle.title("Détails · " + item.name()))
                .label((error == null ? "" : FormStyle.DANGER + error + "\n")
                        + "Disponible : " + item.available())
                .slider("Quantité souhaitée", 1, item.available(), 1,
                        Math.max(1, Math.min(previousQuantity, item.available())))
                .input("Prix par objet", "Exemple : 5,50", previousPrice)
                .closedOrInvalidResultHandler(forms.sync(player, () -> openCreateItems(player, parent)))
                .validResultHandler(forms.sync(player, once(response -> {
                    int quantity = (int) response.asSlider(1);
                    String enteredPrice = response.asInput(2);
                    Double price = parsePrice(enteredPrice);
                    if (price == null) {
                        openCreateDetails(player, parent, item, quantity, enteredPrice,
                                "Saisissez un prix positif valide.");
                        return;
                    }
                    confirmCreate(player, parent, item, quantity, price);
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void confirmCreate(Player player, Runnable parent, OfferItem item, int quantity, double price) {
        ModalForm form = ModalForm.builder().title(FormStyle.title("Confirmer l'offre"))
                .content("Créer une offre pour " + quantity + " × " + item.name()
                        + " à " + PriceFormatter.format(price) + " par objet ?\n"
                        + "Total proposé : " + PriceFormatter.format(
                        source.getOfferTotal(player, item, quantity, price)))
                .button1(FormStyle.CONFIRM).button2(FormStyle.DANGER + "Annuler")
                .closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (!response.clickedFirst()) {
                        openCreateDetails(player, parent, item);
                        return;
                    }
                    ActionResult result = source.createOffer(player, item, quantity, price);
                    openRoot(player, parent, (result.success() ? FormStyle.SUCCESS : FormStyle.DANGER)
                            + result.message());
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void openMine(Player player, Runnable parent) {
        openMine(player, parent, 0);
    }

    private void openMine(Player player, Runnable parent, int page) {
        List<BuyOffer> allOffers = source.getPlayerOffers(player);
        FormPages.Page<BuyOffer> offerPage = FormPages.of(allOffers, page);
        List<BuyOffer> offers = offerPage.values();
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Mes offres"))
                .content(allOffers.isEmpty() ? "Vous n'avez aucune offre d'achat."
                        : "Vos offres d'achat actuellement publiées :"
                        + (offerPage.count() > 1 ? "\nPage " + (offerPage.number() + 1)
                        + " / " + offerPage.count() : ""));
        for (BuyOffer offer : offers) {
            builder.button(FormStyle.PRIMARY + offer.itemName() + " · " + offer.remainingQuantity()
                    + " restants\n" + FormStyle.PRICE + PriceFormatter.format(offer.unitPrice()) + " / unité");
        }
        boolean hasPrevious = offerPage.hasPrevious();
        if (hasPrevious) builder.button(FormStyle.RETURN + "← Page précédente");
        boolean hasNext = offerPage.hasNext();
        if (hasNext) builder.button(FormStyle.PRIMARY + "Page suivante →");
        int previous = hasPrevious ? offers.size() : -1;
        int next = hasNext ? offers.size() + (hasPrevious ? 1 : 0) : -1;
        int back = offers.size() + (hasPrevious ? 1 : 0) + (hasNext ? 1 : 0);
        builder.button(FormStyle.RETURN + "← Retour");
        builder.closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)));
        builder.validResultHandler(forms.sync(player, once(response -> {
            if (response.clickedButtonId() == previous) {
                openMine(player, parent, offerPage.number() - 1);
            } else if (response.clickedButtonId() == next) {
                openMine(player, parent, offerPage.number() + 1);
            } else if (response.clickedButtonId() == back) {
                openRoot(player, parent, null);
            }
        })));
        forms.sendFormSync(player, builder.build());
    }

    private Double parsePrice(String raw) {
        try {
            double price = Double.parseDouble(raw.trim().replace(',', '.'));
            return Double.isFinite(price) && price > 0 ? price : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private <T> Consumer<T> once(Consumer<T> action) {
        AtomicBoolean handled = new AtomicBoolean();
        return value -> {
            if (handled.compareAndSet(false, true)) action.accept(value);
        };
    }
}
