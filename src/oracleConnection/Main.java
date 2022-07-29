package oracleConnection;

import java.sql.*;
import java.util.Scanner;

public class Main {

	static final String user = "T11";
	static final String pword = "Summer2022T11";
	static final String url = "jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
	private Connection con;

	void printResults(ResultSet set) throws SQLException {
		ResultSetMetaData metaData = set.getMetaData();

		// print header
		for(int i = 0; i < metaData.getColumnCount(); i++) {
			String columnName = metaData.getColumnName(i + 1);

			System.out.print(columnName + " ");
		}

		System.out.println();

		while(set.next()) {
			for(int i = 0; i < metaData.getColumnCount(); i++) {
				System.out.print(set.getString(i + 1) + " ");
			}

			System.out.println();
		}
		System.out.println("---------------------");
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
		System.out.println("\nUNPAID BILL(S) REPORT:");
		System.out.println("---------------------");
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
		System.out.println("\nPAID BILL(S) REPORT:");
		System.out.println("---------------------");
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
		System.out.println("\nACTIVE PATIENT(S) REPORT:");
		System.out.println("---------------------");
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
		System.out.println("\nACTIVE DOCTOR(S) REPORT:");
		System.out.println("---------------------");
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

	public Boolean verifyUserInput(String input) {
		//asks user to verify their input
		//if 1 -> return true
		//if 2 -> return false
		Scanner sc = new Scanner(System.in);
		String userInput;
		System.out.println("You entered: " + input + ". Is this correct?");
		System.out.println("1. Correct");
		System.out.println("2. Incorrect");

		userInput = sc.next(); //user input validation, correct or incorrect

		if (userInput.equals("1")) {
			return true; //exit loop
		} else if (userInput.equals("2")) {
			return false; //loop
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		try {
			Main pgm = new Main();
			pgm.con = pgm.GetConnection();
			pgm.MainMenu();
			pgm.con.close();
		}
		catch (SQLException se)
		{
			System.out.printf("Main method has an SQL issue: %s.\n", se);
		}
	}

	//Connection
	Connection GetConnection() throws SQLException {
		return DriverManager.getConnection(url, user, pword);
	}

	//Main menu
	void MainMenu () throws SQLException {
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
		}
	}

	private void Create_Reports() {
		Scanner sc = new Scanner(System.in);

		System.out.println("Reports Menu:");
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
	void Create_Update_Patient() {
		try {
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			while(true) {
				System.out.println("Patient Menu:");
				System.out.println("1. Create new Patient");
				System.out.println("2. Update existing Patient");
				System.out.println("3. Back");
				userInput = sc.next();
				if (userInput.equals("1")) {

					String qry = "INSERT INTO PATIENT (fname, minit, lname, dob ) VALUES (?,?,?,?)";
					PreparedStatement statement = null;
					statement = con.prepareStatement(qry);

					System.out.println("Enter patient first name:");
					statement.setString(1, sc.next());

					System.out.println("Enter patient last name:");
					statement.setString(2, sc.next());

					System.out.println("Enter patient Middle Initial:");
					statement.setString(3, sc.next());

					System.out.println("Enter patient date of birth: (YYYY-MM-DD)");
					statement.setDate(4, Date.valueOf(sc.next())); //Date format Example: YYYY-MM-DD

					statement.execute();
				} else if (userInput.equals("2")) {
					//ask user for patient id they wish to edit
					System.out.println("Enter existing patient ID:");
					int patientID = sc.nextInt();

					while (true) {
						System.out.println("What do you want to update:");
						System.out.println("1. First Name");
						System.out.println("2. Last Name");
						System.out.println("3. Middle Initial");
						System.out.println("4. Date of Birth");
						System.out.println("5. Back");
						userInput = sc.next();

						if (userInput.equals("1")) {
							String qry = "UPDATE PATIENT set fname = ? where patient = ?"; //update fname record from patient ID. EX. UPDATE PATIENT set minit = t WHERE patient = 123
							PreparedStatement statement = con.prepareStatement(qry);

							String fname = "";
							Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
							while (flag == false) {
								System.out.println("Enter Patient's new First Name.");
								fname = sc.next();
								flag = verifyUserInput(fname);
							}//end while

							statement.setString(1, fname);
							statement.setInt(2, patientID);
							statement.executeUpdate();
						} //UPDATE PATIENT LNAME
						else if (userInput.equals("2")) {

							String qry = "UPDATE PATIENT set lname = ? where patient = ?"; //updates patient lname based on patient ID
							PreparedStatement statement = con.prepareStatement(qry);

							String lname = "";
							Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
							while (flag == false) {
								System.out.println("Enter Patient's new Last Name.");
								lname = sc.next();
								flag = verifyUserInput(lname);
							}//end while

							statement.setString(1, lname);
							statement.setInt(2, patientID);
							statement.executeUpdate();
						} else if (userInput.equals("3")) {

							String qry = "UPDATE PATIENT set minit = ? where patient = ?"; //update patient minit based on patient ID
							PreparedStatement statement = con.prepareStatement(qry);

							String minit = "";
							Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
							while (flag == false) {
								System.out.println("Enter Patient's new middle initial.");
								minit = sc.next();
								flag = verifyUserInput(minit);
							}//end while

							statement.setString(1, minit);
							statement.setInt(2, patientID);
							statement.executeUpdate();
						} else if (userInput.equals("4")) {

							String qry = "UPDATE PATIENT set dob = ? where patient = ?"; //update patient dob based on patient id
							PreparedStatement statement = con.prepareStatement(qry);

							String dob = "";
							Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
							while (flag == false) {
								System.out.println("Enter Patient's new Date of Birth. (YYYY-MM-DD)");
								dob = sc.next();
								flag = verifyUserInput(dob);
							}//end while
							statement.setDate(1, Date.valueOf(dob));
							statement.setInt(2, patientID);
							statement.executeUpdate();
						} else if (userInput.equals("5")) {
							break;
							//back choice
						}
					}
				} else if (userInput.equals("3")) {
					break;
					//skips logic and returns to main menu
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
	}

	// Create or update Doctor
	void Create_Update_Doctor() {
		try {
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			while (true) {
				System.out.println("Doctor Menu:");
				System.out.println("1. Create new Doctor");
				System.out.println("2. Update existing Doctor");
				System.out.println("3. Back");
				userInput = sc.next();
				if (userInput.equals("1")) {

					String qry = "INSERT INTO DOCTOR (fname, lname, minit) VALUES (?,?,?)";

					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter doctor first name:");
					statement.setString(1, sc.next());

					System.out.println("Enter doctor last name:");
					statement.setString(2, sc.next());

					System.out.println("Enter doctor middle initial:");
					statement.setString(3, sc.next());

					statement.execute();

					System.out.println("Doctor successfully created.");

				} else if (userInput.equals("2")) {
					System.out.println("Enter existing Doctor ID:");
					int doctorID = sc.nextInt();

					while (true) {
						System.out.println("Doctor Update Menu");
						System.out.println("What do you want to update:");
						System.out.println("1. First Name");
						System.out.println("2. Last Name");
						System.out.println("3. Middle Initial");
						System.out.println("4. Back");
						userInput = sc.next();

						if (userInput.equals("1")) {
							String qry = "UPDATE DOCTOR set fname = ? where doctorid = ?";
							PreparedStatement statement = con.prepareStatement(qry);

							//System.out.println("Enter Doctor's new First Name.");
							String fname = "";
							Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
							while (flag == false) {
								System.out.println("Enter Doctor's new First Name.");
								fname = sc.next();
								flag = verifyUserInput(fname);
							}//end while
							statement.setString(1, fname);
							statement.setInt(2, doctorID);
							statement.executeUpdate();
							System.out.println("Doctor's first name successfully updated.\n");
						} else if (userInput.equals("2")) {

							String qry = "UPDATE DOCTOR set lname = ? where doctorid = ?";
							PreparedStatement statement = con.prepareStatement(qry);

							//System.out.println("Enter Doctor's new Last Name.");
							String lname = "";
							Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
							while (flag == false) {
								System.out.println("Enter Doctor's new Last Name.");
								lname = sc.next();
								flag = verifyUserInput(lname);
							}//end while
							statement.setString(1, lname);
							statement.setInt(2, doctorID);
							statement.executeUpdate();
							System.out.println("Doctor's last name successfully updated.\n");
						} else if (userInput.equals("3")) {

							String qry = "UPDATE DOCTOR set minit = ? where doctorid = ?";
							PreparedStatement statement = con.prepareStatement(qry);

							//System.out.println("Enter Doctor's new middle initial.");
							String minit = "";
							Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
							while (flag == false) {
								System.out.println("Enter Doctor's new middle initial.");
								minit = sc.next();
								flag = verifyUserInput(minit);
							}//end while
							statement.setString(1, minit);
							statement.setInt(2, doctorID);
							statement.executeUpdate();
							System.out.println("Doctor's middle initial successfully updated.\n");
						} else if (userInput.equals("4")) {
							break;
							//skip logic and return to previous menu
						}
					}
				} else if (userInput.equals("3")) {
					break;
					//skips logic and returns to menu
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
	}

	// Create or update appointment
	void Create_Update_Appointment() {
		try {
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			while(true) {
				System.out.println("Appointment Menu:");
				System.out.println("1. Create new Appointment.");
				System.out.println("2. Update existing Appointment.");
				System.out.println("3. Bill Appointment");
				System.out.println("4. Cancel Appointment");
				System.out.println("5. Back");
				userInput = sc.next();

				if (userInput.equals("1")) {
					String qry = "INSERT INTO APPOINTMENT (datetime, PATIENTS_PATIENTID, DOCTORS_DOCTORID ) VALUES (?,?,?)";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter Date/Time: (YYYY-MM-DD)");
					statement.setDate(1, Date.valueOf(sc.next())); //Date format Example: YYYY-MM-DD

					System.out.println("Enter patientID:");
					statement.setInt(2, sc.nextInt());

					System.out.println("Enter doctorID:");
					statement.setInt(3, sc.nextInt());

					statement.execute();

					System.out.println("Appointment successfully created\n");

				} else if (userInput.equals("2")) {
					System.out.println("What do you want to update:");
					System.out.println("1. Appointment Date/Time (YYYY-MM-DD).");
					userInput = sc.next();
					if (userInput.equals("1")) {
						//String qry = "UPDATE APPOINTMENT set datetime = ? where datetime = ?";
						String qry = "UPDATE APPOINTMENT set datetime = ? where apptid = ?";
						PreparedStatement statement = con.prepareStatement(qry);

						System.out.println("Enter appointment's new Date/Time (YYYY-MM-DD).");
						statement.setDate(1, Date.valueOf(sc.next()));

					/*System.out.println("Enter appointment's old Date/Time (YYYY-MM-DD).");
					statement.setDate(2, Date.valueOf(sc.next()));*/

						System.out.println("Enter appointment's ID.");
						statement.setInt(2, sc.nextInt());

						statement.executeUpdate();

						System.out.println("Appointment successfully updated.\n");
					}
				}
				if (userInput.equals("3")) {

					Create_Update_Bill();

					//System.out.println("FIXME: LOGIC AND CALL CREATE BILL\n");

				}
				if (userInput.equals("4")) {
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
					System.out.println("Appointment successfully cancelled.\n");
				}
				if (userInput.equals("5")) {
					break;
					//skips logic and returns to menu
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
		catch (IllegalArgumentException IAE) // thrown by valueOf()
		{
			System.out.println("Incorrect Date format. Try Again!");
		}
	}

	// Create or update bill
	void Create_Update_Bill() {
		try {
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			System.out.println("Bill Menu:");
			System.out.println("1. Create new Bill.");
			System.out.println("2. Update existing Bill.");
			System.out.println("3. Direct Payment.");
			System.out.println("4. Submit a claim.");
			System.out.println("5. Back");
			userInput = sc.next();

			if(userInput.equals("1"))
			{
				//should we add to the qry and to the DATABASE, a new tuple in BILL called, items.
				//It will be a String containing numbers. 1 = checkup, 2 = procedure, as is in the while loop below
				//the application can then decode it to determine both the items and the price
				//42133 = Consultation, Immunization, Checkup, Prescription, Prescription
				//the number above, NOT AN INTEGER, but STRING (could be single characters too) is the itemized bill.
				//String qry = "INSERT INTO Bill (amountdue, items, VISIT_APPOINTMENTS_APPTID, VISITS_VISITID ) VALUES (?,?,?,?)";
				String qry = "INSERT INTO Bill (amountdue, items, VISIT_APPOINTMENTS_APPTID) VALUES (?,?,?)";
				PreparedStatement statement = con.prepareStatement(qry);

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

				statement.setInt(1, total); //amount due

				statement.setString(2, bill); //extra tuple in BILL that is String

				System.out.println("Enter appointment ID:");
				statement.setInt(3, sc.nextInt());

				/*System.out.println("Enter Visit ID:");
				statement.setInt(4, sc.nextInt());*/

				statement.execute();

			}
			else if(userInput.equals("2"))
			{
				System.out.println("What do you want to update:");
				System.out.println("1. Bill Amount Due.");
				userInput = sc.next();
				if(userInput.equals("1")) {
					String qry = "UPDATE BILL set amountdue = ? where billid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter bill's new amount due.");
					statement.setInt(1, sc.nextInt());

					/*System.out.println("Enter bill's old amount due.");
					statement.setInt(2, sc.nextInt());*/

					System.out.println("Enter bill's ID.");
					statement.setInt(2, sc.nextInt());

					statement.executeUpdate();
				}
			}
			if(userInput.equals("3"))
			{
				Direct_Payment();
			}
			if(userInput.equals("4"))
			{
				Create_Claim();
			}
			if(userInput.equals("5"))
			{
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
	}

	//Patient Direct Payment
	void Direct_Payment() throws SQLException {

		Scanner sc = new Scanner(System.in);
		System.out.println();
		createUnpaidBillsReport();

		System.out.print("Enter Patient's bill ID: ");
		int billid = sc.nextInt();

		System.out.print("Enter Patients payment amount: $");
		int paidamount = sc.nextInt();

		String qry = "SELECT amountdue FROM BILL WHERE amountdue > 0 and billid = ?";
		PreparedStatement statement = con.prepareStatement(qry);

		//System.out.print("Enter bill's ID: ");
		//int billid = sc.nextInt();
		statement.setInt(1, billid);

		ResultSet r = statement.executeQuery();
		while(r.next())
		{
			int amountdue = r.getInt(1);

			Payment_Calculator(amountdue, paidamount, billid);
		}
	}

	// TODO: fix database claim table
	void Create_Claim() throws SQLException {
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			while (true) {
				System.out.println("Claim Menu:");
				System.out.println("1. Create new Claim");
				System.out.println("2. Update existing Claim");
				System.out.println("3. Back");
				userInput = sc.next();
				if (userInput.equals("1")) {

					String qry = "INSERT INTO CLAIM () VALUES ()";
					PreparedStatement statement = null;
					statement = con.prepareStatement(qry);

					System.out.println("Enter patient :");
					statement.setString(1, sc.next());

					System.out.println("Enter patient :");
					statement.setString(2, sc.next());

					System.out.println("Enter patient :");
					statement.setString(3, sc.next());

					statement.execute();
				}
			}
		}

	// Calculate remaining balance after payment
	// Updates bills amountdue in the database
	void Payment_Calculator(int amountDue, int paidAmount, int billid) throws SQLException {

		int amountUnpaid = 0;

		amountUnpaid = amountDue - paidAmount;

		//UPDATE BILL
		String qry = "UPDATE BILL set amountdue = ? where billid = ?";
		PreparedStatement statement = con.prepareStatement(qry);

		statement.setInt(1, amountUnpaid);

		statement.setInt(2, billid);
		statement.executeUpdate();

		//SELECT BILL REMAINING BALANCE
		qry = "SELECT amountdue FROM BILL WHERE billid = ?";
		statement = con.prepareStatement(qry);

		statement.setInt(1, billid);

		ResultSet r = statement.executeQuery();
		while(r.next())
		{
			int newAmountDue = r.getInt(1);
			System.out.print("Remaining Balance: $" + newAmountDue + "\n");
		}
	}
}



