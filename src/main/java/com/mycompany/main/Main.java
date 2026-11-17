package com.mycompany.main;

// Imports Scanner so we can get user input
import java.util.Scanner;
// Imports ArrayList so we can store the messages
import java.util.ArrayList;
// Imports Random so we can create message IDs
import java.util.Random;
// Imports classes needed to write and read a file
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

// Create the Main class
public class Main {

    // Create Scanner for user input
    static Scanner input = new Scanner(System.in);

    // Create Random to make message IDs
    static Random random = new Random();

    // Stores the user's entered details
    static String username;
    static String password;
    static String cellPhone;
    static String firstname;
    static String lastname;

    // Stores the registered details
    static String registeredUsername;
    static String registeredPassword;
    static String registeredCellPhone;

    // Stores the number of messages the user wants to enter
    static int messagesAllowed;

    // Details of the message being worked on
    static String currentMessageID;
    static int currentNumber;
    static String currentRecipient;
    static String currentMessage;
    static String currentHash;

    // Stores all messages in the program
    // Each message contains: ID, hash, sender, recipient, message
    static ArrayList<String[]> sentMessages = new ArrayList<String[]>();
    static ArrayList<String[]> disregardedMessages = new ArrayList<String[]>();
    static ArrayList<String[]> storedMessages = new ArrayList<String[]>();

    // Arrays required for Part 3
    static String[] sentMessagesArray = new String[0];
    static String[] disregardedMessagesArray = new String[0];
    static String[] storedMessagesArray = new String[0];
    static String[] messageHashesArray = new String[0];
    static String[] messageIDsArray = new String[0];

    // The JSON file where stored messages are saved
    static String storageFile = "storedMessages.json";

    //PART 1 

    // Checks if the username contains an underscore and is no more than 5 characters
    public static boolean checkUserName(String username) {
        if (username.length() <= 5 && username.contains("_")) {
            System.out.println("Username successfully captured.");
            return true;
        } else {
            System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
            return false;
        }
    }

    // Checks if the password meets the complexity requirements
    public static boolean checkPasswordComplexity(String password) {
        if (password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*")) {
            System.out.println("Password successfully captured.");
            return true;
        } else {
            System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            return false;
        }
    }

    // Checks that the cellphone number has an international code
    public static boolean isValidCell(String number) {
        return number.matches("^\\+\\d{1,3}\\d{1,10}$");
    }

    // Checks the cellphone number and displays a message
    public static boolean checkCellPhoneNumber(String number) {
        if (isValidCell(number)) {
            System.out.println("Cell phone number successfully added.");
            return true;
        } else {
            System.out.println("Cell phone number incorrectly formatted or does not contain international code.");
            return false;
        }
    }

    // Handles the registration process
    public static String registerUser() {
        System.out.print("Enter your first name: ");
        firstname = input.nextLine();

        System.out.print("Enter your last name: ");
        lastname = input.nextLine();

        // Keep asking until the username is correct
        while (true) {
            System.out.print("Enter your username: ");
            username = input.nextLine();

            if (checkUserName(username)) {
                break;
            }
        }

        // Keep asking until the password is correct
        while (true) {
            System.out.print("Enter your password: ");
            password = input.nextLine();

            if (checkPasswordComplexity(password)) {
                break;
            }
        }

        // Keep asking until the cellphone number is correct
        while (true) {
            System.out.print("Enter your cellphone number (e.g. +27838968976): ");
            cellPhone = input.nextLine();

            if (checkCellPhoneNumber(cellPhone)) {
                break;
            }
        }

        registeredUsername = username;
        registeredPassword = password;
        registeredCellPhone = cellPhone;

        return "User registered successfully.";
    }

    // Checks the login details
    public static boolean loginUser(String username, String password) {
        if (username.equals(registeredUsername)
                && password.equals(registeredPassword)) {
            return true;
        } else {
            return false;
        }
    }

    // Returns the login status message
    public static String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstname + ", " + lastname
                    + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    //PART 2

    // Creates a random ten digit message ID that has not been used before
    public static String generateMessageID() {
        String id = "";
        boolean idUsed = true;

        while (idUsed) {
            id = "";

            for (int i = 0; i < 10; i++) {
                id = id + random.nextInt(10);
            }

            idUsed = isIDUsed(id);
        }

        return id;
    }

    // Checks whether a message ID has already been used
    public static boolean isIDUsed(String id) {
        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i)[0].equals(id)) {
                return true;
            }
        }

        for (int i = 0; i < disregardedMessages.size(); i++) {
            if (disregardedMessages.get(i)[0].equals(id)) {
                return true;
            }
        }

        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i)[0].equals(id)) {
                return true;
            }
        }

        return false;
    }

    // Checks that the message ID is not more than ten characters
    public static boolean checkMessageID() {
        return currentMessageID.length() > 0
                && currentMessageID.length() <= 10;
    }

    // Checks that the recipient number has an international code
    public static String checkRecipientCell() {
        if (isValidCell(currentRecipient)) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // Checks that the message is not more than 250 characters
    public static String checkMessageLength(String text) {
        if (text.length() <= 250) {
            return "Message ready to send.";
        } else {
            int tooMany = text.length() - 250;
            return "Message exceeds 250 characters by " + tooMany
                    + "; please reduce the size.";
        }
    }

    // Creates a tracking hash using the format required by the rubric:
    // first two digits of the Message ID : message number : first and last words.
    public static String createMessageHash() {
        String text = currentMessage.trim();

        if (currentMessageID == null || currentMessageID.length() < 2
                || text.length() == 0) {
            return "";
        }

        String[] words = text.split("\\s+");
        String firstWord = words[0].replaceAll("[^A-Za-z0-9]", "");
        String lastWord = words[words.length - 1]
                .replaceAll("[^A-Za-z0-9]", "");

        String firstTwo = currentMessageID.substring(0, 2);
        String hash = firstTwo + ":" + currentNumber + ":"
                + firstWord + lastWord;

        return hash.toUpperCase();
    }

    // Searches for a message hash in sent, disregarded and stored messages.
    public static String searchMessageByHash(String searchHash) {
        readStoredMessages();

        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i)[1].equalsIgnoreCase(searchHash)) {
                return "Message found.\\nMessage ID: " + sentMessages.get(i)[0]
                        + "\\nMessage Hash: " + sentMessages.get(i)[1]
                        + "\\nRecipient: " + sentMessages.get(i)[3]
                        + "\\nMessage: " + sentMessages.get(i)[4]
                        + "\\nStatus: Sent";
            }
        }

        for (int i = 0; i < disregardedMessages.size(); i++) {
            if (disregardedMessages.get(i)[1].equalsIgnoreCase(searchHash)) {
                return "Message found.\\nMessage ID: " + disregardedMessages.get(i)[0]
                        + "\\nMessage Hash: " + disregardedMessages.get(i)[1]
                        + "\\nRecipient: " + disregardedMessages.get(i)[3]
                        + "\\nMessage: " + disregardedMessages.get(i)[4]
                        + "\\nStatus: Disregarded";
            }
        }

        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i)[1].equalsIgnoreCase(searchHash)) {
                return "Message found.\\nMessage ID: " + storedMessages.get(i)[0]
                        + "\\nMessage Hash: " + storedMessages.get(i)[1]
                        + "\\nRecipient: " + storedMessages.get(i)[3]
                        + "\\nMessage: " + storedMessages.get(i)[4]
                        + "\\nStatus: Stored";
            }
        }

        return "Message hash not found.";
    }

    // Sets up a new message with its ID, number and hash
    public static void startMessage(String recipient, String text) {
        currentMessageID = generateMessageID();
        currentNumber = sentMessages.size() + storedMessages.size()
                + disregardedMessages.size();
        currentRecipient = recipient;
        currentMessage = text;
        currentHash = createMessageHash();
    }

    // Updates the arrays after a message has been added or removed
    public static void updateArrays() {
        sentMessagesArray = new String[sentMessages.size()];
        disregardedMessagesArray = new String[disregardedMessages.size()];
        storedMessagesArray = new String[storedMessages.size()];
        messageHashesArray = new String[sentMessages.size()
                + disregardedMessages.size() + storedMessages.size()];
        messageIDsArray = new String[sentMessages.size()
                + disregardedMessages.size() + storedMessages.size()];

        int position = 0;

        for (int i = 0; i < sentMessages.size(); i++) {
            sentMessagesArray[i] = sentMessages.get(i)[4];
            messageHashesArray[position] = sentMessages.get(i)[1];
            messageIDsArray[position] = sentMessages.get(i)[0];
            position++;
        }

        for (int i = 0; i < disregardedMessages.size(); i++) {
            disregardedMessagesArray[i] = disregardedMessages.get(i)[4];
            messageHashesArray[position] = disregardedMessages.get(i)[1];
            messageIDsArray[position] = disregardedMessages.get(i)[0];
            position++;
        }

        for (int i = 0; i < storedMessages.size(); i++) {
            storedMessagesArray[i] = storedMessages.get(i)[4];
            messageHashesArray[position] = storedMessages.get(i)[1];
            messageIDsArray[position] = storedMessages.get(i)[0];
            position++;
        }
    }

    // Lets the user send, disregard or store the message
    // 1 = send, 2 = disregard, 3 = store
    public static String SentMessage(int choice) {
        String[] details = {
            currentMessageID,
            currentHash,
            firstname + " " + lastname,
            currentRecipient,
            currentMessage
        };

        if (choice == 1) {
            sentMessages.add(details);
            updateArrays();
            return "Message successfully sent.";
        } else if (choice == 2) {
            disregardedMessages.add(details);
            updateArrays();
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            storeMessage();
            return "Message successfully stored.";
        } else {
            return "Invalid option.";
        }
    }

    // Stores messages in a JSON file
    public static void storeMessage() {
        String[] details = {
            currentMessageID,
            currentHash,
            firstname + " " + lastname,
            currentRecipient,
            currentMessage
        };

        storedMessages.add(details);
        updateArrays();
        writeStoredMessages();
    }

    // Writes all stored messages to the JSON file
    public static void writeStoredMessages() {
        try {
            FileWriter writer = new FileWriter(storageFile);

            writer.write("[\n");

            for (int i = 0; i < storedMessages.size(); i++) {
                String[] message = storedMessages.get(i);

                writer.write("  {\n");
                writer.write("    \"messageID\": \"" + escapeJson(message[0]) + "\",\n");
                writer.write("    \"messageHash\": \"" + escapeJson(message[1]) + "\",\n");
                writer.write("    \"sender\": \"" + escapeJson(message[2]) + "\",\n");
                writer.write("    \"recipient\": \"" + escapeJson(message[3]) + "\",\n");
                writer.write("    \"message\": \"" + escapeJson(message[4]) + "\"\n");
                writer.write("  }");

                if (i < storedMessages.size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("]\n");
            writer.close();

        } catch (IOException e) {
            System.out.println("The message could not be saved to the file.");
        }
    }

    // Makes text safe to put inside JSON quotation marks
    public static String escapeJson(String text) {
        text = text.replace("\\", "\\\\");
        text = text.replace("\"", "\\\"");
        text = text.replace("\n", "\\n");
        text = text.replace("\r", "\\r");
        text = text.replace("\t", "\\t");
        return text;
    }

    // Changes JSON text back to normal text
    public static String unescapeJson(String text) {
        text = text.replace("\\n", "\n");
        text = text.replace("\\r", "\r");
        text = text.replace("\\t", "\t");
        text = text.replace("\\\"", "\"");
        text = text.replace("\\\\", "\\");
        return text;
    }

    // Gets a value from one line in the JSON file
    public static String getJsonValue(String line) {
        int start = line.indexOf(": \"");

        if (start == -1) {
            return "";
        }

        start = start + 3;
        int end = line.lastIndexOf("\"");

        if (end < start) {
            return "";
        }

        return unescapeJson(line.substring(start, end));
    }

    // Reads the stored messages from the JSON file into the stored messages array
    public static void readStoredMessages() {
        File file = new File(storageFile);

        if (!file.exists()) {
            updateArrays();
            return;
        }

        storedMessages.clear();

        String id = "";
        String hash = "";
        String sender = "";
        String recipient = "";
        String message = "";

        try {
            Scanner fileReader = new Scanner(file);

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine().trim();

                if (line.startsWith("\"messageID\"")) {
                    id = getJsonValue(line);
                } else if (line.startsWith("\"messageHash\"")) {
                    hash = getJsonValue(line);
                } else if (line.startsWith("\"sender\"")) {
                    sender = getJsonValue(line);
                } else if (line.startsWith("\"recipient\"")) {
                    recipient = getJsonValue(line);
                } else if (line.startsWith("\"message\"")) {
                    message = getJsonValue(line);

                    String[] details = {id, hash, sender, recipient, message};
                    storedMessages.add(details);
                }
            }

            fileReader.close();

        } catch (IOException e) {
            System.out.println("The stored messages could not be read.");
        }

        updateArrays();
    }

    // Displays all messages that were sent
    public static String printMessages() {
        if (sentMessages.size() == 0) {
            return "No messages have been sent.";
        }

        String all = "";

        for (int i = 0; i < sentMessages.size(); i++) {
            String[] message = sentMessages.get(i);

            if (i > 0) {
                all = all + "\n\n";
            }

            all = all + "Message ID: " + message[0] + "\n"
                    + "Message Hash: " + message[1] + "\n"
                    + "Recipient: " + message[3] + "\n"
                    + "Message: " + message[4];
        }

        return all;
    }

    // Returns the total number of messages sent
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    //PART 3 

    // Displays the sender and recipient of all stored messages
    public static String displayStoredSendersAndRecipients() {
        readStoredMessages();

        if (storedMessages.size() == 0) {
            return "There are no stored messages.";
        }

        String result = "";

        for (int i = 0; i < storedMessages.size(); i++) {
            String[] message = storedMessages.get(i);
            result = result + "Sender: " + message[2]
                    + "\nRecipient: " + message[3] + "\n\n";
        }

        return result;
    }

    // Finds and displays the longest stored message
    public static String displayLongestStoredMessage() {
        readStoredMessages();

        if (storedMessages.size() == 0) {
            return "There are no stored messages.";
        }

        String longestMessage = storedMessages.get(0)[4];

        for (int i = 1; i < storedMessages.size(); i++) {
            if (storedMessages.get(i)[4].length() > longestMessage.length()) {
                longestMessage = storedMessages.get(i)[4];
            }
        }

        return longestMessage;
    }

    // Searches for a message ID and returns its recipient and message
    public static String searchMessageByID(String searchID) {
        readStoredMessages();

        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i)[0].equals(searchID)) {
                return "Recipient: " + sentMessages.get(i)[3]
                        + "\nMessage: " + sentMessages.get(i)[4];
            }
        }

        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i)[0].equals(searchID)) {
                return "Recipient: " + storedMessages.get(i)[3]
                        + "\nMessage: " + storedMessages.get(i)[4];
            }
        }

        return "Message ID not found.";
    }

    // Searches for all sent or stored messages for a recipient
    public static String searchMessagesByRecipient(String recipient) {
        readStoredMessages();

        String result = "";

        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i)[3].equals(recipient)) {
                result = result + sentMessages.get(i)[4] + "\n";
            }
        }

        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i)[3].equals(recipient)) {
                result = result + storedMessages.get(i)[4] + "\n";
            }
        }

        if (result.equals("")) {
            return "No messages found for this recipient.";
        }

        return result;
    }

    // Deletes a stored message by using its message hash
    public static String deleteStoredMessageByHash(String searchHash) {
        readStoredMessages();

        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i)[1].equals(searchHash)) {
                String deletedMessage = storedMessages.get(i)[4];
                storedMessages.remove(i);
                updateArrays();
                writeStoredMessages();

                return "Message: " + deletedMessage + " successfully deleted.";
            }
        }

        return "Message hash not found.";
    }

    // Displays a report with all stored messages
    public static String displayMessageReport() {
        readStoredMessages();

        if (storedMessages.size() == 0) {
            return "There are no stored messages.";
        }

        String report = "";

        for (int i = 0; i < storedMessages.size(); i++) {
            String[] message = storedMessages.get(i);

            report = report + "Message ID: " + message[0] + "\n"
                    + "Message Hash: " + message[1] + "\n"
                    + "Sender: " + message[2] + "\n"
                    + "Recipient: " + message[3] + "\n"
                    + "Message: " + message[4] + "\n\n";
        }

        return report;
    }

    // Shows the stored messages menu
    public static void storedMessagesMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\nSTORED MESSAGES");
            System.out.println("1) Display senders and recipients");
            System.out.println("2) Display longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search messages by recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Display stored message report");
            System.out.println("7) Search for a message using its hash");
            System.out.println("8) Return to main menu");
            System.out.print("Choose an option: ");

            String choice = input.nextLine();

            if (choice.equals("1")) {
                System.out.println(displayStoredSendersAndRecipients());
            } else if (choice.equals("2")) {
                System.out.println(displayLongestStoredMessage());
            } else if (choice.equals("3")) {
                System.out.print("Enter message ID: ");
                String id = input.nextLine();
                System.out.println(searchMessageByID(id));
            } else if (choice.equals("4")) {
                System.out.print("Enter recipient number: ");
                String recipient = input.nextLine();
                System.out.println(searchMessagesByRecipient(recipient));
            } else if (choice.equals("5")) {
                System.out.print("Enter message hash: ");
                String hash = input.nextLine();
                System.out.println(deleteStoredMessageByHash(hash));
            } else if (choice.equals("6")) {
                System.out.println(displayMessageReport());
            } else if (choice.equals("7")) {
                System.out.print("Enter message hash: ");
                String hash = input.nextLine();
                System.out.println(searchMessageByHash(hash));
            } else if (choice.equals("8")) {
                running = false;
            } else {
                System.out.println("Please enter a number from 1 to 8.");
            }
        }
    }

    // Asks how many messages the user wants to enter
    public static int askNumberOfMessages() {
        while (true) {
            System.out.print("How many messages do you wish to enter? ");
            String answer = input.nextLine().trim();

            if (answer.matches("[0-9]+") && Integer.parseInt(answer) > 0) {
                return Integer.parseInt(answer);
            } else {
                System.out.println("Please enter a whole number greater than 0.");
            }
        }
    }

    // Collects one message and lets the user send, disregard or store it
    public static void composeMessage() {
        String recipient;

        // Keep asking until the recipient number is correct
        while (true) {
            System.out.print("Enter the recipient's cell number (with international code): ");
            recipient = input.nextLine().trim();

            currentRecipient = recipient;
            System.out.println(checkRecipientCell());

            if (isValidCell(recipient)) {
                break;
            }
        }

        // Keep asking until the message is not too long
        String text;

        while (true) {
            System.out.print("Enter your message: ");
            text = input.nextLine();

            if (text.trim().length() == 0) {
                System.out.println("The message cannot be empty.");
            } else if (text.length() > 250) {
                System.out.println(checkMessageLength(text));
            } else {
                System.out.println(checkMessageLength(text));
                break;
            }
        }

        // Create the message ID, number and hash
        startMessage(recipient, text);
        System.out.println("Message ID generated: " + currentMessageID);
        // Display the hash immediately so it is available for stored messages too
        System.out.println("Message Hash generated: " + currentHash);

        // Ask the user what they want to do with the message
        while (true) {
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choose an option: ");

            String answer = input.nextLine();

            if (!answer.equals("1") && !answer.equals("2")
                    && !answer.equals("3")) {
                System.out.println("Please enter 1, 2 or 3.");
                continue;
            }

            int choice = Integer.parseInt(answer);
            System.out.println(SentMessage(choice));

            if (choice == 1) {
                System.out.println("Message ID: " + currentMessageID);
                System.out.println("Message Hash: " + currentHash);
                System.out.println("Recipient: " + currentRecipient);
                System.out.println("Message: " + currentMessage);
                return;
            } else if (choice == 3) {
                // Show the tracking details after the message has been saved.
                // The same hash is saved in storedMessages.json with the ID,
                // sender, recipient and message text.
                System.out.println("\nSTORED MESSAGE TRACKING DETAILS");
                System.out.println("Message ID: " + currentMessageID);
                System.out.println("Message Hash: " + currentHash);
                System.out.println("Recipient: " + currentRecipient);
                System.out.println("Message: " + currentMessage);
                System.out.println("You can now search for this message using its hash or delete it using its hash from the Stored Messages menu.");
                return;
            } else {
                System.out.print("Enter 0 to delete the message: ");
                String confirm = input.nextLine();

                if (confirm.equals("0")) {
                    System.out.println("Message deleted.");
                }

                return;
            }
        }
    }

    // Runs QuickChat after the user has logged in
    public static void quickChat() {
        System.out.println("Welcome to QuickChat.");

        // Read messages already saved in the JSON file
        readStoredMessages();

        boolean running = true;

        while (running) {
            System.out.println("\nMAIN MENU");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");

            String choice = input.nextLine();

            if (choice.equals("1")) {
                messagesAllowed = askNumberOfMessages();

                // The for loop allows the user to enter the selected number of messages
                for (int i = 0; i < messagesAllowed; i++) {
                    System.out.println("\nMessage " + (i + 1));
                    composeMessage();
                }

                System.out.println("Total messages sent: " + returnTotalMessages());

            } else if (choice.equals("2")) {
                System.out.println(printMessages());

            } else if (choice.equals("3")) {
                storedMessagesMenu();

            } else if (choice.equals("4")) {
                System.out.println("Total messages sent: " + returnTotalMessages());
                System.out.println("Goodbye.");
                running = false;

            } else {
                System.out.println("Please enter 1, 2, 3 or 4.");
            }
        }
    }

    // Main method where the program starts
    public static void main(String[] args) {
        // Calls the registration method
        System.out.println(registerUser());

        // Keep asking the user to log in until the details are correct
        boolean loginSuccessful = false;

        while (!loginSuccessful) {
            System.out.print("Enter your username to login: ");
            String loginUsername = input.nextLine();

            System.out.print("Enter your password to login: ");
            String loginPassword = input.nextLine();

            loginSuccessful = loginUser(loginUsername, loginPassword);
            System.out.println(returnLoginStatus(loginSuccessful));
        }

        // The user has logged in, so they can use QuickChat
        quickChat();
    }
}
