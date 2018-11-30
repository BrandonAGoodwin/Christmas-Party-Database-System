package com.bxg796.main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
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
			Random randGenerator = new Random();
			Date date = new Date(System.currentTimeMillis() + (Math.abs(randGenerator.nextLong()) % (1L * 365 * 24 * 60 * 60 * 1000)));
			
			
			print("Trying to connect...");
			db = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
			print("Connected.");
			
			
			
		} catch (SQLException e) {
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
	
	private String partyReport(Integer pid) {
		String report = "";
		
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
