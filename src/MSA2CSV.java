import java.io.File;
import java.util.ArrayList;

public class MSA2CSV {

    /** Public methods **/
    public static void main(String[] args) {
        //Checks for the lack of any command line arguments
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        //Default file name, field and row separators and output folder path
        String namesep = "$";
        String outputFolder = "";
        String fieldsep = ", ";
        String rowsep = "\n";

        //Command line argument parsing and validation
        ArrayList<String> databaseFilePaths = new ArrayList<String>();
        for(int index = 0; index < args.length; index++) {
            switch(args[index]){
                case "-f":
                    fieldsep = getOptionArgument(args, index);
                    index++;
                    break;
                case "-r":
                    rowsep = getOptionArgument(args, index);
                    index++;
                    break;
                case "-n":
                    namesep = getOptionArgument(args, index);
                    index++;
                    break;
                case "-o":
                    outputFolder = getOptionArgument(args, index);
                    ensureOutputFolderExists(outputFolder);
                    index++;
                    break;
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "-v":
                    printVersion();
                    System.exit(0);
                    break;
                default:
                    checkIfDatabaseFileExists(args[index]);
                    databaseFilePaths.add(args[index]);
                    break;
            }
        }
        //Write each table in each database file as database{namesep}tablename.csv
        CSVExporter.exportDatabasesToCSV(databaseFilePaths, namesep, outputFolder, fieldsep, rowsep);
    }

    /** Private methods **/
    private static String getOptionArgument(String[] args, int optionIndex) {
        int argumentIndex = optionIndex + 1;

        //The program does purposely not check if the next element also is a flag, as that would
        //unnecessarily prevent the use of, for example, "-f" or "-n" as a field or row separator
        if (argumentIndex >= args.length) {
            System.out.println("ERROR: option \"" + args[optionIndex] + "\" lacks an argument");
            System.exit(1);
        }
        return args[argumentIndex];
    }

    private static void ensureOutputFolderExists(String folderPath) {
        File folder = new File(folderPath);
        if ( ! folder.isDirectory()) {
            boolean wasDirectoryCreated = false;

            try {
                wasDirectoryCreated = folder.mkdirs();
            }
            catch (SecurityException se) {
                wasDirectoryCreated = false;
            }

            if ( ! wasDirectoryCreated) {
                System.out.println("ERROR: the output folder \"" + folder + "\" does not exist "
                                 + "and it could not be created either");
                System.exit(1);
            }
        }
    }

    private static void checkIfDatabaseFileExists(String filePath) {
        File file = new File(filePath);
        if ( ! file.isFile()) {
            System.out.println("ERROR: the database file \"" + filePath + "\" does not exists");
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("usage: msa2csv [-fhnorv] <Microsoft Access database file(S)>");
    }

    private static void printHelp() {
        printUsage();
        System.out.println("options:");
        System.out.println("    -f <string> sets the field separators used for in the csv file");
        System.out.println("    -h display available options and exit");
        System.out.println("    -n <string> sets the separator in the name of the csv file");
        System.out.println("    -o <string> sets the output folder used for all tables");
        System.out.println("    -r <string> sets the row separators used in the csv file");
        System.out.println("    -v display version information and exit");
    }

    private static void printVersion() {
        System.out.println("msa2csv 0.0.1");
    }
}
