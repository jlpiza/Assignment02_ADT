package appDomain;

import java.io.IOException;

/**
 * Main application class for the XML Parser command-line interface.
 * This class serves as the entry point for the XML validation application,
 * providing a command-line interface to validate XML files using the XMLParser.
 * 
 */
public class XMLParserApp {
    
    /**
     * Main method that serves as the entry point for the XML Parser application.
     * Validates command-line arguments, processes the specified XML file,
     * and displays validation results or error messages.
     * 
     * @param args command-line arguments; expects exactly one argument: the XML filename
     * 
     * Usage examples:
     * <pre>
     * java -jar Parser.jar sample1.xml
     * java -jar Parser.jar C:\path\to\file.xml
     * </pre>
     * 
     */
    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length != 1) {
            System.out.println("Usage: java -jar Parser.jar <xmlfile>");
            System.out.println("Example: java -jar Parser.jar sample1.xml");
            System.exit(1);
        }
        
        String filename = args[0];
        XMLParser parser = new XMLParser();
        
        try {
            System.out.println("Parsing XML file: " + filename);
            boolean isValid = parser.parseFile(filename);
            
            if (isValid) {
                System.out.println("XML document is constructed correctly.");
            } else {
                System.out.println(parser.getErrorMessage());
            }
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}