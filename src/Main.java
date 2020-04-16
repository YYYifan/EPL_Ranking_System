import Model.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import LoadData.*;
import SimulateEPL.*;

public class Main {
	private static LoadData loader;
	private static Predictor predictor;
	private static Map<String, Map<String, PredictMatch>> predictMatches;
	private static Map<String, TeamData> finalTeamMap;
	
	public static void main(String[] args) {
		
		predictor = new Predictor();
		loader = new LoadData();
		predictor.setPastMatchData(loader.loadPastMatchData());
		predictor.setThisSeasonMatchData(loader.loadThisSeasonMatchData());
		predictor.setTeamMap(loader.buildTeamData());
		predictor.setAllMatchInfo(loader.getAllMatchInfo());
		
		predictMatches = predictor.predictThisSeason();
		finalTeamMap = predictor.getTeamMap();
		printCompleteTable();
		printPredictSummary();
		
		String hn = "Arsenal";
		String an = "Leicester";
		printCertainMatchPossibilityTable(hn, an);
		
		printRankTable();
	}
	public static void printCompleteTable() {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src//Result/finalTable.csv"), "GBK"));
			bw.write("HomeName" + "," + "AwayHome" + "," + "FTR" + "," + "HomeGoals" + "," + "AwayGoals");
			for (String hn : finalTeamMap.keySet()) {
				for (String an : finalTeamMap.keySet()) {
					if (!hn.equals(an)) {
						bw.newLine();
						PredictMatch match = predictMatches.get(hn).get(an);
						int hG = match.getMostlikeHomeGoals();
						int aG = match.getMostlikeAwayGoals();
						String FTR = hG > aG ? "H" : (hG < aG ? "A" : "D");
						bw.write(hn + "," + an + "," + FTR + "," + hG + "," + aG);
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void printCertainMatchPossibilityTable(String hn, String an) {
		System.out.printf("Example to predict: Arsenal vs Leicester\n");
		System.out.printf("--------------------------------------------------------\n");
		System.out.printf("%-20s", "Home Team Goals");
		System.out.printf("%-20s", "Away Team Goals");
		System.out.printf("%-20s", "Result Possibility");
		System.out.printf("\n");
		
		PredictMatch match = predictMatches.get(hn).get(an);
		//We only consider the goal result which inside 10 based on history data
		for (int hGoals = 0; hGoals < 11; hGoals++) {
			for (int aGoals = 0; aGoals < 11; aGoals++) {
				System.out.printf("%-20s", hGoals);
				System.out.printf("%-20s", aGoals);
				System.out.printf("%-20.2f", match.getCertainProbalility(hGoals, aGoals));
				System.out.printf("\n");
			}
		}
		System.out.printf("\n");
		System.out.printf("\n");
		
	}
	public static void printPredictSummary() {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src//Result/finalPossibilityTable.csv"), "GBK"));
			bw.write("HomeName" + "," + "AwayHome" + "," + "Possibility of Home Win" + "," + "Possibility of Draw" + "," + 
					"Possibility of Away Win" + "," + "MostLikely Result" + "," + "Mostlikely Result Possibility");
			for (String hn : finalTeamMap.keySet()) {
				for (String an : finalTeamMap.keySet()) {
					if (!hn.equals(an)) {
						bw.newLine();
						PredictMatch match = predictMatches.get(hn).get(an);
						int hG = match.getMostlikeHomeGoals();
						int aG = match.getMostlikeAwayGoals();
						String FTR = hG > aG ? "H" : (hG < aG ? "A" : "D");
						
						String sHG = String.valueOf(hG);
						String sAG = String.valueOf(aG);
						String result = sHG + " vs " + sAG;
						bw.write(hn + "," + an + "," + match.getHomeWinsProbalility() + "," + match.getDrawsProbalility() 
								+ "," + match.getAwayWinsProbalility() + "," + result
								+ "," + match.getMaxProbability());
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printRankTable() {
		System.out.printf("2019-2020 English Premier League Table\n");
		System.out.printf("---------------------------------------------------------------------------------\n");
		System.out.printf("%-10s", "Rank");
		System.out.printf("%-20s", "TeamName");
		System.out.printf("%-5s", "GP"); //GamePlayed
		System.out.printf("%-5s", "W");  //Wins
		System.out.printf("%-5s", "D");  //Draws
		System.out.printf("%-5s", "L"); //Losses
		System.out.printf("%-5s", "F");  //Goals For
		System.out.printf("%-5s", "A");	 //Goals Against
		System.out.printf("%-5s", "GD");  //Goals Difference
		System.out.printf("%-10s", "Score");
		System.out.printf("%-10s", "Points");  //Most likely points for each team
		System.out.printf("\n");
		List<TeamData> rankTeam = new ArrayList<>(finalTeamMap.values());
		Comparator<TeamData> ranker = (TeamData a, TeamData b) -> {
			if (a.getTotalScores() > b.getTotalScores())
				return -1;
			else if (a.getTotalScores() == b.getTotalScores())
				return 0;
			else
				return 1;
		};
		Collections.sort(rankTeam, ranker);
		
		//store ranking table into csv file
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src//Result/EPL_RankingTable.csv"), "GBK"));
			bw.write("Rank" + "," + "TeamName" + "," + "GP" + "," + "W" + "," + "D" 
					+ "," + "L" + "," + "F" + "," + "A" + "," + "GD" + "," + "Score" + "," + "Points");
			for (int i = 0; i < rankTeam.size(); i++) {
				System.out.printf("%-10d", i+1);
				System.out.printf("%-20s", rankTeam.get(i).getName());
				System.out.printf("%-5s", rankTeam.get(i).getNumPlayed());
				System.out.printf("%-5s", rankTeam.get(i).getNumWins());
				System.out.printf("%-5s", rankTeam.get(i).getNumDraws());
				System.out.printf("%-5s", rankTeam.get(i).getNumLoses());
				System.out.printf("%-5s", rankTeam.get(i).getNumGoalsFor());
				System.out.printf("%-5s", rankTeam.get(i).getNumGoalsAgainst());
				int gd = rankTeam.get(i).getNumGoalsFor() - rankTeam.get(i).getNumGoalsAgainst();
				String sGD = gd > 0 ? "+"+String.valueOf(gd) : String.valueOf(gd);
				System.out.printf("%-5s", sGD);
				System.out.printf("%-10.2f", rankTeam.get(i).getTotalScores());
				System.out.printf("%-10d", rankTeam.get(i).getMostlikePoints());
				System.out.printf("\n");
				
				bw.newLine();
				bw.write(i+1 + "," + rankTeam.get(i).getName() + "," + rankTeam.get(i).getNumPlayed() + "," + rankTeam.get(i).getNumWins() 
				+ "," + rankTeam.get(i).getNumDraws() + "," + rankTeam.get(i).getNumLoses() + "," + rankTeam.get(i).getNumGoalsFor()
				+ "," + rankTeam.get(i).getNumGoalsAgainst() + "," + sGD + "," + rankTeam.get(i).getTotalScores() + "," + rankTeam.get(i).getMostlikePoints());
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
