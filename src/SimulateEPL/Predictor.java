package SimulateEPL;

import Model.*;
import LoadData.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Predictor {
	
	private Map<String, Map<String, MatchData>> thisSeasonMatchData;
	private Set<MatchData> pastMatchData;
	private Map<String, TeamData> teamMap;
	private AllMatch allMatchInfo;
	
	public Predictor() {}
	
	public void setThisSeasonMatchData(Map<String, Map<String, MatchData>> thisSeasonMatchData) {
		this.thisSeasonMatchData = thisSeasonMatchData;
	}


	public void setPastMatchData(Set<MatchData> pastMatchData) {
		this.pastMatchData = pastMatchData;
	}


	public void setTeamMap(Map<String, TeamData> teamMap) {
		this.teamMap = teamMap;
	}


	public void setAllMatchInfo(AllMatch allMatchInfo) {
		this.allMatchInfo = allMatchInfo;
	}
	
	public Map<String, TeamData> getTeamMap() {
		return teamMap;
	}

	public Map<String, Map<String, PredictMatch>> predictThisSeason() {
		Map<String, Map<String, PredictMatch>> predictMatches = new HashMap<>();
		// each team plays each other team twice, once at home and once away
		for (Map.Entry<String, TeamData> homeTeam : this.teamMap.entrySet()) {
			Map<String, PredictMatch> predictMatchMap = new HashMap<>();
			for (Map.Entry<String, TeamData> awayTeam : this.teamMap.entrySet()) {
				if (!homeTeam.equals(awayTeam)) {
					PredictMatch match = predictMatch(homeTeam.getKey(), awayTeam.getKey());
					//Accumulate scores based on possibility
					homeTeam.getValue().computeScores(match.getPredictHomeScores());
					awayTeam.getValue().computeScores(match.getPredictAwaySocres());
					//Accumulate points based on most likely result
					homeTeam.getValue().computePoints(match.getMostlikelyHomePoints());
					awayTeam.getValue().computePoints(match.getMostlikelyAwayPoints());
					//update statistic data for ranking table
					int hg = match.getMostlikeHomeGoals();
					int ag = match.getMostlikeAwayGoals();
					homeTeam.getValue().updateTeamDataAsHome(hg, ag);
					awayTeam.getValue().updateTeamDataAsAway(hg, ag);
					
					predictMatchMap.put(awayTeam.getKey(), match);
				}
			}
			predictMatches.put(homeTeam.getKey(), predictMatchMap);
		}
		return predictMatches;
	}
	public PredictMatch predictMatch(String hn, String an) {
		if (existMatchInSeason(thisSeasonMatchData, hn, an)) {
			return new PredictMatch(thisSeasonMatchData.get(hn).get(an));
		}
		double preH = this.teamMap.get(hn).getHomeAttStrength() * this.teamMap.get(an).getAwayDefStrength() 
					* this.allMatchInfo.getAvgHomeGoals();
		double preA = this.teamMap.get(an).getAwayAttStrength() * this.teamMap.get(hn).getHomeDefStrength()
					* this.allMatchInfo.getAvgAwayGoals();
		
		PredictMatch match = new PredictMatch();
		//Use Poisson Distribution to predict different score results for this match
		//not above 10 points
		for (int hGoals = 0; hGoals < 11; hGoals++) {
			for (int aGoals = 0; aGoals < 11; aGoals++) {
				double p = PoissonDistribution.pmf(hGoals, preH) * PoissonDistribution.pmf(aGoals, preA);
				match.loadScore(hGoals, aGoals, p);
			}
		}
		return match;
	}
	public boolean existMatchInSeason(Map<String, Map<String, MatchData>> thisSeason, String hn, String an) {
		if (thisSeason != null) {
			if (thisSeason.containsKey(hn) && thisSeason.get(hn).containsKey(an))
				return true;
		}

		return false;
	}
}