package src;

public class Activity {
	private String iconPathOn;
	private String iconPathOff;
	private String activityName;
	private int activityScore;
	
	public Activity(String newActivityName, String newOnPath, String newOffPath) {
		iconPathOn = newOnPath;
		iconPathOff = newOffPath;
		activityName = newActivityName;
	}
	
	public Activity(String newActivityName, String newOnPath, String newOffPath, int newScore) {
		iconPathOn = newOnPath;
		iconPathOff = newOffPath;
		activityName = newActivityName;
		activityScore = newScore;
	}
	
	public void setIconPath(String newPath) {
		iconPathOn = newPath;
	}
	
	public String getOnPath() {
		return iconPathOn;
	}
	
	public String getOffPath() {
		return iconPathOff;
	}
	
	public void setActivityName(String newName) {
		activityName = newName;
	}
	
	public String getActivityName() {
		return activityName;
	}
	
	public void updateScore(int change) {
		activityScore += change;
	}
	
	public int getScore() {
		return activityScore;
	}
	
	public boolean equals(Activity act) {
		if (act.getOnPath() == this.getOnPath() && act.getOffPath() == this.getOffPath() && act.getActivityName() == this.getActivityName()) {
			return true;
		} else {
			return false;
		}
	}
}
