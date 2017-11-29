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
            doc = Jsoup.connect("https://www.forexfactory.com/calendar.php").proxy("proxy.lan",8080).get();
            //doc = Jsoup.connect("https://www.forexfactory.com/calendar.php").get();
            //doc = Jsoup.connect("http://www.ya.ru").get();
            title = doc.title();
        } catch (IOException e) {
            e.printStackTrace();
        }
  
        System.out.println("Jsoup Can read HTML page from URL, title : " + title+"\n");
        
        Elements rows = doc.getElementsByClass("calendar__row");
        String tmpStrDate = "";
        Date tmpDate = Calendar.getInstance().getTime();
        //System.out.println("current date " + new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        Element el;
        for (Element row : rows) {
        	Elements cells = row.getElementsByClass("calendar__cell");
        	for (Element cell:cells){
        		el = null;
        		if (cell.hasClass("date")){ //calendar__event
        			//String target = "Thu Sep 28 20:29:30 JST 2000";
        		    //DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
        		    //Date result =  df.parse(target); 
        			
        			//mpDate = Date.parse(cell.getElementsByClass("date").html());
        			if (cell.select("span:nth-child(1)").size()>1){
        				Element ell = cell.select("span:nth-child(1)").get(1);
        				if (ell != null){
        					//System.out.println("date: " + ell.text());
        					tmpStrDate = ell.text() + " 2017";
        				}
        			}
        			//System.out.println("date : " + cell.getElementsByClass("date").html() + " t="+cell.getElementsByClass("date").text() );
        			//System.out.println("date t: " + cell.getElementsByClass("date").text() );

        			//System.out.println("date to: " + cell.text() + " to=" + cell.ownText());
        		}
        		
        		if (cell.hasClass("time") ){
        			if (cell.text().length() > 0){
        				//System.out.println("    time s: " + cell.text().length());
        				//System.out.println("    time: " + cell.text());
        				if (tmpStrDate == "") {
        					System.out.println("ERROR date is null!!!");
        					tmpStrDate = new SimpleDateFormat("MMM d yyyy").format(Calendar.getInstance().getTime()); 		
        				}
        				try {
        					tmpDate = (new SimpleDateFormat("MMM d yyyy h:mma Z", Locale.ENGLISH)).parse(tmpStrDate + " " +cell.text() + " -0500");
        				} catch (ParseException e) {
        					System.out.println("Date Parser problem (Friend.java): " + e.toString());
        					e.printStackTrace();
        				}
        			}
        		}
        		
        		if (cell.hasClass("event")){ //calendar__event
        			System.out.println("event : " + cell.getElementsByClass("calendar__event-title").text());
        		}
        		//=================forecast
        		if (cell.hasClass("forecast") ){
        			System.out.println("    forecast : " + cell.text());
        		}
        		
        		if (cell.hasClass("previous") ){
        			System.out.println("    prev : " + cell.text());

        			/*el = cell.selectFirst("span:nth-child(1)");
        			if (el != null){
        				i_ = el.html().indexOf("<span");
        				if (i_ > 0){
        					System.out.println("    span : " + el.html().substring(0, i_));
        				} else System.out.println("    span : " + el.html());
        			}*/
        		}
        		
        		
        		if (cell.hasClass("actual") && cell.html() != null){ //calendar__event
        			System.out.println("    act: " + cell.html());
        			el = cell.selectFirst("span.better");
        			if (el != null){
        				System.out.println("    better : " + el.text());
        			} else {
        				el = cell.selectFirst("span.worse");
        				if (el != null){
        					System.out.println("    worse : " + el.text());
        				} else System.out.println("    values : " + cell.text());
        			}
       			
        		} //actual
        		
        		
        		
        		if (cell.hasClass("currency") ){
        			System.out.println("    currency: " + cell.text());
        		}
        			//System.out.println("height : " + cell.attr("height"));
        			//System.out.println("width : " + cell.attr("width"));
        			//System.out.println("alt : " + cell.attr("alt"));
        	}//cells
        	System.out.println("    date: " + tmpDate.toString());
        }//rows
        

        
  
  
    }
}