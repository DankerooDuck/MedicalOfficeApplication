package oracleConnection;

import java.sql.*;
import java.util.Scanner;

public class Main {

	static final String user = "T11";
	static final String pword = "Summer2022T11";
	static final String url = "jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
	private Connection con;

	public static void main(String[] args) throws InterruptedException, SQLException {

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
	void /*int*/ MainMenu () throws SQLException {
		String userInput = "";
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println("\nMain Menu");
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
				Create_Update_Doctor();
			} else if (userInput.equals("3")) {
				Create_Update_Appointment();
			} else if (userInput.equals("4")) {
				Create_Update_Bill();
			} else if (userInput.equals("5")) {
				//report method
			} else if (userInput.equals("6")) {
				con.close();
				System.exit(0);
			}
			//return 0;
		}
	}

	// Create or update Patient
	int Create_Update_Patient() {
		try {
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			System.out.println("1. Create new Patient.");
			System.out.println("2. Update existing Patient.");
			userInput = sc.next();
			if(userInput.equals("1")) {
				//Scanner sc = new Scanner(System.in);
				String qry = "INSERT INTO PATIENT (patient, fname, minit, lname, dob ) VALUES (?,?,?,?,?)";
				PreparedStatement statement = null;
				statement = con.prepareStatement(qry);

				System.out.println("Enter patientID:");
				statement.setInt(1, sc.nextInt());

				System.out.println("Enter patient first name:");
				statement.setString(2, sc.next());

				System.out.println("Enter patient last name:");
				statement.setString(3, sc.next());

				System.out.println("Enter patient Minit:");
				statement.setString(4, sc.next());

				System.out.println("Enter patient dob:");
				statement.setDate(5, Date.valueOf(sc.next())); //Date format Example: YYYY-MM-DD

				statement.execute();
			}
			else if(userInput.equals("2"))
			{
				System.out.println("What do you want to update:");
				System.out.println("1. Doctor ID");
				System.out.println("2. First Name");
				System.out.println("3. Last Name");
				System.out.println("4. Minit");
				System.out.println("5. Date of Birth");
				userInput = sc.next();
				if(userInput.equals("1")) {

					String qry = "UPDATE PATIENT set patientid = ? where patientid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Patient's new ID.");
					statement.setInt(1, sc.nextInt());

					System.out.println("Enter Patient's old ID.");
					statement.setInt(2, sc.nextInt());

					statement.executeUpdate();
				}
				else if(userInput.equals("2")) {

					String qry = "UPDATE PATIENT set fname = ? where fname = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Patient's new First Name.");
					statement.setString(1, sc.next());

					System.out.println("Enter Patient's old First Name.");
					statement.setString(2, sc.next());

					statement.executeUpdate();
				}
				else if(userInput.equals("3")) {

					String qry = "UPDATE PATIENT set lname = ? where lname = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Patient's new Last Name.");
					statement.setString(1, sc.next());

					System.out.println("Enter Patient's old Last Name.");
					statement.setString(2, sc.next());

					statement.executeUpdate();
				}
				else if(userInput.equals("4")) {

					String qry = "UPDATE PATIENT set minit = ? where minit = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Patient's new minit.");
					statement.setString(1, sc.next());

					System.out.println("Enter Patient's old minit.");
					statement.setString(2, sc.next());

					statement.executeUpdate();
				}
				else if(userInput.equals("5")) {

					String qry = "UPDATE PATIENT set dob = ? where dob = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Patient's new dob.");
					statement.setDate(1, Date.valueOf(sc.next()));

					System.out.println("Enter Patient's old dob.");
					statement.setDate(2, Date.valueOf(sc.next()));

					statement.executeUpdate();
				}
			}

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
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			System.out.println("1. Create new Doctor.");
			System.out.println("2. Update existing Doctor.");
			userInput = sc.next();
			if(userInput.equals("1")) {

				String qry = "INSERT INTO DOCTOR (doctorid, fname, lname, minit ) VALUES (?,?,?,?)";
				PreparedStatement statement = null;
				statement = con.prepareStatement(qry);

				System.out.println("Enter doctorID:");
				statement.setInt(1, sc.nextInt());

				System.out.println("Enter doctor first name:");
				statement.setString(2, sc.next());

				System.out.println("Enter doctor last name:");
				statement.setString(3, sc.next());

				System.out.println("Enter doctor Minit:");
				statement.setString(4, sc.next());

				statement.execute();
				con.close();
			}
			else if(userInput.equals("2"))
			{
				System.out.println("What do you want to update:");
				System.out.println("1. Doctor ID");
				System.out.println("2. First Name");
				System.out.println("3. Last Name");
				System.out.println("4. Minit");
				userInput = sc.next();
				if(userInput.equals("1")) {

					String qry = "UPDATE DOCTOR set doctorid = ? where doctorid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Doctor's new ID.");
					statement.setInt(1, sc.nextInt());

					System.out.println("Enter Doctor's old ID.");
					statement.setInt(2, sc.nextInt());

					statement.executeUpdate();
				}
				else if(userInput.equals("2")) {

					String qry = "UPDATE DOCTOR set fname = ? where fname = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Doctor's new First Name.");
					statement.setString(1, sc.next());

					System.out.println("Enter Doctor's old First Name.");
					statement.setString(2, sc.next());

					statement.executeUpdate();
				}
				else if(userInput.equals("3")) {

					String qry = "UPDATE DOCTOR set lname = ? where lname = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Doctor's new Last Name.");
					statement.setString(1, sc.next());

					System.out.println("Enter Doctor's old Last Name.");
					statement.setString(2, sc.next());

					statement.executeUpdate();
				}
				else if(userInput.equals("4")) {

					String qry = "UPDATE DOCTOR set minit = ? where minit = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Doctor's new minit.");
					statement.setString(1, sc.next());

					System.out.println("Enter Doctor's old minit.");
					statement.setString(2, sc.next());

					statement.executeUpdate();
				}
			}
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
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			System.out.println("1. Create new Appointment.");
			System.out.println("2. Update existing Appointment.");
			userInput = sc.next();

			if(userInput.equals("1"))
			{
				String qry = "INSERT INTO APPOINTMENT (apptid, datetime, PATIENTS_PATIENTID, DOCTORS_DOCTORID ) VALUES (?,?,?,?)";
				PreparedStatement statement = null;
				statement = con.prepareStatement(qry);

				System.out.println("Enter Appointment ID:");
				statement.setInt(1, sc.nextInt());

				System.out.println("Enter Date/Time:");
				statement.setDate(2, Date.valueOf(sc.next())); //Date format Example: YYYY-MM-DD

				System.out.println("Enter patientID:");
				statement.setInt(3, sc.nextInt());

				System.out.println("Enter doctorID:");
				statement.setInt(4, sc.nextInt());

				statement.execute();
				con.close();
			}
			else if(userInput.equals("2"))
			{
				System.out.println("What do you want to update:");
				System.out.println("1. Appointment ID.");
				System.out.println("2. Appointment Date/Time.");
				userInput = sc.next();
				if(userInput.equals("1")) {

					String qry = "UPDATE APPOINTMENT set apptid = ? where apptid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter appointment's new ID.");
					statement.setInt(1, sc.nextInt());

					System.out.println("Enter appointment's old ID.");
					statement.setInt(2, sc.nextInt());

					statement.executeUpdate();
				}
				else if(userInput.equals("2")) {

					String qry = "UPDATE APPOINTMENT set datetime = ? where datetime = ?"; // 2000-01-01   2022-10-11
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter appointment's new Date/Time.");
					statement.setDate(1, Date.valueOf(sc.next()));

					System.out.println("Enter appointment's old Date/Time.");
					statement.setDate(2, Date.valueOf(sc.next()));

					statement.executeUpdate();
				}
			}
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
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			System.out.println("1. Create new Bill.");
			System.out.println("2. Update existing Bill.");
			userInput = sc.next();

			if(userInput.equals("1"))
			{
				String qry = "INSERT INTO Bill (billid, amountdue, VISIT_APPOINTMENTS_APPTID, VISITS_VISITID ) VALUES (?,?,?,?)";
				PreparedStatement statement = null;
				statement = con.prepareStatement(qry);

				System.out.println("Enter Bill ID:");
				statement.setInt(1, sc.nextInt());

				System.out.println("Enter Amount Due:");
				statement.setInt(2, sc.nextInt());

				System.out.println("Enter appointment ID:");
				statement.setInt(3, sc.nextInt());

				System.out.println("Enter Visit ID:");
				statement.setInt(4, sc.nextInt());

				statement.execute();
				con.close();
			}
			else if(userInput.equals("2"))
			{
				System.out.println("What do you want to update:");
				System.out.println("1. Bill ID.");
				System.out.println("2. Bill Amount Due.");
				userInput = sc.next();
				if(userInput.equals("1")) {

					String qry = "UPDATE BILL set billid = ? where billid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter bill's new ID.");
					statement.setInt(1, sc.nextInt());

					System.out.println("Enter bill's old ID.");
					statement.setInt(2, sc.nextInt());

					statement.executeUpdate();
				}
				else if(userInput.equals("2")) {

					String qry = "UPDATE BILL set amountdue = ? where billid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter bill's new mount due.");
					statement.setInt(1, sc.nextInt());

					/*System.out.println("Enter bill's old amount due.");
					statement.setInt(2, sc.nextInt());*/

					System.out.println("Enter bill's ID.");
					statement.setInt(2, sc.nextInt());

					statement.executeUpdate();
				}
			}
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



