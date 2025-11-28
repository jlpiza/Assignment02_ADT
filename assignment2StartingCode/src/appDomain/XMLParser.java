package appDomain;

import implementations.MyStack;
import implementations.MyQueue;
import utilities.StackADT;
import utilities.QueueADT;
import exceptions.EmptyQueueException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * XML Parser implementation based on Kitty's Algorithm for validating XML document structure.
 * This parser checks for proper tag nesting, matching, and well-formedness according to XML standards.
 * It uses custom stack and queue implementations to track tags and report validation errors.
 * 
 */
public class XMLParser {
    private StackADT<TagInfo> tagStack;
    private QueueADT<TagInfo> errorQ;
    private QueueADT<TagInfo> extrasQ;
    private List<String> errorMessages;
    
    // Regex patterns for XML tag detection
    private static final Pattern START_TAG_PATTERN = Pattern.compile("<([a-zA-Z][a-zA-Z0-9_-]*)(\\s[^>]*)?>");
    private static final Pattern END_TAG_PATTERN = Pattern.compile("</([a-zA-Z][a-zA-Z0-9_-]*)>");
    private static final Pattern SELF_CLOSING_TAG_PATTERN = Pattern.compile("<([a-zA-Z][a-zA-Z0-9_-]*)(\\s[^>]*)?/>");
    private static final Pattern PROCESSING_INSTRUCTION_PATTERN = Pattern.compile("<\\?xml[^?]*\\?>");
    
    /**
     * Helper class to store tag information including name, original text, and line number.
     * Used for accurate error reporting and tracking tag context.
     */
    private static class TagInfo {
        String tagName;
        String originalTag;
        int lineNumber;
        
        /**
         * Constructs a TagInfo object with tag details.
         * 
         * @param tagName the extracted tag name without attributes
         * @param originalTag the complete original tag text
         * @param lineNumber the line number where the tag was found
         */
        TagInfo(String tagName, String originalTag, int lineNumber) {
            this.tagName = tagName;
            this.originalTag = originalTag;
            this.lineNumber = lineNumber;
        }
        
        /**
         * Compares two TagInfo objects for equality based on tag name only.
         * 
         * @param obj the object to compare with
         * @return true if tag names are equal, false otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TagInfo that = (TagInfo) obj;
            return tagName.equals(that.tagName);
        }
        
        /**
         * Returns string representation of the tag information.
         * 
         * @return the tag name as string
         */
        @Override
        public String toString() {
            return tagName;
        }
    }
    
    /**
     * Constructs a new XMLParser with empty data structures.
     * Initializes the tag stack, error queue, extras queue, and error messages list.
     */
    public XMLParser() {
        this.tagStack = new MyStack<>();
        this.errorQ = new MyQueue<>();
        this.extrasQ = new MyQueue<>();
        this.errorMessages = new ArrayList<>();
    }
    
    /**
     * Parses an XML file and validates its structure.
     * Reads the file line by line, processes all tags, and checks for well-formedness.
     * 
     * @param filename the path to the XML file to parse
     * @return true if XML is well-formed, false if validation errors are found
     * @throws IOException if the file cannot be read or accessed
     */
    public boolean parseFile(String filename) throws IOException {
        tagStack.clear();
        errorQ.dequeueAll();
        extrasQ.dequeueAll();
        errorMessages.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                processLine(line, lineNumber);
            }
        }
        
        processRemainingStack();
        return processQueues();
    }
    
    /**
     * Processes a single line of XML content, extracting all tags and their line numbers.
     * 
     * @param line the line of text to process
     * @param lineNumber the current line number in the file
     */
    private void processLine(String line, int lineNumber) {
        Pattern tagPattern = Pattern.compile("<[^>]+>");
        Matcher matcher = tagPattern.matcher(line);
        
        while (matcher.find()) {
            String tag = matcher.group();
            processTag(tag, lineNumber);
        }
    }
    
    /**
     * Processes an individual XML tag according to Kitty's algorithm.
     * Classifies the tag type and takes appropriate action.
     * 
     * @param tag the XML tag to process
     * @param lineNumber the line number where the tag was found
     */
    private void processTag(String tag, int lineNumber) {
        if (isProcessingInstruction(tag)) {
            return;
        }
        
        if (isSelfClosingTag(tag)) {
            return;
        }
        
        if (isStartTag(tag)) {
            String tagName = extractTagName(tag);
            tagStack.push(new TagInfo(tagName, tag, lineNumber));
        } 
        else if (isEndTag(tag)) {
            String endTagName = extractTagName(tag);
            processEndTag(endTagName, lineNumber, tag);
        }
    }
    
    /**
     * Processes an end tag according to Kitty's algorithm rules.
     * Handles tag matching, error reporting, and queue management.
     * 
     * @param endTagName the name of the end tag
     * @param lineNumber the line number where the end tag was found
     * @param originalTag the complete original end tag text
     */
    private void processEndTag(String endTagName, int lineNumber, String originalTag) {
        try {
            if (!tagStack.isEmpty() && tagStack.peek().tagName.equals(endTagName)) {
                tagStack.pop();
            } 
            else if (!errorQ.isEmpty() && errorQ.peek().tagName.equals(endTagName)) {
                errorQ.dequeue();
            } 
            else if (tagStack.isEmpty()) {
                errorQ.enqueue(new TagInfo(endTagName, originalTag, lineNumber));
                addErrorMessage(lineNumber, originalTag + " is not constructed correctly.");
            } 
            else {
                boolean foundInStack = searchStackForMatch(endTagName, lineNumber, originalTag);
                
                if (!foundInStack) {
                    extrasQ.enqueue(new TagInfo(endTagName, originalTag, lineNumber));
                    addErrorMessage(lineNumber, originalTag + " is not constructed correctly.");
                }
            }
        } catch (EmptyQueueException e) {
            errorQ.enqueue(new TagInfo(endTagName, originalTag, lineNumber));
            addErrorMessage(lineNumber, originalTag + " is not constructed correctly.");
        }
    }
    
    /**
     * Searches the stack for a matching start tag when direct match fails.
     * Handles intercrossed tag scenarios and reports errors for unmatched tags.
     * 
     * @param endTagName the end tag name to search for
     * @param lineNumber the line number of the end tag
     * @param originalTag the original end tag text
     * @return true if matching start tag found in stack, false otherwise
     */
    private boolean searchStackForMatch(String endTagName, int lineNumber, String originalTag) {
        StackADT<TagInfo> tempStack = new MyStack<>();
        boolean found = false;
        
        try {
            while (!tagStack.isEmpty()) {
                TagInfo currentTag = tagStack.pop();
                tempStack.push(currentTag);
                
                if (currentTag.tagName.equals(endTagName)) {
                    found = true;
                    while (!tempStack.isEmpty()) {
                        TagInfo errorTag = tempStack.pop();
                        if (!errorTag.tagName.equals(endTagName)) {
                            errorQ.enqueue(errorTag);
                            addErrorMessage(errorTag.lineNumber, 
                                errorTag.originalTag + " is not constructed correctly.");
                        }
                    }
                    break;
                }
            }
            
            if (!found) {
                while (!tempStack.isEmpty()) {
                    tagStack.push(tempStack.pop());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error during stack search: " + e.getMessage());
        }
        
        return found;
    }
    
    /**
     * Processes any remaining tags in the stack after EOF.
     * All remaining tags are considered errors (unclosed start tags).
     */
    private void processRemainingStack() {
        while (!tagStack.isEmpty()) {
            try {
                TagInfo errorTag = tagStack.pop();
                errorQ.enqueue(errorTag);
                addErrorMessage(errorTag.lineNumber, 
                    errorTag.originalTag + " is not constructed correctly.");
            } catch (Exception e) {
                System.err.println("Error processing remaining stack: " + e.getMessage());
            }
        }
    }
    
    /**
     * Processes the error and extras queues to resolve remaining tag mismatches.
     * 
     * @return true if both queues are empty after processing, false otherwise
     */
    private boolean processQueues() {
        while (!errorQ.isEmpty() || !extrasQ.isEmpty()) {
            if (errorQ.isEmpty() != extrasQ.isEmpty()) {
                reportQueueErrors();
                return false;
            } 
            else if (!errorQ.isEmpty() && !extrasQ.isEmpty()) {
                try {
                    if (!errorQ.peek().tagName.equals(extrasQ.peek().tagName)) {
                        TagInfo errorTag = errorQ.dequeue();
                        addErrorMessage(errorTag.lineNumber, 
                            errorTag.originalTag + " is not constructed correctly.");
                    } else {
                        errorQ.dequeue();
                        extrasQ.dequeue();
                    }
                } catch (EmptyQueueException e) {
                    break;
                }
            }
        }
        
        return errorMessages.isEmpty();
    }
    
    /**
     * Reports all remaining errors from both error and extras queues.
     * Called when one queue has elements but the other is empty.
     */
    private void reportQueueErrors() {
        while (!errorQ.isEmpty()) {
            try {
                TagInfo errorTag = errorQ.dequeue();
                addErrorMessage(errorTag.lineNumber, 
                    errorTag.originalTag + " is not constructed correctly.");
            } catch (EmptyQueueException e) {
                break;
            }
        }
        
        while (!extrasQ.isEmpty()) {
            try {
                TagInfo extraTag = extrasQ.dequeue();
                addErrorMessage(extraTag.lineNumber, 
                    extraTag.originalTag + " is not constructed correctly.");
            } catch (EmptyQueueException e) {
                break;
            }
        }
    }
    
    /**
     * Adds an error message to the error list, avoiding duplicates.
     * 
     * @param lineNumber the line number where the error occurred
     * @param message the error message to add
     */
    private void addErrorMessage(int lineNumber, String message) {
        // Avoid duplicate error messages
        String errorMsg = "Error at line: " + lineNumber + " " + message;
        if (!errorMessages.contains(errorMsg)) {
            errorMessages.add(errorMsg);
        }
    }
    
    /**
     * Returns a formatted string containing all validation errors found during parsing.
     * If no errors were found, returns a success message.
     * 
     * @return formatted error message or success confirmation
     */
    public String getErrorMessage() {
        if (errorMessages.isEmpty()) {
            return "XML document is constructed correctly.";
        }
        
        StringBuilder errorMsg = new StringBuilder();
        for (String error : errorMessages) {
            errorMsg.append(error).append("\n");
        }
        return errorMsg.toString();
    }
    
    // Helper methods for tag classification
    
    /**
     * Checks if a tag is an XML processing instruction.
     * 
     * @param tag the tag to check
     * @return true if the tag is a processing instruction, false otherwise
     */
    private boolean isProcessingInstruction(String tag) {
        return PROCESSING_INSTRUCTION_PATTERN.matcher(tag).matches();
    }
    
    /**
     * Checks if a tag is self-closing.
     * 
     * @param tag the tag to check
     * @return true if the tag is self-closing, false otherwise
     */
    private boolean isSelfClosingTag(String tag) {
        return SELF_CLOSING_TAG_PATTERN.matcher(tag).matches() || 
               tag.trim().endsWith("/>");
    }
    
    /**
     * Checks if a tag is a start tag (opening tag).
     * 
     * @param tag the tag to check
     * @return true if the tag is a start tag, false otherwise
     */
    private boolean isStartTag(String tag) {
        return START_TAG_PATTERN.matcher(tag).matches() && 
               !tag.startsWith("</") && 
               !isSelfClosingTag(tag);
    }
    
    /**
     * Checks if a tag is an end tag (closing tag).
     * 
     * @param tag the tag to check
     * @return true if the tag is an end tag, false otherwise
     */
    private boolean isEndTag(String tag) {
        return END_TAG_PATTERN.matcher(tag).matches();
    }
    
    /**
     * Extracts the tag name from a complete XML tag, removing attributes and formatting.
     * 
     * @param tag the complete XML tag
     * @return the extracted tag name without attributes or formatting
     */
    private String extractTagName(String tag) {
        String cleanTag = tag.replaceAll("[<>/]", "").trim();
        int spaceIndex = cleanTag.indexOf(' ');
        if (spaceIndex != -1) {
            cleanTag = cleanTag.substring(0, spaceIndex);
        }
        if (cleanTag.startsWith("?")) {
            cleanTag = cleanTag.substring(1);
        }
        return cleanTag.trim();
    }
}