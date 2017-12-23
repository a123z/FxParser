//import java.io.File;
import java.io.*;
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
	
	public static boolean need2File = false;
	
	//public static String FilePath = "C:\\temp\\";
	public static String FilePath = "C:\\Users\\User\\AppData\\Roaming\\MetaQuotes\\Terminal\\Common\\Files\\";
	
	public static String EventFileName = "fx_calend.csv";
	
	public static Calend_rec[] calendar = new Calend_rec[30]; //здесь будут ближайшие записи
  
    public static void main(String args[]){
    	
    	
        //init array
    	int i_;
    	for (i_ = 0; i_ < calendar.length; i_++){
    		calendar[i_] = new Calend_rec();
    	}

    	
    	System.out.println("Run parser (lab19)");
        //parseUrl("https://www.forexfactory.com/calendar.php", "proxy.lan", 8080);
        parseUrl("https://www.forexfactory.com/calendar.php", "", 0);
        
        /*parseUrl("https://www.forexfactory.com/calendar.php?range=jun1.2017-jul1.2017", "", 0);
        parseUrl("https://www.forexfactory.com/calendar.php?range=jul2.2017-aug1.2017", "", 0);
        parseUrl("https://www.forexfactory.com/calendar.php?range=aug2.2017-sep1.2017", "", 0);
        parseUrl("https://www.forexfactory.com/calendar.php?range=sep2.2017-oct1.2017", "", 0);
        parseUrl("https://www.forexfactory.com/calendar.php?range=oct2.2017-nov1.2017", "", 0);
        parseUrl("https://www.forexfactory.com/calendar.php?range=nov2.2017-dec2.2017", "", 0);
        */
        
        
        //"?range=jun25.2017-aug22.2017"
        
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
            return;
        }

        System.out.println("###   "+doc.getElementsByClass("calendar__options").select("span").text());
        //System.out.println("Jsoup Can read HTML page from URL, title : " + title+"\n");

        //if (1==1) return;
        Elements rows = doc.getElementsByClass("calendar__row");
        String tmpStrDate = "";
        String tmpStrTime = "";
        String tmpStr = "";
        Date tmpDate = Calendar.getInstance().getTime();
        //System.out.println("current date " + new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        Element el;
        need2File = false;
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
        			if (cell.text().length() > 0){
        				//System.out.println("dbg: " + tmpStrDate+" ** "+cell.text());
        				if (tmpStrDate == "") {
        					System.out.println("ERROR date is null!!!");
        					tmpStrDate = new SimpleDateFormat("MMM d yyyy").format(Calendar.getInstance().getTime()); 		
        				} 
        				//если время весь день то возьмём текущее время - вдруг данные появились
        				if (cell.text().equals("All Day") || cell.text().equals("Tentative")){
        					SimpleDateFormat df = new SimpleDateFormat("h:mma"); 
        					df.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
        					tmpStrTime = df.format(Calendar.getInstance().getTime());
        				} else tmpStrTime = cell.text();
        				//System.out.println("dbg: " + tmpStrDate+" ** "+cell.text());
        				try {
        					tmpDate = (new SimpleDateFormat("MMM d yyyy h:mma Z", Locale.ENGLISH)).parse(tmpStrDate + " " + tmpStrTime + " -0500");
        				} catch (ParseException e) {
        					System.out.println("!!!!  Date Parser problem: " + e.toString());
        					//e.printStackTrace();
        				}
        			}
        		}
        		
        		if (cell.hasClass("impact")){
        			//System.out.println("impact: " + cell.html());
        			if (cell.select("span").hasClass("low")){
        				//System.out.println("impact: low");
        				aaa.flag02 = 1;
        			} else if (cell.select("span").hasClass("medium")){
        				//System.out.println("impact: med");
        				aaa.flag02 = 2;
        				} else if (cell.select("span").hasClass("high")){
        					//System.out.println("impact: hi");
        					aaa.flag02 = 3;
        					} else aaa.flag02 = 0;
        			
        		}
        		
        		if (cell.hasClass("event")){ //calendar__event
        			//System.out.println("event : " + cell.getElementsByClass("calendar__event-title").text());
        			aaa.eventName = cell.getElementsByClass("calendar__event-title").text();
        		}
        		//=================forecast
        		if (cell.hasClass("forecast") ){
        			try{
        				aaa.forecastValue = Double.parseDouble(regStr2Num(cell.text()));
        			} catch(NumberFormatException e) {
        				aaa.forecastValue = 0;
        			}
        			//System.out.println("    forecast : " + cell.text() + " parsed=" + aaa.forecastValue);
        		}
        		//=================previous
        		if (cell.hasClass("previous") ){
        			try{
        				aaa.prevValue = Double.parseDouble(regStr2Num(cell.text()));
        			} catch(NumberFormatException e) {
        				aaa.prevValue = 0;
        			}
        			//System.out.println("    prev : " + cell.text() + " parsed=" + aaa.prevValue);
        		}
        		
        		//=================actual
        		if (cell.hasClass("actual") && cell.html() != null){ //calendar__event
        			try{
        				aaa.actualValue = Double.parseDouble(regStr2Num(cell.text()));
        			} catch(NumberFormatException e) {
        				aaa.actualValue = 0;
        			}
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
        		//System.out.println(aaa.currName +" "+ new SimpleDateFormat("MM/dd/yyyy HH:mm z").format(tmpDate) + 
        		//		" event=" + aaa.eventName + " av=" + aaa.actualValue + " fv="+aaa.forecastValue+" pv="+aaa.prevValue + " fl1=" + aaa.flag01+ " fl2=" + aaa.flag02);
        		addRec(aaa);
        		//add2file(aaa);
        	}
        	//System.out.println("    date: " + tmpDate.toString());
        }//rows
        System.out.println("\nData from array:");
        for (int i=0; i<calendar.length; i++)
        {
        	if (!calendar[i].eventName.equals("")) System.out.println("ca "+calendar[i].eventName);
        }
        
        if (need2File){
        	try(FileWriter writer = new FileWriter(FilePath+EventFileName, false))
            {
        		//writer.write("forex calendar parser result\n");
        		for (int i=0; i<calendar.length; i++){
        			tmpDate.setTime(Calendar.getInstance().getTimeInMillis() + 1000*60*60*1); //1 hours as milliseconds
        			 
            		if (calendar[i].eventName != "" 
            			&& Math.abs(Calendar.getInstance().getTimeInMillis() - calendar[i].eventDate.getTime()) < 1000*60*5) //5 minutes in milliseconds
            		{
            			//currency;News;date;time;Fact;Forecast;Prev
            			//USD;Unemployment Claims;2017.11.09;18:30;239 000;232 000;229 000
            			writer.write(calendar[i].currName);
            			writer.append(';');
            			writer.write(calendar[i].eventName);
            			writer.append(';');
            			writer.write(new SimpleDateFormat("yyyy.MM.dd HH:mm Z").format(calendar[i].eventDate)); //this format need for mql5 + process time zone
            			//writer.write(new SimpleDateFormat("MM-dd-yyyy HH:mm Z").format(calendar[i].eventDate));
            			writer.append(';');
            			writer.write(String.valueOf(calendar[i].actualValue));
            			writer.append(';');
            			writer.write(String.valueOf(calendar[i].forecastValue));
            			writer.append(';');
            			writer.write(String.valueOf(calendar[i].prevValue));
            			writer.append(';');
            			writer.write(String.valueOf(calendar[i].flag01));
            			writer.append(';');
            			writer.write(String.valueOf(calendar[i].flag02));
           			
            			writer.append('\n');
            			writer.flush();
            			
            			//add flag that record was saved to file? and check this flag early?
            		}
            	}
        		writer.close();
             
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            } 

        	
        }
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
    	boolean needAdd = true;
    	boolean newRec = true;
    	Date tmpDate = new Date();
    	
    	//если левая валюта не будем ничего добавлять
    	if (!(rec.currName.equalsIgnoreCase("USD") || rec.currName.equalsIgnoreCase("EUR") || rec.currName.equalsIgnoreCase("GBP") || rec.currName.equalsIgnoreCase("USD"))){
    		needAdd = false;
    		return;
    	}
 
    	//чистим массив событий
    	//tmpDate.setTime(rec.eventDate.getTime() - 1000*60*6); //6 minutes as milliseconds
    	tmpDate.setTime(Calendar.getInstance().getTimeInMillis() - 1000*60*6); //6 minutes as milliseconds
    	for (int i = 0; i < calendar.length; i++)
    	{
    		if (calendar[i].eventName != "" && tmpDate.after(calendar[i].eventDate))
    		{
    			calendar[i].eventName = "";
    		}
    	}
    	
    	////определим надо ли эту запись вставлять в массив
    	//событие не сильно старое
    	//tmpDate.setTime(rec.eventDate.getTime() - 1000*60*5); //5 minutes as milliseconds
    	tmpDate.setTime(Calendar.getInstance().getTimeInMillis() - 1000*60*5); //5 minutes as milliseconds
    	if (needAdd && tmpDate.after(rec.eventDate)){
    		needAdd = false;
    	} 
    	
    	//событие ещё 1 час(и более) не наступит
    	//tmpDate.setTime(rec.eventDate.getTime() + 1000*60*60*2); //2 hours as milliseconds
    	tmpDate.setTime(Calendar.getInstance().getTimeInMillis() + 1000*60*60*1); //1 hours as milliseconds
    	if (needAdd && tmpDate.before(rec.eventDate)){
    		needAdd = false;
    	}
    	
    
    	//такое событие уже есть в массиве?
    	if (needAdd){
    		for (int i = 0; i < calendar.length; i++){
    			//пока бежим по массиву ищем пустую строку для записи новой строчки
    			if (emptyRecId < 0 && calendar[i].eventName == ""){
    				emptyRecId = i;
    			}
    			
        		//нашли такое же событие
        		if (calendar[i].eventName == rec.eventName && 
        			calendar[i].currName  == rec.currName && 
        			calendar[i].eventDate == rec.eventDate)// && calendar[i].actualValue == rec.actualValue)
        		{
        			newRec = false;
        			if (calendar[i].actualValue == rec.actualValue){
        				needAdd = false;
        			}
        			if (calendar[i].actualValue == 0 && rec.actualValue != 0){
        				emptyRecId = i;
        			}
        		}
        	}
    	}
    	

    	if (needAdd){
    		if (emptyRecId>=0){
    			if (newRec){
    				calendar[emptyRecId].eventName = rec.eventName;
    				calendar[emptyRecId].currName = rec.currName;
    				calendar[emptyRecId].eventDate = rec.eventDate;
        			calendar[emptyRecId].forecastValue = rec.forecastValue;
        			calendar[emptyRecId].prevValue = rec.prevValue;
    			}
    			calendar[emptyRecId].actualValue = rec.actualValue;
    			calendar[emptyRecId].flag01 = rec.flag01;
    			calendar[emptyRecId].flag02 = rec.flag02;
    		} else System.out.println("НЕ НАШЛАСЬ ПУСТАЯ СТРОКА в массиве");
    	}
    	
    	if ((needAdd && newRec && rec.actualValue != 0) || (needAdd && !newRec)){
    		need2File = true;
    	}
    	
    
    }
    
    //выгрузка в файл полного календаря для тестера
    public static void add2file(Calend_rec rec){
    	int emptyRecId = -1;
    	boolean needAdd = true;
    	boolean newRec = true;
    	Date tmpDate = new Date();
    	
    	//если левая валюта не будем ничего добавлять
    	if ((rec.currName.equalsIgnoreCase("USD") || rec.currName.equalsIgnoreCase("EUR") || rec.currName.equalsIgnoreCase("GBP") || rec.currName.equalsIgnoreCase("USD"))){
    		try(FileWriter writer = new FileWriter(FilePath+"fx_calend_full.csv", true))
            {
        		//writer.write("forex calendar parser result\n");
        		
            		if (rec.eventName != "" && rec.actualValue > 0){
            			//currency;News;date;time;Fact;Forecast;Prev
            			//USD;Unemployment Claims;2017.11.09;18:30;239 000;232 000;229 000
            			writer.write(rec.currName);
            			writer.append(';');
            			writer.write(rec.eventName);
            			writer.append(';');
            			writer.write(new SimpleDateFormat("yyyy.MM.dd HH:mm Z", Locale.ENGLISH).format(rec.eventDate));
            			//writer.write(new SimpleDateFormat("MM-dd-yyyy HH:mm Z", Locale.ENGLISH).format(rec.eventDate));
            			writer.append(';');
            			writer.write(String.valueOf(rec.actualValue));
            			writer.append(';');
            			writer.write(String.valueOf(rec.forecastValue));
            			writer.append(';');
            			writer.write(String.valueOf(rec.prevValue));
            			writer.append(';');
            			writer.write(String.valueOf(rec.flag01));
            			writer.append(';');
            			writer.write(String.valueOf(rec.flag02));
           			
            			writer.append('\n');
            			writer.flush();
            		}
        		writer.close();
             
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            } 
    	}
    }
    
}