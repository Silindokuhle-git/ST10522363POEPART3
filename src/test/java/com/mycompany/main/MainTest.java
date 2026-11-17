package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * JUnit 5 tests for the Main.java source code supplied with QuickChat.
 *
 * Important:
 * 1. Save this file as MainTest.java.
 * 2. Keep it in package com.mycompany.main.
 * 3. Make sure the project has JUnit Jupiter (JUnit 5) added.
 * 4. These tests do not call interactive methods such as main(), quickChat(),
 *    composeMessage(), registerUser() or storedMessagesMenu().
 */
public class MainTest {

    private File tempFile;
    private String originalStorageFile;

    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 =
            "Hi Mike, can you join us for dinner tonight?";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String TEXT_2 =
            "Hi Keegan, did you receive the payment?";

    @BeforeEach
    public void setUp() throws IOException {
        // Keep the original file location so it can be restored after each test.
        originalStorageFile = Main.storageFile;

        // Use a separate temporary JSON file for each test.
        tempFile = Files.createTempFile("quickchat-test", ".json").toFile();
        Main.storageFile = tempFile.getAbsolutePath();

        // Clear all messages so that tests do not affect one another.
        Main.sentMessages.clear();
        Main.disregardedMessages.clear();
        Main.storedMessages.clear();

        Main.sentMessagesArray = new String[0];
        Main.disregardedMessagesArray = new String[0];
        Main.storedMessagesArray = new String[0];
        Main.messageHashesArray = new String[0];
        Main.messageIDsArray = new String[0];

        Main.currentMessageID = "";
        Main.currentNumber = 0;
        Main.currentRecipient = "";
        Main.currentMessage = "";
        Main.currentHash = "";

        // Details of the registered user used in the tests.
        Main.firstname = "Kyle";
        Main.lastname = "Smith";
        Main.registeredUsername = "kyl_1";
        Main.registeredPassword = "Ch&&sec@ke99!";
        Main.registeredCellPhone = "+27838968976";
    }

    @AfterEach
    public void tearDown() {
        // Restore the normal storage file location.
        Main.storageFile = originalStorageFile;

        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    // Captures text printed by methods that use System.out.
    private String printedBy(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output));
            action.run();
        } finally {
            System.setOut(original);
        }

        return output.toString().trim();
    }

    // Sets up a message directly, without asking for console input.
    private void setCurrent(String id, int number,
            String recipient, String message) {
        Main.currentMessageID = id;
        Main.currentNumber = number;
        Main.currentRecipient = recipient;
        Main.currentMessage = message;
        Main.currentHash = Main.createMessageHash();
    }

    //PART 1: REGISTRATION AND LOGIN

    @Test
    public void usernameCorrectMessage() {
        assertEquals(
                "Username successfully captured.",
                printedBy(() -> Main.checkUserName("kyl_1")));
    }

    @Test
    public void usernameIncorrectMessage() {
        assertEquals(
                "Username is not correctly formatted; please ensure that your username "
                + "contains an underscore and is no more than five characters in length.",
                printedBy(() -> Main.checkUserName("kyle!!!!!!!")));
    }

    @Test
    public void passwordCorrectMessage() {
        assertEquals(
                "Password successfully captured.",
                printedBy(() -> Main.checkPasswordComplexity("Ch&&sec@ke99!")));
    }

    @Test
    public void passwordIncorrectMessage() {
        assertEquals(
                "Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, "
                + "and a special character.",
                printedBy(() -> Main.checkPasswordComplexity("password")));
    }

    @Test
    public void cellPhoneCorrectMessage() {
        assertEquals(
                "Cell phone number successfully added.",
                printedBy(() -> Main.checkCellPhoneNumber("+27838968976")));
    }

    @Test
    public void cellPhoneIncorrectMessage() {
        assertEquals(
                "Cell phone number incorrectly formatted or does not contain international code.",
                printedBy(() -> Main.checkCellPhoneNumber("08966553")));
    }

    @Test
    public void usernameCorrectlyFormatted() {
        assertTrue(Main.checkUserName("kyl_1"));
    }

    @Test
    public void usernameIncorrectlyFormatted() {
        assertFalse(Main.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void passwordMeetsComplexity() {
        assertTrue(Main.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void passwordDoesNotMeetComplexity() {
        assertFalse(Main.checkPasswordComplexity("password"));
    }

    @Test
    public void internationalCellNumberIsValid() {
        assertTrue(Main.isValidCell("+27838968976"));
    }

    @Test
    public void localCellNumberWithoutInternationalCodeIsInvalid() {
        assertFalse(Main.isValidCell("08966553"));
    }

    @Test
    public void loginSuccessful() {
        assertTrue(Main.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void loginFailed() {
        assertFalse(Main.loginUser("kyl_1", "wrongPass1!"));
    }

    @Test
    public void loginSuccessMessage() {
        assertEquals(
                "Welcome Kyle, Smith it is great to see you again.",
                Main.returnLoginStatus(true));
    }

    @Test
    public void loginFailMessage() {
        assertEquals(
                "Username or password incorrect, please try again.",
                Main.returnLoginStatus(false));
    }

    //PART 2: MESSAGE CREATION

    @Test
    public void messageLengthSuccess() {
        String message = "a".repeat(250);
        assertEquals("Message ready to send.",
                Main.checkMessageLength(message));
    }

    @Test
    public void messageLengthFailure() {
        String message = "a".repeat(255);
        assertEquals(
                "Message exceeds 250 characters by 5; please reduce the size.",
                Main.checkMessageLength(message));
    }

    @Test
    public void recipientCorrectlyFormattedMessage() {
        setCurrent("1234567890", 0, RECIPIENT_1, TEXT_1);

        assertEquals(
                "Cell phone number successfully captured.",
                Main.checkRecipientCell());
    }

    @Test
    public void recipientWithoutInternationalCodeIsRejected() {
        setCurrent("1234567890", 1, RECIPIENT_2, TEXT_2);

        assertEquals(
                "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.",
                Main.checkRecipientCell());
    }

    @Test
    public void messageHashCorrect() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        assertEquals("00:0:HITONIGHT", Main.createMessageHash());

        setCurrent("1234567890", 1, RECIPIENT_1, TEXT_2);
        assertEquals("12:1:HIPAYMENT", Main.createMessageHash());

        setCurrent("9876543210", 2, RECIPIENT_1, "Thanks!");
        assertEquals("98:2:THANKSTHANKS", Main.createMessageHash());
    }

    @Test
    public void messageIDIsGeneratedAsTenDigits() {
        Main.startMessage(RECIPIENT_1, TEXT_1);

        assertEquals(10, Main.currentMessageID.length());
        assertTrue(Main.currentMessageID.matches("[0-9]{10}"));
        assertTrue(Main.checkMessageID());
        assertFalse(Main.currentHash.isEmpty());
    }

    @Test
    public void messageIDCheckRejectsMoreThanTenCharacters() {
        setCurrent("12345678901", 0, RECIPIENT_1, TEXT_1);

        assertFalse(Main.checkMessageID());
    }

    @Test
    public void messageCanBeSent() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);

        assertEquals("Message successfully sent.", Main.SentMessage(1));
        assertEquals(1, Main.sentMessages.size());
        assertEquals("00:0:HITONIGHT", Main.sentMessages.get(0)[1]);
    }

    @Test
    public void messageCanBeDisregarded() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);

        assertEquals("Press 0 to delete the message.",
                Main.SentMessage(2));
        assertEquals(1, Main.disregardedMessages.size());
        assertEquals("00:0:HITONIGHT",
                Main.disregardedMessages.get(0)[1]);
    }

    @Test
    public void messageCanBeStored() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);

        assertEquals("Message successfully stored.",
                Main.SentMessage(3));

        assertEquals(1, Main.storedMessages.size());
        assertEquals("0012345678", Main.storedMessages.get(0)[0]);
        assertEquals("00:0:HITONIGHT", Main.storedMessages.get(0)[1]);
        assertEquals(RECIPIENT_1, Main.storedMessages.get(0)[3]);
        assertEquals(TEXT_1, Main.storedMessages.get(0)[4]);
    }

    @Test
    public void invalidMessageChoiceIsRejected() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);

        assertEquals("Invalid option.", Main.SentMessage(9));
        assertEquals(0, Main.sentMessages.size());
        assertEquals(0, Main.storedMessages.size());
    }

    @Test
    public void totalMessagesSentCountsOnlySentMessages() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        Main.SentMessage(1);

        setCurrent("1112345678", 1, RECIPIENT_2, TEXT_2);
        Main.SentMessage(2);

        setCurrent("2212345678", 2, RECIPIENT_1, "A stored message");
        Main.SentMessage(3);

        assertEquals(1, Main.returnTotalMessages());
    }

    @Test
    public void printMessagesShowsIDHashRecipientAndMessage() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        Main.SentMessage(1);

        String expected = "Message ID: 0012345678\n"
                + "Message Hash: 00:0:HITONIGHT\n"
                + "Recipient: " + RECIPIENT_1 + "\n"
                + "Message: " + TEXT_1;

        assertEquals(expected, Main.printMessages());
    }

    // PART 2: JSON STORAGE

    @Test
    public void storedMessageIsWrittenAsJson() throws IOException {
        setCurrent("0012345678", 0, RECIPIENT_1, "Say \"hi\"");

        assertEquals("Message successfully stored.",
                Main.SentMessage(3));

        String json = Files.readString(
                tempFile.toPath(), StandardCharsets.UTF_8);

        assertTrue(json.contains("\"messageID\": \"0012345678\""));
        assertTrue(json.contains("\"messageHash\": \"00:0:SAYHI\""));
        assertTrue(json.contains("\"recipient\": \"" + RECIPIENT_1 + "\""));
        assertTrue(json.contains("\"message\": \"Say \\\"hi\\\"\""));
    }

    @Test
    public void storedMessageCanBeReadBackFromJson() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        Main.SentMessage(3);

        Main.storedMessages.clear();
        Main.readStoredMessages();

        assertEquals(1, Main.storedMessages.size());
        assertEquals("0012345678", Main.storedMessages.get(0)[0]);
        assertEquals("00:0:HITONIGHT", Main.storedMessages.get(0)[1]);
        assertEquals(RECIPIENT_1, Main.storedMessages.get(0)[3]);
        assertEquals(TEXT_1, Main.storedMessages.get(0)[4]);
    }

    @Test
    public void jsonEscapingAndUnescapingWork() {
        String original = "Hello \"friend\"\nHow are you?";
        String escaped = Main.escapeJson(original);

        assertEquals("Hello \\\"friend\\\"\\nHow are you?", escaped);
        assertEquals(original, Main.unescapeJson(escaped));
    }

    //PART 3: MESSAGE SEARCH AND REPORTS

    @Test
    public void searchMessageByHashFindsStoredMessage() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        Main.SentMessage(3);

        String result = Main.searchMessageByHash("00:0:HITONIGHT");

        assertTrue(result.contains("Message found."));
        assertTrue(result.contains("Message ID: 0012345678"));
        assertTrue(result.contains("Message Hash: 00:0:HITONIGHT"));
        assertTrue(result.contains("Recipient: " + RECIPIENT_1));
        assertTrue(result.contains("Message: " + TEXT_1));
        assertTrue(result.contains("Status: Stored"));
    }

    @Test
    public void searchMessageByHashReturnsNotFoundForUnknownHash() {
        assertEquals("Message hash not found.",
                Main.searchMessageByHash("UNKNOWN"));
    }

    @Test
    public void searchMessageByIDFindsStoredMessage() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        Main.SentMessage(3);

        String result = Main.searchMessageByID("0012345678");

        assertTrue(result.contains("Recipient: " + RECIPIENT_1));
        assertTrue(result.contains("Message: " + TEXT_1));
    }

    @Test
    public void searchMessagesByRecipientFindsStoredMessages() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        Main.SentMessage(3);

        setCurrent("1112345678", 1, RECIPIENT_1, "Another message");
        Main.SentMessage(3);

        String result = Main.searchMessagesByRecipient(RECIPIENT_1);

        assertTrue(result.contains(TEXT_1));
        assertTrue(result.contains("Another message"));
    }

    @Test
    public void displayStoredSendersAndRecipientsShowsBothFields() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        Main.SentMessage(3);

        String result = Main.displayStoredSendersAndRecipients();

        assertTrue(result.contains("Sender: Kyle Smith"));
        assertTrue(result.contains("Recipient: " + RECIPIENT_1));
    }

    @Test
    public void displayLongestStoredMessageReturnsLongestText() {
        String longest = "This is the longest stored message.";

        setCurrent("0012345678", 0, RECIPIENT_1, "Short");
        Main.SentMessage(3);

        setCurrent("1112345678", 1, RECIPIENT_1, longest);
        Main.SentMessage(3);

        assertEquals(longest, Main.displayLongestStoredMessage());
    }

    @Test
    public void deleteStoredMessageByHashRemovesMessageFromJsonAndList()
            throws IOException {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        Main.SentMessage(3);

        String result = Main.deleteStoredMessageByHash("00:0:HITONIGHT");

        assertTrue(result.contains("successfully deleted"));
        assertEquals(0, Main.storedMessages.size());

        String json = Files.readString(
                tempFile.toPath(), StandardCharsets.UTF_8);
        assertFalse(json.contains("0012345678"));
        assertFalse(json.contains("00:0:HITONIGHT"));
    }

    @Test
    public void deleteStoredMessageDoesNotDeleteUnknownHash() {
        assertEquals("Message hash not found.",
                Main.deleteStoredMessageByHash("UNKNOWN"));
    }

    @Test
    public void storedMessageReportIncludesHashAndMessageDetails() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        Main.SentMessage(3);

        String report = Main.displayMessageReport();

        assertTrue(report.contains("Message ID: 0012345678"));
        assertTrue(report.contains("Message Hash: 00:0:HITONIGHT"));
        assertTrue(report.contains("Sender: Kyle Smith"));
        assertTrue(report.contains("Recipient: " + RECIPIENT_1));
        assertTrue(report.contains("Message: " + TEXT_1));
    }

    @Test
    public void updateArraysIncludesMessageIDsAndHashes() {
        setCurrent("0012345678", 0, RECIPIENT_1, TEXT_1);
        Main.SentMessage(1);

        setCurrent("1112345678", 1, RECIPIENT_2, TEXT_2);
        Main.SentMessage(3);

        assertEquals(1, Main.sentMessagesArray.length);
        assertEquals(TEXT_1, Main.sentMessagesArray[0]);

        assertEquals(1, Main.storedMessagesArray.length);
        assertEquals(TEXT_2, Main.storedMessagesArray[0]);

        assertEquals(2, Main.messageIDsArray.length);
        assertEquals("0012345678", Main.messageIDsArray[0]);
        assertEquals("1112345678", Main.messageIDsArray[1]);

        assertEquals(2, Main.messageHashesArray.length);
        assertEquals("00:0:HITONIGHT", Main.messageHashesArray[0]);
        assertEquals("11:1:HIPAYMENT", Main.messageHashesArray[1]);
    }
}
