//import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.*;
  
/**
* Java Program to parse/read HTML documents from File using Jsoup library.
* Jsoup is an open source library which allows Java developer to parse HTML
* files and extract elements, manipulate data, change style using DOM, CSS and
* JQuery like method.
*
* @author Javin Paul
*/
public class ParserHTML{

	public static class Calend_rec{
		public Date eventDate;//dateTime
		public String eventName;
		public String currName;
		double actualValue;
		double forecastValue;
		double prevValue;
		int flag01;
		int flag02;
		
		//constructor
		Calend_rec(){
			eventDate = new Date();
			eventName = "";
			currName = "";
			actualValue = 0;
			forecastValue = 0;
			prevValue = 0;
			flag01 = 0;
			flag02 = 0;
		}
	}
	
	public static Calend_rec[] calendar = new Calend_rec[30]; //здесь будут ближайшие записи
  
    public static void main(String args[]){
    	
    	
        //init array
    	int i_;
    	for (i_ = 0; i_ < calendar.length; i_++){
    		calendar[i_] = new Calend_rec();
    	}

    	
    	
        //parseUrl("https://www.forexfactory.com/calendar.php", "proxy.lan", 8080);
        parseUrl("https://www.forexfactory.com/calendar.php", "", 0);
        
    	//doc = Jsoup.connect("https://www.forexfactory.com/calendar.php").proxy("proxy.lan",8080).get();
        //doc = Jsoup.connect("https://www.forexfactory.com/calendar.php").get();

        
  
  
    }
    
    public static void parseUrl(String urlStr, String proxy, int proxyPort){
    	// JSoup - Reading HTML page from URL
    	Calend_rec aaa = new Calend_rec();
    	
        Document doc = null;
        String title = "";
        try {
        	if (proxy == ""){
        		doc = Jsoup.connect(urlStr).get();
        	} else {
        			doc = Jsoup.connect(urlStr).proxy(proxy,proxyPort).get();
        		}
        		
            title = doc.title();
        } catch (IOException e) {
            e.printStackTrace();
        }
  
        System.out.println("Jsoup Can read HTML page from URL, title : " + title+"\n");
        
        Elements rows = doc.getElementsByClass("calendar__row");
        String tmpStrDate = "";
        String tmpStr = "";
        Date tmpDate = Calendar.getInstance().getTime();
        //System.out.println("current date " + new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        Element el;
        for (Element row : rows) {
        	Elements cells = row.getElementsByClass("calendar__cell");
        	aaa.eventName = "";
        	for (Element cell:cells){
        		el = null;
        		if (cell.hasClass("date")){ //calendar__event
        			//String target = "Thu Sep 28 20:29:30 JST 2000";
        		    //DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
        		    //Date result =  df.parse(target); 
        			
        			if (cell.select("span:nth-child(1)").size()>1){
        				Element ell = cell.select("span:nth-child(1)").get(1);
        				if (ell != null){
        					tmpStrDate = ell.text() + " 2017";
        				}
        			}
        		}
        		
        		if (cell.hasClass("time") ){
        			if (cell.text().length() > 0 && cell.text() != "All Day"){
        				if (tmpStrDate == "") {
        					System.out.println("ERROR date is null!!!");
        					tmpStrDate = new SimpleDateFormat("MMM d yyyy").format(Calendar.getInstance().getTime()); 		
        				}
        				try {
        					tmpDate = (new SimpleDateFormat("MMM d yyyy h:mma Z", Locale.ENGLISH)).parse(tmpStrDate + " " +cell.text() + " -0500");
        				} catch (ParseException e) {
        					System.out.println("!!!!  Date Parser problem (Friend.java): " + e.toString());
        					//e.printStackTrace();
        				}
        			}
        		}
        		
        		if (cell.hasClass("impact")){
        			System.out.println("impact: " + cell.html());
        			if (cell.hasClass("low")){
        				System.out.println("impact: low");
        				aaa.flag02 = 1;
        			} else if (cell.hasClass("medium")){
        				System.out.println("impact: low");
        				aaa.flag02 = 2;
        				} else if (cell.hasClass("high")){
        					aaa.flag02 = 3;
        					} else aaa.flag02 = 0;
        		}
        		
        		if (cell.hasClass("event")){ //calendar__event
        			//System.out.println("event : " + cell.getElementsByClass("calendar__event-title").text());
        			aaa.eventName = cell.getElementsByClass("calendar__event-title").text();
        		}
        		//=================forecast
        		if (cell.hasClass("forecast") ){
        			aaa.forecastValue = Double.parseDouble(regStr2Num(cell.text()));
        			//System.out.println("    forecast : " + cell.text() + " parsed=" + aaa.forecastValue);
        		}
        		//=================previous
        		if (cell.hasClass("previous") ){
        			aaa.prevValue = Double.parseDouble(regStr2Num(cell.text()));
        			//System.out.println("    prev : " + cell.text() + " parsed=" + aaa.prevValue);
        		}
        		
        		//=================actual
        		if (cell.hasClass("actual") && cell.html() != null){ //calendar__event
        			aaa.actualValue = Double.parseDouble(regStr2Num(cell.text()));
        			//System.out.println("    prev : " + cell.text() + " parsed=" + aaa.actualValue);
        			
        			//System.out.println("    act: " + cell.text());
        			el = cell.selectFirst("span.better");
        			if (el != null){
        				//System.out.println("    better : " + el.text());
        				aaa.flag01 = 1;
        			} else {
        				el = cell.selectFirst("span.worse");
        				if (el != null){
        					//System.out.println("    worse : " + el.text());
        					aaa.flag01 = -1;
        				} else {
        					aaa.flag01 = 0;
        					//System.out.println("    values : " + cell.text());
        					}
        			}
       			
        		} //actual
        		
        		
        		if (cell.hasClass("currency") ){
        			//System.out.println("    currency: " + cell.text());
        			aaa.currName = cell.text();
        		}
        	}//cells
        	if (aaa.eventName != "" && (aaa.actualValue != 0 || aaa.prevValue != 0 || aaa.forecastValue != 0)){
        		aaa.eventDate = tmpDate;
        		System.out.println("date: " + new SimpleDateFormat("MM/dd/yyyy HH:mm z").format(tmpDate) + " curr=" + aaa.currName +
        				" event=" + aaa.eventName + " av=" + aaa.actualValue + " fv="+aaa.forecastValue+" pv="+aaa.prevValue + " fl1=" + aaa.flag01+ " fl2=" + aaa.flag02);
        	}
        	//System.out.println("    date: " + tmpDate.toString());
        }//rows
    }
    
    public static String regStr2Num(String strNum){
    	Pattern pat=Pattern.compile("[-]?[0-9]+(.[0-9]+)?");
    	Matcher matcher=pat.matcher(strNum);
    	if (matcher.find()) {
    	    //System.out.println(matcher.group());
    		return matcher.group();
    	} else return "0";
    }
    
    public static void addRec(Calend_rec rec){
    	int emptyRecId = -1;
    	boolean needAdd = false;
    	
    	Date tmpDate = new Date(); 
    	tmpDate.setTime(rec.eventDate.getTime() - 1000*60*10); //10 minutes as milliseconds 
    	//определим надо ли эту запись вставлять в массив
    	if (tmpDate.before(rec.eventDate) ){
    		
    	}
    	for (int i = 0; i < calendar.length; i++){
    		//запомним ИД пустой строчки
    		/*if (emptyRecId < 0 && calendar[i].eventName == ""){
    			emptyRecId = i;
    		}*/
    		
    		//нашли такое событие, но в нём актуальные значения не совпадают - надо сохранить
    		if (calendar[i].eventName == rec.eventName && 
    			calendar[i].currName  == rec.currName && 
    			calendar[i].eventDate == rec.eventDate &&
    			calendar[i].actualValue != rec.actualValue)
    		{
    			needAdd = true;
    		}
    	}
    
    }
    
}