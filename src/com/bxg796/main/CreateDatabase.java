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
                                                                      		 								
	private final Integer INITIAL_NO_OF_PARTIES = 1000;
	private final Integer INITIAL_NO_OF_ENTRIES = 100;
	
	private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM");

	private boolean verbose;
	
	Connection db;
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		CreateDatabase createDB = new CreateDatabase();
	}
	
	public CreateDatabase() { initDatabase(); }
	
	private void initDatabase() {
		verbose = false;
		db = null;
		try {
			print("Trying to connect... ");
			db = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
			print("Connected to server.");
			
			// Delete all pre-existing party, venue, menu and entertainment tables
			
			deleteTable(db, "Party");
			deleteTable(db, "Venue"); 
			deleteTable(db, "Menu");
			deleteTable(db, "Entertainment");
			
			// Create and populate each table
			
			createNewVenueTable(INITIAL_NO_OF_ENTRIES);
			if(verbose) System.out.println(getResultSetString(db.prepareStatement(
					"SELECT * FROM Venue;",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)
					.executeQuery()));
			
			createNewMenuTable(INITIAL_NO_OF_ENTRIES);
			if(verbose) System.out.println(getResultSetString(db.prepareStatement(
					"SELECT * FROM Menu;",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)
					.executeQuery()));
			
			createNewEntertainmentTable(INITIAL_NO_OF_ENTRIES);
			if(verbose) System.out.println(getResultSetString(db.prepareStatement(
					"SELECT * FROM Entertainment;",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)
					.executeQuery()));
			
			createNewPartyTable(INITIAL_NO_OF_ENTRIES);
			if(verbose)System.out.println(getResultSetString(db.prepareStatement(
					"SELECT * FROM Party;",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)
					.executeQuery()));

			////////////////////////////////////////////
			
			addUniqueEntries();
	
		} catch(SQLException e) { e.printStackTrace(); } 
		
		// Check if the connection is closed, if not close it
		 try {
         	if(!db.isClosed()) {
         		print("Closing connection.");
         		db.close(); 
         	}
        } catch (SQLException e) { e.printStackTrace(); }
	}
	
	// Creating and populating tables
	private void createNewVenueTable(int noOfEntries) throws SQLException {
		String makeVenueTableString = 
				"CREATE TABLE Venue (\r\n" + 
				"	vid				INTEGER		NOT NULL UNIQUE,\r\n" + 
				"	name			VARCHAR(30),\r\n" + 
				"	venuecost		INTEGER,\r\n" +  
				"	PRIMARY KEY (vid),\r\n" +
				"   CHECK (vid>=0),\r\n" +
				"	CHECK (venuecost>=0)\r\n" + 
				");";
		
		PreparedStatement makeVenueTable = db.prepareStatement(makeVenueTableString);
		print("Trying to create Venue table... ");
		
		try { makeVenueTable.execute(); } catch(PSQLException e) { System.out.println("Venue table already exists"); }
		
		print("Populating Venue... ");
		String venueTableName = "Venue";
		
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
				"	description		VARCHAR(50),\r\n" + 
				"	costprice		INTEGER,\r\n" + 
				"	PRIMARY KEY (mid),\r\n" +
				"   CHECK (mid>=0),\r\n" +
				"	CHECK (costprice>=0)\r\n" + 
				");";
		
		PreparedStatement makeMenuTable = db.prepareStatement(makeMenuTableString);
		print("Trying to create Menu table... ");
		makeMenuTable.execute();
		
		print("Populating Menu... ");
		String menuTableName = "Menu";
	
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
				"	description		VARCHAR(50),\r\n" + 
				"	costprice		INTEGER,\r\n" + 
				"	PRIMARY KEY (eid),\r\n" +
				"   CHECK (eid >= 0),\r\n" +
				"	CHECK (costprice>=0)\r\n" + 
				");";
		
		PreparedStatement makeEntertainmentTable = db.prepareStatement(makeEntertainmentTableString);
		print("Trying to create Entertainment table... ");
		makeEntertainmentTable.execute();
		
		print("Populating Entertainment... ");
		String entertainmentTableName = "Entertainment";
	
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
				"	name			VARCHAR(30),\r\n" + 
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
				"   CHECK (pid >= 0),\r\n" +
				"	CHECK (price >= 0),\r\n" + 
				"	CHECK (numberofguests >= 1)\r\n" + 
				");";
		
		PreparedStatement makePartyTable = db.prepareStatement(makePartyTableString);
		print("Trying to create Party table... ");
		makePartyTable.execute();
		
		print("Populating Party... ");

		String attributeString = "INSERT INTO Party (pid,name,mid,vid,eid,price,timing,numberofguests) VALUES (";
		String popString = "";
		Random randGenerator = new Random();
		for(int i = 1; i <= INITIAL_NO_OF_PARTIES; i++) {
			// Generate a random mid, vid, eid and numberofguests to use
			Integer mid = randGenerator.nextInt(INITIAL_NO_OF_ENTRIES) + 1;
			Integer vid = randGenerator.nextInt(INITIAL_NO_OF_ENTRIES) + 1;
			Integer eid = randGenerator.nextInt(INITIAL_NO_OF_ENTRIES) + 1;
			Integer numberofguests = randGenerator.nextInt(300) + 1;
			
			// Calculating the total cost of the party so a quoted price can be generated that is less that the net price
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
			// Generating a random valid time and date within the next year
			Date date = new Date(System.currentTimeMillis() + (Math.abs(randGenerator.nextLong()) % (1L * 365 * 24 * 60 * 60 * 1000)));
			Time time = new Time(randGenerator.nextLong() % (1000 * 60 * 60));
			popString += attributeString + i + ",'name" + i + "'," + mid + "," + vid + "," + eid + "," + price + ",'" + date.toString() + " " + time.toString() + "'," + numberofguests + ");\n";
		}

		PreparedStatement popPartyTable = db.prepareStatement(popString);
		popPartyTable.execute();
	}
	
	// Populates a table given the table name and a hash map of attributes and their data type
	private String populateTable(String tableName, HashMap<String, DatabaseInfo.Type> attributes, int noOfRecords) {
		String popString = "";
		String attributeString = tableName + " (";
		
		for (String attribute : attributes.keySet()) { attributeString += attribute + ","; }
		
		attributeString = attributeString.substring(0, attributeString.length() - 1) + ")"; 
		
		for(int i = 1; i <= noOfRecords; i++) {
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
	
	// Adding entries to tables
	
	// Adds a number of unique entries to the database
	private void addUniqueEntries() throws SQLException {
		addToVenueTable(110, "Subs House", 3500);
		addToVenueTable(101, "CS Atrium", 0);
		addToVenueTable(102, "Party Hall", 10000);
		addToVenueTable(103, "Slomans Lounge", 24000);
		addToVenueTable(104, "Devins House", 200);
		addToVenueTable(105, "ISS", 3145900);
		addToVenueTable(106, "Sports Hall", 63300);
		addToVenueTable(107, "Theater", 30000);
		addToVenueTable(108, "Warehouse", 57000);
		addToVenueTable(109, "Howls Moving Castle", 9999900);
		
		addToMenuTable(110, "Dixys Chicken", 450);
		addToMenuTable(101, "Roosters", 420);
		addToMenuTable(102, "Big Johns", 390);
		addToMenuTable(103, "Liberty Hot Pot", 110);
		addToMenuTable(104, "Fish and Chips", 190);
		addToMenuTable(105, "Ramen and Beans", 0);
		addToMenuTable(106, "Cold Finger Foods", 50);
		addToMenuTable(107, "One Pound Fish", 110);
		addToMenuTable(108, "Sag aloo and Cake", 1350);
		addToMenuTable(109, "Lots of Salad", 80);
		
		addToEntertainmentTable(110, "Crawl and Drinks", 2000);
		addToEntertainmentTable(101, "Secret Hitler", 3000);
		addToEntertainmentTable(102, "Laser Tag", 15000);
		addToEntertainmentTable(103, "Cinema", 12000);
		addToEntertainmentTable(104, "Skiing", 1000000);
		addToEntertainmentTable(105, "Snowboarding", 850000);
		addToEntertainmentTable(106, "Jazz Band", 30000);
		addToEntertainmentTable(107, "Circus", 50000);
		addToEntertainmentTable(108, "Synchronised Swimmers", 38900);
		addToEntertainmentTable(109, "DJ", 13000);
		
		addToPartyTable(1001, "Student Night", 105, 104, 110, 2500, "2018-10-13 18:30:00", 24);
		addToPartyTable(1002, "Switch at the Cinema", 101, 107, 110, 30000, "2019-01-10 12:45:00", 10);
		addToPartyTable(1003, "CS Ball", 106, 106, 109, 400000, "2018-12-09 19:30:00", 250);
		addToPartyTable(1004, "Work Party", 109, 108, 102, 125000, "2019-04-11 07:30:00", 63);
		addToPartyTable(1005, "Upper Echelon Function", 107, 105, 104, 10000000, "2019-01-01 22:00:00", 4);
		addToPartyTable(1006, "Birthday Party", 110, 109, 108, 30000, "2018-07-14 21:15:00", 31);
		addToPartyTable(1007, "Family Gathering", 104, 109, 107, 48500, "2019-03-28 14:30:00", 55);
		addToPartyTable(1008, "Super Mario Party", 103, 110, 110, 4000, "2018-11-30 21:00:00", 8);
		addToPartyTable(1009, "S Party 8", 105, 101, 106, 125500, "2018-05-06 20:00:00", 3000);
		addToPartyTable(1010, "UNKNOWN", 110, 108, 108, 0, "2020-12-01", 40000);
		addToPartyTable(1011, "Subs Xmas Bash", 110, 110, 101, 5000, "2018-12-24 20:00:00", 13);
		
	}
	
	private void addToVenueTable(Integer vid, String name, Integer venuecost) throws SQLException {
		db.prepareStatement("INSERT INTO Venue (vid,name,venuecost) "
				+ "VALUES (" + vid + ",'" + name + "'," + venuecost + ");")
		.execute();
	}
	
	private void addToMenuTable(Integer mid, String description, Integer costprice) throws SQLException {
		db.prepareStatement("INSERT INTO Menu (mid,description,costprice) "
				+ "VALUES (" + mid + ",'" + description + "'," + costprice + ");")
		.execute();
	}
	
	private void addToEntertainmentTable(Integer eid, String description, Integer costprice) throws SQLException {
		db.prepareStatement("INSERT INTO Entertainment (eid,description,costprice) "
				+ "VALUES (" + eid + ",'" + description + "'," + costprice + ");")
		.execute();
	}
	
	// Both make and run a statement that adds a party to the database
	private void addToPartyTable(Integer pid, String name, Integer mid, Integer vid, Integer eid, Integer price, String timing, Integer numberofguests) throws SQLException {
		getAddToPartyTableStatement(db, pid, name, mid, vid, eid, price, timing, numberofguests).execute();
	}
	
	// Get a statement which can be run to add a party to the database
	public static PreparedStatement getAddToPartyTableStatement(Connection db, Integer pid, String name, Integer mid, Integer vid, Integer eid, Integer price, String timing, Integer numberofguests) throws SQLException {
		return db.prepareStatement("INSERT INTO Party (pid,name,mid,vid,eid,price,timing,numberofguests) "
				+ "VALUES (" + pid + ",'" + name + "'," + mid + "," + vid + "," + eid + "," + price + ",'" + timing + "'," + numberofguests + ");");
	}
		
	private Integer generateRandomCost(int _bound) {
		int bound = _bound / 100;
		Random rand = new Random();
		return rand.nextInt(bound) * 100;
	}
	
	public static String getResultSetString(ResultSet rs) throws SQLException {
		String result = "";
		// Get metadata to get column labels and the number of columns
		ResultSetMetaData rsmd = rs.getMetaData();
		int colNo = rsmd.getColumnCount();
		// Make int array to store max column widths
		int[] colWidths = new int[colNo];
	
		// Store all the labels in the list and their lengths in the width array
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
		int totalWidth = 0;
		for(int maxWidth : colWidths) { 
			format.append("%-").append(maxWidth + 2).append("s| ");
			totalWidth += maxWidth + 4;
		}
		
		String underline = "";
		for(int i = 0; i < totalWidth - 1; i++) underline += "-"; 
		
		String formatString = format.toString();
		// Add out formatted labels
		result += String.format(formatString, labels.toArray(new String[0])) + "\n";
		// Add out label underline
		result += underline + "\n";
		// Add out rows
		for(List<String> row : rows) { result += String.format(formatString, row.toArray(new String[0])) + "\n"; }
		
		return result;	
	}
	
	private void deleteTable(Connection db, String table) throws SQLException {
		print("Deleting " + table + " table... ");
		try {
			db.prepareStatement("DROP TABLE " + table + ";").execute();
			print("Done.");
		} catch (PSQLException e) {
			print("Table " + table + " does not exist."); 
		}
	}
	
	private void print(String message) { System.out.println(getTime() + ": " + message); }
	
	private String getTime() {
		Calendar cal = Calendar.getInstance();
		String time = TIME_FORMAT.format(cal.getTime());
		String date = DATE_FORMAT.format(cal.getTime());
		return ("[" + date + " | " + time + "]");
	}
	
}
