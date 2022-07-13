package oracleConnection;

import java.sql.*;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws InterruptedException, SQLException {
		Scanner input = new Scanner(System.in);
		Connection con = null;
		String user = "T11";
		String pword = "Summer2022T11";
		String url = null;
		
		
		//database url
		url = "jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
				
		//initialize variables
		int i = 0;
		int j = 0;
		
		//attempt to connect to the server 3 times only
		while(i<3)
		{	
			System.out.print("You are on login attempt " + (i+1) + " of 3.\n");
			//prompt user for username
			
			//alert user program will attempt to connect
			System.out.print("Establishing connection to " + url + " as " + user + "...\n");
			System.out.println();

			PreparedStatement statement;

			String qry = "INSERT INTO DOCTOR (doctorid, fname, lname, minit ) VALUES (?,?,?,?)";


			//attempt to connect
			try {
				con = DriverManager.getConnection(url, user, pword);

				statement = con.prepareStatement(qry);

				statement.setInt(1,0001);
				statement.setString(2, "johnnn");
				statement.setString(3, "larrs");
				statement.setString(4, "R");

				if(!statement.execute())
					System.out.println("NOT SUCCESSFUL");

			} catch (SQLException e) {
				//quit with error if failed
				e.printStackTrace();
				Thread.sleep(1000); //wait for 1 second because formatting from printStackTrace messes up the following prints in the while loop somehow
				System.out.println();
				if(i!=2) {
					System.out.print("Error connecting to " + url + " as " + user + ". Please try again.\n");
				} else {
					//failed to login within 3 attempts
					System.out.print("Failed to login on attempt " + (i+1) + " of 3. Exiting the program.\n");
				}
				
			}
			//if connected, alert user and end while loop
			if(con != null)
			{
				System.out.print("Successfully connected to " + url + " as " + user + ".\n\n");
				i = 3;
			}
			i++;
			
		}//end while
		
		//if connected, close connection and alert user
		if(con != null) {
			System.out.print("Closing connection to " + url + " as " + user + ".\n");
			try {
				con.close();
			} catch (Exception e) {
				System.out.println(e);
				System.out.print("Error closing connection to " + url + " as " + user + ".\n");
			}
		}
		System.out.print("\nExiting the program.");
	}
}


