package Model;

public class TeamData {
	private final String name;
	
	private int numHomeGames;
	private int numAwayGames;
	
	private double teamTotalHomeGoals = 0;
	private double teamTotalAwayGoals = 0;
	private double teamTotalHomeLoses = 0;
	private double teamTotalAwayLoses = 0;
	
	private double homeAttStrength;
	private double homeDefStrength;
	private double awayAttStrength;
	private double awayDefStrength;
	
	private double totalScores; //used for accumulate scores for each team in teams of possibility
	private int mostlikePoints;  //used for accumulate most likely points for each team
	
	//info used for ranking table
	private int numPlayedThisSeason;
	private int numWins;
	private int numDraws;
	private int numLoses;
	private int numGoalsFor;
	private int numGoalsAgainst;
	
	public TeamData(String name) {
		this.name = name;
	}
	
	public void AddMatchAsHome(MatchData match) {
		this.numHomeGames += 1;
		this.teamTotalHomeGoals += match.getHomeGoals();
		this.teamTotalHomeLoses += match.getAwayGoals();
	}
	public void AddMatchAsAway(MatchData match) {
		this.numAwayGames += 1;
		this.teamTotalAwayGoals += match.getAwayGoals();
		this.teamTotalAwayLoses += match.getHomeGoals();
	}
	public double getTeamAvgHomeGoals() {
		return teamTotalHomeGoals / numHomeGames;
	}
	public double getTeamAvgHomeLoses() {
		return teamTotalHomeLoses / numHomeGames;
	}
	public double getTeamAvgAwayGoals() {
		return teamTotalAwayGoals / numAwayGames;
	}
	public double getTeamAvgAwayLoses() {
		return teamTotalAwayLoses / numAwayGames;
	}

	public double getHomeAttStrength() {
		return homeAttStrength;
	}

	public double getHomeDefStrength() {
		return homeDefStrength;
	}

	public double getAwayAttStrength() {
		return awayAttStrength;
	}

	public double getAwayDefStrength() {
		return awayDefStrength;
	}
	public void computeStrength(AllMatch allMatch) {
		this.homeAttStrength = getTeamAvgHomeGoals() / allMatch.getAvgHomeGoals();
		this.awayAttStrength = getTeamAvgAwayGoals() / allMatch.getAvgAwayGoals();
		this.homeDefStrength = getTeamAvgHomeLoses() / allMatch.getAvgHomeLoses();
		this.awayDefStrength = getTeamAvgAwayLoses() / allMatch.getAvgAwayLoses();
	}
	public void computeScores(double points) {
		this.totalScores += points;
	}
	public void computePoints(int points) {
		this.mostlikePoints += points;
	}
	public int getNumPlayed() {
		return this.numPlayedThisSeason;
	}
	public void updateTeamDataAsHome(int hg, int ag) {
		if (hg > ag) {
			this.numWins += 1;
		} else if (hg == ag) {
			this.numDraws += 1;
		} else {
			this.numLoses += 1;
		}
		this.numGoalsFor += hg;
		this.numGoalsAgainst += ag;
		this.numPlayedThisSeason += 1;
	}
	public void updateTeamDataAsAway(int hg, int ag) {
		if (hg > ag) {
			this.numLoses += 1;
		} else if (hg == ag) {
			this.numDraws += 1;
		} else {
			this.numWins += 1;
		}
		this.numGoalsFor += ag;
		this.numGoalsAgainst += hg;
		this.numPlayedThisSeason += 1;
	}
	public double getTotalScores() {
		return totalScores;
	}

	public String getName() {
		return name;
	}

	public int getMostlikePoints() {
		return mostlikePoints;
	}

	public int getNumWins() {
		return numWins;
	}

	public int getNumDraws() {
		return numDraws;
	}

	public int getNumLoses() {
		return numLoses;
	}

	public int getNumGoalsFor() {
		return numGoalsFor;
	}

	public int getNumGoalsAgainst() {
		return numGoalsAgainst;
	}
	
}
