import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

public class db_work {
	public static Connection conn;
	public static Statement statmt;
	public static ResultSet resSet;
	
	public static void Conn() throws ClassNotFoundException, SQLException 
	{
	   conn = null;
	   Class.forName("org.sqlite.JDBC");
	   conn = DriverManager.getConnection("jdbc:sqlite:./db/calendar.s3db");
	   statmt = conn.createStatement();
	   
	   System.out.println("База Подключена!");
	}
	
	// --------Заполнение таблицы--------
	public static void sqlExec(String sqlCmd) throws SQLException
	{
		if (statmt != null){
			statmt.execute(sqlCmd);
		} else System.out.println("statmt - не определен (sqlExec error)");
	   	  
	   //System.out.println("Таблица заполнена");
	}
	
	public static void select(String selectCmd) throws ClassNotFoundException, SQLException
	{
		if (statmt != null){
			resSet = statmt.executeQuery(selectCmd);
		} else System.out.println("statmt - не определен (select error)");
		
		
		/*while(resSet.next())
		{
			int id = resSet.getInt("id");
			String  name = resSet.getString("name");
			String  phone = resSet.getString("phone");
	         System.out.println( "ID = " + id );
	         System.out.println( "name = " + name );
	         System.out.println( "phone = " + phone );
	         System.out.println();
		}	*/
		
		//System.out.println("Таблица выведена");
	}
	
	
	public void run_db()throws ClassNotFoundException, SQLException{
		db_work.Conn();
   	
	    long t = System.currentTimeMillis();
	    db_work.sqlExec("insert into currency values(2,'EUR','Euro union currency')");
	    db_work.sqlExec("insert into event values(2,'testEvent2', 1)");
	    db_work.sqlExec("insert into event values(3,'testEvent3', 2)");
	    
	    String sqlCheckEvent = "select case c.actual_value when is null then 0 else 1 end as res"+
	                           "from calendar c join event e on e.event_id = c.event_id "+
	                           "join currency cu on cu.currency_id = c.currency_id "+
	    		               "where c.short_name = ? and e.name = ? and c.date = ? and c.time = ? ";
	    
	    
	    PreparedStatement pstmt = db_work.conn.prepareStatement(sqlCheckEvent);
	    
	    pstmt.setString(1, "USD");
	    pstmt.setString(2, "EventName1");
	    pstmt.setDate(3, Date.valueOf("28.11.2017"));
	    pstmt.setTime(4, Time.valueOf("14:45"));
	    
	    db_work.resSet = pstmt.executeQuery();
	    
	    //db_work.select(sqlCheckEvent);
	    
	    
	    while(db_work.resSet.next())
	    {
	    	/*int id = db_work.resSet.getInt("id");
	    	String  name = db_work.resSet.getString("name");
	    	String  phone = db_work.resSet.getString("phone");*/
		}
	    
	    System.out.println("run time (ms) : " + (System.currentTimeMillis() - t));
	
	}
	
}
