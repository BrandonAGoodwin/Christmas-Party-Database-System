package com.bxg796.main;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.postgresql.util.PSQLException;

public class CreateDatabase {
	
	private final ArrayList<String> tables;
	private final String URL = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/bxg796";
	private final String USERNAME = "bxg796";
	private final String PASSWORD =                                                                             		 "CartersJr10";
	
	private final Integer INITIAL_NO_OF_PARTIES = 1000;
	private final Integer INITIAL_NO_OF_ENTRIES = 100;
	
	private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM");

	Connection db;
	
	public static void main(String[] args) {
		CreateDatabase createDB = new CreateDatabase();
	}
	
	public CreateDatabase() {
		tables = new ArrayList<String>();
		initDatabase();
	}
	
	private void initDatabase() {
		db = null;
		try {
			//String username = System.console().readLine();
			//String password = new String(System.console().readPassword());

			print("Trying to connect... ");
			db = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			print("Connected to server.");
			
			//////////////////////////////////////////
			
			deleteTable(db, "Party");
			deleteTable(db, "Venue"); 
			deleteTable(db, "Menu");
			deleteTable(db, "Entertainment");
			
			///////////////////////////////////////////
			
			createNewVenueTable(INITIAL_NO_OF_ENTRIES);
			printResultSet(db.prepareStatement(
					"SELECT * FROM Venue;",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)
					.executeQuery());
			
			createNewMenuTable(INITIAL_NO_OF_ENTRIES);
			printResultSet(db.prepareStatement(
					"SELECT * FROM Menu;",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)
					.executeQuery());
			
			createNewEntertainmentTable(INITIAL_NO_OF_ENTRIES);
			printResultSet(db.prepareStatement(
					"SELECT * FROM Entertainment;",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)
					.executeQuery());
			
			createNewPartyTable(INITIAL_NO_OF_ENTRIES);
			printResultSet(db.prepareStatement(
					"SELECT * FROM Party;",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)
					.executeQuery());

			////////////////////////////////////////////
			
			print("Closing connection.");
			db.close();
		} catch(SQLException e) {
			e.printStackTrace();
		} 
		
		 try {
         	if(!db.isClosed()) {
         		print("Closing connection.");
         		db.close(); 
         	}
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	private void createNewVenueTable(int noOfEntries) throws SQLException {
		String makeVenueTableString = 
				"CREATE TABLE Venue (\r\n" + 
				"	vid				INTEGER		NOT NULL UNIQUE,\r\n" + 
				"	name			CHAR(15),\r\n" + 
				"	venuecost		INTEGER,\r\n" +  
				"	PRIMARY KEY (vid),\r\n" + 
				"	CHECK (venuecost>=0)\r\n" + 
				");";
		
		PreparedStatement makeVenueTable = db.prepareStatement(makeVenueTableString);
		print("Trying to create Venue table... ");
		
		try { makeVenueTable.execute(); } catch(PSQLException e) { System.out.println("Venue table already exists"); }
		
		print("Populating Venue... ");
		String venueTableName = "Venue";
		tables.add(venueTableName);
		
		HashMap<String, DatabaseInfo.Type> venueAttributes = new HashMap<String, DatabaseInfo.Type>();
		venueAttributes.put("vid", DatabaseInfo.Type.INTEGER);
		venueAttributes.put("name", DatabaseInfo.Type.STRING);
		venueAttributes.put("venuecost", DatabaseInfo.Type.MONEY);
		
		String tablePop = populateTable(venueTableName, venueAttributes, noOfEntries);
		//System.out.println(tablePop);
		PreparedStatement popVenueTable = db.prepareStatement(tablePop);
		popVenueTable.execute();
	}
	
	private void createNewMenuTable(int noOfEntries) throws SQLException {
		String makeMenuTableString = 
				"CREATE TABLE Menu (\r\n" + 
				"	mid				INTEGER		NOT NULL UNIQUE,\r\n" + 
				"	description		CHAR(50),\r\n" + 
				"	costprice		INTEGER,\r\n" + 
				"	PRIMARY KEY (mid),\r\n" + 
				"	CHECK (costprice>=0)\r\n" + 
				");";
		
		PreparedStatement makeMenuTable = db.prepareStatement(makeMenuTableString);
		print("Trying to create Menu table... ");
		makeMenuTable.execute();
		
		print("Populating Menu... ");
		String menuTableName = "Menu";
		tables.add(menuTableName);
		
		HashMap<String, DatabaseInfo.Type> menuAttributes = new HashMap<String, DatabaseInfo.Type>();
		menuAttributes.put("mid", DatabaseInfo.Type.INTEGER);
		menuAttributes.put("description", DatabaseInfo.Type.STRING);
		menuAttributes.put("costprice", DatabaseInfo.Type.MONEY);
		
		String tablePop = populateTable(menuTableName, menuAttributes, noOfEntries);
		PreparedStatement popMenuTable = db.prepareStatement(tablePop);
		popMenuTable.execute();
	}
	
	private void createNewEntertainmentTable(int noOfEntries) throws SQLException {
		String makeEntertainmentTableString =
				"CREATE TABLE Entertainment (\r\n" + 
				"	eid				INTEGER		NOT NULL UNIQUE,\r\n" + 
				"	description		CHAR(50),\r\n" + 
				"	costprice		INTEGER,\r\n" + 
				"	PRIMARY KEY (eid),\r\n" + 
				"	CHECK (costprice>=0)\r\n" + 
				");";
		
		PreparedStatement makeEntertainmentTable = db.prepareStatement(makeEntertainmentTableString);
		print("Trying to create Entertainment table... ");
		makeEntertainmentTable.execute();
		
		print("Populating Entertainment... ");
		String entertainmentTableName = "Entertainment";
		tables.add(entertainmentTableName);
		
		HashMap<String, DatabaseInfo.Type> entertainmentAttributes = new HashMap<String, DatabaseInfo.Type>();
		entertainmentAttributes.put("eid", DatabaseInfo.Type.INTEGER);
		entertainmentAttributes.put("description", DatabaseInfo.Type.STRING);
		entertainmentAttributes.put("costprice", DatabaseInfo.Type.MONEY);
		
		String tablePop = populateTable(entertainmentTableName, entertainmentAttributes, noOfEntries);
		PreparedStatement popEntertainmentTable = db.prepareStatement(tablePop);
		popEntertainmentTable.execute();
	}
	
	private void createNewPartyTable(int noOfEntries) throws SQLException {
		String makePartyTableString =
				"CREATE TABLE Party (\r\n" + 
				"	pid				INTEGER		NOT NULL UNIQUE,\r\n" + 
				"	name			CHAR(15),\r\n" + 
				"	mid				INTEGER,\r\n" + 
				"	vid				INTEGER,\r\n" + 
				"	eid				INTEGER,\r\n" + 
				"	price			INTEGER,\r\n" + 
				"	timing			TIMESTAMP,\r\n" + 
				"	numberofguests	INTEGER,\r\n" + 
				"	PRIMARY KEY (pid),\r\n" + 
				"	FOREIGN KEY (mid) REFERENCES Menu,\r\n" + 
				"	FOREIGN KEY (vid) REFERENCES Venue,\r\n" + 
				"	FOREIGN KEY (eid) REFERENCES Entertainment,\r\n" + 
				"	CHECK (price >= 0),\r\n" + 
				"	CHECK (numberofguests >= 1)\r\n" + 
				");";
		
		PreparedStatement makePartyTable = db.prepareStatement(makePartyTableString);
		print("Trying to create Party table... ");
		makePartyTable.execute();
		
		print("Populating Party... ");
		String partyTableName = "Party";
		tables.add(partyTableName);
		
		String attributeString = "INSERT INTO Party (pid,name,mid,vid,eid,price,timing,numberofguests) VALUES (";
		String popString = "";
		Random randGenerator = new Random();
		for(int i = 0; i < INITIAL_NO_OF_PARTIES; i++) {
			Integer mid = randGenerator.nextInt(INITIAL_NO_OF_ENTRIES);
			Integer vid = randGenerator.nextInt(INITIAL_NO_OF_ENTRIES);
			Integer eid = randGenerator.nextInt(INITIAL_NO_OF_ENTRIES);
			Integer numberofguests = randGenerator.nextInt(300) + 1;
			
			ResultSet menuResult = db.prepareStatement("SELECT costprice FROM Menu WHERE mid = " + mid).executeQuery();
			menuResult.next();
			Integer menuCost = menuResult.getInt("costprice");
			ResultSet venueResult = db.prepareStatement("SELECT venuecost FROM Venue WHERE vid = " + vid).executeQuery();
			venueResult.next();
			Integer venueCost = venueResult.getInt("venuecost");
			ResultSet entertainmentResult = db.prepareStatement("SELECT costprice FROM Entertainment WHERE eid = " + eid).executeQuery();
			entertainmentResult.next();
			Integer entertainmentCost = entertainmentResult.getInt("costprice");
			Integer price = randGenerator.nextInt((menuCost * numberofguests) + venueCost + entertainmentCost) + 1; 
			Date date = new Date(System.currentTimeMillis() + (Math.abs(randGenerator.nextLong()) % (1L * 365 * 24 * 60 * 60 * 1000)));
			Time time = new Time(randGenerator.nextLong() % (1000 * 60 * 60));
			popString += attributeString + i + ",'name" + i + "'," + mid + "," + vid + "," + eid + "," + price + ",'" + date.toString() + " " + time.toString() + "'," + numberofguests + ");\n";
		}
		System.out.println(popString);

		PreparedStatement popPartyTable = db.prepareStatement(popString);
		popPartyTable.execute();
	}
	
	private String populateTable(String tableName, HashMap<String, DatabaseInfo.Type> attributes, int noOfRecords) {
		String popString = "";
		String attributeString = tableName + " (";
		
		for (String attribute : attributes.keySet()) { attributeString += attribute + ","; }
		
		attributeString = attributeString.substring(0, attributeString.length() - 1) + ")"; 
		
		for(int i = 0; i < noOfRecords; i++) {
			popString += "INSERT INTO " + attributeString + " VALUES (";
			for (String attribute : attributes.keySet()) {
				DatabaseInfo.Type type = attributes.get(attribute);
				if(type.equals(DatabaseInfo.Type.INTEGER)) 		{ popString += i; }
				else if(type.equals(DatabaseInfo.Type.STRING)) 	{ popString += "'" + attribute + i + "'"; } 
				else if(type.equals(DatabaseInfo.Type.MONEY)) 	{ popString += generateRandomCost(10000); }
				popString += ",";
			}
			popString = popString.substring(0, popString.length() - 1) + ");\n";
		}
		return popString;
	}
	
	private Integer generateRandomCost(int _bound) {
		int bound = _bound / 100;
		Random rand = new Random();
		return rand.nextInt(bound) * 100;
	}
	
	@SuppressWarnings("unchecked")
	public static void printResultSet(ResultSet rs) throws SQLException {
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int colNo = rsmd.getColumnCount();
		
		int[] colWidths = new int[colNo];
	
		List<String> labels = new LinkedList<String>();
		for(int i = 1; i <= colNo; i++) {
			String label = rsmd.getColumnLabel(i);
			labels.add(label);
			colWidths[i-1] = label.length();
		}
		
		List<List<String>> rows = new LinkedList<List<String>>();
		rs.beforeFirst();
		while(rs.next()) {
			List<String> row = new LinkedList<String>();
			for(int i = 1; i <= colNo; i++) {
				String item = rs.getString(i);
				row.add(item);
				int stringLength = item.length();
				colWidths[i-1] = stringLength > colWidths[i-1] ? stringLength : colWidths[i-1];
			}
			rows.add(row);
		}
		
		StringBuilder format = new StringBuilder();
		for(int maxWidth : colWidths) { format.append("%-").append(maxWidth + 2).append("s| "); }
		String formatString = format.toString();

		System.out.println(String.format(formatString, labels.toArray(new String[0])));
		
		for(List<String> row : rows) { System.out.println(String.format(formatString, row.toArray(new String[0]))); }
	
	}
	
	private void deleteTable(Connection db, String table, boolean verbose) throws SQLException {
		if(verbose) print("Deleting " + table + " table... ");
		try {
			db.prepareStatement("DROP TABLE " + table + ";").execute();
			if(verbose) print("Done.");
		} catch (PSQLException e) {
			System.out.println("Table " + table + " does not exist."); 
		}
	}
	
	private void deleteTable(Connection db, String table) throws SQLException {
		deleteTable(db, table, false);
	}
		
	private void print(String message) {
		System.out.println(getTime() + ": " + message);
	}
	
	private String getTime() {
		Calendar cal = Calendar.getInstance();
		String time = TIME_FORMAT.format(cal.getTime());
		String date = DATE_FORMAT.format(cal.getTime());
		return ("[" + date + " | " + time + "]");
	}
	
}
