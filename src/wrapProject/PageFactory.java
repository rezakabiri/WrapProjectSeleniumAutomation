package wrapProject;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageFactory {
	private static Random generator = new Random();
	private static WebDriver driver;
	private static WebElement element;
	private static List<WebElement> elementsList;
	private static WebDriverWait wait;
	private static Actions action;
	private static HashSet<String> textKeyWords;
	private static HashSet<String> picKeyWords;
	
	public static void setDriver(WebDriver driver, long timeOutSecond) {
		PageFactory.driver = driver;
		setWait(timeOutSecond);
		setAction();
		// Set up a set of words for reference
		textKeyWords = new HashSet<String>();
		textKeyWords.add("quote");
		textKeyWords.add("paragraph");
		textKeyWords.add("title");
		textKeyWords.add("name");
		textKeyWords.add("link");
		textKeyWords.add("headline");
		textKeyWords.add("text");
		textKeyWords.add("textbox");
		// Set up a set of words for tabs with image
		picKeyWords = new HashSet<String>();
		picKeyWords.add("image");
		picKeyWords.add("label");
		picKeyWords.add("logo");
		picKeyWords.add("quote");
	}

	private static void setWait(long timeOutSecond) {
		PageFactory.wait = new WebDriverWait(driver, timeOutSecond);
	}
	
	private static void setAction() {
		PageFactory.action = new Actions(driver);
	}
	
	public static boolean publish() throws InterruptedException{
		// click on Save button
		element = driver.findElement(By.xpath("//button[text()='Save']"));
		action.moveToElement(element).click().build().perform();
		
		//click on publish button
		element = driver.findElement(By.xpath("//button[text()='Publish']"));	
		wait.until(ExpectedConditions.elementToBeClickable(element));
		action.moveToElement(element).click().build().perform();
		
		// wait for the page to disappear
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='rect-container']")));
		
		// read the text in this box and see if it is published
		element = driver.findElement(By.xpath("//wm-action-bar//span"));
		String auxStr = element.getText().toLowerCase();
		String[] auxStrSplit = auxStr.split(" ");
		if (auxStrSplit[0].equals("published")){
			return true;
		} else {
			return false;
		}
	}
	
	public static void loopOverElementsChangeText(String input){
		String divText;
		String[] divTextList;
		int numTextComp, numPicComp;
		boolean isText;
		boolean isPic;
		
		// collect list of cards
		List<WebElement> cardsList = driver.findElements(By.xpath("//wm-assemble-area//ng-transclude"));
		int numOfCards = cardsList.size();
		
		// loop over list of cards
		for (int i = 0; i < numOfCards; i++){
			numTextComp = 0; // set the number of text component to zero
			numPicComp = 0; // set the number of text component to zero
			
			// navigate to the parent element which is click-able
			wait.until(ExpectedConditions.elementToBeClickable(cardsList.get(i)));
			element = cardsList.get(i).findElement(By.xpath(".."));
			action.moveToElement(element).click().build().perform();
			
			// collect list of different component of the card 
			List<WebElement> componentsList = driver.findElements(By.xpath("//div[contains(@class,'components_item ng-binding ng-scope ng-isolate-scope')]"));
			
			// loop over components and get text. if the text exist in the keywords change the text to input.
			for (int j = 0; j<componentsList.size(); j++){
				try{
					divText = componentsList.get(j).getText().toLowerCase();
					divTextList = divText.split(" ");
					
					// loop over parsed text and check if they exist in the keyword arrays
					isText = false;
					isPic = false;
					for (String str : divTextList){
						if (textKeyWords.contains(str)){// Check if the component is a text component
							isText = true;
						}
						if (picKeyWords.contains(str)){ // Check if the component is a picture component
							isPic = true;
						}
					}
					
					if (numTextComp < 3 && isText){ // if the component has text
						// make change to the component
						// navigate to the component and click on it
						element = componentsList.get(j);
						action.moveToElement(element).click().build().perform();
						// get text editor and send the input text into it
						element = driver.findElement(By.xpath("//wm-scribe//div[@class='ng-pristine ng-untouched ng-valid ng-scope ng-isolate-scope text-editor ng-not-empty']"));
						element.clear();
						element.sendKeys(input);
						numTextComp++;
					} else if (numPicComp < 3 && isPic){ // if the component has picture
						// make change to the component
						// navigate to the component and click on it
						element = componentsList.get(j);
						action.moveToElement(element).click().build().perform();
						
						// select the last image from the template
						elementsList = driver.findElements(By.xpath("//div[@class='assets_item-image']"));
						if (elementsList.size() > 0){
							// choose the last image
							element = elementsList.get(elementsList.size()-1);
							action.moveToElement(element).click().build().perform();
							
							// update number of pictures in the card
							numPicComp++;
						}
					} 
				} finally{
					
				}
				
			}
		}
		
		// loop over list of cards again and click on them
		for (int i = 0; i < numOfCards; i++){
			numTextComp = 0; // set the number of text component to zero
			numPicComp = 0; // set the number of text component to zero

			// navigate to the parent element which is click-able
			wait.until(ExpectedConditions.elementToBeClickable(cardsList.get(i)));
			element = cardsList.get(i).findElement(By.xpath(".."));
			action.moveToElement(element).click().build().perform();
		}
	}
	
	public static void loopOverCardsKeepSome(int numToKeep) throws InterruptedException{
		elementsList = driver.findElements(By.xpath("//ng-transclude"));
		wait.until(ExpectedConditions.elementToBeClickable(elementsList.get(0)));

		int numOfElems = elementsList.size();
		if (numToKeep > numOfElems){
			return;
		} else {
			for (int i = numToKeep; i < numOfElems-1; i++){
				element = elementsList.get(i).findElement(By.xpath(".."));
				Thread.sleep(1000);
				
				action.moveToElement(element,0,0).click().build().perform();
				element = elementsList.get(i).findElement(By.xpath("./button"));
				
				Thread.sleep(1000);
				action.moveToElement(element,0,0).click().build().perform();;
				element = driver.findElement(By.xpath("//span[text()='Yes, delete card']/.."));
				
				Thread.sleep(1000);
				action.moveToElement(element).click().build().perform();;
			}
		}
	}
	
	public static void closeTutorial(){ // closes the tutorial
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='help-tour_nav help-tour_nav--close']")));
		element = driver.findElement(By.xpath("//button[@class='help-tour_nav help-tour_nav--close']"));
		wait.until(ExpectedConditions.elementToBeClickable(element));
		
		element.click();
	}
	
	public static void createNewWrapAndSelectRandomly() throws InterruptedException{
		// find click on the CREATE NEW WRAP button
		element = driver.findElement(By.xpath("//button[@class='wraps_create-btn ng-scope']//label"));
		wait.until(ExpectedConditions.elementToBeClickable(element));
		Thread.sleep(5000);
		action.moveToElement(element,0,0).click().build().perform();
		
		// get a list of templates and select one randomly
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(.,'Start from scratch')]")));
		List<WebElement> elements = driver.findElements(By.xpath("//div[@class='wrap-settings_grouped-category-name ng-binding']"));
		int numOfItems = elements.size()-1; // -1 is to count for the Test template that has been added on 5/23 and is empty
		int randItem = generator.nextInt(numOfItems);
		element = elements.get(randItem);
		element.click();
		
		// get the list of templates and select one randomly
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[contains(.,'Create Wrap')]")));
		List<WebElement> elements1 = driver.findElements(By.xpath("//button[contains(.,'Create Wrap')]"));
		int numOfItems1 = elements1.size();
		int randElem1 = generator.nextInt(numOfItems1);
		element = elements1.get(randElem1);
		Thread.sleep(2000);
		element.click();
	}
	
	public static void completeAccountInfo() throws InterruptedException{
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wrap-theme")));
		
		driver.findElement(By.xpath("//input[@placeholder='First Name *']")).sendKeys("firstName");
		driver.findElement(By.xpath("//input[@placeholder='Last Name *']")).sendKeys("lastName");
		driver.findElement(By.xpath("//input[@placeholder='Company *']")).sendKeys("companyName");
		
		Thread.sleep(3000);
		
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//button[text()='Create account']")).click();
	}
	
	public static void fillInUserPass(String user, String pass) throws InterruptedException{
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wrap-theme")));
		
		driver.findElement(By.xpath("//input[@placeholder='Create a username']")).sendKeys(user);
		driver.findElement(By.xpath("//input[@placeholder='Password']")).sendKeys(pass);
		
		Thread.sleep(3000);
		
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//button[text()='Create account']")).click();
	}
	
	public static void fillInEmailClickSignUp(String email){
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wrap-theme")));
		driver.findElement(By.xpath("//*[@id='wrap-theme']/div[2]/div[1]//input")).sendKeys(email);
		driver.findElement(By.xpath("//*[@id='wrap-theme']//button[text()='Sign Up']")).click();
	}
	
	public static void clickOnBuyNow(){
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("hero-carousel")));
		driver.findElement(By.xpath("//*[@id='hero-carousel']//a[text()='Buy Now']")).click();
	}
	
	public static void clickOnSignUpButton(){
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='buttons-desktop']//a[text()='Sign Up']")));
		driver.findElement(By.xpath("//*[@class='buttons-desktop']//a[text()='Sign Up']")).click();
	}
	
	public static String generateRandomName(String Name){
		int randNum = generator.nextInt(10000);
		String name = Name+String.valueOf(randNum);
		return name;
	}
}
