package oracleConnection;

import java.sql.*;
import java.util.Scanner;

public class Main {

	static final String user = "T11";
	static final String pword = "Summer2022T11";
	static final String url = "jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
	private Connection con;

	void printResults(ResultSet set) throws SQLException {
		while(set.next()) {
			System.out.println(set.getString(2));
		}
	}

	void billDecode(String bill) {
		System.out.println();
		int total = 0;
		int billLen = bill.length();
		char[] ch = bill.toCharArray();
		for (int i = 0; i < billLen; i++) {
			switch(ch[i]) {
			case '1':
				System.out.println("Checkup - $250");
				total += 250;
				break;
			case '2':
				System.out.println("Immunization - $125");
				total += 125;
				break;
			case '3':
				System.out.println("Prescription - $40");
				total += 40;
				break;
			case '4':
				total += 200;
				System.out.println("Consultation - $200");
				break;
			}//end switch
		}//end for loop
		System.out.println("Total: $" + total);
		System.out.println();

	}//end billDecode

	public void createOutstandingAppointmentsReport() throws SQLException {
		String qry = "SELECT * FROM APPOINTMENT WHERE apptid = ANY (SELECT visit_appointments_apptid FROM BILL WHERE amountdue > 0)";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	public void createUnpaidBillsReport() throws SQLException {
		String qry = "SELECT * FROM BILL WHERE amountdue > 0";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	public void createPaidBillsReports() throws SQLException {
		String qry = "SELECT * FROM BILL WHERE amountdue = 0";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	public void createActivePatientReport() throws SQLException {
		// TODO: are active patients really ones that have outstanding bills? or should it based on upcoming appts or something similar?
		String qry = "SELECT * FROM PATIENT";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	public void createActiveDoctorReport() throws SQLException {
		// TODO: broken query
		String qry = "SELECT * FROM DOCTOR";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

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
			System.out.println("1. Create/edit patients");
			System.out.println("2. Create/edit doctors");
			System.out.println("3. Create/edit appointments");
			System.out.println("4. Create/edit bills");
			System.out.println("5. Create reports");
			System.out.println("6. Exit the program");

			userInput = sc.next();
			System.out.println();
			if (userInput.equals("1")) {
				Create_Update_Patient();
			} else if (userInput.equals("2")) {
				Create_Update_Doctor();
			} else if (userInput.equals("3")) {
				Create_Update_Appointment();
			} else if (userInput.equals("4")) {
				Create_Update_Bill();
			} else if (userInput.equals("5")) {
				Create_Reports();
			} else if (userInput.equals("6")) {
				System.out.println("Closing connections and exiting the program.");
				con.close();
				System.exit(0);
			}
			//return 0;
		}
	}

	private void Create_Reports() {
		Scanner sc = new Scanner(System.in);

		System.out.println("1. Outstanding Appointments.");
		System.out.println("2. Unpaid Bills");
		System.out.println("3. Paid Bills");
		System.out.println("4. Active Patients");
		System.out.println("5. Active Doctors");
		System.out.println("6. Back");

		try {
			int input = sc.nextInt();
			switch(input) {
				case 1:
					createOutstandingAppointmentsReport();
					break;
				case 2:
					createUnpaidBillsReport();
					break;
				case 3:
					createPaidBillsReports();
					break;
				case 4:
					createActivePatientReport();
					break;
				case 5:
					createActiveDoctorReport();
					break;
				case 6:
					//return to menu
					break;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// Create or update Patient
	int Create_Update_Patient() {
		try {
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			System.out.println("1. Create new Patient");
			System.out.println("2. Update existing Patient");
			System.out.println("3. Back");
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

				System.out.println("Enter patient Middle Initial:");
				statement.setString(4, sc.next());

				System.out.println("Enter patient date of birth: (YYYY-MM-DD)");
				statement.setDate(5, Date.valueOf(sc.next())); //Date format Example: YYYY-MM-DD

				statement.execute();
			}
			else if(userInput.equals("2"))
			{
				//ask user for patient id they wish to edit
				int patientID = 0;
				System.out.println("Enter existing patient ID:");
				patientID = sc.nextInt();

				System.out.println("What do you want to update:");
				System.out.println("1. Patient ID");
				System.out.println("2. First Name");
				System.out.println("3. Last Name");
				System.out.println("4. Middle Initial");
				System.out.println("5. Date of Birth");
				System.out.println("6. Back");
				userInput = sc.next();

				//UPDATE PATIENT ID
				if(userInput.equals("1")) {

					String qry = "UPDATE PATIENT set patient = ? where patient = ?"; //update patientid from patient id
					PreparedStatement statement = con.prepareStatement(qry);

					int newPatientID = 0;
					Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
					while (flag == false) {
						System.out.println("Input new Patient ID:"); //new doctor id
						newPatientID = sc.nextInt();
						System.out.println("You entered: " + newPatientID + ". Is this correct?");
						System.out.println("1. Correct");
						System.out.println("2. Incorrect");

						userInput = sc.next(); //user input validation, correct or incorrect

						if (userInput.equals("1")) {
							flag = true; //exit loop
						} else if (userInput.equals("2")) {
							flag = false; //loop input doctor id
						}
					}//end while

					statement.setInt(1, newPatientID);
					statement.setInt(2, patientID);
					statement.executeUpdate();
				} //UPDATE PATIENT FNAME
				else if(userInput.equals("2")) {

					String qry = "UPDATE PATIENT set fname = ? where patient = ?"; //update fname record from patient ID. EX. UPDATE PATIENT set minit = t WHERE patient = 123
					PreparedStatement statement = con.prepareStatement(qry);

					String fname = "";
					Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
					while (flag == false) {
						System.out.println("Enter Patient's new First Name.");
						fname = sc.next();
						System.out.println("You entered: " + fname + ". Is this correct?");
						System.out.println("1. Correct");
						System.out.println("2. Incorrect");

						userInput = sc.next(); //user input validation, correct or incorrect

						if (userInput.equals("1")) {
							flag = true; //exit loop
						} else if (userInput.equals("2")) {
							flag = false; //loop
						}
					}//end while

					statement.setString(1, fname);
					statement.setInt(2, patientID);
					statement.executeUpdate();
				} //UPDATE PATIENT LNAME
				else if(userInput.equals("3")) {

					String qry = "UPDATE PATIENT set lname = ? where patient = ?"; //updates patient lname based on patient ID
					PreparedStatement statement = con.prepareStatement(qry);

					String lname = "";
					Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
					while (flag == false) {
						System.out.println("Enter Patient's new Last Name.");
						lname = sc.next();
						System.out.println("You entered: " + lname + ". Is this correct?");
						System.out.println("1. Correct");
						System.out.println("2. Incorrect");

						userInput = sc.next(); //user input validation, correct or incorrect

						if (userInput.equals("1")) {
							flag = true; //exit loop
						} else if (userInput.equals("2")) {
							flag = false; //loop
						}
					}//end while

					statement.setString(1, lname);
					statement.setInt(2, patientID);
					statement.executeUpdate();
				}
				else if(userInput.equals("4")) {

					String qry = "UPDATE PATIENT set minit = ? where minit = ?"; //update patient minit based on patient ID
					PreparedStatement statement = con.prepareStatement(qry);

					String minit = "";
					Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
					while (flag == false) {
						System.out.println("Enter Patient's new middle initial.");
						minit = sc.next();
						System.out.println("You entered: " + minit + ". Is this correct?");
						System.out.println("1. Correct");
						System.out.println("2. Incorrect");

						userInput = sc.next(); //user input validation, correct or incorrect

						if (userInput.equals("1")) {
							flag = true; //exit loop
						} else if (userInput.equals("2")) {
							flag = false; //loop
						}
					}//end while

					statement.setString(1, minit);
					statement.setInt(2, patientID);
					statement.executeUpdate();
				}
				else if(userInput.equals("5")) {

					String qry = "UPDATE PATIENT set dob = ? where patient = ?"; //update patient dob based on patient id
					PreparedStatement statement = con.prepareStatement(qry);

					String dob = "";
					Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
					while (flag == false) {
						System.out.println("Enter Patient's new Date of Birth. (YYYY-MM-DD)");
						dob = sc.next();
						System.out.println("You entered: " + dob + ". Is this correct?");
						System.out.println("1. Correct");
						System.out.println("2. Incorrect");

						userInput = sc.next(); //user input validation, correct or incorrect

						if (userInput.equals("1")) {
							flag = true; //exit loop
						} else if (userInput.equals("2")) {
							flag = false; //loop
						}
					}//end while

					statement.setDate(1, Date.valueOf(dob));
					statement.setInt(2, patientID);
					statement.executeUpdate();
				}
				else if(userInput.equals("6")) {
					//back choice
				}
			}
			else if(userInput.equals("3")) {
				//skips logic and returns to main menu
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
			System.out.println("1. Create new Doctor");
			System.out.println("2. Update existing Doctor");
			System.out.println("3. Back");
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

				System.out.println("Enter doctor middle initial:");
				statement.setString(4, sc.next());

				statement.execute();
				con.close();
			}
			else if(userInput.equals("2"))
			{
				int doctorID = 0;
				System.out.println("Enter existing Doctor ID:");
				doctorID = sc.nextInt();

				System.out.println("What do you want to update:");
				System.out.println("1. Doctor ID");
				System.out.println("2. First Name");
				System.out.println("3. Last Name");
				System.out.println("4. Middle Initial");
				System.out.println("5. Back");
				userInput = sc.next();
				if(userInput.equals("1")) {

					String qry = "UPDATE DOCTOR set doctorid = ? where doctorid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Doctor's new ID.");

					statement.setInt(1, sc.nextInt());
					statement.setInt(2, doctorID);
					statement.executeUpdate();
				}
				else if(userInput.equals("2")) {

					String qry = "UPDATE DOCTOR set fname = ? where doctorid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Doctor's new First Name.");
					statement.setString(1, sc.next());
					statement.setInt(2, doctorID);
					statement.executeUpdate();
				}
				else if(userInput.equals("3")) {

					String qry = "UPDATE DOCTOR set lname = ? where doctorid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Doctor's new Last Name.");
					statement.setString(1, sc.next());
					statement.setInt(2, doctorID);
					statement.executeUpdate();
				}
				else if(userInput.equals("4")) {

					String qry = "UPDATE DOCTOR set minit = ? where doctorid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Doctor's new middle initial.");
					statement.setString(1, sc.next());
					statement.setInt(2, doctorID);
					statement.executeUpdate();
				}
				else if(userInput.equals("5")) {
					//skip logic and return to previous menu
				}
			}
			if(userInput.equals("3")) {
				//skips logic and returns to menu
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
			System.out.println("3. Bill Appointment");
			System.out.println("4. Cancel Appointment");
			System.out.println("5. Back");
			userInput = sc.next();

			if(userInput.equals("1"))
			{
				String qry = "INSERT INTO APPOINTMENT (apptid, datetime, PATIENTS_PATIENTID, DOCTORS_DOCTORID ) VALUES (?,?,?,?)";
				PreparedStatement statement = null;
				statement = con.prepareStatement(qry);

				System.out.println("Enter Appointment ID:");
				statement.setInt(1, sc.nextInt());

				System.out.println("Enter Date/Time: (YYYY-MM-DD)");
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
				System.out.println("2. Appointment Date/Time (YYYY-MM-DD).");
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

					System.out.println("Enter appointment's new Date/Time (YYYY-MM-DD).");
					statement.setDate(1, Date.valueOf(sc.next()));

					System.out.println("Enter appointment's old Date/Time (YYYY-MM-DD).");
					statement.setDate(2, Date.valueOf(sc.next()));

					statement.executeUpdate();
				}
			}
			if(userInput.equals("3")) {
				System.out.println("FIXME: LOGIC AND CALL CREATE BILL");
			}
			if(userInput.equals("4")) {
				String qry = "DELETE FROM APPOINTMENT WHERE APPTID = ?";
				PreparedStatement statement = con.prepareStatement(qry);

				int appointmentID = 0;
				Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
				while (flag == false) {
					System.out.println("Enter the ID of the appointment you wish to DELETE: ");
					appointmentID = sc.nextInt();
					System.out.println("Are you sure you want to delete Appointment " + appointmentID + ". Is this correct?");
					System.out.println("This action CANNOT be undone.");
					System.out.println("1. Correct");
					System.out.println("2. Incorrect");

					userInput = sc.next(); //user input validation, correct or incorrect

					if (userInput.equals("1")) {
						flag = true; //exit loop
					} else if (userInput.equals("2")) {
						flag = false; //loop input
					}
				}//end while
				statement.setInt(1, appointmentID);
				statement.executeUpdate();
			}
			if(userInput.equals("5")) {
				//skips logic and returns to menu
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
			System.out.println("3. Back");
			userInput = sc.next();

			if(userInput.equals("1"))
			{
				//should we add to the qry and to the DATABASE, a new tuple in BILL called, items.
				//It will be a String containing numbers. 1 = checkup, 2 = procedure, as is in the while loop below
				//the application can then decode it to determine both the items and the price
				//42133 = Consultation, Immunization, Checkup, Prescription, Prescription
				//the number above, NOT AN INTEGER, but STRING (could be single characters too) is the itemized bill.
				String qry = "INSERT INTO Bill (billid, amountdue, items, VISIT_APPOINTMENTS_APPTID, VISITS_VISITID ) VALUES (?,?,?,?,?)";
				PreparedStatement statement = null;
				statement = con.prepareStatement(qry);

				System.out.println("Enter Bill ID:");
				statement.setInt(1, sc.nextInt());

				//STRING OF NUMBERS TO ITEMIZE A BILL
				String bill = "";
				int total = 0; //bill total
				Boolean flag = false; //flag for while loop
				while (flag == false) {
					System.out.println();
					System.out.println("Enter Procedure:");
					System.out.println();
					System.out.println("1. Checkup");
					System.out.println("2. Immunization");
					System.out.println("3. Prescription");
					System.out.println("4. Consultation");
					System.out.println("5. Done");


					int input = sc.nextInt();

					switch(input) {
						case 1:
							bill = bill + '1';
							total += 250;
							flag = false;
							break;
						case 2:
							bill = bill + '2';
							total += 125;
							flag = false;
							break;
						case 3:
							bill = bill + '3';
							total += 40;
							flag = false;
							break;
						case 4:
							bill = bill + '4';
							total += 200;
							flag = false;
							break;
						case 5:
							flag = true; //exit loop
							break;
					}
				}
				billDecode(bill); //bill = items, its a String

				statement.setInt(2, total); //amount due

				statement.setString(3, bill); //extra tuple in BILL that is String

				System.out.println("Enter appointment ID:");
				statement.setInt(4, sc.nextInt());

				System.out.println("Enter Visit ID:");
				statement.setInt(5, sc.nextInt());

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
			if(userInput.equals("3")) {
				//skips logic and returns to menu
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



