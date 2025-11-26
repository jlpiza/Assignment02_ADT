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

public class XMLParser {
    private StackADT<TagInfo> tagStack;
    private QueueADT<TagInfo> errorQ;
    private QueueADT<TagInfo> extrasQ;
    private List<String> errorMessages;
    
    // Regex patterns
    private static final Pattern START_TAG_PATTERN = Pattern.compile("<([a-zA-Z][a-zA-Z0-9_-]*)(\\s[^>]*)?>");
    private static final Pattern END_TAG_PATTERN = Pattern.compile("</([a-zA-Z][a-zA-Z0-9_-]*)>");
    private static final Pattern SELF_CLOSING_TAG_PATTERN = Pattern.compile("<([a-zA-Z][a-zA-Z0-9_-]*)(\\s[^>]*)?/>");
    private static final Pattern PROCESSING_INSTRUCTION_PATTERN = Pattern.compile("<\\?xml[^?]*\\?>");
    
    // Helper class to track tag information
    private static class TagInfo {
        String tagName;
        String originalTag;
        int lineNumber;
        
        TagInfo(String tagName, String originalTag, int lineNumber) {
            this.tagName = tagName;
            this.originalTag = originalTag;
            this.lineNumber = lineNumber;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TagInfo that = (TagInfo) obj;
            return tagName.equals(that.tagName);
        }
        
        @Override
        public String toString() {
            return tagName;
        }
    }
    
    public XMLParser() {
        this.tagStack = new MyStack<>();
        this.errorQ = new MyQueue<>();
        this.extrasQ = new MyQueue<>();
        this.errorMessages = new ArrayList<>();
    }
    
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
    
    private void processLine(String line, int lineNumber) {
        Pattern tagPattern = Pattern.compile("<[^>]+>");
        Matcher matcher = tagPattern.matcher(line);
        
        while (matcher.find()) {
            String tag = matcher.group();
            processTag(tag, lineNumber);
        }
    }
    
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
    
    private void addErrorMessage(int lineNumber, String message) {
        // Avoid duplicate error messages
        String errorMsg = "Error at line: " + lineNumber + " " + message;
        if (!errorMessages.contains(errorMsg)) {
            errorMessages.add(errorMsg);
        }
    }
    
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
    
    // Helper methods
    private boolean isProcessingInstruction(String tag) {
        return PROCESSING_INSTRUCTION_PATTERN.matcher(tag).matches();
    }
    
    private boolean isSelfClosingTag(String tag) {
        return SELF_CLOSING_TAG_PATTERN.matcher(tag).matches() || 
               tag.trim().endsWith("/>");
    }
    
    private boolean isStartTag(String tag) {
        return START_TAG_PATTERN.matcher(tag).matches() && 
               !tag.startsWith("</") && 
               !isSelfClosingTag(tag);
    }
    
    private boolean isEndTag(String tag) {
        return END_TAG_PATTERN.matcher(tag).matches();
    }
    
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