package src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

//TODO Make new entry/edit entry page look better so that activities don't overlap
//TODO Custom buttons for activities.
//TODO Ability to add your own activities
//TODO Scrollbar for activities, or just some better way of displaying them on new entry page

public class Journal extends Application {

	/* The main window of the program. **/
	Stage window;

	/* Scene used to enter a date for a journal entry. **/
	Scene dateEntryScene;

	/* Scene used to enter a new journal entry, happiness, and activities. **/
	Scene newEntryScene;

	/* Button in the date-entering scene to move on to the journal entry scene. **/
	Button submitDateButton;

	/* Boxes used to hold the month, day, and year of the entry being created/edited. **/
	ComboBox<String> monthBox;
	ComboBox<String> dayBox;
	ComboBox<String> yearBox;

	/* Button in the journal entry scene, used to save the entry once everything is filled out. **/
	Button submitButton;

	/* The total number of activities that have been created. **/
	// TODO make it so array is only as big as total activities
	int totalActivities = 23;

	/* List that holds all the activities that have been read and created from the activities.txt text document. **/
	ArrayList<Activity> activityList;

	/* List that holds all the entries that have been read and created from the entries folder. **/
	ArrayList<JournalEntry> entryList = new ArrayList<JournalEntry>();

	/* Array that holds all the activity buttons in the journal entry scene. **/
	// TODO Change to ArrayList
	ArrayList<ToggleButton> activityButtons;

	/*
	 * The main function. This just starts the program by calling the launch method.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/*
	 * Reads all the activities created and saved in the activities.txt file, and adds them to a list for later.
	 */
	public void readActivitiesFromFile(String fileName) {

		//TODO Change this to an ArrayList. Seriously.
		activityList = new ArrayList<Activity>();
		Scanner fileIn = null;
		try {
			// Creates a scanner, and reads each line in the file to create Activity objects.
			fileIn = new Scanner(new File(fileName));
			while (fileIn.hasNextLine()) {
				// Reads the activity name, ON icon file path, and OFF icon file path.
				String activityName = fileIn.nextLine();
				String activityOnIcon = fileIn.nextLine();
				String activityOffIcon = fileIn.nextLine();
				int activityScore = Integer.parseInt(fileIn.nextLine());
				Activity newActivity = new Activity(activityName, activityOnIcon, activityOffIcon, activityScore);
				activityList.add(newActivity);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fileIn != null) {
				fileIn.close();
			}
		}
	}

	/*
	 * Once a journal entry has been completed, this method takes all the data from the journal entry and date entry scenes, and saves the entry to a .txt file in the entries folder for later use.
	 */
	public void saveEntryToFile(JournalEntry entry) {
		BufferedWriter buffWrite = null;
		FileWriter fileWrite = null;

		// Gets the date string from the entry, and removes the slashes so it can be used as a file name.
		String dateWithoutSlashes = "";
		for (int i = 0; i < entry.getDate().length(); i++) {
			if (entry.getDate().charAt(i) != '/') {
				dateWithoutSlashes += entry.getDate().charAt(i);
			}
		}
		try {
			// The entries will be written to a .txt file in the entries folder, using the date as numbers for the filename.
			fileWrite = new FileWriter("entries/" + dateWithoutSlashes + ".txt");
			buffWrite = new BufferedWriter(fileWrite);
			// If the user pressed the newline key, make sure to accomodate for that in the .txt file by adding a new line.
			String[] entryArray = entry.getText().split("\n");
			for (String s : entryArray) {
				buffWrite.write(s);
				buffWrite.newLine();
			}
			// Write the word END so when reading the file back, the scanner knows when to stop assigning text to the entry.
			buffWrite.write("END");
			buffWrite.newLine();
			// Write the date, happiness, as well as activities selected to the file.
			buffWrite.write(entry.getDate());
			buffWrite.newLine();
			int happinessLevel = entry.getHappinessLevel();
			buffWrite.write("" + happinessLevel);
			buffWrite.newLine();
			for (Activity act : entry.getActivityList()) {
				if (happinessLevel > 3) {
					updateScore(act, 1);
				} else if (happinessLevel < 3) {
					updateScore(act, -1);
				}
				buffWrite.write(act.getActivityName());
				buffWrite.newLine();
			}
			updateActivities();
			System.out.println("Written to file: \"" + entry.getDate() + ".txt\"");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Make sure to close your writers!
			try {
				if (buffWrite != null) {
					buffWrite.close();
				}
				if (fileWrite != null) {
					fileWrite.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// TODO make sure it updates score properly (not just every time you press submit
	public void updateScore(Activity updateActivity, int scoreChange) {
		for (Activity act : activityList) {
			if (updateActivity.equals(act)) {
				act.updateScore(scoreChange);
				break;
			}
		}
	}

	public void updateActivities() {
		BufferedWriter buffWrite = null;
		FileWriter fileWrite = null;
		try {
			// The entries will be written to a .txt file in the entries folder, using the date as numbers for the filename.
			fileWrite = new FileWriter("activities.txt");
			buffWrite = new BufferedWriter(fileWrite);
			for (Activity act : activityList) {
				buffWrite.write(act.getActivityName());
				buffWrite.newLine();
				buffWrite.write(act.getOnPath());
				buffWrite.newLine();
				buffWrite.write(act.getOffPath());
				buffWrite.newLine();
				buffWrite.write("" + act.getScore());
				buffWrite.newLine();
			}
			System.out.println("Written to file: \"activities.txt\"");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Make sure to close your writers!
			try {
				if (buffWrite != null) {
					buffWrite.close();
				}
				if (fileWrite != null) {
					fileWrite.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Reads all the journal entries contained in the entries folder, and saves them to a list for later use, as well as for the journal reading scene.
	 */
	public void readEntriesFromFile() {
		// Make sure to read files saved in the entries folder.
		File folder = new File("entries/");
		// Gets a list of all the files in the entries folder.
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				Scanner scnr = null;
				try {
					scnr = new Scanner(file);
					String entry = "";
					String date = "";
					int happinessLevel = 3;
					ArrayList<Activity> actList = new ArrayList<Activity>();
					// Read the text until the word END is encountered, signaling the program that the date and happiness values are next.
					while (scnr.hasNextLine()) {
						String nextLine = scnr.nextLine();
						if (!nextLine.equals("END")) {
							entry += nextLine + "\n";
						} else {
							date = scnr.nextLine();
							happinessLevel = Integer.parseInt(scnr.nextLine());
							// Add activities until the end of the file is reached (since only activities should be left).
							while (scnr.hasNextLine()) {
								String activityName = scnr.nextLine();
								Activity act = new Activity(activityName, activityName + "On.png", activityName + "Off.png");
								actList.add(act);
							}
						}
					}
					// Creates a new journal entry, and adds it to the list of entries to be used later.
					JournalEntry newEntry = new JournalEntry(date, entry, happinessLevel);
					newEntry.addActivityList(actList);
					entryList.add(newEntry);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					// Always close your scanners!
					if (scnr != null) {
						scnr.close();
					}
				}
			}
		}
	}

	/*
	 * Gets the amount of activities in the activity list, so as not to use a method on a null object.
	 * TODO Make this method obsolete by using an ArrayList instead of an array
	 */
	public int getActivityListLength() {
		return activityList.size();
	}

	/*
	 * Method that creates all the scenes, and objects contained in those scenes, as well as assigning those elements certain behaviors when interacted with. I.e. the submit button in the journal entry scene creates
	 * a new JournalEntry object, and saves it to a file.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// First, read all the activities from the saved file so they can be used.
		readActivitiesFromFile("activities.txt");
		// Second, read all the entries from the entries folder so they can be read, edited, etc.
		readEntriesFromFile();

		// The main window is now assigned to the window variable, since "window" makes more sense than "stage" to me.
		window = primaryStage;
		window.setTitle("Journal Project");

		// Creates a button to submit the date, and adds a listener to verify the date.
		submitDateButton = new Button("Continue");
		submitDateButton.setOnAction(new 
				EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				// Gets the month, day, and year entered in the combo boxes.
				int monthFinal = Integer.parseInt(monthBox.getValue());
				int dayFinal = Integer.parseInt(dayBox.getValue());
				int yearFinal = Integer.parseInt(yearBox.getValue());
				// Makes sure the month value is valid.
				if (monthFinal < 13 && monthFinal > 0) {
					// Makes sure the day selected actually exists for the selected month.
					int daysInMonth = 31;
					if (monthFinal == 4 || monthFinal == 5 || monthFinal == 
							6 || monthFinal == 9 || monthFinal == 11) {
						daysInMonth = 30;
					} else if (monthFinal == 2) {
						daysInMonth = 28;
					}
					if (dayFinal <= daysInMonth && dayFinal > 0) {
						// Finally, checks if the year entered is valid.
						if (yearFinal > 0 && yearFinal < 2100) {
							// Then, the data is verified, so change to the journal entry scene.
							window.setScene(newEntryScene);
						}
					}
				} else {
					// If the data is invalid, let the user see they need to entry a valid date.
					submitDateButton.setText("Enter valid date");
				}
				// Complete the button press event.
				event.consume();
			}
		});

		// SCENE: DATE ENTRY
		GridPane dateLayout = new GridPane();
		dateLayout.setPadding(new Insets(10, 10, 10, 10));
		dateLayout.setHgap(10);
		dateLayout.setVgap(10);

		// Get the system's current date to use in the month, day, and year boxes.
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDateTime now = LocalDateTime.now();
		String currentTime = dtf.format(now);
		String[] seperatedTimes = currentTime.split("/");
		int month = Integer.parseInt(seperatedTimes[0]);
		int day = Integer.parseInt(seperatedTimes[1]);
		int year = Integer.parseInt(seperatedTimes[2]);

		Label monthLabel = new Label("Month");
		Label dayLabel = new Label("Day");
		Label yearLabel = new Label("Year");

		// ComboBox for choosing the day of the month.
		dayBox = new ComboBox<>();

		// ComboBox for choosing the year. Set it to start with the current year.
		yearBox = new ComboBox<>();
		yearBox.getItems().addAll("2018", "2019", "2020", "2021", "2022");
		yearBox.getSelectionModel().select(year - 2018);

		// ComboBox for choosing the month for an entry. Only options are the numbers 1 through 12.
		monthBox = new ComboBox<>();
		monthBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", 
				"8", "9", "10", "11", "12");

		// Adds a listener for when the month box changes, so that the user is forced to update the day box. This ensures the user can't choose a nonexistant date like 2/31/2018.
		monthBox.valueProperty().addListener(new ChangeListener<String>() {
			// TODO Account for February 29th.
			@Override
			public void changed(ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				int month = Integer.parseInt(monthBox.getValue());
				// Clear the day box of the values it previously contained.
				dayBox.getItems().clear();
				// All months have 28 days, so add that to the day box regardless of the month.
				dayBox.getItems().addAll(
						"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"
						, "12", "13", "14", "15", "16", "17", "18", "19", "20",
						"21", "22", "23", "24", "25", "26", "27", "28");
				// February only has 28 days, so if it's any other month, add the 29th and 30th.
				if (month != 2) {
					dayBox.getItems().addAll("29", "30");
					// If it's a month with 31 days (Jan, Mar, Jul, Aug, Oct, Dec), add the 31st day.
					if (month == 1 || month == 3 || month == 7 || month == 8 || month == 10 || month == 12) {
						dayBox.getItems().addAll("31");
					}
				} else {
					// If February 29th exists in the selected year, add the 29th to Feb's day box.
					if ((Integer.parseInt(yearBox.getValue()) - 2000) % 4 == 0) {
						dayBox.getItems().add("29");
					}
				}
			}
		});
		// Select the current month and day, according to the system.
		monthBox.getSelectionModel().select(month - 1);
		dayBox.getSelectionModel().select(day - 1);

		// Add everything to the grid layout, completing the scene.
		dateLayout.add(monthLabel, 0, 0);
		dateLayout.add(new Label("/"), 1, 0);
		dateLayout.add(dayLabel, 2, 0);
		dateLayout.add(new Label("/"), 3, 0);
		dateLayout.add(yearLabel, 4, 0);

		dateLayout.add(monthBox, 0, 1);
		dateLayout.add(new Label("/"), 1, 1);
		dateLayout.add(dayBox, 2, 1);
		dateLayout.add(new Label("/"), 3, 1);
		dateLayout.add(yearBox, 4, 1);

		dateLayout.add(submitDateButton, 0, 2);

		dateEntryScene = new Scene(dateLayout, 800, 600);

		// SCENE: NEW JOURNAL ENTRY
		GridPane newEntryLayout = new GridPane();
		newEntryLayout.setPadding(new Insets(10, 10, 10, 10));
		newEntryLayout.setHgap(10);
		newEntryLayout.setVgap(10);

		Label entryLabel = new Label("Journal Entry");

		// Create a TextArea for entering the text of the journal entry.
		TextArea entryArea = new TextArea();
		entryArea.setPrefHeight(600);
		entryArea.setPrefWidth(400);
		entryArea.setWrapText(true);

		// This label is used to keep track of how many characters the user has left. They start with 300, and as they type, the number decreases.
		Label charLabel = new Label("" + 300);

		// The user can select a happiness to rate their day, and this will later be used to see what activities are done on positive or negative days.
		Label happinessLabel = new Label("Happiness Today:");
		ComboBox<String> happinessBox = new ComboBox<>();
		// Ratings are on a 1-5 scale.
		happinessBox.getItems().addAll("1", "2", "3", "4", "5");
		// Makes sure that both a happiness is selected, and the user hasn't exceeded the character limit.
		happinessBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				int charsLeft = 300 - entryArea.getLength();

				if (charsLeft >= 0 && happinessBox.getValue() != null) {
					submitButton.setDisable(false);
				} else {
					submitButton.setDisable(true);
				}
			}
		});
		// Adds a listener to the text area, so that when it is typed in, it updates the submit button and character count accordingly.
		entryArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> 
			observable, final String oldValue, final String newValue) {
				int charsLeft = 300 - entryArea.getLength();
				charLabel.setText("" + charsLeft);

				if (charsLeft >= 0 && happinessBox.getValue() != null) {
					submitButton.setDisable(false);
				} else {
					submitButton.setDisable(true);
				}
			}
		});
		// Creates a label and buttons for a bunch of activities that the user can select, depending on what they did that day.
		Label activityLabel = new Label("Activities:");
		// Only create as many buttons as activities.
		activityButtons = new ArrayList<ToggleButton>();
		// Adds a ScrollPane which contains a TilePane of the activity buttons
		ScrollPane scrollPane = new ScrollPane();
		TilePane actLayout = new TilePane();
		actLayout.setVgap(10);
		actLayout.setHgap(10);
		actLayout.setPadding(new Insets(10, 10, 10, 10));
		for (Activity act : activityList) {
			if (act != null) {
				// Each activity creates a new toggle button with the activity's name on it.
				ToggleButton toggle = new ToggleButton();
				toggle.setText(act.getActivityName());
				Image selected = new Image("on.png");
				Image unselected = new Image("off.png");
				ImageView toggleImage = new ImageView();
				toggle.setGraphic(toggleImage);
				// When the button is clicked, the image for it changes between the "on" and "off" states so the user can see what they selected.
				toggleImage.imageProperty().bind(Bindings.when(toggle.selectedProperty()).then(selected).otherwise(unselected));
				// Adds the button to the TilePane.
				actLayout.getChildren().add(toggle);
				// Adds the toggle button to an array so it can be easily accessed, with the first button in activityButtons corresponding with the first activity in activityList.
				activityButtons.add(toggle);
			}
		}
		scrollPane.setContent(actLayout);
		// Adds the activities to the bottom of the entries layout
		newEntryLayout.add(scrollPane, 0, 6);
		// Create a submit button that collects the entered info, and creates a JournalEntry, which is then saved to the entries folder.
		submitButton = new Button("Submit");
		submitButton.setDisable(true);
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				String dateString = monthBox.getValue() + "/" + dayBox.getValue() + "/" + yearBox.getValue();
				JournalEntry newEntry = new JournalEntry(dateString, entryArea.getText(), Integer.parseInt(happinessBox.getValue()));
				// Checks each activity button, and adds the activities that were selected to the new entry.
				ArrayList<Activity> entryActivitiesList = new ArrayList<Activity>();
				int activityIDCounter = 0;
				for (ToggleButton togBut : activityButtons) {
					if (togBut.isSelected()) {
						entryActivitiesList.add(activityList.get(activityIDCounter));
					}
					activityIDCounter++;
				}
				newEntry.addActivityList(entryActivitiesList);
				saveEntryToFile(newEntry);
			}
		});
		// Adds all the elements to the new entry layout, completing the scene.
		newEntryLayout.add(entryLabel, 0, 0);
		newEntryLayout.add(entryArea, 0, 1);
		newEntryLayout.add(charLabel, 0, 2);
		newEntryLayout.add(happinessLabel, 0, 3);
		newEntryLayout.add(happinessBox, 1, 3);
		newEntryLayout.add(activityLabel, 0, 4);
		newEntryLayout.add(submitButton, 1, 0);
		newEntryScene = new Scene(newEntryLayout, 1200, 900);

		// SCENE: READ JOURNAL ENTRIES
		VBox entryLayout = new VBox(10);
		entryLayout.setPadding(new Insets(10, 10, 10, 10));

		// Add listener to change scene to select a date when the "New Entry" button is pressed
		Button newEntryButton = new Button("New Entry");
		newEntryButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				primaryStage.setScene(dateEntryScene);
			}
		});
		// Gonna be honest, not totally sure how this works. Essentially though, it takes the files contained inside "entries/", and sends them to a zip file (backup.zip).
		Button backupButton = new Button("Backup Entries");
		backupButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				File f = new File("backup.zip");
				if (f.exists() && !f.isDirectory()) {
					f.delete();
				}
				try {
					Path p = Files.createFile(Paths.get("backup.zip")); // zipFilePath

					try(ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
						Path pp = Paths.get("entries/"); // sourceDirPath
						Files.walk(pp).filter(path -> !Files.isDirectory(path)).forEach(path -> {
							ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
							try {
								zs.putNextEntry(zipEntry);
								Files.copy(path, zs);
								zs.closeEntry();
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		entryLayout.getChildren().addAll(newEntryButton, backupButton);
		for (JournalEntry entry : entryList) {
			// Add an edit button that, when clicked, loads the data from that entry into the "new entry" scene, and then overrites it when saved.
			Button editButton = new Button("Edit Entry");
			editButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					primaryStage.setScene(newEntryScene);
					entryArea.setText(entry.getText());
					// Select the happiness level from the entry being edited.
					happinessBox.getSelectionModel().select(entry.getHappinessLevel() - 1);
					// Get the date from the entry being edited and select it. (No need to go to the date select scene)
					String[] monthDayYear = entry.getDate().split("/");
					monthBox.getSelectionModel().select(monthDayYear[0]);
					dayBox.getSelectionModel().select(monthDayYear[1]);
					yearBox.getSelectionModel().select(monthDayYear[2]);
					// Reselect any activities that were selected in the entry being edited.
					for (Activity act : entry.getActivityList()) {
						for (ToggleButton toggle : activityButtons) {
							if (toggle.getText().equals(act.getActivityName())) {
								toggle.setSelected(true);
								break;
							}
						}
					}
				}
			});
			// Creates a text box to display each entry, as well as other objects to display the date, happiness rating, and activities.
			TextArea entryTextArea = new TextArea();
			entryTextArea.setEditable(false);
			entryTextArea.setText(entry.getText());
			// Date and happiness labels
			Label dateLabel = new Label("Date: " + entry.getDate());
			Label happinessLabel2 = new Label("Joy: " + entry.getHappinessLevel());
			// An ArrayList of activity buttons. While the buttons don't do anything, they will display which activities were chosen for that day (They're essentially icons.)
			ArrayList<Button> activityButtons = new ArrayList<Button>();
			Label actLabel = new Label("Activities: ");
			// Creates a horizontal box for the first row.
			HBox row1 = new HBox(20);
			row1.setPadding(new Insets(10, 10, 10, 10));
			row1.getChildren().addAll(dateLabel, happinessLabel2, editButton);
			// Creates a horizontal box for the third row.
			HBox row3 = new HBox(15);
			row3.setPadding(new Insets(10, 10, 10, 10));
			// Creates buttons for all the activities that were selected to the third row.
			for (Activity act : entry.getActivityList()) {
				Button but = new Button(act.getActivityName());
				//TODO Set button image to activity instead of text
				//but.setIcon(act.getActivityName() + ".png");
				activityButtons.add(but);
			}
			// Adds the activity label, as well as the activity buttons that were created.
			row3.getChildren().addAll(actLabel);
			for (Button but : activityButtons) {
				row3.getChildren().add(but);
			}
			Separator separator = new Separator();
			entryLayout.getChildren().addAll(row1, entryTextArea, row3, separator);
		}
		// Finishes creation of the journal scene, and sets it to the startup screen.
		Scene journalScene = new Scene(entryLayout, 600, 900);
		primaryStage.setScene(journalScene);
		primaryStage.show();
	}
}
