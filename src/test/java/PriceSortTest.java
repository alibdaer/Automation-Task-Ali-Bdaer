import base.TestBase;
import org.testng.Reporter;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;

import java.math.BigDecimal;
import java.util.List;

public class PriceSortTest extends TestBase {

    LoginPage loginPage;
    ProductsPage productsPage;

    @Test(priority = 1, description = "Displayed prices must be strictly increasing after selecting Price (low to high)")
    public void pricesShouldBeStrictlyAscending() {

        loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");

        productsPage = new ProductsPage(driver);
        List<BigDecimal> pricesBeforeSorting = productsPage.getProductPrices();

        productsPage.sortByPriceLowToHigh();
        List<BigDecimal> pricesAfterSorting = productsPage.getProductPrices();

        Reporter.log("Prices after sorting: " + pricesAfterSorting, true);

        softAssert.assertTrue(pricesAfterSorting.size() >= 2,
                "At least two displayed prices are required to verify sorting");

        softAssert.assertEquals(pricesAfterSorting.size(), pricesBeforeSorting.size(),
                "Sorting changed the number of displayed products");

        for (int i = 1; i < pricesAfterSorting.size(); i++) {
            BigDecimal previousPrice = pricesAfterSorting.get(i - 1);
            BigDecimal currentPrice = pricesAfterSorting.get(i);

            softAssert.assertTrue(currentPrice.compareTo(previousPrice) > 0,
                    "Price at position " + (i + 1) + " (" + currentPrice
                            + ") must be greater than the previous price (" + previousPrice
                            + "). Displayed prices: " + pricesAfterSorting);
        }

        softAssert.assertAll();
    }
}
