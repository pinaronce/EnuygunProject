package org.enuygun.step;

import com.thoughtworks.gauge.Gauge;
import com.thoughtworks.gauge.Step;
import org.enuygun.base.BaseTest;
import org.enuygun.model.ElementInfo;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseSteps extends BaseTest {

    public BaseSteps() {
        initMap(getFileList());
    }
    private void clickElement(WebElement element) {
        element.click();
    }

    WebElement findElement(String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        return webElement;
    }
    List<WebElement> findElements(String key) {
        return driver.findElements(getElementInfoToBy(findElementInfoByKey(key)));
    }

    public By getElementInfoToBy(ElementInfo elementInfo) {
        By by = null;

        if (elementInfo.getType().equals("id")) {
            by = By.id(elementInfo.getValue());
        } else if (elementInfo.getType().equals("css")) {
            by = By.cssSelector(elementInfo.getValue());
        } else if (elementInfo.getType().equals("xpath")) {
            by = By.xpath(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("name"))) {
            by = By.name(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("classname"))) {
            by = By.className(elementInfo.getValue());
        } else if (elementInfo.getType().equals("linkText")) {
            by = By.linkText(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("partialLinkText"))) {
            by = By.partialLinkText(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("tagName"))) {
            by = By.tagName(elementInfo.getValue());
        }

        return by;
    }

    @Step("<url> adresine gidilir")
    public void goToUrl(String url){
        driver.navigate().to(url);
        Gauge.writeMessage("Page title is %s",driver.getTitle());
        Gauge.captureScreenshot();
    }

    @Step("<key> elementine tıklanır")
    public void clickElement(String key) {
        if (!key.isEmpty()) {
            clickElement(findElement(key));
            logger.info(key + " elementine tiklandi");
        }
    }

    public void javascriptClicker(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    @Step("<key> elementine javascript ile tıklanır")
    public void clickToElementWithJavaScript(String key) {
        WebElement element = findElement(key);
        javascriptClicker(element);
        logger.info(key + " elementine javascript ile tiklandi");
    }


    @Step("<key> elementine <text> texti yazılır")
    public void sendKeys(String key, String text) {
        if (!key.equals("")) {
            findElement(key).sendKeys(text);
            logger.info(key + " elementine " + text + " texti yazildi");
        }
    }
    @Step("<saniye> saniye beklenir")
    public void waitBySeconds(int saniye) throws InterruptedException {
        Thread.sleep(saniye*1000);
    }

    @Step("<key> checkboxı seçili degilse seçilir")
    public void selectionCheckbox(String key){
        boolean isMarkedDone = findElement(key).isSelected();
        System.out.println("Aktarmasiz checkboxi seçili değil" +isMarkedDone);

        if(isMarkedDone==false){
            clickElement(key);
            System.out.println("Aktarmasiz checkboxi secildi.");
        }
    }

    @Step("<key> checkboxı seçili ise seçim kaldırılır")
    public void unselectedCheckbox(String key){
        boolean isMarkedDone = findElement(key).isSelected();
        System.out.println("Tek yon checkboxi secili." +isMarkedDone);

        if(isMarkedDone==true){
            clickElement(key);
            System.out.println("ek yon checkboxi secildi.");
        }
    }

    @Step("<key> elementinin görünürlüğü kontrol edilir")
    public void checkElement(String key) {
        assertTrue(findElement(key).isDisplayed(), "Aranan element bulunamadi");
        logger.info(key+ "elementi gorundu.");
    }

    @Step("Gün-Ay-Yıl(ddMMyy) formatında <day>-<month>-<year> bilet tarihi girişi yapılır")
    public void selectDate(String day,String month,int year) throws InterruptedException {

        LocalDate localDate=LocalDate.now();
        int localYear=localDate.getYear();
        String ayYil= MonthConst.getAy(month) + " " + year;
        System.out.println("Ay ve yıl bilgisi : " + ayYil);


        if(localYear==year || localYear==((year)-1)){

            while(true){
                String text=findElement("ayYilBasligi").getText();

                if(text.equals(ayYil)){
                    break;
                }
                else{
                    clickElement("gelecekAyButonu");
                    waitBySeconds(2);
                }
            }

            List <WebElement> allDates=driver.findElements(By.xpath("//div[@data-visible='true']/table/tbody/tr/td[@role='button' and @aria-disabled='false' and @aria-label=contains(@aria-label,'"+MonthConst.getAy(month)+"')]"));

            for(WebElement ele:allDates)
            {
                String text=ele.getText();
                if(text.equals(day)){
                    ele.click();
                    System.out.println("Elemente tiklandi..");
                    break;
                }
            }
        }
        else {
            Assertions.fail("Lutfen gecerli bir tarih araligi giriniz!.. Girilebilecek Yıllar : "+localYear+ "ve "+((localYear)+1));
        }
    }

    @Step("<yetiskin> biletini <adet1> adet <cocuk> biletini <adet2> adet <bebek> biletini <adet3> adet <yas65> biletini <adet4> <ogrenci> biletini <adet5> adet arttır")
    public void passengerSelection(String yetiskin,int adet1,String cocuk,int adet2,String bebek,int adet3,String yas65,int adet4,String ogrenci,int adet5) {

        int count=adet1+adet2+adet4+adet5;

        if(count > 9){
            Assertions.fail("En fazla alinabilecek bilet sayisi 9'dur. İstenilen bilet sayisi: " + count);
        }

        else if (adet3> (adet1+adet4)){
            Assertions.fail("Bebek yolcu sayisi, yetiskin ve 65 yas üstü yolcu sayilarinin toplamindan fazla olamaz !!!  \n Bebek yolcu Sayısı: " + adet3 + " \n Yetiskin ve 65 yas üstü yolcu sayisinin toplami: " + (adet1+adet4) );
        }
        for(int i=0;i<adet1;i++){
            clickElement(yetiskin);
        }
        clickElement("yetiskinYolcuSayisiAzaltmaButonu");

        Assertions.assertEquals(Integer.toString(adet1),findElement("yetiskinYolcuSayisi").getText(),"Yetiskin bilet adeti ile secilen bilet adeti esit degil");

        for(int i=0;i<adet2;i++){
            clickElement(cocuk);
        }
        Assertions.assertEquals(Integer.toString(adet2),findElement("cocukYolcuSayisi").getText(),"Cocuk bilet adeti ile secilen bilet adeti esit degil");

        for(int i=0;i<adet3;i++){
            clickElement(bebek);
        }
        Assertions.assertEquals(Integer.toString(adet3),findElement("bebekYolcuSayisi").getText(),"Bebek bilet adeti ile secilen bilet adeti esit degil");

        for(int i=0;i<adet4;i++){
            clickElement(yas65);
        }
        Assertions.assertEquals(Integer.toString(adet4),findElement("yas65YolcuSayisi").getText(),"65YaşYolcu bilet adeti ile secilen bilet adeti esit degil");

        for(int i=0;i<adet5;i++){
            clickElement(ogrenci);
        }
        Assertions.assertEquals(Integer.toString(adet5),findElement("ogrenciYolcuSayisi").getText(),"Ogrenci bilet adeti ile secilen bilet adeti esit degil");

        clickElement("yolcuTamamButonu");
    }
}


