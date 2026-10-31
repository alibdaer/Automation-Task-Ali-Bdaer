package utils;

import java.math.BigDecimal;

public class PriceParser {

    public static BigDecimal parseUsd(String priceText) {
        if (priceText == null || priceText.isBlank()) {
            throw new IllegalArgumentException("Product price is empty");
        }

        String price = priceText.replace('\u00A0', ' ')
                .replace('\u202F', ' ').trim();

        // Accept USD values such as $9.99 and $1,234.56; reject malformed prices.
        String usdFormat = "\\$\\s*(?:0|[1-9]\\d*|[1-9]\\d{0,2}(?:,\\d{3})+)\\.\\d{2}";
        if (!price.matches(usdFormat)) {
            throw new IllegalArgumentException("Invalid USD price: " + priceText);
        }

        String amount = price.replace("$", "").replace(",", "").trim();
        return new BigDecimal(amount);
    }
}
