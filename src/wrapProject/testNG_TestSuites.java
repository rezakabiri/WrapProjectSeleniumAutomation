package wrapProject;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.testng.Assert;

public class testNG_TestSuites {

	private WebDriver driver;
	private String baseURL;
	private int numOfCards = 5; // define the number of cards in the wrap

	@Test
	public void signUpAndLogIn() throws InterruptedException {
//		PageFactory.clickOnBuyNow();
		PageFactory.clickOnSignUpButton();
		
		// fill in email address
		String email = PageFactory.generateRandomName("testEmail");
		PageFactory.fillInEmailClickSignUp(email+"@gmail.com");
		
		// fill in user name and password
		String passWord = PageFactory.generateRandomName("pass");
		PageFactory.fillInUserPass(email, passWord);
		
		// complete account info
		PageFactory.completeAccountInfo();
	}
	
	@Test(dependsOnMethods = { "signUpAndLogIn" })
	public void createNewWrap() throws InterruptedException{
		PageFactory.createNewWrapAndSelectRandomly();
		PageFactory.closeTutorial();
		PageFactory.loopOverCardsKeepSome(numOfCards);
		PageFactory.loopOverElementsChangeText("Hi! This is a Test...");
	}
	
	@Test(dependsOnMethods = { "createNewWrap" })
	public void publishTheWrap() throws InterruptedException{
		Assert.assertTrue(PageFactory.publish());
	}

	@BeforeSuite
	public void beforeClass() {
//		baseURL = "https://www.qa.wrapdev.net/index/";
		baseURL = "https://authoring-qa.wrap.co/#/plans/select?scroll-to-pricing=true#introducingWrap";
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get(baseURL);
		long maxWaitTime = 90;
		
		PageFactory.setDriver(driver, maxWaitTime);
	}
}
