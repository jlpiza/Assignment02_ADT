package appDomain;

import java.io.IOException;

/**
 * Main application class for XML Parser
 * Usage: java -jar Parser.jar filename.xml
 */
public class XMLParserApp {
    
    public static void main(String[] args) {
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