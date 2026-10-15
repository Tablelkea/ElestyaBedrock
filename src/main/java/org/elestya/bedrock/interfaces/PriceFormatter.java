package org.elestya.bedrock.interfaces;

import java.text.NumberFormat;
import java.util.Locale;

public final class PriceFormatter {

    private PriceFormatter() {
    }

    public static String format(double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.FRANCE);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(price);
    }
}
