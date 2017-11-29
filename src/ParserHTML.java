//import java.io.File;
import java.io.IOException;
  
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

  
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
		double actualValue;
		double forecastValue;
		double prevValue;
		int flag01;
		int flag02;
		
		//constructor
		Calend_rec(){
			eventDate = new Date();
			eventName = "";
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

        // JSoup Example 2 - Reading HTML page from URL
        Document doc = null;
        String title = "";
        try {
            //doc = Jsoup.connect("https://www.forexfactory.com/calendar.php").proxy("proxy.lan",8080).get();
            doc = Jsoup.connect("https://www.forexfactory.com/calendar.php").get();
            //doc = Jsoup.connect("http://www.ya.ru").get();
            title = doc.title();
        } catch (IOException e) {
            e.printStackTrace();
        }
  
        System.out.println("Jsoup Can read HTML page from URL, title : " + title+"\n");
        
        Elements rows = doc.getElementsByClass("calendar__row");
        Date tmpDate = new Date();
        //Elements cells = doc.select("calendar__event-title");
        Element el;
        for (Element row : rows) {
        	Elements cells = row.getElementsByClass("calendar__cell");
        	tmpDate = null;
        	for (Element cell:cells){
        		el = null;
        		if (cell.hasClass("date")){ //calendar__event
        			tmpDate = Date.parse(cell.getElementsByClass("date").html());
        			System.out.println("date : " + cell.getElementsByClass("date").html());
        		}
        		if (cell.hasClass("event")){ //calendar__event
        			System.out.println("event : " + cell.getElementsByClass("calendar__event-title").html());
        		}
        		if (cell.hasClass("forecast") ){
        			System.out.println("    forecast : " + cell.html());
        		}
        		if (cell.hasClass("previous") ){
        			System.out.println("    prev : " + cell.html());
        			el = cell.selectFirst("span:nth-child(1)");
        			if (el != null){
        				i_ = el.html().indexOf("<span");
        				if (i_ > 0){
        					System.out.println("    span : " + el.html().substring(0, i_));
        				} else System.out.println("    span : " + el.html());
        			}
        		}
        		if (cell.hasClass("actual") && cell.html() != null){ //calendar__event
        			
        			//System.out.println("  data : " + cell.html());
        			el = cell.selectFirst("span.better");
        			if (el != null){
        				System.out.println("    better : " + el.html());
        			} else {
        				el = cell.selectFirst("span.worse");
        				if (el != null){
        					System.out.println("    worse : " + el.html());
        				} else System.out.println("    values : " + cell.html());
        			}
       			
        		} //actual
        			//System.out.println("height : " + cell.attr("height"));
        			//System.out.println("width : " + cell.attr("width"));
        			//System.out.println("alt : " + cell.attr("alt"));
        	}
        }
        

        
  
  
    }
}