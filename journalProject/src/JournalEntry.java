package src;

import java.util.ArrayList;

public class JournalEntry {
	private String date;
	private String journalEntry;
	private int happinessLevel;
	private ArrayList<Activity> activityList = new ArrayList<Activity>();
	
	public JournalEntry() {
		date = ""; // set to today's date
		journalEntry = "No text entered";
		happinessLevel = 0;
	}
	
	public JournalEntry(String date, String journalEntry, int happinessLevel) {
		this.date = date;
		this.journalEntry = journalEntry;
		this.happinessLevel = happinessLevel;
	}
	
	private void setText(String newText) {
		// IF newText only contains valid characters
		journalEntry = newText;
		// ELSE alert user it contains illegal chars
	}
	
	public String getText() {
		return journalEntry;
	}
	
	public void setHappinessLevel(int newHappiness) {
		if (newHappiness > 0 && newHappiness < 6) {
			happinessLevel = newHappiness;
		} else {
			//alert user invalid happiness (replace w/ icons eventually)
		}
	}
	
	public int getHappinessLevel() {
		return happinessLevel;
	}
	
	public String getDate() {
		return date;
	}
	
	public ArrayList<Activity> getActivityList() {
		return activityList;
	}
	
	public void addActivity(Activity newActivity) {
		activityList.add(newActivity);
	}
	
	public void addActivityList(ArrayList<Activity> actList) {
		activityList = actList;
	}
	
	public void removeActivity(Activity removedActivity) {
		int counter = 0;
		for (Activity act : activityList) {
			if (activityList.get(counter).equals(removedActivity)) {
				activityList.remove(counter);
				return;
			}
		}
		System.out.println("No such activity found.");
	}
}
