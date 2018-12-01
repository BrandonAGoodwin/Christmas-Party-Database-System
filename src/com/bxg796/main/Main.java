package com.bxg796.main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.postgresql.util.PSQLException;

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
			String choice;
			boolean quit = false;
			while(!quit) {
				try {
					clientUI.tell("Would you like to:\n"
							+ "\t1. Generate a report on a party?\n"
							+ "\t2. Generate a report on a menu?\n"
							+ "\t3. Add a new party?\n"
							+ "\t4. Print a table?\n"
							+ "\t5. Quit\n"
							+ "(Please input your option number)");
					choice = clientUI.readLine();
					switch (choice) {
						case "1": partyReport(); break;
						case "2": menuReport();	break;
						case "3": addToPartyTable(); break;
						case "4": printTable(); break;
						case "5": quit = true; break;
						default	: clientUI.tell("<Invalid option please try again>"); break;
					}
				} 
				catch (InterruptedException e) { /*Continue*/ }
				catch (IOException e) { /*Continue */ }
			}

		} catch (SQLException e) { e.printStackTrace(); }
		
		try {
         	if(!db.isClosed()) {
         		print("Closing connection.");
         		db.close(); 
         	}
        } catch (SQLException e) {
            e.printStackTrace();
        }

	}
	
	private void partyReport() throws SQLException, InterruptedException, IOException {
		print("Please input the pid for the party you wish you get a report on:");
		String stringInput = clientUI.readLine();
		try {
			PreparedStatement ps = db.prepareStatement(
					"SELECT\n" + 
					"	Party.pid,\n" + 
					"	Party.name,\n" + 
					"	Venue.name AS venue,\n" + 
					"	Menu.description AS menu,\n" + 
					"	Entertainment.description AS entertainment,\n" + 
					"	Party.numberofguests,\n" + 
					"	Party.price,\n" + 
					"	Venue.venuecost+Entertainment.costprice+Menu.costprice*Party.numberofguests AS profit\n" + 
					"FROM Party\n" + 
					"INNER JOIN Venue ON Party.vid = Venue.vid\n" + 
					"INNER JOIN Menu ON Party.mid = Menu.mid\n" + 
					"INNER JOIN Entertainment ON Party.eid = Entertainment.eid\n" + 
					"WHERE Party.pid = " + stringInput + ";", ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);
			clientUI.tell("\n" + CreateDatabase.getResultSetString(ps.executeQuery()));

		} catch (PSQLException e) {
			clientUI.tell("<ERROR: Invalid PID>\n");
		}
		
	}
	
	private void menuReport() throws SQLException, InterruptedException, IOException {
		clientUI.tell("Please input the mid for the menu you wish you get a report on:");
		String stringInput = clientUI.readLine();
		try {
			PreparedStatement ps = db.prepareStatement(
					"SELECT * FROM\n" + 
					"	(SELECT\n" + 
					"		Menu.mid,\n" + 
					"		Menu.description,\n" + 
					"		Menu.costprice,\n" + 
					"		COUNT(Menu.mid),\n" + 
					"		SUM(Party.numberofguests)\n" + 
					"	FROM Menu\n" + 
					"	LEFT JOIN Party ON Party.mid = Menu.mid\n" + 
					"	GROUP BY Menu.mid, Menu.description, Menu.costprice) AS sub\n" + 
					"WHERE sub.mid = " + stringInput + ";", ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);
			clientUI.tell("\n" + CreateDatabase.getResultSetString(ps.executeQuery()));
		
		} catch (PSQLException e) {
			clientUI.tell("<ERROR: Invalid MID>\n");
		}
	}
	
	private void addToPartyTable() throws InterruptedException, IOException, SQLException {
		try {
			clientUI.tell("Please input the pid:");
			Integer pid = Integer.parseInt(clientUI.readLine());
			clientUI.tell("Please input the party name:");
			String name = clientUI.readLine();
			clientUI.tell("Please input the mid of the menu to be used:");
			Integer mid = Integer.parseInt(clientUI.readLine());
			clientUI.tell("Please input the vid of the venue to be used:");
			Integer vid = Integer.parseInt(clientUI.readLine());
			clientUI.tell("Please input the eid of the entertainment to be used:");
			Integer eid = Integer.parseInt(clientUI.readLine());
			clientUI.tell("Please input the quoted price in pounds in the form XXXX.XX:");
			String priceString = clientUI.readLine();
			Integer price = Integer.parseInt(priceString.replaceAll("[^0-9]", ""));
			clientUI.tell("Please input the date of the party [Format=YYYY-MM-DD]:");
			String date = clientUI.readLine();
			clientUI.tell("Please input the time of the party [Format=HH:MM:SS]:");
			String time = clientUI.readLine();
			String timing = date.concat(" " + time);
			clientUI.tell("Please input the number of guests that will be attending:");
			Integer numberofguests = Integer.parseInt(clientUI.readLine());
			
			CreateDatabase.getAddToPartyTableStatement(db, pid, name, mid, vid, eid, price, timing, numberofguests).execute();
			
			clientUI.tell("The party '" + name + "' has been successfully added to the table!");
		
		}
		catch (NumberFormatException e) { clientUI.tell("<ERROR: The input must be an integer>"); }
		catch (PSQLException e) {
			clientUI.tell("<" + e.getMessage() + ">\n");
			clientUI.tell("<Input formats:\n\tpid:+integer | name:length<=30 | mid:+integer | vid:+integer | eid:+integer | price:+integer('.'is allowed) | date:format='YYYY-MM-DD' | time:format='HH:MM:SS' | numberofguests:+integer>\n\n");
		}
		
	}

	
	private void printTable() throws SQLException, InterruptedException, IOException {
		clientUI.tell("What table would you like to print?:");
		String tableName = clientUI.readLine();
		try {
			ResultSet rs = db.prepareStatement("SELECT * FROM " + tableName + ";", ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE).executeQuery();
			clientUI.tell(CreateDatabase.getResultSetString(rs));
		} catch(PSQLException e) {
			clientUI.tell("<ERROR: Table does not exist>");
		}
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
		@SuppressWarnings("unused")
		Main m = new Main();
	}

}
