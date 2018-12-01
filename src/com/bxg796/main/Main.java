package com.bxg796.main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Main {
	
	private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM");

	private UI clientUI;
	private Connection db;
	
	public Main() {

		Object holder = new Object();
		clientUI = new UI("Database Manager", holder);
		db = null;
		try {
			print("Trying to connect...");
			db = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
			print("Connected.");
			
			
			String report = partyReport(Integer.parseInt(clientUI.readLine()));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true) {
			try {
				String input = clientUI.readLine();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		

	}
	
	private String partyReport(Integer pid) throws SQLException {
		String report = "";
		//@formatter:off
		PreparedStatement ps = db.prepareStatement(
				"SELECT\n" + 
				"	Party.pid,\n" + 
				"	Party.name,\n" + 
				"	Venue.name,\n" + 
				"	Menu.description,\n" + 
				"	Entertainment.description,\n" + 
				"	Party.numberofguests,\n" + 
				"	Party.price,\n" + 
				"	Venue.venuecost+Entertainment.costprice+Menu.costprice*Party.numberofguests\n" + 
				"FROM Party\n" + 
				"INNER JOIN Venue ON Party.vid = Venue.vid\n" + 
				"INNER JOIN Menu ON Party.mid = Menu.mid\n" + 
				"INNER JOIN Entertainment ON Party.eid = Entertainment.eid\n" + 
				"WHERE Party.pid = " + pid + ";");
		//@formatter:on
		
		CreateDatabase.printResultSet(ps.executeQuery());
		return report;
	}
	
	private void print(String message) {
		clientUI.tell(getTime() + ": " + message);
	}
	
	private String getTime() {
		Calendar cal = Calendar.getInstance();
		String time = TIME_FORMAT.format(cal.getTime());
		String date = DATE_FORMAT.format(cal.getTime());
		return ("[" + date + " | " + time + "]");
	}
	
	
	public static void main(String[] args) {
		Main m = new Main();
	}

}
