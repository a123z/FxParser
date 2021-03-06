//+------------------------------------------------------------------+
//|                                                 FundAnalysis.mq5 |
//|                                        Copyright 2017, SoftLab19 |
//|                                      https://www.rrater.narod.ru |
//+------------------------------------------------------------------+
#property copyright "Copyright 2017, SoftLab19"
#property link      "https://www.rrater.narod.ru"
#property version   "1.00"
//+------------------------------------------------------------------+
//| Include                                                          |
//+------------------------------------------------------------------+
#include <Expert\Expert.mqh>
//--- available signals
#include <Expert\Signal\SignalTEMA.mqh>
//--- available trailing
#include <Expert\Trailing\TrailingFixedPips.mqh>
//--- available money management
#include <Expert\Money\MoneyFixedLot.mqh>
//+------------------------------------------------------------------+
//| Inputs                                                           |
//+------------------------------------------------------------------+
//--- inputs for expert
input string             Expert_Title                  ="FundAnalysis"; // Document name

input double             Signal_StopLevel              =40.0;           // Stop Loss level (in points)
input double             Signal_TakeLevel              =60.0;           // Take Profit level (in points)

//--- inputs for trailing
input int                Trailing_FixedPips_StopLevel  =30;             // Stop Loss trailing level (in points)
input int                Trailing_FixedPips_ProfitLevel=50;             // Take Profit trailing level (in points)
//--- inputs for money
input double             Order_Lot         =0.1;            // Fixed volume

datetime lastBarTime;
ulong                    Expert_MagicNumber            =112017;         // 
int    Server_GMT = 7200;//seconds  -2 GMT = - 2 * 60 * 60 = -7200;
int cnt1 = 0;

struct CalendarRecord{
   string Currency;
   string EventName;
   datetime EventDateTime;
   double FactValue;
   double ForecastValue;
   double PrevValue;
   int flag_1;
   int flag_2;
   ulong order_no;
};

CalendarRecord Calendar[30];

//-----------Initialization function of the expert                         
int OnInit()
{
  //load from file
    //string FileName = "c:\\temp\\usd_news.csv";
 /*   string FileName = "usd_news.csv";
    if(!FileIsExist(FileName,FILE_COMMON))
    {
      printf(FileName+" not found");
      return(INIT_FAILED);
    }
    int FileHandle = FileOpen(FileName,FILE_READ|FILE_CSV|FILE_COMMON|FILE_ANSI,';');
    if(FileHandle<0) 
    { 
      Print("Неудачная попытка открыть файл "+FileName); 
      Print("Код ошибки ",GetLastError()); 
    }
    int col = 0;
    int noRec = 0;
    string str;
    string str2;
    CalendarRecord tmpCR;

    while(!FileIsEnding(FileHandle)) 
    { 
      if(col>=7)
      {
        col = 0;
        noRec++; 
        if (noRec>=ArraySize(Calendar))
        {
          col++;
          str = FileReadString(FileHandle); 
          Print("Превышение размера массива финансовых событий");
          break;
        }
      }
      col++;
      str = FileReadString(FileHandle); 
      //Print(IntegerToString(noRec)+"=rec  col="+IntegerToString(col)+" "+str);
      if (noRec>0){ //первую строчку с описанием колонок пропустили теперь заполняем массив
        switch(col){
          case 1: tmpCR.Currency = str;
                  break;
          case 2: tmpCR.EventName = str;
                  break;
          case 3: tmpCR.EventDateTime = GetDateWithTimeZone(str);
                  //Print(IntegerToString(noRec)+" dbg  "+str2+" "+str);
                  break;
          case 4: tmpCR.FactValue = StringToDouble(str);
                  break;
          case 5: tmpCR.ForecastValue = StringToDouble(str);
                  break;
          case 6: tmpCR.PrevValue = StringToDouble(str);
                  break;
          case 7: tmpCR.flag_1 = StringToInteger(str);
                  break;
          case 8: tmpCR.flag_2 = StringToInteger(str);
                  tmpCR.order_no = 0;
                  break;
        }
      }

      Calendar[noRec-1] = tmpCR;

    } //while end
    FileClose(FileHandle);
    Print("Calendar load successful");
*/
    EventSetTimer(10);//каждые 10 секунд будем запускать проверку наступления события
    //--- ok.
    Calendar[1].EventName = "";
    if (Calendar[1].EventName == "") Print("== Equal"); else  Print("!= Equal");
    return(INIT_SUCCEEDED);
}// init end
//+------------------------------------------------------------------+
//| Deinitialization function of the expert                          |
//+------------------------------------------------------------------+
void OnDeinit(const int reason)
{
   
}
//+------------------------------------------------------------------+
//| "Tick" event handler function                                    |
//+------------------------------------------------------------------+
void OnTick()
{
   
}
//+------------------------------------------------------------------+
//| "Trade" event handler function                                   |
//+------------------------------------------------------------------+
void OnTrade()
{

}
//+------------------------------------------------------------------+
//| "Timer" event handler function                                   |
//+------------------------------------------------------------------+
void OnTimer()
{
  int i;
  loadArrayFromFile("fx_calend.csv");

  //проверим массив
  for (i=0; i<ArraySize(Calendar); i++)
  {
    //Print("Arr " +DoubleToString(Calendar[i].FactValue )+" * " +IntegerToString(Calendar[i].order_no) + IntegerToString(i));
    if (Calendar[i].FactValue !=0 && Calendar[i].order_no == 0)
    {
      Print("need open " + IntegerToString(i));
      OpenPosition(i);
    }
  }
  
    
  //почистим массив 
  if (cnt1 > 0) //чистим раз в 5 раз ~50 sec
  {
    cnt1--;
    return;
  } else cnt1 = 5;

  for (i = 0; i<ArraySize(Calendar); i++)
  {
    if (TimeTradeServer() - Calendar[i].EventDateTime > 60*5) //время события старее 5 минут
    {
      Calendar[i].Currency = "";
      Calendar[i].EventName = "";
      Calendar[i].FactValue = 0;
      Calendar[i].ForecastValue = 0;
      Calendar[i].PrevValue = 0;
      Calendar[i].flag_1 = 0;
      Calendar[i].flag_2 = 0;
      Calendar[i].order_no = 0;
    }
  }
    

  /*int eventId = FindEvent(lastBarTime, "USD","Unemployment Claims");
  if(eventId>=0)
  {
    Print(TimeToString(lastBarTime));
    OpenPosition(eventId);
  } */
}
  
/*bool CExpert::Processing()
{
  Print("test");
  return(false);
}  
  */
  
//===========================================================================  
int FindEvent(datetime _eventDateTime, string _currency, string _eventName)  {
  for(int i=0;i<ArraySize(Calendar);i++){
    if (Calendar[i].Currency == _currency){
      
      if((Calendar[i].Currency == _currency)&&
         (Calendar[i].EventName == _eventName)&&
         (Calendar[i].EventDateTime == StringToTime(TimeToString(_eventDateTime)))&&
         (Calendar[i].FactValue != 0)&&
         (Calendar[i].order_no == 0)//ордер по этому событию не открыт - как при нескольких ордерах по событию?
        )
      {
        Print(TimeToString(Calendar[i].EventDateTime)+" - "+TimeToString(_eventDateTime)+" N"+DoubleToString(Calendar[i].FactValue));
        return(i);
      }
    }
  }
  return(-1);
}

//===========================================================================
ulong OpenPosition(int _calendarID){
  MqlTradeRequest request={0};
  MqlTradeResult  result={0};
  Print("open pos");
  //--- параметры запроса

  string symb[] = {"EURGBP","EURUSD","GBPUSD"};
  int symb_pos = -1;
  int symb_dir = 0;
  ulong res=0; //возможно сделать строкой чтоб можно было записать более 1 значения
  //работаем с 3 валютами - смотрим на 3 пары

  for (int i=0; i < ArraySize(symb); i++){
    symb_pos = StringFind(symb[i],Calendar[_calendarID].Currency);
    if (symb_pos<0) continue; //событие не влияет на пару - пропускаем
    request.action   = TRADE_ACTION_DEAL;                     // тип торговой операции
    request.symbol   = symb[i];//Symbol();                    // символ
    request.volume   = 0.1;                                   // объем в 0.1 лот
    request.deviation= 5;                                     // допустимое отклонение от цены
    request.magic    = Expert_MagicNumber;                          // MagicNumber ордера
    request.comment  = "calR";

    if(Calendar[_calendarID].flag_1 == 0){
      if(Calendar[_calendarID].FactValue > Calendar[_calendarID].ForecastValue)
      {
        symb_dir = 1;
      } else if(Calendar[_calendarID].FactValue < Calendar[_calendarID].ForecastValue)
             {
               symb_dir = -1;
             }
    } else symb_dir = Calendar[_calendarID].flag_1;

    if (symb_pos>0) symb_dir *= -1; //если к этой валюте пара обратная - перевернём
     
    if(symb_dir > 0){ 
      request.type  = ORDER_TYPE_BUY;                        // тип ордера
      request.price = SymbolInfoDouble(symb[i],SYMBOL_ASK); // цена для открытия
      request.sl    = request.price - Signal_StopLevel*SymbolInfoDouble(symb[i],SYMBOL_POINT);
      request.tp    = request.price + Signal_TakeLevel*SymbolInfoDouble(symb[i],SYMBOL_POINT);
    }
    if(symb_dir < 0){ 
      request.type  = ORDER_TYPE_SELL;                        // тип ордера
      request.price = SymbolInfoDouble(symb[i],SYMBOL_BID); // цена для открытия
      request.sl    = request.price + Signal_StopLevel*SymbolInfoDouble(symb[i],SYMBOL_POINT);
      request.tp    = request.price - Signal_TakeLevel*SymbolInfoDouble(symb[i],SYMBOL_POINT);
    }
    
    //--- отправка запроса
    
    if(!OrderSend(request,result)){
        res = GetLastError();
        PrintFormat("OrderSend error %d",res);     // если отправить запрос не удалось, вывести код ошибки
    } else {
             res = result.deal;
             Calendar[_calendarID].order_no = result.deal; //или order?
             PrintFormat("Order Open # %I64u",result.order);
           }
    //--- информация об операции
    PrintFormat("retcode=%u  deal=%I64u  order=%I64u",result.retcode,result.deal,result.order);
  } //foreach(symb)
  return(res);
}

//===========================================================================
//переводит из даты с зоной(из файла от парсера) в дату сервера с учётом зоны сервера
datetime GetDateWithTimeZone(string dateStr){
  string tStr = StringSubstr(dateStr, StringLen(dateStr)-5, 5);
  //convert minutes and hours to seconds
  int tMin = StringToInteger(StringSubstr(tStr, 3, 2))*60;
  int tHour = StringToInteger(StringSubstr(tStr, 1, 2))*60*60;
  datetime tDT = StringToTime(StringSubstr(dateStr, 0,StringLen(dateStr)-6));
  //to GMT 0
  switch ((char)StringSubstr(dateStr, StringLen(dateStr)-5, 1)){
    case '+': tDT -= (tMin+tHour);
              break;
    case '-': tDT += (tMin+tHour);
              break;
  }
  //to Server time
  tDT += Server_GMT;
  return tDT;
}

//===========================================================================
bool loadArrayFromFile(string fileName)
{
  if(!FileIsExist(fileName,FILE_COMMON)){
    //printf(fileName+" not found");
    return(false);
  }
  int FileHandle = FileOpen(fileName,FILE_READ|FILE_CSV|FILE_COMMON|FILE_ANSI,';');
  if(FileHandle<0) 
  { 
    Print("Неудачная попытка открыть файл "+fileName); 
    Print("Код ошибки ",GetLastError()); 
    return(false);
  }

  int col = 0;
//  int noRec = 0;
  string str;
  string str2;
  CalendarRecord tmpCR;
  while(!FileIsEnding(FileHandle)) 
  { 
/*    if(col>=8){
      col = 0;
      do 
      {
        noRec++; 
        if (noRec>=ArraySize(Calendar)){
          break;
        }
      } while (Calendar[noRec-1].EventName != "");
      if (noRec>=ArraySize(Calendar)){
          //col++; 
          //str = FileReadString(FileHandle); 
          Print("Превышение размера массива финансовых событий");
          //FileClose(FileHandle);
          //return(false);
          break; //не выдаём ошибку загрузки файла, чтоб обработались загруженные записи
      }
    }*/
    col++;
    str = FileReadString(FileHandle); 
    //Print("col="+IntegerToString(col)+" "+str);
    //if (noRec>0){ //первую строчку с описанием колонок пропустили теперь заполняем массив
      switch(col){
        case 1: tmpCR.Currency = str;
                break;
        case 2: tmpCR.EventName = str;
                break;
        case 3: tmpCR.EventDateTime = GetDateWithTimeZone(str);
                //Print(IntegerToString(noRec)+" dbg  "+str2+" "+str);
                break;
        case 4: tmpCR.FactValue = StringToDouble(str);
                break;
        case 5: tmpCR.ForecastValue = StringToDouble(str);
                break;
        case 6: tmpCR.PrevValue = StringToDouble(str);
                break;
        case 7: tmpCR.flag_1 = StringToInteger(str);
                break;
        case 8: tmpCR.flag_2 = StringToInteger(str);
                tmpCR.order_no = 0;
                break;
      }
    //}
    if (col >= 8)  //считали всю строчку проверим на задвоение и запишем в массив
    {
      if (FindEvent(tmpCR.EventDateTime, tmpCR.Currency, tmpCR.EventName) < 0)
      {
        int i=0;
        for (i=0; i < ArraySize(Calendar); i++) //ищем пустую строчку массива для записи
        {
          if (Calendar[i].EventName == "") break;
        }
        if (i < ArraySize(Calendar))
        {
          Calendar[i] = tmpCR;
        } else {
                 Print("Превышение размера массива финансовых событий");
                 break; //не выдаём ошибку загрузки файла, чтоб обработались загруженные записи
               }
      } else Print("Найден дубликат записи - запись пропущена: "+TimeToString(tmpCR.EventDateTime) + " " + tmpCR.Currency + " " + tmpCR.EventName);
      col = 0;
    }
  }
  FileClose(FileHandle);
  Print("Calendar load successful");
  if (FileDelete(fileName, FILE_COMMON))
  {
    Print("File " + fileName + " deleted");
  } else {
           Print("File " + fileName + " NOT deleted");
           PrintFormat("Ошибка, код = %d",GetLastError()); 
         }
  return(true);
}
//+------------------------------------------------------------------+
