package org.elestya.bedrock.interfaces.hdv;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.AuctionListing;
import org.elestya.bedrock.api.sources.AuctionSource;
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

public final class AuctionMainForm {

    private final FormService forms;
    private final AuctionSource source;

    public AuctionMainForm(FormService forms, AuctionSource source) {
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

    public void openSearchResults(Player player, String query) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(query);
        if (query.isBlank()) throw new IllegalArgumentException("Search query cannot be blank");
        forms.runSync(player, () -> openListings(player, () -> {}, null, source.search(query)));
    }

    private void openRoot(Player player, Runnable parent, String result) {
        List<String> categories = source.getCategories();
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Hôtel de vente"))
                .content((result == null ? "" : result + "\n\n")
                        + "Parcourir les annonces, rechercher ou vendre l'objet en main.");
        for (String category : categories) {
            builder.button(FormStyle.PRIMARY + category, FormImage.Type.PATH,
                    FormImagePaths.forMaterial(categoryIcon(category)));
        }
        int searchIndex = categories.size();
        builder.button(FormStyle.PRIMARY + "Rechercher", FormImage.Type.PATH,
                FormImagePaths.forMaterial(Material.COMPASS));
        int mineIndex = searchIndex + 1;
        builder.button(FormStyle.PRIMARY + "Mes ventes", FormImage.Type.PATH,
                FormImagePaths.forMaterial(Material.CHEST));
        int sellIndex = mineIndex + 1;
        builder.button(FormStyle.PRIMARY + "Vendre l'objet en main", FormImage.Type.PATH,
                FormImagePaths.forMaterial(Material.HOPPER));
        int backIndex = sellIndex + 1;
        builder.button(FormStyle.RETURN + "← Retour");
        builder.closedOrInvalidResultHandler(forms.sync(player, parent));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index < categories.size()) {
                openListings(player, parent, categories.get(index), null);
            } else if (index == searchIndex) {
                openSearch(player, parent);
            } else if (index == mineIndex) {
                openMyListings(player, parent);
            } else if (index == sellIndex) {
                openSellForm(player, parent);
            } else if (index == backIndex) {
                parent.run();
            }
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openSearch(Player player, Runnable parent) {
        openSearch(player, parent, "", null);
    }

    private void openSearch(Player player, Runnable parent, String previousQuery, String error) {
        CustomForm form = CustomForm.builder()
                .title(FormStyle.title("Rechercher une annonce"))
                .label(error == null ? "Saisissez un objet, une catégorie ou un vendeur."
                        : FormStyle.DANGER + error)
                .input("Recherche", "Exemple : diamant", previousQuery)
                .closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    String query = response.asInput(0).trim();
                    if (query.isEmpty()) {
                        openSearch(player, parent, response.asInput(0), "Saisissez un terme de recherche.");
                        return;
                    }
                    openListings(player, parent, null, source.search(query));
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void openListings(Player player, Runnable parent, String category, List<AuctionListing> results) {
        openListings(player, parent, category, results, 0);
    }

    private void openListings(Player player, Runnable parent, String category, List<AuctionListing> results, int page) {
        List<AuctionListing> allListings = results == null ? source.getListings(category) : results;
        FormPages.Page<AuctionListing> listingPage = FormPages.of(allListings, page);
        List<AuctionListing> listings = listingPage.values();
        String title = category == null ? "Résultats de recherche" : category;
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("HDV · " + title))
                .content((allListings.isEmpty() ? "Aucune annonce dans cette liste."
                        : "Choisissez une annonce pour consulter son prix et confirmer l'achat.")
                        + (listingPage.count() > 1 ? "\nPage " + (listingPage.number() + 1)
                        + " / " + listingPage.count() : ""));
        for (AuctionListing listing : listings) {
            builder.button(FormStyle.PRIMARY + listing.itemName() + " × " + listing.quantity()
                            + "\n" + FormStyle.PRICE + PriceFormatter.format(listing.price()) + " · " + listing.seller(),
                    FormImage.Type.PATH, FormImagePaths.forMaterial(listing.icon()));
        }
        boolean hasPrevious = listingPage.hasPrevious();
        int previousIndex = hasPrevious ? listings.size() : -1;
        if (hasPrevious) {
            builder.button(FormStyle.RETURN + "← Page précédente");
        }
        boolean hasNext = listingPage.hasNext();
        int nextIndex = hasNext ? listings.size() + (hasPrevious ? 1 : 0) : -1;
        if (hasNext) {
            builder.button(FormStyle.PRIMARY + "Page suivante →");
        }
        builder.button(FormStyle.RETURN + "← Retour");
        int returnIndex = listings.size() + (hasPrevious ? 1 : 0) + (hasNext ? 1 : 0);
        builder.closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index >= 0 && index < listings.size()) {
                confirmPurchase(player, parent, listings.get(index), category, allListings, listingPage.number());
            } else if (index == previousIndex) {
                openListings(player, parent, category, allListings, listingPage.number() - 1);
            } else if (index == nextIndex) {
                openListings(player, parent, category, allListings, listingPage.number() + 1);
            } else if (index == returnIndex) {
                openRoot(player, parent, null);
            } else {
                openRoot(player, parent, null);
            }
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void confirmPurchase(Player player, Runnable parent, AuctionListing listing,
                                 String category, List<AuctionListing> results, int page) {
        ModalForm form = ModalForm.builder().title(FormStyle.title("Confirmer l'achat"))
                .content("Acheter " + listing.quantity() + " × " + listing.itemName()
                        + " auprès de " + listing.seller() + " ?\nPrix total : "
                        + PriceFormatter.format(listing.price()))
                .button1(FormStyle.CONFIRM)
                .button2(FormStyle.DANGER + "Annuler")
                .closedOrInvalidResultHandler(forms.sync(player,
                        () -> openListings(player, parent, category, results, page)))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (!response.clickedFirst()) {
                        openListings(player, parent, category, results, page);
                        return;
                    }
                    ActionResult action = source.buy(player, listing);
                    openRoot(player, parent, (action.success() ? FormStyle.SUCCESS : FormStyle.DANGER)
                            + action.message());
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void openMyListings(Player player, Runnable parent) {
        openMyListings(player, parent, 0);
    }

    private void openMyListings(Player player, Runnable parent, int page) {
        List<AuctionListing> allListings = source.getPlayerListings(player);
        FormPages.Page<AuctionListing> listingPage = FormPages.of(allListings, page);
        List<AuctionListing> listings = listingPage.values();
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Mes ventes"))
                .content(allListings.isEmpty() ? "Vous n'avez aucune vente en cours."
                        : "Vos annonces actuellement en vente :"
                        + (listingPage.count() > 1 ? "\nPage " + (listingPage.number() + 1)
                        + " / " + listingPage.count() : ""));
        for (AuctionListing listing : listings) {
            builder.button(FormStyle.PRIMARY + listing.itemName() + " × " + listing.quantity()
                    + "\n" + FormStyle.PRICE + PriceFormatter.format(listing.price()));
        }
        boolean hasPrevious = listingPage.hasPrevious();
        if (hasPrevious) builder.button(FormStyle.RETURN + "← Page précédente");
        boolean hasNext = listingPage.hasNext();
        if (hasNext) builder.button(FormStyle.PRIMARY + "Page suivante →");
        int previous = hasPrevious ? listings.size() : -1;
        int next = hasNext ? listings.size() + (hasPrevious ? 1 : 0) : -1;
        int back = listings.size() + (hasPrevious ? 1 : 0) + (hasNext ? 1 : 0);
        builder.button(FormStyle.RETURN + "← Retour");
        builder.closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)));
        builder.validResultHandler(forms.sync(player, once(response -> {
            if (response.clickedButtonId() == previous) {
                openMyListings(player, parent, listingPage.number() - 1);
            } else if (response.clickedButtonId() == next) {
                openMyListings(player, parent, listingPage.number() + 1);
            } else if (response.clickedButtonId() == back) {
                openRoot(player, parent, null);
            }
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openSellForm(Player player, Runnable parent) {
        openSellForm(player, parent, "", null);
    }

    private void openSellForm(Player player, Runnable parent, String previousPrice, String error) {
        var held = player.getInventory().getItemInMainHand().clone();
        if (held.getType().isAir()) {
            openRoot(player, parent, FormStyle.DANGER + "Vous devez tenir l'objet à vendre en main.");
            return;
        }
        String itemName = held.hasItemMeta() && held.getItemMeta().hasDisplayName()
                ? held.getItemMeta().getDisplayName() : held.getType().name();
        CustomForm form = CustomForm.builder().title(FormStyle.title("Vendre · " + itemName))
                .label((error == null ? "" : FormStyle.DANGER + error + "\n")
                        + "Quantité : " + held.getAmount())
                .input("Prix total", "Exemple : 250", previousPrice)
                .closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    String enteredPrice = response.asInput(1);
                    Double price = parsePrice(enteredPrice);
                    if (price == null) {
                        openSellForm(player, parent, enteredPrice, "Saisissez un prix positif valide.");
                        return;
                    }
                    confirmSell(player, parent, itemName, held.getType(), held.getAmount(), price);
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void confirmSell(Player player, Runnable parent, String itemName, Material material,
                             int quantity, double price) {
        ModalForm form = ModalForm.builder().title(FormStyle.title("Confirmer la mise en vente"))
                .content("Mettre en vente " + quantity + " × " + itemName + " pour "
                        + PriceFormatter.format(price) + " ?")
                .button1(FormStyle.CONFIRM).button2(FormStyle.DANGER + "Annuler")
                .closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (!response.clickedFirst()) {
                        openRoot(player, parent, null);
                        return;
                    }
                    ActionResult action = source.sellHeldItem(player, material, quantity, price);
                    openRoot(player, parent, (action.success() ? FormStyle.SUCCESS : FormStyle.DANGER)
                            + action.message());
                }))).build();
        forms.sendFormSync(player, form);
    }

    private Material categoryIcon(String category) {
        return switch (category.toLowerCase(java.util.Locale.ROOT)) {
            case "minerais" -> Material.DIAMOND;
            case "nourriture" -> Material.BREAD;
            case "blocs" -> Material.BRICKS;
            default -> Material.CHEST;
        };
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
