package oracleConnection;

import java.sql.*;
import java.util.Scanner;

public class Main {

	static final String user = "T11";
	static final String pword = "Summer2022T11";
	static final String url = "jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
	private Connection con;

	public static void main(String[] args) throws InterruptedException, SQLException {
		Scanner input = new Scanner(System.in);
		//Connection con;
		//String user = "T11";
		//String pword = "Summer2022T11";
		//String url = null;

		//database url
		//url = "jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";

		try {
			Main pgm = new Main();
			pgm.con = pgm.GetConnection(url, user, pword);
			pgm.MainMenu();
			pgm.con.close();
		}
		catch (SQLException se)
		{
			System.out.printf("Main method has an SQL issue: %s.\n", se.toString());
		}
	}
/*
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

			// new doctor
			String qry = "INSERT INTO DOCTOR (doctorid, fname, lname, minit ) VALUES (?,?,?,?)";
			//new patient
			String qry2 = "INSERT INTO PATIENT (patientid, fname, lname, minit ) VALUES (?,?,?,?)";*/

/*
			//attempt to connect
			try {
				con = DriverManager.getConnection(url, user, pword);

				// doctor statement
				statement = con.prepareStatement(qry);

				statement.setInt(1,1);
				statement.setString(2, "johnnn");
				statement.setString(3, "larrs");
				statement.setString(4, "R");

				// patient statement
				statement = con.prepareStatement(qry2);

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

	*/

	//Connection
	Connection GetConnection (String url, String user, String pword) throws SQLException {
		Connection Con = null;
		try
		{
			Con = DriverManager.getConnection(url, user, pword);
		}
		catch (SQLException e)
		{
			System.out.println("Failed to connect to DBMS in GetConnection() method.");
		}
		return Con;
	}

	//Main menu
	int MainMenu () throws SQLException {
		String userInput = "";
		Scanner sc = new Scanner(System.in);
		System.out.println("Main Menu");
		System.out.println("Enter your action:");
		System.out.println("1. Create/edit patients.");
		System.out.println("2. Create/edit doctors.");
		System.out.println("3. Create/edit appointments.");
		System.out.println("4. Create/edit bills.");
		System.out.println("5. Create reports.");
		System.out.println("6. Exit the program.");

		userInput = sc.next();
		if (userInput.equals("1")) {
			Create_Update_Patient();
		} else if (userInput.equals("2")) {
			Create_Update_Doctor ();
		} else if (userInput.equals("3")) {
			Create_Update_Appointment();
		} else if (userInput.equals("4")) {
			Create_Update_Bill();
		} else if (userInput.equals("5")) {
			//report method
		} else if (userInput.equals("6")) {
			System.exit(0);
		}
		return 0;
	}

	// Create or update Patient
	int Create_Update_Patient() {
		try {
			String qry = "INSERT INTO PATIENT (patient, fname, minit, lname, dob ) VALUES (?,?,?,?,?)";
			PreparedStatement statement = null;
			statement = con.prepareStatement(qry);

			System.out.println("Enter patientID:");
			Scanner patientID = new Scanner(System.in);
			statement.setInt(1, patientID.nextInt());
			System.out.println("Enter patient first name:");
			Scanner fname = new Scanner(System.in);
			statement.setString(2, fname.next());
			System.out.println("Enter patient last name:");
			Scanner lname = new Scanner(System.in);
			statement.setString(3, lname.next());
			System.out.println("Enter patient Minit:");
			Scanner minit = new Scanner(System.in);
			statement.setString(4, minit.next());
			System.out.println("Enter patient dob:");
			Scanner dob = new Scanner(System.in);
			statement.setDate(5, Date.valueOf(dob.next())); //Date format Example: 2019-09-01

			statement.execute();

		}
		catch (SQLException x)
		{
			System.out.println(x);
		}
		catch (NullPointerException NPE)
		{
			System.out.println("NUll pointer exception");
		}
		return 0;
	}

	// Create or update Doctor
	int Create_Update_Doctor() {
		try {
			String qry = "INSERT INTO DOCTOR (doctorid, fname, lname, minit ) VALUES (?,?,?,?)";
			PreparedStatement statement = null;
			statement = con.prepareStatement(qry);

			System.out.println("Enter doctorID:");
			Scanner doctorID = new Scanner(System.in);
			statement.setInt(1, doctorID.nextInt());
			System.out.println("Enter doctor first name:");
			Scanner fname = new Scanner(System.in);
			statement.setString(2, fname.next());
			System.out.println("Enter doctor last name:");
			Scanner lname = new Scanner(System.in);
			statement.setString(3, lname.next());
			System.out.println("Enter doctor Minit:");
			Scanner minit = new Scanner(System.in);
			statement.setString(4, minit.next());

			statement.execute();
			con.close();
		}
		catch (SQLException x)
		{
			System.out.println(x);
		}
		catch (NullPointerException NPE)
		{
			System.out.println("setInt throws NULL pointer exception");
		}
		return 0;
	}

	// Create or update appointment
	int Create_Update_Appointment() {
		try {
			String qry = "INSERT INTO APPOINTMENT (apptid, datetime, PATIENTS_PATIENTID, DOCTORS_DOCTORID ) VALUES (?,?,?,?)";
			PreparedStatement statement = null;
			statement = con.prepareStatement(qry);

			System.out.println("Enter Appointment ID:");
			Scanner apptidID = new Scanner(System.in);
			statement.setInt(1, apptidID.nextInt());
			System.out.println("Enter Date/Time:");
			Scanner DateTime = new Scanner(System.in);
			statement.setDate(2, Date.valueOf(DateTime.next())); //Date format Example: 2019-09-01
			System.out.println("Enter patientID:");
			Scanner patientID = new Scanner(System.in);
			statement.setInt(3, patientID.nextInt());
			System.out.println("Enter doctorID:");
			Scanner doctorID = new Scanner(System.in);
			statement.setInt(4, doctorID.nextInt());

			statement.execute();
			con.close();
		}
		catch (SQLException x)
		{
			System.out.println(x);
		}
		catch (NullPointerException NPE)
		{
			System.out.println("setInt throws NULL pointer exception");
		}
		return 0;
	}

	// Create or update bill
	int Create_Update_Bill() {
		try {
			String qry = "INSERT INTO Bill (billid, amountdue, VISIT_APPOINTMENTS_APPTID, VISITS_VISITID ) VALUES (?,?,?,?)";
			PreparedStatement statement = null;
			statement = con.prepareStatement(qry);

			System.out.println("Enter Bill ID:");
			Scanner billID = new Scanner(System.in);
			statement.setInt(1, billID.nextInt());
			System.out.println("Enter Amount Due:");
			Scanner amountDue = new Scanner(System.in);
			statement.setInt(2, amountDue.nextInt());
			System.out.println("Enter appointment ID:");
			Scanner appointmentID = new Scanner(System.in);
			statement.setInt(3, appointmentID.nextInt());
			System.out.println("Enter Visit ID:");
			Scanner visitID = new Scanner(System.in);
			statement.setInt(4, visitID.nextInt());

			statement.execute();
			con.close();
		}
		catch (SQLException x)
		{
			System.out.println(x);
		}
		catch (NullPointerException NPE)
		{
			System.out.println("setInt throws NULL pointer exception");
		}
		return 0;
	}
}



