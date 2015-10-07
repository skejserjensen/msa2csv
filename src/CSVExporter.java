import java.util.ArrayList;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Row;


public class CSVExporter {

    /** Public methods **/
    public static void exportDatabasesToCSV(ArrayList<String> databaseFilePaths,
                                            String namesep, String outputFolder,
                                            String fieldsep, String rowsep) {
        //Exports all tables from the database file to csv files in the output folder
        Database database = null;
        String genericOutputPath = null;

        for(String filePath : databaseFilePaths) {
            database = openDatabase(filePath);
            genericOutputPath = constructGenericOutputPath(database, outputFolder);
            exportTables(database, namesep, genericOutputPath, fieldsep, rowsep);
            closeDatabase(database);
        }
    }

    /** Private methods **/
    private static Database openDatabase(String filePath) {
        Database database = null;
        try {
            database = DatabaseBuilder.open(new File(filePath));
        } catch(IOException ioe) {
            System.out.println("ERROR: the database \"" + filePath + "\" file could be opened");
            System.exit(1);
        }
        return database;
    }

    private static String constructGenericOutputPath(Database database, String outputFolder) {
        //Prepares a generic output path string without the table name and csv suffix
        String genericOutputPath = null;

        //An empty string is the default outputFolder, the path of the database file is then used
        if(outputFolder.isEmpty()) {
            try {
                genericOutputPath = database.getFile().getCanonicalPath();
            }
            catch (IOException ioe) {
                System.out.println("ERROR: unable to construct the canonical path of the "
                                 + "database file \"" + database.getFile().getName() + "\"");
                closeDatabase(database);
                System.exit(1);
            }
        }
        else{
            //Ensure that the output folder path always ends with a path separator
            if( ! outputFolder.endsWith(File.separator)) {
                outputFolder += File.separator;
            }
            genericOutputPath = outputFolder;
            genericOutputPath += database.getFile().getName();
        }
        return genericOutputPath.substring(0, genericOutputPath.lastIndexOf("."));
    }

    private static void exportTables(Database database,
                                     String namesep, String genericOutputPath,
                                     String fieldsep, String rowsep) {
        try {
            for(Table table : database) {
                //Prepare the full csv file path, an integer is added if the file already exists
                String outputPath = constructOutputPath(genericOutputPath, namesep, table);
                FileWriter outputFileWriter = new FileWriter(outputPath);
                BufferedWriter outputBufferedWriter = new BufferedWriter(outputFileWriter);

                //Write the column names to the csv file
                ArrayList<String> columns = new ArrayList<String>();
                for(Column column : table.getColumns()) {
                    columns.add(column.getName());
                }
                outputBufferedWriter.write(StringUtils.join(columns, fieldsep));
                outputBufferedWriter.write(rowsep);

                //Write the rows to the csv file
                for(Row row : table) {
                    outputBufferedWriter.write(StringUtils.join(row.values(), fieldsep));
                    outputBufferedWriter.write(rowsep);
                }
                outputBufferedWriter.close();
                outputFileWriter.close();
            }
        } catch(IOException ioe) {
            System.out.println("ERROR: unable to write the csv file to the file system");
            closeDatabase(database);
            System.exit(1);
        }
    }

    private static String constructOutputPath(String genericOutputPath, String namesep, Table table)
                                              throws IOException {
        String outputPathWithoutSuffix = genericOutputPath + namesep + table.getName();

        //Increments and appends filesExist to the filename until an unused name is found
        String outputPath = outputPathWithoutSuffix + ".csv";
        for(int filesExist = 1; filesExist <= Integer.MAX_VALUE; filesExist++) {
            if( ! new File(outputPath).exists()) {
                return outputPath;
            }
            outputPath = outputPathWithoutSuffix + "-(" + filesExist + ").csv";
        }

        //The output folder contains Integer.MAX_VALUE files using our naming schema, we give up...
        throw new IOException();
    }

    private static void closeDatabase(Database database) {
        try {
            database.close();
        } catch(IOException ioe) {
            System.out.println(ioe);
            System.out.println("ERROR: the database file could not be closed correctly");
            System.exit(1);
        }
    }
}
