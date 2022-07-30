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
				blankLine();
				
				if (userInput.equals("1")) {

					String qry = "INSERT INTO PATIENT (fname, minit, lname, dob ) VALUES (?,?,?,?)";
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
						System.out.println("5. Back");
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
							System.out.println("Date of Birth Updated.");
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
				blankLine();
				
				if (userInput.equals("1")) {

					String qry = "INSERT INTO DOCTOR (fname, lname, minit) VALUES (?,?,?)";

					PreparedStatement statement = con.prepareStatement(qry);

					//DOCTOR FIRST NAME
					String fname = userInput(7);
					statement.setString(1, fname);

					//DOCTOR MIDDLE INITIAL
					String minit = userInput(8);
					statement.setString(3, minit);

					//DOCTOR LAST NAME
					String lname = userInput(9);
					statement.setString(2, lname);

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
						System.out.println("Doctor Update Menu");
						System.out.println("What do you want to update:");
						System.out.println("1. First Name");
						System.out.println("2. Middle Initial");
						System.out.println("3. Last Name");
						System.out.println("4. Back");
						userInput = sc.next();

						if (userInput.equals("1")) {
							//DOCTOR NEW FIRST NAME
							String qry = "UPDATE DOCTOR set fname = ? where doctorid = ?";
							PreparedStatement statement = con.prepareStatement(qry);

							String fname = userInput(10);
							statement.setString(1, fname);
							statement.setInt(2, doctorID);
							statement.executeUpdate();
							System.out.println("Doctor's First Name Successfully Updated.\n");
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
						}  else if (userInput.equals("4")) {
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
				blankLine();

				if (userInput.equals("1")) {
					String qry = "INSERT INTO APPOINTMENT (datetime, PATIENTS_PATIENTID, DOCTORS_DOCTORID ) VALUES (?,?,?)";
					PreparedStatement statement = con.prepareStatement(qry);

					String tempDate = updateDate(3);
					statement.setDate(1, Date.valueOf(tempDate)); //Date format Example: YYYY-MM-DD
					
					viewPatients();
					blankLine();
					System.out.println("Enter patientID:");
					statement.setInt(2, sc.nextInt());
					blankLine();
					
					viewDoctors();
					blankLine();
					System.out.println("Enter doctorID:");
					statement.setInt(3, sc.nextInt());
					blankLine();

					statement.execute();

					System.out.println("New Appointment Successfully Created.\n");

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

						System.out.println("Appointment successfully updated.\n");
					} else if (userInput.equals("2")) {
						//return to main menu
					}
				}
				if (userInput.equals("3")) {

					Create_Update_Bill();

					//System.out.println("FIXME: LOGIC AND CALL CREATE BILL\n");

				}
				if (userInput.equals("4")) {
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
	//returns fname, minit, etc.
	public String userInput(int sw) {
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
			}
			return null;
	}
	
	//input is integer for the switch below
	// 1 = Enter Date of Birth
	// 2 = Enter New Date of Birth
	// 3 = Enter Date
	// 3 = Enter New Date
	//returns String hopefully formatted as YYYY-MM-DD
	public String updateDate(int sw) {
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
		  } //end switch
		  blankLine();
		  return tempDate;
		} //end updateDateTime	
		
	public void viewDoctors() throws SQLException {
	    String qry = "SELECT doctor.doctorid, doctor.fname, doctor.minit, doctor.lname FROM DOCTOR";

	    PreparedStatement statement = con.prepareStatement(qry);

	    ResultSet result = statement.executeQuery();
	    if (result != null) {
	        printResults(result);
	    } else {
	        System.out.println("Failed to get results for this report.");
	    }
	} //end viewDoctors
	
	public void viewPatients() throws SQLException {
		String qry = "SELECT patient.patient, patient.fname, patient.minit, patient.lname, patient.dob FROM PATIENT";
		
		PreparedStatement statement = con.prepareStatement(qry);
		
		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
	}//end viewPatients
	
	public void viewAppointments() throws SQLException {
		String qry = "SELECT appointment.apptid, patient.fname, patient.lname,appointment.datetime FROM appointment INNER JOIN patient ON appointment.patients_patientid=patient.patient";
		
		PreparedStatement statement = con.prepareStatement(qry);
		
		ResultSet result = statement.executeQuery();
		if(result != null) {
			printResults(result);
		} else {
			System.out.println("Failed to get results for this report.");
		}
		
	}//end viewAppointments
	
	void blankLine() {
		System.out.println("");
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



