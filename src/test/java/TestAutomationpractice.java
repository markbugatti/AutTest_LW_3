import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.util.concurrent.TimeUnit;


public class TestAutomationpractice {
    public static WebDriver driver = null;
    public static WebDriverWait wait = null;
    static String baseUrl = "http://automationpractice.com/";
    static ChromeOptions chromeOptions;

    String yourEmail = "18fi.m.kravchenko@std.npu.edu.ua";
    String yourPassword = "tcYESUW9D@j8EU6";

    @BeforeClass
    public static void setUp() {
        try {
            chromeOptions = new ChromeOptions();
            chromeOptions.setCapability("browserVersion", "83");
            chromeOptions.setCapability("platformName", "Windows 10");

            driver = new RemoteWebDriver(new URL("http://localhost:9515"), chromeOptions);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.get(baseUrl);

            wait = new WebDriverWait(driver, 10);
        } catch (Exception e) {
            System.out.println("Error in setUp: " + e.getMessage());
            if(driver != null) {
                driver.quit();
            }
        }
    }

    // click SignIn and check Title
    @Test
    public void Test1() {
        System.out.println("\nTest1:");
        boolean titleExist = false;
        String title = "";

        /* Sign in*/
        SignIn();

        title = driver.getTitle();
        System.out.println("Title of this page: " + title);

        LogIn(yourEmail, yourPassword);
        /* check First and Last name*/
        String path = "//a[@title='View my customer account'][@class='account']/span[1]";
        String actualName = "";
        WebElement nameSpan = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(path)));
        actualName = nameSpan.getText();
        System.out.println("Actual Name: " + actualName);
    }

    @Test
    public void Test2() {
        System.out.println("\nTest 2:");

        SignIn();
        LogIn("", yourPassword);

        String alert = getAlert();
        System.out.println("Alert message: " + alert);

    }

    @Test
    public void Test3() {
        System.out.println("\nTest 2:");

        SignIn();
        LogIn(yourEmail, "");

        String alert = getAlert();
        System.out.println("Alert message: " + alert);
    }

    @Test
    public void Test4() throws InterruptedException {
        SignIn();
        LogIn(yourEmail, yourPassword);

        //driver.findElement(By.linkText("T-shirts")).click();
        //(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(path)))).click();

        /* go on T-Shirts catalog*/
        String path = "//div[@id='block_top_menu']//li[3]/a[@title='T-shirts']";
        driver.findElement(By.xpath(path)).click();

        /* choose a T-Shirt */
        path = "//div[@class='product-container']//div[@class='product-image-container']/a[@class='product_img_link']";
        driver.findElement(By.xpath(path)).click();

        /* add to card */
        path = "//p[@id='add_to_cart']/button[1]";
        driver.findElement(By.xpath(path)).click();

        /* go to card */
        path = "//span[contains(text(), 'Proceed to checkout')]";
        //driver.findElement(By.id("button_order_cart")).click();
        driver.findElement(By.xpath(path)).click();

        /* check Product name */
        path = "//table[@id='cart_summary']/tbody//td[2]//a";
        String text = driver.findElement(By.xpath(path)).getText();

        System.out.println("Expected name of current product: " + "Faded Short Sleeve T-shirts");
        System.out.println("Actual name of current product: " + text);

        /* get actual price */
        path = "//table[@id='cart_summary']/tbody//td[4]/span/span[contains(text(), '16.51')]";
        text = checkValue(path, "price", "16.51");

        /* multiply price by 2 */
        StringBuilder stringBuilder = new StringBuilder(text);
        stringBuilder = stringBuilder.deleteCharAt(0);
        text = stringBuilder.toString();
        Double price = Double.valueOf(text);
        price *= 2;
        text = price.toString();


        /* Add to quantity one more T-Shirt and check total price */
        //path = "//table[@id='cart_summary']/tbody//td[5]"
        driver.findElement(By.id("cart_quantity_up_1_1_0_325428")).click();

        path = "//table[@id='cart_summary']/tbody//td[6]/span[contains(text(), '" + text + "')]";
        checkValue(path, "price", text);
    }

    @Test
    public void Test5() throws InterruptedException {
        SignIn();
        LogIn(yourEmail, yourPassword);

        String searchString = "Printed Chiffon Dress";

        /* Find Search Bar */
        WebElement searchBar = driver.findElement(By.id("search_query_top"));
        searchBar.sendKeys(searchString);

        /* Find dress */
        String path = "//form[@id='searchbox']/button[@name='submit_search']";
        driver.findElement(By.xpath(path)).click();

        /* make sure that dress correct dress was found */
        path = "//div[@id='center_column']//div[@class='product-container']//a[@class='product-name'][contains(text(), '"+ searchString + "')]";
        String text = checkValue(path, "dress", searchString);

        /* go to the div, which contains discount as well as dress name and check discount*/


        path += "/../..//span[@class='price-percent-reduction']";
        checkValue(path, "discount", "20%");


        Thread.sleep(3000);
    }

    public void SignIn() {
        driver.findElement(By.partialLinkText("Sign in")).click();
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().contentEquals("Login - My Store");
            }
        });
    }

    public void LogIn(String yourEmail, String yourPassword) {
        // find email and password fields
        WebElement email = driver.findElement(By.id("email"));
        WebElement password = driver.findElement(By.id("passwd"));
        /* input email and password*/
        email.sendKeys(yourEmail);
        password.sendKeys(yourPassword);
        driver.findElement(By.id("SubmitLogin")).click();
    }

    public String getAlert() {
        String path = "//div[@id='center_column']/div[@class='alert alert-danger']//li[1]";
        String alert = (wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(path)))).getText();
        return alert;
    }

    public String checkValue(String path, String expectedType, String expectedPrice) {
        String text = "";
        try {
            text = driver.findElement(By.xpath(path)).getText();
            System.out.println("Expected " + expectedType + ": " + expectedPrice);
            System.out.println("Current " + expectedType + ": " + text);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("The " + expectedType + " isn't correct");
        }
        return text;
    }

    @AfterClass
    public static void freeResources() {
        driver.quit();
    }
}
