# ST10522363POEPART3
# QuickChat -- Java Messaging Application

## Overview

QuickChat is a console-based Java application in the
`com.mycompany.main` package. It allows a user to register, log in,
create messages, and choose whether to send, disregard, or store each
message.

Each new message receives a ten-digit message ID and a generated
tracking hash. Stored messages are saved to a JSON file so they can be
loaded again when the application runs.

## Project files

  -----------------------------------------------------------------------
  File                                Purpose
  ----------------------------------- -----------------------------------
  `Main.java`                         Main application: registration,
                                      login, message handling, menus,
                                      JSON storage, searching, and
                                      reports.

  `MainTest.java`                     JUnit 5 tests for methods in
                                      `Main.java`. Keep this in the test
                                      source folder, separate from
                                      `Main.java`.

  `storedMessages.json`               Created or updated when messages
                                      are stored. Contains stored message
                                      details.
  -----------------------------------------------------------------------

Both Java files use:

``` java
package com.mycompany.main;
```

## Requirements

-   Java Development Kit (JDK) 11 or later.
-   NetBeans or another Java IDE.
-   JUnit Jupiter (JUnit 5) to run `MainTest.java`.

The test file uses Java 11 methods including `String.repeat()` and
`Files.readString()`.

## Application features

### Registration and login

At startup, the user enters their first name, last name, username,
password, and cellphone number.

The current validation rules are:

-   Username must contain an underscore and be no more than five
    characters long.
-   Password must contain at least eight characters, an uppercase
    letter, a number, and a special character.
-   Cellphone number must include an international `+` country code.
-   Login must use the username and password entered during
    registration.

Registration details are held in memory while the application is
running. User accounts are not saved permanently by this version.

### Message creation and management

After login, the main menu provides:

1.  Send Messages
2.  Show recently sent messages
3.  Stored Messages
4.  Quit

The user chooses how many messages to enter, then supplies a recipient
number and message text. Messages are limited to 250 characters.

For each new message, the program generates a ten-digit message ID and a
message hash. The user then chooses to:

1.  Send Message
2.  Disregard Message
3.  Store Message to send later

Sent and disregarded messages are held in memory for the current
session. Stored messages are written to `storedMessages.json`.

### Message hash and tracking

The hash format is:

``` text
First two digits of message ID : message number : first word + last word
```

The first and last words are converted to uppercase, with punctuation
removed.

Example:

``` text
Message ID: 0012345678
Message number: 0
Message: Hi Mike, can you join us for dinner tonight?
Message Hash: 00:0:HITONIGHT
```

The hash is displayed when a message is created and is saved together
with the message ID, sender, recipient, and message text when the
message is stored. It is used to search for a stored message and to
delete a stored message.

### Stored-message menu

The Stored Messages menu provides:

1.  Display senders and recipients
2.  Display longest stored message
3.  Search for a message ID
4.  Search messages by recipient
5.  Delete a message using its hash
6.  Display stored message report
7.  Search for a message using its hash
8.  Return to main menu

The stored-message report includes the message ID, hash, sender,
recipient, and message text.

### JSON storage

Stored messages are written to `storedMessages.json` with these fields:

-   `messageID`
-   `messageHash`
-   `sender`
-   `recipient`
-   `message`

The program reads this file when QuickChat starts and when
stored-message operations are performed. The file is created when the
first message is stored. Keep it in the application's working directory
and ensure the program can read and write it.

## Running the application in NetBeans

1.  Open the QuickChat project in NetBeans.
2.  Under **Source Packages**, open `com.mycompany.main`.
3.  Make sure `Main.java` is inside that package.
4.  Right-click the project and select **Clean and Build**.
5.  Run the `Main` class.
6.  Follow the prompts in the Output window.

For a new project, create a Java application, create the
`com.mycompany.main` package, and add `Main.java` to it.

## Running the JUnit tests

Place the files in this structure:

``` text
Source Packages
└── com.mycompany.main
    └── Main.java

Test Packages
└── com.mycompany.main
    └── MainTest.java
```

Then:

1.  Add JUnit Jupiter (JUnit 5) to the project dependencies.
2.  Add `MainTest.java` under **Test Packages** in `com.mycompany.main`.
3.  Right-click `MainTest.java`.
4.  Select **Test File**.
5.  Review the results in the NetBeans Test Results window.

The tests clear message lists before each test and use a temporary JSON
file, helping keep test data separate from the application's normal
`storedMessages.json`.

The test class covers registration validation, login, message length,
recipient validation, message ID and hash generation, sending,
disregarding, storing, JSON reading and writing, message searches,
deletion by hash, arrays, and stored-message reports.

Interactive methods that wait for keyboard input, such as `main()`,
`registerUser()`, `quickChat()`, `composeMessage()`, and
`storedMessagesMenu()`, are not executed by this test class.

## Notes and current limitations

-   Sent and disregarded messages are held in memory and are not written
    to JSON. Stored messages are persisted in `storedMessages.json`.
-   Registered user details are held in memory and are not saved between
    runs.
-   The JSON reader in `Main.java` is a simple line-based reader
    designed for the JSON layout produced by this application. It is not
    a general-purpose JSON parser.
-   The message hash is a tracking value based on message details; it is
    not a cryptographic security hash.
-   The arrays are refreshed by `updateArrays()` when messages are
    added, loaded, or deleted.

## Troubleshooting

**JUnit imports cannot be found:** Confirm that JUnit Jupiter (JUnit 5)
is added to the project and that `MainTest.java` is in the test source
folder.

**The program cannot find `Main`:** Check that both Java files use
`package com.mycompany.main;` and that `Main.java` is in the matching
package.

**Stored messages do not appear:** Check that `storedMessages.json` is
in the application's working directory and that the program has
permission to read and write it.

**Tests affect normal stored-message data:** Keep the temporary-file
setup in `MainTest.java`. It redirects storage to a temporary file
during each test and restores the normal file setting afterward.

## Project note

This README describes the supplied `Main.java` and `MainTest.java`
source code. Run the application and JUnit tests in the target NetBeans
project to verify the project configuration and runtime environment.
