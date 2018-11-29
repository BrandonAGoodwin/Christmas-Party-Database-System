package com.bxg796.main;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateDatabase {

	private final String TEST_URL = "jdbc:postgresql://localhost/";
	private final String URL = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/bxg796"; //"jdbc:postgresql://bxg796@tinky-winky.cs.bham.ac.uk:22/mod-intro-databases";
	private final String USERNAME = "bxg796";
	private final String PASSWORD =                                                                             		 "CartersJr10";
	
	
	
	public static void main(String[] args) {
		CreateDatabase createDB = new CreateDatabase();
	}
	
	public CreateDatabase() {
		initDatabase();
		
		
	}
	
	private void initDatabase() {
		Connection db;
		try {
			//String username = System.console().readLine();
			//String password = new String(System.console().readPassword());
			System.out.print("Trying to connect...");
			db = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			//db = DriverManager.
			System.out.println("Connected to server");
			//ResultSet allBooks = db.	prepareStatement("SELECT * FROM books;").executeQuery();
			//printResultSet(allBooks);
			
			String makeVenueTableString = 
					"CREATE TABLE Venue (\r\n" + 
					"	vid				INTEGER		NOT NULL UNIQUE,\r\n" + 
					"	name			CHAR(15),\r\n" + 
					"	venuecost		INTEGER\r\n" + 
					")";
			
			PreparedStatement makeVenueTable = db.prepareStatement(makeVenueTableString);
			
			Statement popVenue = db.createStatement();
			
			
			HashMap<String, Class> venueAttributes = new HashMap<String, Class>();
			venueAttributes.put("vid", Integer.class);
			venueAttributes.put("name", String.class);
			venueAttributes.put("venuecost", Integer.class);
			String tablePop = populateTable("Venue", venueAttributes, 1000);
			Statement s = db.prepareStatement(tablePop);
			//System.out.println(tablePop);
			
			db.close();
		} catch(SQLException e) {
			e.printStackTrace(); // Change to be neater
		} 
	}
	
	private String populateTable(String tableName, HashMap<String, Class> attributes, int noOfRecords) {
		String sqlPopulateCommand = "";
		
		String attributeString = "(";
		for (String attribute : attributes.keySet()) {
			attributeString += attribute + ",";
		}
		attributeString = attributeString.substring(0, attributeString.length() - 1);
		attributeString += ")"; 
		
		for(int i = 0; i < noOfRecords; i++) {
			sqlPopulateCommand += "INSERT INTO " + attributeString + " VALUES(";
			for (String attribute : attributes.keySet()) {
				Class type = attributes.get(attribute);
				if(type.isAssignableFrom(Integer.class)) {
					sqlPopulateCommand += i;
				} else if(type.isAssignableFrom(String.class)) {
					sqlPopulateCommand += "\"" + attribute + i + "\""; 
				}
				sqlPopulateCommand += ",";
			}
			sqlPopulateCommand = sqlPopulateCommand.substring(0, sqlPopulateCommand.length() - 1);
			
			sqlPopulateCommand += ");\n";
		}
		return sqlPopulateCommand;
	}
	
	private void printResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int colNo = rsmd.getColumnCount();
		System.out.println();
		for(int i = 1; i <= colNo; i++) {
			System.out.print(rsmd.getColumnLabel(i) + "\t|\t");
		}
		System.out.println("\n----------------------------------------------------------------------");
		//if(rs.first()) {
			while(rs.next()) {
				for(int i = 1; i <= colNo; i++) {
					System.out.print(rs.getString(i) + "\t");
				}
				System.out.println();
			} 
		//}
	}
	
	
	
}
