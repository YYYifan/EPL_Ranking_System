package Model;

public class PredictMatch {
	private double homeWinsProbalility;
	private double awayWinsProbalility;
	private double drawsProbalility;
	
	private double maxProbability;
	private int mostlikeHomeGoals;
	private int mostlikeAwayGoals;
	private double[][] combineProbability = new double[11][11];
	
	public PredictMatch() {
		
	}
	public PredictMatch(MatchData match) {
		this.homeWinsProbalility = match.getHomeGoals() > match.getAwayGoals() ? 1.0 : 0.0;
		this.awayWinsProbalility = match.getHomeGoals() < match.getAwayGoals() ? 1.0 : 0.0;
		this.drawsProbalility = match.getHomeGoals() == match.getAwayGoals() ? 1.0 : 0.0;
		this.maxProbability = 1.0;
		this.mostlikeHomeGoals = match.getHomeGoals();
		this.mostlikeAwayGoals = match.getAwayGoals();
	}
	
	public void loadScore(int homeGoals, int awayGoals, double p) {
		this.combineProbability[homeGoals][awayGoals] = p;
		if (p > maxProbability) {
			this.mostlikeHomeGoals = homeGoals;
			this.mostlikeAwayGoals = awayGoals;
			this.maxProbability = p;
		}
		if (homeGoals > awayGoals)
			homeWinsProbalility += p;
		else if (homeGoals == awayGoals)
			drawsProbalility += p;
		else
			awayWinsProbalility += p;
	}
	
	public double getPredictHomeScores() {
		return 3 * homeWinsProbalility + drawsProbalility;
	}
	public double getPredictAwaySocres() {
		return 3 * awayWinsProbalility + drawsProbalility;
	}
	public int getMostlikelyHomePoints() {
		int hG = this.getMostlikeHomeGoals();
		int aG = this.getMostlikeAwayGoals();
		int point = hG > aG ? 3 : (hG < aG ? 0 : 1);
		return point;
	}
	public int getMostlikelyAwayPoints() {
		int hG = this.getMostlikeHomeGoals();
		int aG = this.getMostlikeAwayGoals();
		int point = hG > aG ? 0 : (hG < aG ? 3 : 1);
		return point;
	}
	public double getCertainProbalility(int homeGoals, int awayGoals) {
		return combineProbability[homeGoals][awayGoals];
	}
	public double getMaxProbability() {
		return maxProbability;
	}
	public int getMostlikeHomeGoals() {
		return mostlikeHomeGoals;
	}
	public int getMostlikeAwayGoals() {
		return mostlikeAwayGoals;
	}
	public double getHomeWinsProbalility() {
		return homeWinsProbalility;
	}
	public double getAwayWinsProbalility() {
		return awayWinsProbalility;
	}
	public double getDrawsProbalility() {
		return drawsProbalility;
	}
	
}
