
import java.io.RandomAccessFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class DavisBase {

	static String prompt_3 = "davisBasesql> ";
	static String catalog_dir = "data/catalog";
	static String userdata_dir = "data/user_data";
	static String _version = "V1.0";
	

	static boolean isExit = false;
		
	public static int pageSize = 512;
	
	static Scanner sc = new Scanner(System.in).useDelimiter(";");
	
    public static void main(String[] args) {
    	init();
		
		welcomeMsg();

		String user_Command = ""; 

		while(!isExit) {
			System.out.print(prompt_3);
			user_Command = sc.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
			parseInputCommand(user_Command);
		}
		System.out.println("Exiting...");


	}
	
    public static void welcomeMsg() {
		System.out.println(lineFromat("*",80));
        System.out.println("Welcome to DavisBase Project");
		System.out.println("DavisBase Version " + _version);
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(lineFromat("*",80));
	}
	

	
	public static String lineFromat(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	
	
	public static void helpWindow() {
		System.out.println(lineFromat("*",80));
		System.out.println("SUPPORTED COMMANDS");
		System.out.println("All commands below are case insensitive");
		System.out.println();
		System.out.println("\tSHOW TABLES;                                               Display all the tables in the database.");
		System.out.println("\tCREATE TABLE table_name (<column_name datatype> <NOT NULL/UNIQUE>);   Create a new table in the database. First record should be primary key of type Int.");
		System.out.println("\tINSERT INTO table_name VALUES (value1,value2,..);          Insert a new record into the table. First Column is primary key which has inbuilt auto increment function.");
		System.out.println("\tSELECT * FROM table_name;                                  Display all records in the table.");
		System.out.println("\tSELECT * FROM table_name WHERE column_name operator value; Display records in the table where the given condition is satisfied.");
		System.out.println("\tVERSION;                                                   Show the program version.");
		System.out.println("\tHELP;                                                      Show this help information.");
		System.out.println("\tEXIT;                                                      Exit the program.");
		System.out.println();
		System.out.println();
		System.out.println(lineFromat("*",80));
	}


	
	public static boolean tableExists(String tableName){
		tableName = tableName+".tbl";
		
		try {
			
			
			File dataDir = new File(userdata_dir);
			if (tableName.equalsIgnoreCase("davisbase_tables.tbl") || tableName.equalsIgnoreCase("davisbase_columns.tbl"))
				dataDir = new File(catalog_dir) ;
			
			String[] oldTableFiles;
			oldTableFiles = dataDir.list();
			for (int i=0; i<oldTableFiles.length; i++) {
				if(oldTableFiles[i].equals(tableName))
					return true;
			}
		}
		catch (SecurityException se) {
			System.out.println("Unable to create data container directory");
			System.out.println(se);
		}

		return false;
	}

	public static void init(){
		try {
			File dataDir = new File("data");
			if(dataDir.mkdir()){
				System.out.println("The data base doesn't exit, initializing data base...");
				initialize();
			}
			else {
				dataDir = new File(catalog_dir);
				String[] oldTableFiles = dataDir.list();
				boolean checkTab = false;
				boolean checkCol = false;
				for (int i=0; i<oldTableFiles.length; i++) {
					if(oldTableFiles[i].equals("davisbase_tables.tbl"))
						checkTab = true;
					if(oldTableFiles[i].equals("davisbase_columns.tbl"))
						checkCol = true;
				}
				
				if(!checkTab){
					System.out.println("The davisbase_tables does not exit, initializing data base...");
					System.out.println();
					initialize();
				}
				
				if(!checkCol){
					System.out.println("The davisbase_columns table does not exit, initializing data base...");
					System.out.println();
					initialize();
				}
				
			}
		}
		catch (SecurityException e) {
			System.out.println(e);
		}

	}
	
public static void initialize() {

		
		try {
			File dataDir = new File(userdata_dir);
			dataDir.mkdir();
			dataDir = new File(catalog_dir);
			dataDir.mkdir();
			String[] oldTableFiles;
			oldTableFiles = dataDir.list();
			for (int i=0; i<oldTableFiles.length; i++) {
				File anOldFile = new File(dataDir, oldTableFiles[i]); 
				anOldFile.delete();
			}
		}
		catch (SecurityException e) {
			System.out.println(e);
		}

		try {
			RandomAccessFile tables_Catalog = new RandomAccessFile(catalog_dir+"/davisbase_tables.tbl", "rw");
			tables_Catalog.setLength(pageSize);
			tables_Catalog.seek(0);
			tables_Catalog.write(0x0D);
			tables_Catalog.writeByte(0x02);
			
			int size1=24;
			int size2=25;
			
			int offsetT=pageSize-size1;
			int offsetC=offsetT-size2;
			
			tables_Catalog.writeShort(offsetC);
			tables_Catalog.writeInt(0);
			tables_Catalog.writeInt(0);
			tables_Catalog.writeShort(offsetT);
			tables_Catalog.writeShort(offsetC);
			
			tables_Catalog.seek(offsetT);
			tables_Catalog.writeShort(20);
			tables_Catalog.writeInt(1); 
			tables_Catalog.writeByte(1);
			tables_Catalog.writeByte(28);
			tables_Catalog.writeBytes("davisbase_tables");
			
			tables_Catalog.seek(offsetC);
			tables_Catalog.writeShort(21);
			tables_Catalog.writeInt(2); 
			tables_Catalog.writeByte(1);
			tables_Catalog.writeByte(29);
			tables_Catalog.writeBytes("davisbase_columns");
			
			tables_Catalog.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			RandomAccessFile columns_Catalog = new RandomAccessFile(catalog_dir+"/davisbase_columns.tbl", "rw");
			columns_Catalog.setLength(pageSize);
			columns_Catalog.seek(0);       
			columns_Catalog.writeByte(0x0D); 
			columns_Catalog.writeByte(0x09); //no of records
			
			int[] offset=new int[9];
			offset[0]=pageSize-45;
			offset[1]=offset[0]-49;
			offset[2]=offset[1]-46;
			offset[3]=offset[2]-50;
			offset[4]=offset[3]-51;
			offset[5]=offset[4]-49;
			offset[6]=offset[5]-59;
			offset[7]=offset[6]-51;
			offset[8]=offset[7]-49;
			
			columns_Catalog.writeShort(offset[8]); 
			columns_Catalog.writeInt(0); 
			columns_Catalog.writeInt(0); 
			
			for(int i=0;i<offset.length;i++)
				columns_Catalog.writeShort(offset[i]);

			
			columns_Catalog.seek(offset[0]);
			columns_Catalog.writeShort(36);
			columns_Catalog.writeInt(1); //key
			columns_Catalog.writeByte(6); //no of columns
			columns_Catalog.writeByte(28); //16+12next file lines indicate the code for datatype/length of the 5 columns
			columns_Catalog.writeByte(17); //5+12
			columns_Catalog.writeByte(15); //3+12
			columns_Catalog.writeByte(4);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeBytes("davisbase_tables"); 
			columns_Catalog.writeBytes("rowid"); 
			columns_Catalog.writeBytes("INT"); 
			columns_Catalog.writeByte(1); 
			columns_Catalog.writeBytes("NO"); 
			columns_Catalog.writeBytes("NO"); 
			columns_Catalog.writeBytes("NO");
			
			columns_Catalog.seek(offset[1]);
			columns_Catalog.writeShort(42); 
			columns_Catalog.writeInt(2); 
			columns_Catalog.writeByte(6);
			columns_Catalog.writeByte(28);
			columns_Catalog.writeByte(22);
			columns_Catalog.writeByte(16);
			columns_Catalog.writeByte(4);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeBytes("davisbase_tables"); 
			columns_Catalog.writeBytes("table_name"); 
			columns_Catalog.writeBytes("TEXT"); 
			columns_Catalog.writeByte(2);
			columns_Catalog.writeBytes("NO"); 
			columns_Catalog.writeBytes("NO");
			
			columns_Catalog.seek(offset[2]);
			columns_Catalog.writeShort(37); 
			columns_Catalog.writeInt(3); 
			columns_Catalog.writeByte(6);
			columns_Catalog.writeByte(29);
			columns_Catalog.writeByte(17);
			columns_Catalog.writeByte(15);
			columns_Catalog.writeByte(4);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeBytes("davisbase_columns");
			columns_Catalog.writeBytes("rowid");
			columns_Catalog.writeBytes("INT");
			columns_Catalog.writeByte(1);
			columns_Catalog.writeBytes("NO");
			columns_Catalog.writeBytes("NO");
			
			columns_Catalog.seek(offset[3]);
			columns_Catalog.writeShort(43);
			columns_Catalog.writeInt(4); 
			columns_Catalog.writeByte(6);
			columns_Catalog.writeByte(29);
			columns_Catalog.writeByte(22);
			columns_Catalog.writeByte(16);
			columns_Catalog.writeByte(4);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeBytes("davisbase_columns");
			columns_Catalog.writeBytes("table_name");
			columns_Catalog.writeBytes("TEXT");
			columns_Catalog.writeByte(2);
			columns_Catalog.writeBytes("NO");
			columns_Catalog.writeBytes("NO");
			
			columns_Catalog.seek(offset[4]);
			columns_Catalog.writeShort(44);
			columns_Catalog.writeInt(5); 
			columns_Catalog.writeByte(6);
			columns_Catalog.writeByte(29);
			columns_Catalog.writeByte(23);
			columns_Catalog.writeByte(16);
			columns_Catalog.writeByte(4);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeBytes("davisbase_columns");
			columns_Catalog.writeBytes("column_name");
			columns_Catalog.writeBytes("TEXT");
			columns_Catalog.writeByte(3);
			columns_Catalog.writeBytes("NO");
			columns_Catalog.writeBytes("NO");
			
			columns_Catalog.seek(offset[5]);
			columns_Catalog.writeShort(42);
			columns_Catalog.writeInt(6); 
			columns_Catalog.writeByte(6);
			columns_Catalog.writeByte(29);
			columns_Catalog.writeByte(21);
			columns_Catalog.writeByte(16);
			columns_Catalog.writeByte(4);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeBytes("davisbase_columns");
			columns_Catalog.writeBytes("data_type");
			columns_Catalog.writeBytes("TEXT");
			columns_Catalog.writeByte(4);
			columns_Catalog.writeBytes("NO");
			columns_Catalog.writeBytes("NO");
			
			columns_Catalog.seek(offset[6]);
			columns_Catalog.writeShort(52); 
			columns_Catalog.writeInt(7); 
			columns_Catalog.writeByte(6);
			columns_Catalog.writeByte(29);
			columns_Catalog.writeByte(28);
			columns_Catalog.writeByte(19);
			columns_Catalog.writeByte(4);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeBytes("davisbase_columns");
			columns_Catalog.writeBytes("ordinal_position");
			columns_Catalog.writeBytes("TINYINT");
			columns_Catalog.writeByte(5);
			columns_Catalog.writeBytes("NO");
			columns_Catalog.writeBytes("NO");
			
			columns_Catalog.seek(offset[7]);
			columns_Catalog.writeShort(44); 
			columns_Catalog.writeInt(8); 
			columns_Catalog.writeByte(6);
			columns_Catalog.writeByte(29);
			columns_Catalog.writeByte(23);
			columns_Catalog.writeByte(16);
			columns_Catalog.writeByte(4);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeBytes("davisbase_columns");
			columns_Catalog.writeBytes("is_nullable");
			columns_Catalog.writeBytes("TEXT");
			columns_Catalog.writeByte(6);
			columns_Catalog.writeBytes("NO");
			columns_Catalog.writeBytes("NO");
		

			columns_Catalog.seek(offset[8]);
			columns_Catalog.writeShort(42); 
			columns_Catalog.writeInt(9); 
			columns_Catalog.writeByte(6);
			columns_Catalog.writeByte(29);
			columns_Catalog.writeByte(21);
			columns_Catalog.writeByte(16);
			columns_Catalog.writeByte(4);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeByte(14);
			columns_Catalog.writeBytes("davisbase_columns");
			columns_Catalog.writeBytes("is_unique");
			columns_Catalog.writeBytes("TEXT");
			columns_Catalog.writeByte(7);
			columns_Catalog.writeBytes("NO");
			columns_Catalog.writeBytes("NO");
			
			columns_Catalog.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}
}



	public static String[] parserEquation(String equ){
		String comparator[] = new String[3];
		String temp[] = new String[2];
		if(equ.contains("=")) {
			temp = equ.split("=");
			comparator[0] = temp[0].trim();
			comparator[1] = "=";
			comparator[2] = temp[1].trim();
		}
		
		if(equ.contains("<")) {
			temp = equ.split("<");
			comparator[0] = temp[0].trim();
			comparator[1] = "<";
			comparator[2] = temp[1].trim();
		}
		
		if(equ.contains(">")) {
			temp = equ.split(">");
			comparator[0] = temp[0].trim();
			comparator[1] = ">";
			comparator[2] = temp[1].trim();
		}
		
		if(equ.contains("<=")) {
			temp = equ.split("<=");
			comparator[0] = temp[0].trim();
			comparator[1] = "<=";
			comparator[2] = temp[1].trim();
		}

		if(equ.contains(">=")) {
			temp = equ.split(">=");
			comparator[0] = temp[0].trim();
			comparator[1] = ">=";
			comparator[2] = temp[1].trim();
		}
		
		if(equ.contains("!=")) {
			temp = equ.split("!=");
			comparator[0] = temp[0].trim();
			comparator[1] = "!=";
			comparator[2] = temp[1].trim();
		}

		return comparator;
	}
		
	public static void parseInputCommand (String inputCommand) {
		
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(inputCommand.split(" ")));

		switch (commandTokens.get(0)) {

		    case "show":
			    printTables();
			    break;
			
		    case "create":
		    	switch (commandTokens.get(1)) {
		    	case "table": 
		    		parseCreateStr(inputCommand);
		    		break;
		    				    		
		    	default:
					System.out.println("Invalid Command.Please type help : \"" + inputCommand + "\"");
					System.out.println();
					break;
		    	}
		    	break;

			case "insert":
				parseInsertStr(inputCommand);
				break;				
			case "select":
				parseQueryStr(inputCommand);
				break;

			case "drop":
				dropTable(inputCommand);
				break;	

			case "help":
				helpWindow();
				break;

			case "version":
				System.out.println("DavisBase Version " + _version);
				break;

			case "exit":
				isExit=true;
				break;
				
			case "quit":
				isExit=true;
				break;
	
			default:
				System.out.println("I didn't understand the command: \"" + inputCommand + "\"");
				System.out.println();
				break;
		}
	} 

	public static void printTables() {
		System.out.println("STUB: Calling the method to process the command");
		System.out.println("Parsing the string:\"show tables\"");
		
		String table = "davisbase_tables";
		String[] cols = {"table_name"};
		String[] cmptr = new String[0];
		Table.select(table, cols, cmptr,userdata_dir+"/");
	}
	
    public static void parseCreateStr(String createString) {
		
		System.out.println("STUB: Calling your method to process the command");
		System.out.println("Parsing the string:\"" + createString + "\"");
		
		String[] tokens=createString.split(" ");
		String tableName = tokens[2];
		String[] temp = createString.split(tableName);
		String cols = temp[1].trim();
		String[] create_cols = cols.substring(1, cols.length()-1).split(",");
		
		for(int i = 0; i < create_cols.length; i++)
			create_cols[i] = create_cols[i].trim();
		
		if(tableExists(tableName)){
			System.out.println("Table "+tableName+" already exists.");
		}
		else
			{
			Table.createTable(tableName, create_cols);		
			}

	}
    
    public static void parseInsertStr(String insertString) {
    	try{
		System.out.println("STUB: Calling the method to process the command");
		System.out.println("Parsing the string:\"" + insertString + "\"");
		
		String[] tokens=insertString.split(" ");
		String table = tokens[2];
		String[] temp = insertString.split("values");
		String temporary=temp[1].trim();
		String[] insert_vals = temporary.substring(1, temporary.length()-1).split(",");
		for(int i = 0; i < insert_vals.length; i++)
			insert_vals[i] = insert_vals[i].trim();
	
		if(!tableExists(table)){
			System.out.println("Table "+table+" does not exist.");
		}
		else
		{
			Table.insertInto(table, insert_vals,userdata_dir+"/");
		}
    	}
    	catch(Exception e)
    	{
    		System.out.println(e+e.toString());
    	}

	}
    
    
    public static void parseQueryStr(String queryString) {
		System.out.println("STUB: Calling the method to process the command");
		System.out.println("Parsing the string:\"" + queryString + "\"");
		
		String[] cmp;
		String[] column;
		String[] temp = queryString.split("where");
		if(temp.length > 1){
			String tmp = temp[1].trim();
			cmp = parserEquation(tmp);
		}
		else{
			cmp = new String[0];
		}
		String[] select = temp[0].split("from");
		String tableName = select[1].trim();
		String cols = select[0].replace("select", "").trim();
		if(cols.contains("*")){
			column = new String[1];
			column[0] = "*";
		}
		else{
			column = cols.split(",");
			for(int i = 0; i < column.length; i++)
				column[i] = column[i].trim();
		}
		
		if(!tableExists(tableName)){
			System.out.println("Table "+tableName+" does not exist.");
		}
		else
		{
		    Table.select(tableName, column, cmp,userdata_dir+"/");
		}
	}
	
	public static void dropTable(String dropTableString) {
		System.out.println("STUB: Calling the method to process the command");
		System.out.println("Parsing the string:\"" + dropTableString + "\"");
		
		String[] tokens=dropTableString.split(" ");
		String tableName = tokens[2];
		if(!tableExists(tableName)){
			System.out.println("Table "+tableName+" does not exist.");
		}
		else
		{
			Table.drop(tableName);
		}		

	}
}