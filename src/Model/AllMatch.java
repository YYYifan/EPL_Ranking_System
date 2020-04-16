package Model;

public class AllMatch {
	private int numGames = 0;
	private double totalHomeGoals = 0;
	private double totalAwayGoals = 0;
	private double totalHomeLoses = 0;
	private double totalAwayLoses = 0;
	
	public void loadMatch(MatchData match) {
		this.numGames += 1;
		this.totalHomeGoals += match.getHomeGoals();
		this.totalAwayGoals += match.getAwayGoals();
		this.totalHomeLoses += match.getAwayGoals();
		this.totalAwayLoses += match.getHomeGoals();
	}
	
	public double getAvgHomeGoals() {
		return totalHomeGoals / numGames;
	}
	public double getAvgHomeLoses() {
		return totalHomeLoses / numGames;
	}
	public double getAvgAwayGoals() {
		return totalAwayGoals / numGames;
	}
	public double getAvgAwayLoses() {
		return totalAwayLoses / numGames;
	}
}
