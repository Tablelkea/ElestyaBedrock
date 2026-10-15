package org.elestya.bedrock.interfaces;

public final class FormStyle {

    public static final String TITLE = "§8§l";
    public static final String PRIMARY = "§8";
    public static final String SECONDARY = "§5";
    public static final String RETURN = "§7";
    public static final String ITEM_TITLE = "§8§l";
    public static final String TEXT = "§7";
    public static final String MUTED = "§8";
    public static final String VALUE = "§f";
    public static final String PRICE = "§8";
    public static final String SUCCESS = "§8";
    public static final String DANGER = "§c";
    public static final String CONFIRM = "§2§lValider";
    public static final String RESET = "§r";

    private FormStyle() {
    }

    public static String title(String text) {
        return TITLE + text;
    }
}
