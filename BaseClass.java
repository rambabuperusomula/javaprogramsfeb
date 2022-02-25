package com.Utilities;

//import java.util.ServiceLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.sql.Driver;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
//import javax.activation.DataHandler;
//import javax.activation.FileDataSource;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.Multipart;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.ITestResult;
//import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
//import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
//import com.aventstack.extentreports.reporter.ExtentReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass  {

	public static WebDriver driver;
	ExtentHtmlReporter Report;
	protected static ExtentReports extent;
	protected static ExtentTest Test;
	protected static Properties Pro;
	protected static Properties Pro1;
	public  static Actions act;

	public static Xls_Reader  xls=new Xls_Reader(System.getProperty("user.dir")+"\\src\\test\\resources\\Exceldata_rims\\Rims_excel_Data.xlsx");
	@SuppressWarnings("deprecation")
	@BeforeMethod(alwaysRun = true)
	public void suiteSetUp() throws IOException 
	{
		Pro = new Properties();
		InputStream input = new FileInputStream(System.getProperty("user.dir") +"\\src\\test\\resources\\Configurations\\config.properties");
		Pro.load(input);

		WebDriverManager.chromedriver().setup();
		ChromeOptions co = new ChromeOptions();
		co.addArguments("--disable-notifications");

		String[] s = new String[] {"enable-automation"};
		co.setExperimentalOption("excludeSwitches",s);

		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("credentials_enable_service", false);
		prefs.put("profile.password_manager_enabled", false);

		co.setExperimentalOption("prefs", prefs);

		driver = new ChromeDriver(co);   // 1
		System.setProperty("webdriver.chrome.silentOutput","true");
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().deleteAllCookies();
		driver.get(Pro.getProperty("url"));
		driver.manage().window().maximize();
	    act=new Actions(driver);
	}

	@BeforeTest()
	public void ExtentReport() 
	{
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());//time stamp
		String repName="Test-Report-"+timeStamp+".html";
		//		String repName="Test-Report-"+".html";
		Report=new ExtentHtmlReporter(System.getProperty("user.dir")+ "/Reports/"+repName);//specify location of the report
		Report.config().setEncoding("utf-8");
		Report.config().setDocumentTitle("Automation Report");
		Report.config().setReportName("Automation Test Result");
		Report.config().setTheme(Theme.STANDARD);

		extent = new ExtentReports();
		extent.attachReporter(Report);
		extent.setSystemInfo("Organization","Audree Infotech Pvt Ltd");
		extent.setSystemInfo("Environemnt","QA");
		extent.setSystemInfo("Tester","Rambabu");
		Report.config().setDocumentTitle("Electronic Log Management Sysytem"); // Tile of report
		Report.config().setReportName("ELMS Test Automation Report"); // name of the report
	}
	
	@AfterTest
	public void EndReport() 
	{
		extent.flush();
//		driver.quit();
	}

	@AfterMethod()
	public void getResult(ITestResult result) throws IOException 
	{
		if (result.getStatus() ==ITestResult.FAILURE) 
		{
			Test.log(Status.FAIL, "Test Case Failed " + result.getName());
			Test.log(Status.FAIL, "Test Case Failed " + result.getThrowable()); //get exception in extent Reports
			String scrceenshotpath = BaseClass.getScreenshot(driver, result.getName());
			Test.fail(result.getName(), MediaEntityBuilder.createScreenCaptureFromPath(scrceenshotpath).build()); // To Add screenshot in extent report
		} 
		else if(result.getStatus() == ITestResult.SKIP) 
		{
			Test.log(Status.SKIP, "Test Case Skipped " + result.getName());
		}
		else if(result.getStatus() == ITestResult.SUCCESS) 
		{
			Test.log(Status.PASS, "Test Case Passed ");
		}
	}
	// Takes Screenshot
	private static String getScreenshot(WebDriver driver, String screenshotName) throws IOException 
	{
		String dateName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());//time stamp
		File source = ((TakesScreenshot) (driver)).getScreenshotAs(OutputType.FILE);
		String destination = System.getProperty("user.dir") + "/FailedTestScreenshots/" + screenshotName + dateName + ".png";
		File finalDestination = new File(destination);
		org.apache.commons.io.FileUtils.copyFile(source, finalDestination);
		return destination;
	}

	public static void scrollPagedown() throws Exception
	{
		JavascriptExecutor js =  (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}
	public static void scrollPageup() throws Exception
	{
		JavascriptExecutor js =  (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(document.body.scrollHeight, 0)");
	}	
	public static void login(String username,String pswd) throws Exception
	{
		driver.findElement(By.xpath("//button[contains(.,'Login')]")).click(); // Sign Button 
		Thread.sleep(2000);
		driver.findElement(By.xpath("//*[@id=\"myModal\"]/div/div/div[2]/form/div[2]/input")).sendKeys(Pro.getProperty(username));
		Thread.sleep(2000);
		driver.findElement(By.xpath("//*[@id=\"myModal\"]/div/div/div[2]/form/div[3]/input")).sendKeys(Pro.getProperty(pswd));

		driver.findElement(By.xpath("//*[@id=\"myModal\"]/div/div/div[2]/form/div[4]/div/div/button")).click();
		Thread.sleep(3000);

		List<WebElement> enabledButtonsa2 = driver.findElements(By.xpath("//*[@id=\"BtnWApp\"]"));
		if(!enabledButtonsa2.isEmpty()){
			enabledButtonsa2.get(0).click();
		}
		Thread.sleep(3000); 

		driver.findElement(By.xpath("//span[contains(.,'Regulatory Information Management System - Corporate')]")).click();
		Thread.sleep(3000);
		//click on menu
		driver.findElement(By.id("navbarDropdown")).click();
		Thread.sleep(2000);

	}
		
	public static void Logout() throws Exception
	{
		// Logout
		WebElement icon = driver.findElement(By.xpath("//*[@class=\"avatarIcon\"]"));
//		Actions a = new Actions(driver);
//		a.doubleClick(icon).perform();	
//		Thread.sleep(2000);
		icon.click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//a[contains(.,' Quit')]")).click(); // Quit
		Test.log(Status.PASS, "User Clicked on Quit  button");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(),\"Yes\")]")).click(); // Yes 
		Test.log(Status.PASS, "User Clicked on Yes  button");
		Thread.sleep(2000);

		driver.findElement(By.xpath("//*[@class=\"avatarIcon\"]")).click(); // 
		Test.log(Status.PASS, "User Clicked on Icon  button");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//a[contains(.,'Logout')]")).click(); // logout
		Test.log(Status.PASS, "User Clicked on Logout  button");
		Thread.sleep(2000);
		driver.findElement(By.xpath("(//a[contains(.,'Yes')])[1]")).click(); // Yes 
		Test.log(Status.PASS, "User Clicked on Yes  button");
		Thread.sleep(2000);
	}
	
	public static void save() throws Exception
	{
		driver.findElement(By.xpath("//*[contains(text(),\"Save\") or @ title=\"submit\" or @type=\"submit\" or contains(text(),\"Submit\")]")).click(); //save
		Thread.sleep(2000);
		driver.findElement(By.xpath("(//*[contains(text(),\"Yes\")])[2]")).click(); //yes
		Thread.sleep(2000);
		driver.findElement(By.xpath("//*[@placeholder=\"Password\"]")).sendKeys(Pro.getProperty("password"));
		Thread.sleep(2000);
		driver.findElement(By.xpath("//button[contains(.,'Submit')]")).click(); //submit
		Thread.sleep(2000);
		driver.findElement(By.xpath("//*[contains(text(),\"OK\") or contains(text(),\"Ok\")]")).click(); //ok
		Thread.sleep(2000);
		
	}

}
