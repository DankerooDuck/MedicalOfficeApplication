package oracleConnection;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {

	static final String user = "";
	static final String pword = "";
	static final String url = "";
	private Connection con;

	void printResults(ResultSet set) throws SQLException {
		ResultSetMetaData metaData = set.getMetaData();

		// print header
		for(int i = 0; i < metaData.getColumnCount(); i++) {
			String columnName = metaData.getColumnName(i + 1);
			int columnLen = 0;
			switch(columnName) {
				case "DATETIME":
					columnLen = 20;
					break;
				case "DOB":
					columnLen = 20;
					break;
				case "FNAME":
					columnLen = 10;
					break;
				case "MINIT":
					columnLen = 5;
					break;
				case "LNAME":
					columnLen = 10;
					break;
				case "AGE":
					columnLen = 4;
					break;
				case "INSURANCE_NAME":
					columnLen = 20;
					break;
				case "INSURANCE_ID":
					columnLen = 15;
					break;
				case "ITEMS":
					columnLen = 5;
					break;
				default:
					columnLen = 6;
			} // End Switch
			String padded = String.format("%"+columnLen+"s"+" ", columnName);
			System.out.print(padded);
		}

		System.out.println();

		while(set.next()) {
			for(int i = 0; i < metaData.getColumnCount(); i++) {
				String temp = metaData.getColumnName(i + 1); // Get column name
				int columnLen = 0;
				String line = (set.getString(i + 1));
				// Pad this string with spaces on the left to columnLen Length
				switch(temp) {
					case "DATETIME":
						columnLen = 20;
						break;
					case "DOB":
						columnLen = 20;
						break;
					case "FNAME":
						columnLen = 10;
						break;
					case "MINIT":
						columnLen = 5;
						break;
					case "LNAME":
						columnLen = 10;
						break;
					case "AGE":
						columnLen = 4;
						break;
					case "INSURANCE_NAME":
						columnLen = 20;
						break;
					case "INSURANCE_ID":
						columnLen = 15;
						break;
					case "ITEMS":
						columnLen = 5;
						break;
					default:
						columnLen = temp.length();
				} // End Switch
				String padded = String.format("%"+columnLen+"s"+" ", line);
				System.out.print(padded);
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

	public void createOutstandingAppointmentsReportByDateRange() throws SQLException {
		String qry = "SELECT apptid, datetime, patients_patientid, doctors_doctorid"
				+ " FROM appointment WHERE datetime"
				+ " BETWEEN ? AND ?";

		PreparedStatement statement = con.prepareStatement(qry);
		String dateTemp = updateDate(5);
		Date date = Date.valueOf(dateTemp);
		statement.setDate(1, date);

		String dateTemp2 = updateDate(6);
		Date date2 = Date.valueOf(dateTemp2);
		statement.setDate(2, date2);

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

	public void createUnpaidBillsReportByCustomer(int patientid) throws SQLException {
		//take patient id
		//select unpaid bills where amountdue > 0 where patientid = ?

		System.out.println("\nUNPAID BILL(S) REPORT:");
		System.out.println("---------------------");
		String qry = "SELECT bill.billid, patient.fname, patient.minit, patient.lname, appointment.datetime"
				+ " FROM bill"
				+ " INNER JOIN appointment ON bill.visit_appointments_apptid=appointment.apptid"
				+ " INNER JOIN patient ON appointment.patients_patientid=patient.patient"
				+ " WHERE bill.amountdue > 0"
				+ " AND patient.patient = ?";

		PreparedStatement statement = con.prepareStatement(qry);

		statement.setInt(1, patientid);
		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	public void createUnpaidBillsReportByDate() throws SQLException {
		System.out.println("\nUNPAID BILL(S) REPORT:");
		System.out.println("---------------------");
		String qry = "SELECT bill.billid, patient.fname, patient.minit, patient.lname, appointment.datetime, bill.amountdue"
				+ " FROM bill"
				+ " INNER JOIN appointment ON bill.visit_appointments_apptid=appointment.apptid"
				+ " INNER JOIN patient ON appointment.patients_patientid=patient.patient"
				+ " WHERE bill.amountdue > 0"
				+ " AND appointment.datetime BETWEEN ? AND ?";

		//FROM BILL WHERE amountdue > 0
		PreparedStatement statement = con.prepareStatement(qry);
		String dateTemp = updateDate(5);
		Date date = Date.valueOf(dateTemp);
		statement.setDate(1, date);

		String dateTemp2 = updateDate(6);
		Date date2 = Date.valueOf(dateTemp2);
		statement.setDate(2, date2);
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

	public void createPaidBillsReportByCustomer(int patientid) throws SQLException {
		//take patient id
		//select unpaid bills where amountdue > 0 where patientid = ?

		System.out.println("\nPAID BILL(S) REPORT:");
		System.out.println("---------------------");
		String qry = "SELECT bill.billid, patient.fname, patient.minit, patient.lname, appointment.datetime"
				+ " FROM bill"
				+ " INNER JOIN appointment ON bill.visit_appointments_apptid=appointment.apptid"
				+ " INNER JOIN patient ON appointment.patients_patientid=patient.patient"
				+ " WHERE bill.amountdue = 0"
				+ " AND patient.patient = ?";

		PreparedStatement statement = con.prepareStatement(qry);

		statement.setInt(1, patientid);
		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	public void createPaidBillsReportByDate() throws SQLException {
		System.out.println("\nPAID BILL(S) REPORT:");
		System.out.println("---------------------");
		String qry = "SELECT bill.billid, patient.fname, patient.minit, patient.lname, appointment.datetime, bill.amountdue"
				+ " FROM bill"
				+ " INNER JOIN appointment ON bill.visit_appointments_apptid=appointment.apptid"
				+ " INNER JOIN patient ON appointment.patients_patientid=patient.patient"
				+ " WHERE bill.amountdue = 0"
				+ " AND appointment.datetime BETWEEN ? AND ?";

		//FROM BILL WHERE amountdue > 0
		PreparedStatement statement = con.prepareStatement(qry);
		String dateTemp = updateDate(5);
		Date date = Date.valueOf(dateTemp);
		statement.setDate(1, date);

		String dateTemp2 = updateDate(6);
		Date date2 = Date.valueOf(dateTemp2);
		statement.setDate(2, date2);
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
		String qry = "SELECT * FROM PATIENT where patient_records = ?";

		PreparedStatement statement = con.prepareStatement(qry);

		statement.setString(1, "Active");

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	public void createInactivePatientReport() throws SQLException {
		System.out.println("\nINACTIVE PATIENT(S) REPORT:");
		System.out.println("---------------------");
		// TODO: are active patients really ones that have outstanding bills? or should it based on upcoming appts or something similar?
		String qry = "SELECT * FROM PATIENT where patient_records = ?";

		PreparedStatement statement = con.prepareStatement(qry);

		statement.setString(1, "Active");

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
		String qry = "SELECT * FROM DOCTOR WHERE doctor_records = ?";

		PreparedStatement statement = con.prepareStatement(qry);
		String doctorRecords = "Active";
		statement.setString(1, doctorRecords);
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
		blankLine();
		System.out.println("You entered: " + input + ". Is this correct?");
		System.out.println("1. Correct");
		System.out.println("2. Incorrect");
		userInput = sc.next(); //user input validation, correct or incorrect
		blankLine();
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

	public void setPatientActivityMenu(int patientID) throws SQLException {
		Scanner sc = new Scanner(System.in);
		viewPatientActivity();
		blankLine();
		int tempID = patientID;

		System.out.println("Set Patient Activity to: ");
		System.out.println("1. Active: ");
		System.out.println("2. Inactive: ");
		int desiredActivity = sc.nextInt();
		blankLine();
		switch(desiredActivity) {
			case 1:
				setPatientActivity(tempID, true);
				break;
			case 2:
				setPatientActivity(tempID, false);
				break;
		}
	}

	public void setDoctorActivityMenu(int doctorID) throws SQLException {
		Scanner sc = new Scanner(System.in);
		int tempID = doctorID;

		setDoctorActivityMenuViewDoctors(tempID);
		blankLine();


		System.out.println("Set Doctor Activity to: ");
		System.out.println("1. Active: ");
		System.out.println("2. Inactive: ");
		int desiredActivity = sc.nextInt();
		blankLine();
		switch(desiredActivity) {
			case 1:
				setDoctorActivity(tempID, true, 1);
				break;
			case 2:
				setDoctorActivity(tempID, false, 1);
				break;
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
			blankLine();
			switch(input) {
				case 1:
					createOutstandingAppointmentsReport();
					blankLine();
					createOutstandingAppointmentsReportByDateRange();

					break;
				case 2:
					System.out.println("Create Unpaid Bill Report by:");
					System.out.println("1. Customer");
					System.out.println("2. Date Range");
					System.out.println("3. Back");
					input = sc.nextInt();
					blankLine();

					//submenu
					switch(input) {
						case 1: //sort by customer
							viewPatients();
							blankLine();
							int patientID = userInputInts(1);
							createUnpaidBillsReportByCustomer(patientID);
							break;

						case 2: //sort by date range
							createUnpaidBillsReport();
							blankLine();
							createUnpaidBillsReportByDate();
							break;

						case 3:
							break;
					}//end submenu
					break;
				case 3:
					System.out.println("Create Paid Bill Report by:");
					System.out.println("1. Customer");
					System.out.println("2. Date Range");
					System.out.println("3. Back");
					input = sc.nextInt();
					blankLine();

					//submenu
					switch(input) {
						case 1:
							viewPatients();
							blankLine();
							int patientID = userInputInts(1);
							createPaidBillsReportByCustomer(patientID);
							break;
						case 2:
							createPaidBillsReportByDate();
							break;
					}//end submenu
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
				blankLine();

				if (userInput.equals("1")) {

					String qry = "INSERT INTO PATIENT (fname, minit, lname, dob, insurance_name, insurance_id, patient_records) VALUES (?,?,?,?,?,?,?)";
					PreparedStatement statement = null;
					statement = con.prepareStatement(qry);

					//PATIENT FIRST NAME
					String fname = userInput(1);
					statement.setString(1, fname);

					//PATIENT MIDDLE INITAL
					String minit = userInput(2);
					statement.setString(2, minit);

					//PATIENT LAST NAME
					String lname = userInput(3);
					statement.setString(3, lname);

					//PATIENT DATE OF BIRTH
					String tempDate = updateDate(1);
					statement.setDate(4, Date.valueOf(tempDate)); //Date format Example: YYYY-MM-DD

					System.out.println("Does the patient have insurance?");
					System.out.println("1. Yes");
					System.out.println("2. No");
					userInput = sc.next();
					blankLine();

					String insuranceName = "";
					int insuranceID = 0;
					switch(userInput) {
						case "1":
							insuranceName = userInput(13);
							statement.setString(5, insuranceName);
							System.out.println("Enter Insurance ID:");
							insuranceID = sc.nextInt();
							statement.setInt(6, insuranceID);
							break;
						case "2":
							insuranceName = null;
							statement.setString(5, insuranceName);
							insuranceID = 0;
							statement.setInt(6, insuranceID);
							break;
					}
					statement.setString(7, "Inactive"); //set patient activity to inactive by default
					//CREATE NEW PATIENT COMPLETE
					statement.execute();
					System.out.println("Patient Successfully Created.\n");
				} else if (userInput.equals("2")) {

					//Print Patient List
					viewPatients();
					blankLine();

					System.out.println("Enter ID of PATIENT you wish to EDIT:");
					int patientID = sc.nextInt();
					blankLine();

					while (true) {
						System.out.println("What do you want to update:");
						System.out.println("1. First Name");
						System.out.println("2. Middle Initial");
						System.out.println("3. Last Name");
						System.out.println("4. Date of Birth");
						System.out.println("5. Patient Records");
						System.out.println("6. Back");
						userInput = sc.next();
						blankLine();

						if (userInput.equals("1")) {
							//PATIENT NEW FIRST NAME
							String qry = "UPDATE PATIENT set fname = ? where patient = ?"; //update fname record from patient ID. EX. UPDATE PATIENT set minit = t WHERE patient = 123
							PreparedStatement statement = con.prepareStatement(qry);

							String fname = userInput(4);
							statement.setString(1, fname);
							statement.setInt(2, patientID);
							statement.executeUpdate();
						} else if (userInput.equals("2")) {
							//PATIENT NEW MIDDLE INITIAL
							String qry = "UPDATE PATIENT set minit = ? where patient = ?"; //update patient minit based on patient ID
							PreparedStatement statement = con.prepareStatement(qry);

							String minit = userInput(5);
							statement.setString(1, minit);
							statement.setInt(2, patientID);
							statement.executeUpdate();
						} else if (userInput.equals("3")) {
							//PATIENT NEW LAST NAME
							String qry = "UPDATE PATIENT set lname = ? where patient = ?"; //updates patient lname based on patient ID
							PreparedStatement statement = con.prepareStatement(qry);

							String lname = userInput(6);
							statement.setString(1, lname);
							statement.setInt(2, patientID);
							statement.executeUpdate();
						} else if (userInput.equals("4")) {
							//NEW PATIENT DATE OF BIRTH
							String qry = "UPDATE PATIENT set dob = ? where patient = ?"; //update patient dob based on patient id
							PreparedStatement statement = con.prepareStatement(qry);
							String tempDate = updateDate(2);
							statement.setDate(1, Date.valueOf(tempDate));
							statement.setInt(2, patientID);

							statement.executeUpdate();
						}
						else if (userInput.equals("5")) {
							setPatientActivityMenu(patientID);
						}
						else if (userInput.equals("6")) {
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
		catch (InputMismatchException IME)
		{
			System.out.println("Invalid Input. Try Again!");
		}
		catch (NullPointerException NPE)
		{
			System.out.println("Invalid Input. Try Again! ");
		}
		catch (SQLException x)
		{
			System.out.println(x);
		}
		catch (Exception e)
		{
			System.out.println("Unexpected error. Try Again!");
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
				blankLine();

				if (userInput.equals("1")) {

					String qry = "INSERT INTO DOCTOR (fname, minit, lname, doctor_records) VALUES (?,?,?,?)";

					PreparedStatement statement = con.prepareStatement(qry);

					//DOCTOR FIRST NAME
					String fname = userInput(7);
					statement.setString(1, fname);

					//DOCTOR MIDDLE INITIAL
					String minit = userInput(8);
					statement.setString(2, minit);

					//DOCTOR LAST NAME
					String lname = userInput(9);
					statement.setString(3, lname);

					String records = "Inactive"; //set doctor to inactive by default
					statement.setString(4, records);

					statement.execute();

					System.out.println("New Doctor Successfully Created.\n");;


				} else if (userInput.equals("2")) {

					blankLine();
					viewDoctors();
					blankLine();

					System.out.println("Enter ID of DOCTOR you wish to EDIT:");
					int doctorID = sc.nextInt();
					blankLine();

					while (true) {
						System.out.println("What do you want to update:");
						System.out.println("1. First Name");
						System.out.println("2. Middle Initial");
						System.out.println("3. Last Name");
						System.out.println("4. Activity");
						System.out.println("5. Back");
						userInput = sc.next();
						blankLine();

						if (userInput.equals("1")) {
							//DOCTOR NEW FIRST NAME
							String qry = "UPDATE DOCTOR set fname = ? where doctorid = ?";
							PreparedStatement statement = con.prepareStatement(qry);

							String fname = userInput(10);
							statement.setString(1, fname);
							statement.setInt(2, doctorID);
							statement.executeUpdate();
						} else if (userInput.equals("2")) {
							//DOCTOR NEW MIDDLE INITIAL
							String qry = "UPDATE DOCTOR set minit = ? where doctorid = ?";
							PreparedStatement statement = con.prepareStatement(qry);

							String minit = userInput(11);
							statement.setString(1, minit);
							statement.setInt(2, doctorID);
							statement.executeUpdate();
							System.out.println("Doctor's Middle Initial Successfully Updated.\n");
						} else if (userInput.equals("3")) {
							//DOCTOR NEW LAST NAME
							String qry = "UPDATE DOCTOR set lname = ? where doctorid = ?";
							PreparedStatement statement = con.prepareStatement(qry);

							String lname = userInput(12);
							statement.setString(1, lname);
							statement.setInt(2, doctorID);
							statement.executeUpdate();
							System.out.println("Doctor's Last Name Successfully Updated.\n");
						} else if (userInput.equals("4")) {
							//DOCTOR CHANGE ACTIVITY STATUS
							setDoctorActivityMenu(doctorID);
						} else if (userInput.equals("5")) {
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
		catch (InputMismatchException IME)
		{
			System.out.println("Invalid Input. Try Again!");
		}
		catch (NullPointerException NPE)
		{
			System.out.println("Invalid Input. Try Again! ");
		}
		catch (SQLException x)
		{
			System.out.println(x);
		}
		catch(Exception e)
		{
			System.out.println("Unexpected error. Try Again");
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
				blankLine();

				if (userInput.equals("1")) {
					String qry = "INSERT INTO APPOINTMENT (datetime, PATIENTS_PATIENTID, DOCTORS_DOCTORID ) VALUES (?,?,?)";
					PreparedStatement statement = con.prepareStatement(qry);

					String tempDate = updateDate(3);
					statement.setDate(1, Date.valueOf(tempDate)); //Date format Example: YYYY-MM-DD

					viewPatients();
					blankLine();
					int patientID = userInputInts(1);
					statement.setInt(2, patientID);
					blankLine();

					viewDoctors();
					blankLine();
					int doctorID = userInputInts(9);
					statement.setInt(3, doctorID);
					blankLine();

					statement.execute();

					System.out.println("New Appointment Successfully Created.\n");

					setPatientActivity(patientID, true); //appointment created, so try to set patient activity to true
					setDoctorActivity(doctorID, true, 1); //appointment created, so set doctor activity to true

				} else if (userInput.equals("2")) {
					int apptID = 0;
					viewAppointments();
					blankLine();
					System.out.println("Enter ID of APPOINTMENT you wish to EDIT: ");
					apptID = sc.nextInt();
					blankLine();

					System.out.println("What do you want to update:");
					System.out.println("1. Appointment Date/Time (YYYY-MM-DD).");
					System.out.println("2. Back");
					userInput = sc.next();
					blankLine();
					if (userInput.equals("1")) {
						String qry = "UPDATE APPOINTMENT set datetime = ? where apptid = ?";
						PreparedStatement statement = con.prepareStatement(qry);
						String apptDate = updateDate(4);
						Date newDate = Date.valueOf(apptDate);

						statement.setDate(1, newDate);
						statement.setInt(2, apptID);
						statement.executeUpdate();

						//System.out.println("Appointment successfully updated.\n");
					} else if (userInput.equals("2")) {
						//return to main menu
					}
				}
				if (userInput.equals("3")) {

					Create_Update_Bill();
					//System.out.println("FIXME: LOGIC AND CALL CREATE BILL\n");
				}
				if (userInput.equals("4")) {
					try {
						viewAppointments();
						blankLine();
						String qry = "DELETE FROM APPOINTMENT WHERE APPTID = ?";
						PreparedStatement statement = con.prepareStatement(qry);

						int appointmentID = 0;
						Boolean flag = false; //loop if flag false, if user selects correct -> boolean == true -> exit loop
						while (flag == false) {
							System.out.println("Enter the ID of the appointment you wish to DELETE: ");
							appointmentID = sc.nextInt();
							blankLine();
							System.out.println("Are you sure you want to delete Appointment " + appointmentID + ". Is this correct?");
							System.out.println("This action CANNOT be undone.");
							System.out.println("1. Correct");
							System.out.println("2. Incorrect");
							userInput = sc.next(); //user input validation, correct or incorrect
							blankLine();

							if (userInput.equals("1")) {
								flag = true; //exit loop
							} else if (userInput.equals("2")) {
								flag = false; //loop input
							}
						}//end while
						statement.setInt(1, appointmentID);

						int patientID = getPatientIDFromAppointment(appointmentID); //get patientID from appointment before deleted

						statement.executeUpdate(); //execute appointment delete

						System.out.println("Successfully Deleted Appointment.");
						System.out.println("Attempting to set Patient Records to \"Inactive\"");
						setPatientActivity(patientID, false);
						blankLine();
					}
					catch(SQLIntegrityConstraintViolationException s)
					{
						System.out.println("Invalid action. This appointment has already been billed.\n");
					}

				}
				if (userInput.equals("5")) {
					break;
					//skips logic and returns to menu
				}
			}
		}
		catch (InputMismatchException IME)
		{
			System.out.println("Invalid Input. Try Again!");
		}
		catch(SQLIntegrityConstraintViolationException s)
		{
			System.out.println("Invalid input non-existent Patient or Doctor ID!");
		}
		catch (NullPointerException NPE) {
			System.out.println("Invalid Input. Try Again! ");
		}
		catch (IllegalArgumentException IAE){ // thrown by valueOf()
			System.out.println("Incorrect Date format. Try Again!");
		}
		catch (SQLException x) {
			System.out.println("This appointment ID does not exist! Try Again!");
		}
		catch (Exception e)
		{
			System.out.println("Unexpected error. Try Again!");
		}
	}

	public int getPatientIDFromAppointment(int appointmentID) throws SQLException {
		//attempts to get a patients ID from an appointment
		//input: appointment id
		//output: patient id
		String qry = "SELECT patients_patientid FROM appointment where apptid = ?";
		PreparedStatement statement = con.prepareStatement(qry);
		statement.setInt(1, appointmentID);

		ResultSet r = statement.executeQuery();
		r.next();
		int tempID = r.getInt(1);
		return tempID;
	}

	// Create or update bill
	void Create_Update_Bill() {
		try {
			String userInput = "";
			Scanner sc = new Scanner(System.in);
			System.out.println("Bill Menu:");
			System.out.println("1. Create New Bill");
			System.out.println("2. Update Existing Bill");
			System.out.println("3. Direct Payment");
			System.out.println("4. Submit Insurance Claim");
			System.out.println("5. Print Bill");
			System.out.println("6. Back");
			userInput = sc.next();

			if(userInput.equals("1"))
			{
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

				viewAppointments();
				blankLine();
				System.out.println("Enter ID of APPOINTMENT you wish to BILL:");
				statement.setInt(3, sc.nextInt());
				blankLine();

				/*System.out.println("Enter Visit ID:");
				statement.setInt(4, sc.nextInt());*/

				statement.execute();
				System.out.println("New Bill Successfully Created.");

			}
			else if(userInput.equals("2"))
			{
				int billID = 0;
				createUnpaidBillsReport();
				blankLine();
				createPaidBillsReports();
				blankLine();
				System.out.println("Enter ID of BILL you wish to EDIT: ");
				billID = sc.nextInt();
				blankLine();

				System.out.println("Bill Update Menu:");
				System.out.println("1. Bill Amount Due.");
				System.out.println("2. Back");
				userInput = sc.next();
				if(userInput.equals("1")) {
					String qry = "UPDATE BILL set amountdue = ? where billid = ?";
					PreparedStatement statement = con.prepareStatement(qry);

					System.out.println("Enter bill's new amount due.");
					statement.setInt(1, sc.nextInt());
					statement.setInt(2, billID);
					statement.executeUpdate();
					createUnpaidBillsReport();
					blankLine();
					createPaidBillsReports();
				}
				else if(userInput.equals("2")) {
					//return to previous menu
				}
			}
			if(userInput.equals("3"))
			{
				Direct_Payment();
			}
			if(userInput.equals("4"))
			{
				Create_Update_Submit_Claim();
			}
			if(userInput.equals("5"))
			{
				printBill();
			}
			if(userInput.equals("6"))
			{
				//skips logic and returns to menu
			}
		}
		catch (NullPointerException NPE) {
			System.out.println("setInt throws NULL pointer exception");
		}
		catch (SQLIntegrityConstraintViolationException RIC)
		{
			System.out.println("ID does not exist! Try Again!\n");
		}
		catch (SQLException x)
		{
			System.out.println(x);
		}
		catch(InputMismatchException IME)
		{
			System.out.println("Invalid user input! Try Again!");
		}
		catch(Exception e)
		{
			System.out.println("Unexpected Exception!");
		}

	}

	void Create_Update_Submit_Claim() throws SQLException {

		String userInput = "";
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("\nClaim Menu:");
			System.out.println("1. Create New Claim");
			System.out.println("2. Update Existing Claim");
			System.out.println("3. Submit Existing Claim");
			System.out.println("4. View Settled Claims");
			System.out.println("5. View Unsettled Claims");
			System.out.println("6. Back");
			userInput = sc.next();
			blankLine();
			if (userInput.equals("1")) {
				try {
					//CREATE NEW CLAIM
					String qry = "INSERT INTO CLAIM (insuranceid, patients_patientid, claimamount, billid, settled) VALUES (?,?,?,?,?)"; //claimid generated by DBMS
					PreparedStatement statement = con.prepareStatement(qry);
					statement = con.prepareStatement(qry);

					viewPatients();
					//viewBill(); // we need to see bills not patients
					//get patient id from user
					int patientID = userInputInts(1);
					statement.setInt(2, patientID);

					//get patient insurance id
					int patientInsuranceID = userInputInts(3);
					statement.setInt(1, patientInsuranceID);
					blankLine();

					statement.setString(5, "No"); //set "SETTLED" in DB to "No" by default

					// CHECKING if Insurance ID entered does not match with Patients Insurance ID
					String qry3 = "SELECT patient.insurance_id FROM PATIENT WHERE patient = ? ";
					PreparedStatement statement3 = con.prepareStatement(qry3);
					statement3.setInt(1, patientID);
					statement3.execute();

					ResultSet r = statement3.executeQuery();
					while (r.next()) {
						int InsuranceID = r.getInt(1);

						if (InsuranceID == patientInsuranceID) {
							//PRINT BILLS FOR PATIENTID
							String qry2 = "SELECT bill.billid, patient.fname, patient.minit, patient.lname, bill.amountdue FROM bill"
									+ " INNER JOIN appointment ON bill.visit_appointments_apptid=appointment.apptid"
									+ " INNER JOIN patient ON appointment.patients_patientid=patient.patient"
									+ " WHERE patient.patient = ? ";

							PreparedStatement statement2 = con.prepareStatement(qry2);
							statement2.setInt(1, patientID);

							statement2.execute();

							ResultSet result2 = statement2.executeQuery();
							if (result2 != null) {
								printResults(result2);
							} else {
								System.out.println("Failed to get results for this report.");
							}
							//END PRINT BILL SCREEN

							//get bill id from user
							System.out.println("1. Enter Bill ID: ");
							System.out.println("2. Back");
							userInput = sc.next();

							if (userInput.equals("1")) {
								int billID = userInputInts(7);
								statement.setInt(4, billID);

								//get claim amount from user
								int claimID = userInputInts(6);
								statement.setInt(3, claimID);

								statement.execute();
								blankLine();
								System.out.println("New Claim Successfully Created");
							} else if (userInput.equals("2")) {
								//break;
								//System.out.println("Non-existent Patient ID! Try Again!");
								Create_Update_Submit_Claim();
							}
						} else {
							System.out.println("Insurance ID entered does not match with Patients Insurance ID! Try Again!");
						}
					}
				}
				catch (SQLIntegrityConstraintViolationException e)
				{
					System.out.println("Invalid non-existing Patient or Insurance or Bill ID! Try Again!");
				}
			}
			if(userInput.equals("2"))
			{
				viewClaims();
				blankLine();

				int claimID = userInputInts(11);
				blankLine();

				int menuInt = 0;
				System.out.println("Update Claim Menu:");
				//System.out.println("1. Update Insurance ID:");
				//System.out.println("2. Update Patient ID:");
				System.out.println("1. Update Claim Amount:");
				//System.out.println("4. Update Bill ID");
				System.out.println("2. Back");
				menuInt = sc.nextInt(); //user menu input used for switch below
				blankLine();

				/*if (menuInt == 1) { //UPDATE INSURANCE ID
					String qry = "UPDATE CLAIM SET insuranceid=? where claimid=?";
					PreparedStatement statementInsurance = con.prepareStatement(qry);
					int insuranceID = userInputInts(4);
					statementInsurance.setInt(1, insuranceID);
					statementInsurance.setInt(2, claimID);
					statementInsurance.executeUpdate();
					System.out.println("Successfully updated Insurance ID.");
				}
				if (menuInt == 2) { //UPDATE PATIENT ID
					viewPatients(); //print patients
					String qry = "UPDATE CLAIM set patients_patientid = ? WHERE claimid = ?";
					PreparedStatement statementPatient = con.prepareStatement(qry);
					int patientID = userInputInts(2);
					statementPatient.setInt(1, patientID);
					statementPatient.setInt(2, claimID);
					statementPatient.executeUpdate();
					System.out.println("Successfully updated Patient ID.");
				}*/
				if (menuInt == 1) { //UPDATE CLAIM AMOUNT
					String qry = "UPDATE CLAIM set claimamount = ? WHERE claimid = ?";
					PreparedStatement statementClaimAmnt = con.prepareStatement(qry);
					int claimAmount = userInputInts(6);
					statementClaimAmnt.setInt(1, claimAmount);
					statementClaimAmnt.setInt(2, claimID);
					statementClaimAmnt.executeUpdate();
					System.out.println("Successfully updated Claim Amount.");
				}
				/*if (menuInt == 4) { //UPDATE BILL ID
					String qry = "UPDATE CLAIM set billid = ? WHERE claimid = ?";
					PreparedStatement statementBill = con.prepareStatement(qry);
					int billID = userInputInts(8);
					statementBill.setInt(1, billID);
					statementBill.setInt(2, claimID);
					statementBill.executeUpdate();
					System.out.println("Successfully updated Bill ID.");
				}*/
				else {
					//return to prev menu
				}
			}
			if(userInput.equals("3"))
			{
				//SUBMIT CLAIM
				Claim_Payment(); //pays amount to bill as determined in claim
			}
			if(userInput.equals("4"))
			{
				viewSettledClaims();
				//view settled claims
				break;

			}
			if(userInput.equals("5"))
			{
				viewUnsettledClaims();
				//view unsettled claims
				break;

			}
			if(userInput.equals("6"))
			{
				//Go Back to Main menu
				break;

			}
		}
	}

	public void viewSettledClaims() throws SQLException {
		Scanner sc = new Scanner(System.in);
		int patientID;
		String qry = "(SELECT claim.claimid, patient.fname, patient.minit, patient.lname, patient.insurance_name, patient.insurance_id, bill.billid, bill.amountdue, bill.items, appointment.datetime, claim.claimamount, claim.settled"
				+ " FROM claim"
				+ " INNER JOIN patient ON claim.patients_patientid=patient.patient"
				+ " INNER JOIN bill ON claim.billid=bill.billid"
				+ " INNER JOIN appointment ON bill.visit_appointments_apptid=appointment.apptid"
				+ " WHERE settled = ?)";
		PreparedStatement statement = con.prepareStatement(qry); //print normally
		statement.setString(1, "Yes");

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	public void viewUnsettledClaims() throws SQLException {
		Scanner sc = new Scanner(System.in);
		int patientID;
		String qry = "(SELECT claim.claimid, patient.fname, patient.minit, patient.lname, patient.insurance_name, patient.insurance_id, bill.billid, bill.amountdue, bill.items, appointment.datetime, claim.claimamount, claim.settled"
				+ " FROM claim"
				+ " INNER JOIN patient ON claim.patients_patientid=patient.patient"
				+ " INNER JOIN bill ON claim.billid=bill.billid"
				+ " INNER JOIN appointment ON bill.visit_appointments_apptid=appointment.apptid"
				+ " WHERE settled = ?)";
		PreparedStatement statement = con.prepareStatement(qry); //print normally
		statement.setString(1, "No");

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	public String userInput(int sw) {
		//input is a hardcoded integer for switch below
		//each case calls verifyUserInput()
		// 1 = PATIENT fname
		// 2 = PATIENT minit
		// 3 = PATIENT lname
		// 4 = PATIENT NEW fname
		// 5 = PATIENT NEW minit
		// 6 = PATIENT NEW lname
		// 7 = DOCTOR fname
		// 8 = DOCTOR minit
		// 9 = DOCTOR lname
		// 10 = DOCTOR NEW fname
		// 11 = DOCTOR NEW minit
		// 12 = DOCTOR NEW lname
		// 13 = Insurance Name
		// 14 = New Insurance Name
		//returns fname, minit, etc.
		Scanner sc = new Scanner(System.in);
		Boolean flag = false;
		switch(sw) {
			case 1: //patient fname
				String pfname = "";
				while (flag == false) {
					System.out.println("Enter Patient's First Name:"); //prompt user for input
					pfname = sc.next(); //store input
					flag = verifyUserInput(pfname); //verify input
				}//end while
				return pfname; //return string
			case 2: //patient minit
				String pminit = "";
				while (flag == false) {
					System.out.println("Enter Patient's Middle Initial:"); //prompt user for input
					pminit = sc.next(); //store input
					flag = verifyUserInput(pminit); //verify input
				}//end while
				return pminit; //return string
			case 3: //patient lname
				String plname = "";
				while (flag == false) {
					System.out.println("Enter Patient's Last Name:"); //prompt user for input
					plname = sc.next(); //store input
					flag = verifyUserInput(plname); //verify input
				}//end while
				return plname; //return string
			case 4: //patient NEW fname
				String pfnameNew = "";
				while (flag == false) {
					System.out.println("Enter Patient's New First Name:"); //prompt user for input
					pfnameNew = sc.next(); //store input
					flag = verifyUserInput(pfnameNew); //verify input
				}
				return pfnameNew; //return string
			case 5: //patient NEW minit
				String pminitNew = "";
				while (flag == false) {
					System.out.println("Enter Patient's New Middle Initial:"); //prompt user for input
					pminitNew = sc.next(); //store input
					flag = verifyUserInput(pminitNew); //verify input
				}
				return pminitNew; //return string
			case 6: //patient NEW lname
				String plnameNew = "";
				while (flag == false) {
					System.out.println("Enter Patient's New Last Name:"); //prompt user for input
					plnameNew = sc.next(); //store input
					flag = verifyUserInput(plnameNew); //verify input
				}
				return plnameNew; //return string
			case 7: //doctor fname
				String dfname = "";
				while (flag == false) {
					System.out.println("Enter Doctors's First Name:"); //prompt user for input
					dfname = sc.next(); //store input
					flag = verifyUserInput(dfname); //verify input
				}
				return dfname; //return string
			case 8: //doctor minit
				String dminit = "";
				while (flag == false) {
					System.out.println("Enter Doctor's Middle Initial:"); //prompt user for input
					dminit = sc.next(); //store input
					flag = verifyUserInput(dminit); //verify input
				}
				return dminit; //return string
			case 9: //doctor lname
				String dlname = "";
				while (flag == false) {
					System.out.println("Enter Doctors's Last Name:"); //prompt user for input
					dlname = sc.next(); //store input
					flag = verifyUserInput(dlname); //verify input
				}
				return dlname; //return string
			case 10: //doctor fname
				String dfnameNew = "";
				while (flag == false) {
					System.out.println("Enter Doctors's New First Name:"); //prompt user for input
					dfnameNew = sc.next(); //store input
					flag = verifyUserInput(dfnameNew); //verify input
				}
				return dfnameNew; //return string
			case 11: //doctor minit
				String dminitNew = "";
				while (flag == false) {
					System.out.println("Enter Doctor's New Middle Initial:"); //prompt user for input
					dminitNew = sc.next(); //store input
					flag = verifyUserInput(dminitNew); //verify input
				}
				return dminitNew; //return string
			case 12: //doctor lname
				String dlnameNew = "";
				while (flag == false) {
					System.out.println("Enter Doctors's New Last Name:"); //prompt user for input
					dlnameNew = sc.next(); //store input
					flag = verifyUserInput(dlnameNew); //verify input
				}
				return dlnameNew; //return string
			case 13: //insurance name
				String insuranceName = "";
				while (flag == false) {
					System.out.println("Enter Insurance Name: (No Spaces Allowed/Underscores Only)"); //prompt user for input
					insuranceName = sc.next(); //store input
					flag = verifyUserInput(insuranceName); //verify input
				}
				return insuranceName; //return string
			case 14: //NEW insurance name
				String insuranceNameNew = "";
				while (flag == false) {
					System.out.println("Enter New Insurance Name: (No Spaces Allowed/Underscores Only)"); //prompt user for input
					insuranceNameNew = sc.next(); //store input
					flag = verifyUserInput(insuranceNameNew); //verify input
				}
				return insuranceNameNew; //return string
		}
		return null;
	}

	public Boolean verifyUserInputInt(int input) {
		//asks user to verify their input for integers
		//if 1 -> return true
		//if 2 -> return false
		Scanner sc = new Scanner(System.in);
		int userInput;
		blankLine();
		System.out.println("You entered: " + input + ". Is this correct?");
		System.out.println("1. Correct");
		System.out.println("2. Incorrect");
		userInput = sc.nextInt(); //user input validation, correct or incorrect
		blankLine();
		if (userInput == 1) {
			return true; //exit loop
		} else if (userInput == 2) {
			return false; //loop
		} else {
			return null;
		}
	}

	public int userInputInts(int sw) {
		//input is a hardcoded integer for switch below
		//each case calls verifyUserInput()
		// 1 = patientID
		// 2 = New patientID
		// 3 = insuranceID
		// 4 = newInsuranceID
		// 5 = claimAmount
		// 6 = newClaimAmount
		// 7 = billID
		// 8 = newBillID
		// 9 = doctorID
		// 10 = newDoctorID
		// 11 = claimID
		// 12 = newClaimID
		// 13 =
		// 14 =
		//returns integer value of attributes above
		Scanner sc = new Scanner(System.in);
		Boolean flag = false;
		switch(sw) {
			case 1: //patientID
				int patientID = 0;
				while (flag == false) {
					System.out.println("Enter Patient ID:");
					patientID = sc.nextInt();
					flag = verifyUserInputInt(patientID);
				}//end while
				return patientID;
			case 2: //patientID
				int patientIDNew = 0;
				while (flag == false) {
					System.out.println("Enter New Patient ID:");
					patientIDNew = sc.nextInt();
					flag = verifyUserInputInt(patientIDNew);
				}//end while
				return patientIDNew;
			case 3: //insuranceID
				int insuranceID = 0;
				while (flag == false) {
					System.out.println("Enter Insurance ID:");
					insuranceID = sc.nextInt();
					flag = verifyUserInputInt(insuranceID);
				}//end while
				return insuranceID;
			case 4: //insuranceID
				int insuranceIDNew = 0;
				while (flag == false) {
					System.out.println("Enter New Insurance ID:");
					insuranceIDNew = sc.nextInt();
					flag = verifyUserInputInt(insuranceIDNew);
				}//end while
				return insuranceIDNew;
			case 5: //claimAmount
				int claimAmount = 0;
				while (flag == false) {
					System.out.println("Enter Claim Amount $: ");
					claimAmount = sc.nextInt();
					flag = verifyUserInputInt(claimAmount);
				}
				return claimAmount;
			case 6: //claimAmount
				int claimAmountNew = 0;
				while (flag == false) {
					System.out.println("Enter New Claim Amount $: ");
					claimAmountNew = sc.nextInt();
					flag = verifyUserInputInt(claimAmountNew);
				}
				return claimAmountNew;
			case 7: //billID
				int billID = 0;
				while (flag == false) {
					System.out.println("Enter Bill ID: ");
					billID = sc.nextInt();
					flag = verifyUserInputInt(billID);
				}
				return billID;
			case 8: //billID
				int billIDNew = 0;
				while (flag == false) {
					System.out.println("Enter New Bill ID: ");
					billIDNew = sc.nextInt();
					flag = verifyUserInputInt(billIDNew);
				}
				return billIDNew;
			case 9: //doctorID
				int doctorID = 0;
				while (flag == false) {
					System.out.println("Enter Doctor ID: ");
					doctorID = sc.nextInt();
					flag = verifyUserInputInt(doctorID);
				}
				return doctorID;
			case 10: //newDoctorID
				int doctorIDNew = 0;
				while (flag == false) {
					System.out.println("Enter New Doctor ID: ");
					doctorIDNew = sc.nextInt();
					flag = verifyUserInputInt(doctorIDNew);
				}
				return doctorIDNew;
			case 11: //Claim ID
				int claimID = 0;
				while (flag == false) {
					System.out.println("Enter Claim ID: ");
					claimID = sc.nextInt();
					flag = verifyUserInputInt(claimID);
				}
				return claimID;
			case 12: //Claim ID
				int claimIDNew = 0;
				while (flag == false) {
					System.out.println("Enter New Claim ID: ");
					claimIDNew = sc.nextInt();
					flag = verifyUserInputInt(claimIDNew);
				}
				return claimIDNew;
		}
		return 0;
	}

	public void viewPatientActivity() throws SQLException {
		String qry = "SELECT patient.patient, patient.fname, patient.minit, patient.lname, patient.dob, patient.patient_records FROM PATIENT";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}//end viewPatientsActivity

	public void viewDoctorActivity() throws SQLException {
		String qry = "SELECT doctor.doctorid, doctor.fname, doctor.minit, doctor.lname, doctor.doctor_records FROM doctor";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}//end viewPatientsActivity

	public void viewPatients() throws SQLException {
		String qry = "SELECT patient.patient, patient.fname, patient.minit, patient.lname, patient.dob, patient.insurance_name, patient.insurance_id, patient.patient_records FROM PATIENT";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}//end viewPatients

	public void viewBill() throws SQLException {
		String qry = "SELECT bill.billid, patient.fname, patient.minit, patient.lname, bill.amountdue FROM bill"
				+ " INNER JOIN appointment ON bill.visit_appointments_apptid=appointment.apptid"
				+ " INNER JOIN patient ON appointment.patients_patientid=patient.patient";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}//end viewPatients

	public void viewAppointments() throws SQLException {
		String qry = "SELECT appointment.apptid, patient.fname, patient.lname, doctor.fname, doctor.lname, appointment.datetime FROM appointment INNER JOIN patient ON appointment.patients_patientid=patient.patient INNER JOIN doctor ON appointment.doctors_doctorid=doctor.doctorid";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if(result != null) {
			System.out.println("          PATIENT    PATIENT     DOCTOR     DOCTOR");
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}

	}//end viewAppointments

	public void printBill() throws SQLException {
		//print bills
		//ask user for bill id to print
		Scanner sc = new Scanner(System.in);
		int billID = 0;
		createUnpaidBillsReport();
		blankLine();
		createPaidBillsReports();
		blankLine();
		System.out.println("Enter ID of BILL you wish to PRINT: ");
		billID = sc.nextInt();

		String qry = "(SELECT bill.amountdue, appointment.datetime, patient.fname, patient.minit, patient.lname"
				+ " FROM bill"
				+ " RIGHT JOIN appointment on bill.visit_appointments_apptid=appointment.apptid"
				+ " INNER JOIN patient on appointment.patients_patientid=patient.patient"
				+ " WHERE billid = ?)";
		PreparedStatement statement = con.prepareStatement(qry); //print normally
		statement.setInt(1, billID);

		String qry2 = "SELECT bill.items FROM bill WHERE billid = ?";
		PreparedStatement statement2 = con.prepareStatement(qry2);
		statement2.setInt(1, billID);

		//NORMAL PRINT
		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
		//BILL DECODE PRINT
		ResultSet result2 = statement2.executeQuery();
		if(result2 != null) {
			while(result2.next()) {
				String items = result2.getString(1);
				billDecode(items);
			}
		} else {
			System.out.println("Failed to get results for this report.");
		}

	}

	public String updateDate(int sw) {
		//input is integer for the switch below
		// 1 = Enter Date of Birth
		// 2 = Enter New Date of Birth
		// 3 = Enter Date
		// 3 = Enter New Date
		//for reports
		// 4 = Enter Start Date
		// 5 = Enter End Date
		//returns String hopefully formatted as YYYY-MM-DD
		Scanner sc = new Scanner(System.in);
		String tempDate = null;
		switch (sw) {
			case 1:
				Boolean flag = false;
				while (flag == false) {
					System.out.println("Enter Date of Birth: (YYYY-MM-DD)");
					tempDate = (sc.next());
					flag = verifyUserInput(tempDate);
				} //end while
				break;
			case 2:
				flag = false;
				while (flag == false) {
					System.out.println("Enter New Date of Birth: (YYYY-MM-DD)");
					tempDate = (sc.next());
					flag = verifyUserInput(tempDate);
				} //end while
				break;
			case 3:
				flag = false;
				while (flag == false) {
					System.out.println("Enter Appointment Date: (YYYY-MM-DD)");
					tempDate = (sc.next());
					flag = verifyUserInput(tempDate);
				} //end while
				break;
			case 4:
				flag = false;
				while (flag == false) {
					System.out.println("Enter New Appointment Date: (YYYY-MM-DD)");
					tempDate = (sc.next());
					flag = verifyUserInput(tempDate);
				} //end while
				break;
			case 5:
				flag = false;
				while (flag == false) {
					System.out.println("Enter Start Date: (YYYY-MM-DD)");
					tempDate = (sc.next());
					flag = verifyUserInput(tempDate);
				} //end while
				break;
			case 6:
				flag = false;
				while (flag == false) {
					System.out.println("Enter End Date: (YYYY-MM-DD)");
					tempDate = (sc.next());
					flag = verifyUserInput(tempDate);
				} //end while
				break;
		} //end switch
		blankLine();
		return tempDate;
	} //end updateDateTime

	public void viewDoctors() throws SQLException {
		String qry = "SELECT doctor.doctorid, doctor.fname, doctor.minit, doctor.lname, doctor.doctor_records FROM DOCTOR";

		PreparedStatement statement = con.prepareStatement(qry);

		ResultSet result = statement.executeQuery();
		if (result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	} //end viewDoctors

	public void setDoctorActivityMenuViewDoctors(int doctorID) throws SQLException {
		String qry = "SELECT doctor.doctorid, doctor.fname, doctor.minit, doctor.lname, doctor.doctor_records FROM doctor WHERE doctorid = ?";

		PreparedStatement statement = con.prepareStatement(qry);
		statement.setInt(1, doctorID);
		ResultSet result = statement.executeQuery();
		if (result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	public void viewClaims() throws SQLException {
		Scanner sc = new Scanner(System.in);
		int patientID;
		String qry = "(SELECT claim.claimid, patient.fname, patient.minit, patient.lname, patient.insurance_name, patient.insurance_id, bill.billid, bill.amountdue, bill.items, appointment.datetime, claim.claimamount, claim.settled"
				+ " FROM claim"
				+ " INNER JOIN patient ON claim.patients_patientid=patient.patient"
				+ " INNER JOIN bill ON claim.billid=bill.billid"
				+ " INNER JOIN appointment ON bill.visit_appointments_apptid=appointment.apptid)";
		PreparedStatement statement = con.prepareStatement(qry); //print normally

		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}

	void blankLine() {
		System.out.println("");
	}

	//Patient Direct Payment
	void Direct_Payment() throws SQLException {

		Scanner sc = new Scanner(System.in);
		System.out.println();
		viewBill();

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

	// Calculate remaining balance after payment
	// Updates bills amountdue in the database
	void Payment_Calculator(int amountDue, int paidAmount, int billid) throws SQLException {

		int amountUnpaid = 0;

		amountUnpaid = amountDue - paidAmount;

//		if (amountUnpaid <= 0) {
//			System.out.println("Could not pay bill. Remaining balance would be negative.");
//		}
//		else {
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

	void Claim_Payment() throws SQLException {
		//prints existing claims
		//asks user for which claim to submit (pay)
		//pay bill amount = claim amountdue
		//call function/query to pay
		//print bill table where billid = claim.billid
		//make amount due on claim $0 because the amount was just paid
		viewUnsettledClaims();
		blankLine();

		Scanner sc = new Scanner(System.in);
		String qry = "(SELECT claimamount, claim.billid, bill.amountdue FROM CLAIM INNER JOIN bill ON claim.billid=bill.billid WHERE claimamount > 0 and claimid = ?)"; //PULL CLAIM.CLAIMAMOUNT AND CLAIM.BILLID
		PreparedStatement statement = con.prepareStatement(qry);

		System.out.println("Enter Claim ID for which you wish to SUBMIT:");
		int claimID = sc.nextInt();
		statement.setInt(1, claimID);
		blankLine();

		ResultSet r = statement.executeQuery();
		while(r.next()) {
			int paidamount = r.getInt(1); //amount to pay
			int billid = r.getInt(2); //bill id
			int amountdue = r.getInt(3); //amount due on bill

			qry = "SELECT settled FROM CLAIM WHERE claimid = ?";
			statement = con.prepareStatement(qry);

			statement.setInt(1, claimID);

			r = statement.executeQuery();
			while(r.next()) {
				String settled = r.getString(1);

				// attribute "settled" in the claims table
				// if "No" = process the claim
				if (settled.equals("No"))
				{
					Payment_Calculator(amountdue, paidamount, billid);

					// UPDATE SETTLED ATTRIBUTE TO "YES"
					String nowSettled = "Yes";
					qry = "UPDATE CLAIM set settled = ? where claimid = ?";
					statement = con.prepareStatement(qry);
					statement.setString(1, nowSettled);
					statement.setInt(2, claimID);
					statement.executeUpdate();
				}
				else
				{
					System.out.println("Claim has been settled.");
				}
			}
//        	String qry2 = "UPDATE claimamount = 0 WHERE claimid = ?";
//        	PreparedStatement statement2 = con.prepareStatement(qry2);
		}
//        System.out.print("Enter Patient's Bill ID: ");
//        int billid = sc.nextInt();
//
//        System.out.print("Enter Insurance Claim payment amount: $");
//        int paidamount = sc.nextInt();
//
//        String qry = "SELECT claimamount FROM claim WHERE billid = ?";
//        PreparedStatement statement = con.prepareStatement(qry);
//
//        statement.setInt(1, billid);
//
//        ResultSet r = statement.executeQuery();
//        while(r.next())
//        {
//            int claimPaidAmount = r.getInt(1);
//
//            //Insurance_Payment_Calculator(amountdue, paidamount, billid);
//            //Claim_Payment_Calculator(amountdue, claimPaidAmount, billid);
//        }
	}

	public void setDoctorActivityTrue(int doctorID) throws SQLException {
		//do not directly call this function
		//input patient id to set to active
		String qry = "UPDATE doctor set doctor_records = ? where doctorid = ?";
		PreparedStatement statement = con.prepareStatement(qry);
		statement.setString(1, "Active");
		statement.setInt(2, doctorID);
		statement.executeUpdate();
	}

	public void setDoctorActivityFalse(int doctorID) throws SQLException {
		//do not directly call this function
		//input patient id to set to active
		String qry = "UPDATE doctor set doctor_records = ? where doctorid = ?";
		PreparedStatement statement = con.prepareStatement(qry);
		statement.setString(1, "Inactive");
		statement.setInt(2, doctorID);
		statement.executeUpdate();
	}

	public void setDoctorActivity(int doctorID, Boolean flag, int sw) throws SQLException {
		//checks doctor current activity, then attempts logic based on flag
		//input doctorID and flag
		//flag is whether you want to set doctor activity to true or false
		int tempID = doctorID;

		switch(sw) {
			case 1:
				//if user wants to set activity to true
				if (flag == true) {
					//check doctor current activity
					Boolean apptFlag = checkDoctorAppointments(tempID);

					//doctor does have an appointment today
					if (apptFlag == true) {
						setDoctorActivityTrue(tempID);
					}

					//doctor does not have an appointment today
					if (apptFlag == false) {
						setDoctorActivityTrue(tempID);
					}

				}
				//if user wants to set activity to false
				if (flag == false) {
					//check doctor current activity
					Boolean apptFlag = checkDoctorAppointments(tempID);

					//doctor does have an appointment today
					if (apptFlag == true) {
						setDoctorActivityTrue(tempID);
					}

					//doctor does not have an appointment today
					if (apptFlag == false) {
						setDoctorActivityFalse(tempID);
					}

				}//end if
				break;

			case 2:
				//check doctor current activity (if appointment today)
				Boolean apptFlag = checkDoctorAppointments(tempID);

				//doctor does have an appointment today
				if (apptFlag == true) {
					setDoctorActivityTrue(tempID);
				}
				break;
		}

	}//end setDoctorActivity

	public int countDoctors() throws SQLException {
		//count number of rows in doctor
		String qryCount = "SELECT COUNT(doctorid) FROM doctor";
		PreparedStatement statementCount = con.prepareStatement(qryCount);
		ResultSet count = statementCount.executeQuery();
		count.next();
		int rowCount = count.getInt(1);
		return rowCount;
	}

	public void updateDoctorActivityOnStartup() throws SQLException {
		//runs on application start
		//if doctors have an appointment today, they will become active
		int count = countDoctors();
		count += 1;

		for (int i = 0; i < count; i++) {
			//setDoctorActivity(doctorID, active/inactive, 2nd case)
			setDoctorActivity(i, null, 2);
		}
	}//end setDoctorActivity

	public Boolean checkDoctorAppointments(int doctorID) throws SQLException {
		//check if a doctor has appointments today
		//if doctor appointment today == true
		//	return true; //doctor is active
		//if doctor appointment today == false
		//  return false; //doctor is inactive
		int temp = doctorID;

		String qryCount = "SELECT COUNT(apptid) FROM appointment WHERE doctors_doctorid = ?";
		PreparedStatement statementCount = con.prepareStatement(qryCount);
		statementCount.setInt(1, temp);
		ResultSet count = statementCount.executeQuery();
		count.next();
		int rowCount = count.getInt(1);
		rowCount += 1;

		String qry = "SELECT apptid, datetime FROM appointment WHERE doctors_doctorid = ?";
		PreparedStatement statement = con.prepareStatement(qry);
		statement.setInt(1, temp);
		ResultSet r = statement.executeQuery();
		blankLine();

		//DETERMINE IF APPOINTMENT DATE IS TODAY
		String date = getDate();
		String tempApptDate = "";
		for (int i = 1; i < (rowCount); i++) {
			while(r.next()) {
				//System.out.println(r.getString(i));
				tempApptDate = r.getString(i + 1);
			}
			r.next();
			String apptDate = tempApptDate;
			if (apptDate.compareTo(date) == 0) {
				//return true if appointmentDate is today
				return true;
			}
			else {
				return false;
			}
		}//end for
		blankLine();
		return false;
	}

	public String getDate() throws SQLException {
		String qry = "SELECT TRUNC(CURRENT_DATE) FROM DUAL";
		PreparedStatement statement = con.prepareStatement(qry);
		ResultSet r = statement.executeQuery();
		r.next();
		String date = r.getString(1);
		return date;
	}

	public Boolean getDoctorActivity(int doctorID) throws SQLException {
		//if doctor is active, return true
		//if doctor is inactive, return false
		String qry = "SELECT doctor_records FROM doctor where doctor = ?";
		PreparedStatement statement = con.prepareStatement(qry);
		int temp = doctorID;
		statement.setInt(1, temp);

		ResultSet r = statement.executeQuery();
		while(r.next()) {
			String activity = r.getString(1);
			if (activity == "Active") {
				return true;
			}
			else if (activity == "Inactive") {
				return false;
			}
		}//end while
		return false;
	}

	public void setPatientActivityTrue(int patientID) throws SQLException {
		//do not directly call this function
		//input patient id to set to active
		String qry = "UPDATE patient set patient_records = ? where patient = ?";
		PreparedStatement statement = con.prepareStatement(qry);
		statement.setString(1, "Active");
		statement.setInt(2, patientID);
		statement.executeUpdate();
	}

	public void setPatientActivityFalse(int patientID) throws SQLException {
		//do not directly call this function
		//input patient id to set to active
		String qry = "UPDATE patient set patient_records = ? where patient = ?";
		PreparedStatement statement = con.prepareStatement(qry);
		statement.setString(1, "Inactive");
		statement.setInt(2, patientID);
		statement.executeUpdate();
	}

	public void setPatientActivity(int patientID, Boolean flag) throws SQLException {
		//checks patients current activity, then if they have >=1 appointment, then attempts logic based on flag
		//input patientID and flag
		//flag is whether you want to set patient activity to true or false
		int tempID = patientID;

		//if user wants to set activity to true
		if (flag == true) {
			//check patients current activity
			Boolean activity = getPatientActivity(tempID);

			//if patient is active
			if (activity == true) {
				//check if patient has >=1 appointments
				Boolean checkAppt = checkPatientAppointments(tempID);

				//if patient has >= 1 appointment
				if (checkAppt == true) {
					setPatientActivityTrue(tempID);
					System.out.println("Patient has future appointments.");
					System.out.println("Setting Patient Activity to: \"Active\"");
					blankLine();
				}

				//if patient does not have any appointments
				if (checkAppt == false ) {
					System.out.println("Patient does not have any appointments.");
					System.out.println("Setting Patient Activity to: \"Inactive\"");
					blankLine();
					setPatientActivityFalse(tempID);
				}

			}

			//if patient is inactive
			if (activity == false) {
				//check if patient has >=1 appointments
				Boolean checkAppt = checkPatientAppointments(tempID);

				//if patient has >= 1 appointment
				if (checkAppt == true) {
					setPatientActivityTrue(tempID);
					System.out.println("Patient has future appointments.");
					System.out.println("Setting Patient Activity to: \"Active\"");
					blankLine();
				}

				//if patient does not have any appointments
				if (checkAppt == false ) {
					System.out.println("Patient does not have any appointments.");
					System.out.println("Setting Patient Activity to: \"Inactive\"");
					blankLine();
					setPatientActivityFalse(tempID);
				}
			}
		}//end if

		//if user wants to set activity to false
		if (flag == false) {
			//check patients current activity
			Boolean activity = getPatientActivity(tempID);

			//if patient is active
			if (activity == true) {
				//check if patient has >=1 appointments
				Boolean checkAppt = checkPatientAppointments(tempID);

				//if patient has >= 1 appointment
				if (checkAppt == true) {
					setPatientActivityTrue(tempID);
					System.out.println("Patient has future appointments.");
					System.out.println("Setting Patient Activity to: \"Active\"");
					blankLine();
				}

				//if patient does not have any appointments
				if (checkAppt == false ) {
					System.out.println("Patient does not have any appointments.");
					System.out.println("Setting Patient Activity to: \"Inactive\"");
					blankLine();
					setPatientActivityFalse(tempID);
				}

			}

			//if patient is inactive
			if (activity == false) {
				//check if patient has >=1 appointments
				Boolean checkAppt = checkPatientAppointments(tempID);

				//if patient has >= 1 appointment
				if (checkAppt == true) {
					setPatientActivityTrue(tempID);
					System.out.println("Patient has future appointments.");
					System.out.println("Setting Patient Activity to: \"Active\"");
					blankLine();
				}

				//if patient does not have any appointments
				if (checkAppt == false ) {
					System.out.println("Patient does not have any appointments.");
					System.out.println("Setting Patient Activity to: \"Inactive\"");
					blankLine();
					setPatientActivityFalse(tempID);
				}
			}
		}//end if

	}

	public Boolean checkPatientAppointments(int patientID) throws SQLException {
		//check if a patient has appointments
		//if a patient has >1 appointment, return true
		//if a patient has <= 0 appointments, return false
		String qry = "SELECT apptid FROM appointment WHERE patients_patientid = ?";
		PreparedStatement statement = con.prepareStatement(qry);
		int temp = patientID;
		statement.setInt(1, temp);
		ResultSet r = statement.executeQuery();
		while(r.next()) {
			boolean appt = r.getBoolean(1);
			if (appt == true) {
				return true;
			}
			else if (appt == false) {
				return false;
			}
		}//end while
		return false;
	}

	public Boolean getPatientActivity(int patientID) throws SQLException {
		//if patient is active, return true
		//if patient is inactive, return false
		String qry = "SELECT patient_records FROM patient where patient = ?";
		PreparedStatement statement = con.prepareStatement(qry);
		int temp = patientID;
		statement.setInt(1, temp);

		ResultSet r = statement.executeQuery();
		while(r.next()) {
			String activity = r.getString(1);
			if (activity == "Active") {
				return true;
			}
			else if (activity == "Inactive") {
				return false;
			}
		}//end while
		return false;
	}

	void Claim_Payment_Calculator(int amountDue, int paidAmount, int billid) throws SQLException {

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



