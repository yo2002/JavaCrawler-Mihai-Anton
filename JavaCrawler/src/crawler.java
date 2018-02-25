import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class crawler {

	static String tempA;
	static String tempB;
	static String tempC;
	static WebDriver driver = new ChromeDriver();
	static WebDriverWait wait = new WebDriverWait(driver, 20);

	public static void main(String[] args) throws InterruptedException, IOException {
		driver.navigate().to("https://www.reifen.com/");
		// for English
		// driver.findElement(By.xpath(".//*[@id='metanavi']/li[4]/a[2]")).click();
		getDimensionList();
		getCSV();
		
	// ------------------------Dimension List Creator--------------------------
	public static void getDimensionList() throws InterruptedException, FileNotFoundException {
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		Select dropdown = new Select(driver.findElement(By.id("drpTyreWidthSB")));
		Select dropdown2 = new Select(driver.findElement(By.id("drpTyreCrossSectionSB")));
		Select dropdown3 = new Select(driver.findElement(By.id("drpTyreDiameterSB")));
		PrintWriter writer = new PrintWriter(new File("dimensionList.txt"));
		List<WebElement> tyreWidth = dropdown.getOptions();
		List<WebElement> tyreCross;
		List<WebElement> tyreDiam;
		List<String> listS = new ArrayList<String>();

		for (int i = 0; i < tyreWidth.size(); i++) {
			tempA = tyreWidth.get(i).getText();
			dropdown.selectByIndex(i);
			Thread.sleep(1100);
			tyreCross = dropdown2.getOptions();
			for (int j = 0; j < tyreCross.size(); j++) {
				tempB = tyreCross.get(j).getText();
				dropdown2.selectByIndex(j);
				Thread.sleep(1100);
				tyreDiam = dropdown3.getOptions();
				for (int k = 0; k < tyreDiam.size(); k++) {
					listS.add(tempA + " " + tempB + " " + tyreDiam.get(k).getText());
					System.out.println(tempA + " " + tempB + " " + tyreDiam.get(k).getText());
				}
				tyreDiam.removeAll(tyreDiam);
			}
			tyreCross.removeAll(tyreCross);
		}

		for (int i = 0; i < listS.size(); i++) {
			writer.println(listS.get(i));
		}
		writer.close();

	}

	// ------------------------CSV product list creator------------------------
	static int nb = 0;

	public static void trySelect(Select dd1, Select dd2, Select dd3, String t1, String t2, String t3, int n)
			throws InterruptedException {
		try {
			Thread.sleep(1000);
			if (n == 1)
				dd1.selectByVisibleText(t1);
			else if (n == 2)
				dd2.selectByVisibleText(t2);
			else {
				dd3.selectByVisibleText(t3);
				System.out.println(t1 + " " + t2 + " " + t3 + " ");
			}
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			if (nb < 5) {
				Thread.sleep(2500);
				if (n == 1)
					trySelect(dd1, dd2, dd3, t1, t2, t3, n);
				else if (n == 2) {
					dd1.selectByVisibleText(t1);
					trySelect(dd1, dd2, dd3, t1, t2, t3, n);
				} else {
					dd1.selectByVisibleText(t1);
					Thread.sleep(2500);
					dd2.selectByVisibleText(t2);
					trySelect(dd1, dd2, dd3, t1, t2, t3, n);
				}
				nb++;
			}
		}
	}

	static int nb2 = 0;

	public static void clickedAll() throws InterruptedException {
		try {
			wait.until(ExpectedConditions.attributeContains(By.xpath(".//*[@id='bottomArticleCount']/ul/li[3]/button"),
					"class", "active"));
			System.out.println("Pass2");
		} catch (TimeoutException e) {
			if (nb2 < 3) {
				System.out.println("Timeout");
				driver.findElement(By.xpath(".//*[@id='bottomArticleCount']/ul/li[2]/button")).click();
				Thread.sleep(1000);
				driver.findElement(By.xpath(".//*[@id='bottomArticleCount']/ul/li[3]/button")).click();
				clickedAll();
			}
		}
	}

	public static void getCSV() throws InterruptedException, IOException {

		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		List<String> listA = new ArrayList<String>();
		List<String> listB = new ArrayList<String>();
		List<String> listC = new ArrayList<String>();
		PrintWriter pw = new PrintWriter(new File("test.csv"));
		StringBuilder sb = new StringBuilder();
		List<String> lines = Files.readAllLines(Paths.get("dimensionList.txt"));
		String[] splitStr;
		int x = 0;

		for (int i = 0; i < lines.size(); i++) {
			splitStr = lines.get(i).split("\\s+");
			listA.add(splitStr[0]);
			listB.add(splitStr[1]);
			listC.add(splitStr[2]);
		}

		for (int i = 0; i < listA.size(); i++) {
			Select ddA = new Select(driver.findElement(By.id("drpTyreWidthSB")));
			Select ddB = new Select(driver.findElement(By.id("drpTyreCrossSectionSB")));
			Select ddC = new Select(driver.findElement(By.id("drpTyreDiameterSB")));
			wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("drpTyreWidthSB"))));
			Thread.sleep(1000);
			x = 1;
			trySelect(ddA, ddB, ddC, listA.get(i), listB.get(i), listC.get(i), x);
			Thread.sleep(1000);
			x = 2;
			trySelect(ddA, ddB, ddC, listA.get(i), listB.get(i), listC.get(i), x);
			Thread.sleep(1000);
			x = 3;
			trySelect(ddA, ddB, ddC, listA.get(i), listB.get(i), listC.get(i), x);
			nb = 0;
			Thread.sleep(1000);
			System.out.println("Pass");
			driver.findElement(By.xpath(".//*[@id='content-groesse']/div[3]/span[2]/button")).click();
			driver.findElement(By.xpath(".//*[@id='bottomArticleCount']/ul/li[3]/button")).click();
			clickedAll();
			nb2 = 0;
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("secondHeaderStyle")));
			List<WebElement> brand = driver.findElements(By.className("secondHeaderStyle"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[1]/p[3]")));
			List<WebElement> profile = driver.findElements(By.xpath("//div[1]/p[3]"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[1]/div/p/span/span")));
			List<WebElement> price = driver.findElements(By.xpath("//div[1]/div/p/span/span"));

			for (int j = 0; j < brand.size(); j++) {
				tempA = profile.get(j).getText();
				tempA = tempA.substring(tempA.indexOf(" ", tempA.indexOf(" ", tempA.indexOf(" ") + 1) + 1) + 1,
						tempA.length());
				tempB = price.get(j).getText().replace(',', '.').replace(" €", "EUR");
				tempC = brand.get(j).getText();
				sb.append(listA.get(i));
				sb.append(',');
				sb.append(listB.get(i));
				sb.append(',');
				sb.append(listC.get(i));
				sb.append(',');
				sb.append(tempC);
				sb.append(',');
				sb.append(tempA);
				sb.append(',');
				sb.append(tempB);
				sb.append('\n');
			}

			driver.navigate().back();
			driver.navigate().back();
		}
		pw.write(sb.toString());
		pw.close();
		driver.close();
	}
}
