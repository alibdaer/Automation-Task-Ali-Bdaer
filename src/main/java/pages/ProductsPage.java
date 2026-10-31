package pages;

import base.PageBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.PriceParser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductsPage extends PageBase {

    @FindBy(css = "select.product_sort_container")
    private WebElement sortDropdown;

    @FindBy(css = ".inventory_list .inventory_item_price")
    private List<WebElement> productPrices;

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public void sortByPriceLowToHigh() {
        normalSelect(sortDropdown, "Price (low to high)");
        wait.until(ExpectedConditions.attributeToBe(sortDropdown, "value", "lohi"));
    }

    public List<BigDecimal> getProductPrices() {
        // Re-read the elements if the page replaces them during rendering.
        return wait.until(ExpectedConditions.refreshed(webDriver -> readProductPrices()));
    }

    private List<BigDecimal> readProductPrices() {
        List<WebElement> elements = new ArrayList<>(productPrices);
        List<BigDecimal> prices = new ArrayList<>();

        if (elements.isEmpty()) {
            return null;
        }

        for (WebElement element : elements) {
            String priceText = element.getText().trim();

            // A null result keeps the explicit wait running until prices are ready.
            if (!element.isDisplayed() || priceText.isEmpty()) {
                return null;
            }

            prices.add(PriceParser.parseUsd(priceText));
        }

        return prices;
    }
}
