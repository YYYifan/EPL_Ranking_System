package LoadData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Model.*;


public class LoadData {
	
	private Map<String, Map<String, MatchData>> thisSeasonMatchData;
	private Set<MatchData> pastMatchData;
	private Map<String, TeamData> teamMap;
	private AllMatch allMatchInfo;
	
//	public LoadData() {
//		loadPastMatchData();
//		loadThisSeasonMatchData();
//		buildTeamData();
//	}
//	
	public static Set<MatchData> loadPastMatchData() {
		Set<MatchData> matches = new HashSet<>();
		String filePath = "src/datasets/";
		String[] fileNames = new String[] {"2000-2001.csv", "2001-2002.csv", "2002-2003.csv", 
										   "2003-2004.csv", "2004-2005.csv", "2005-2006.csv",
										   "2006-2007.csv", "2007-2008.csv", "2008-2009.csv",
										   "2009-2010.csv", "2010-2011.csv", "2011-2012.csv",
										   "2012-2013.csv", "2013-2014.csv", "2014-2015.csv",
										   "2015-2016.csv", "2016-2017.csv", "2017-2018.csv",
										   "2018-2019.csv", "2019-2020.csv"};
		for (String fileName : fileNames) {
			matches.addAll(getDataFromCsv(Paths.get(filePath, fileName).toString()));
		}
		return matches;
	}
	
	public static Map<String, Map<String, MatchData>> loadThisSeasonMatchData() {
		String filePath = "src/datasets/2019-2020.csv";
		Set<MatchData> matches = new HashSet<>();
		matches = getDataFromCsv(filePath);
		Map<String, Map<String, MatchData>> thisSeasonMatchMap = new HashMap<>();
		for (MatchData match : matches) {
			String hn = match.getHomeTeam();
			String an = match.getAwayTeam();
			if (thisSeasonMatchMap.containsKey(hn)) {
				thisSeasonMatchMap.get(hn).put(an, match);
			} else {
				thisSeasonMatchMap.put(hn, new HashMap<String, MatchData>());
				thisSeasonMatchMap.get(hn).put(an, match);
			} 
		}
		return thisSeasonMatchMap;
	}
	
	public Map<String, TeamData> buildTeamData() {
		this.allMatchInfo = new AllMatch();
		this.teamMap = new HashMap<String, TeamData>();
		this.pastMatchData = loadPastMatchData();
		this.thisSeasonMatchData = loadThisSeasonMatchData();
		
		for (MatchData match : pastMatchData) {
			allMatchInfo.loadMatch(match);
			String hn = match.getHomeTeam();
			String an = match.getAwayTeam();
			//We only need to build those teams exist in this season for predictions
			if (this.thisSeasonMatchData == null || this.thisSeasonMatchData.containsKey(hn)) {
				if (teamMap.containsKey(hn)) {
					teamMap.get(hn).AddMatchAsHome(match);
				} else {
					teamMap.put(hn, new TeamData(hn));
					teamMap.get(hn).AddMatchAsHome(match);
				}
			}
			if (this.thisSeasonMatchData == null || this.thisSeasonMatchData.containsKey(an)) {
				if (teamMap.containsKey(an)) {
					teamMap.get(an).AddMatchAsAway(match);
				} else {
					teamMap.put(an, new TeamData(an));
					teamMap.get(an).AddMatchAsAway(match);
				}
			}
		}
		
		//compute the team strength using allMatchInfo data
		for (Map.Entry<String, TeamData> team : teamMap.entrySet()) {
			team.getValue().computeStrength(allMatchInfo);
		}
		return teamMap;
	}
	
	public static Set<MatchData> getDataFromCsv(String fileName) {
		File csvFile = new File(fileName);
		Set<MatchData> temp = new HashSet<>();
		
		String line = "";
		String split = ",";
		int homeCol = 0;
		int awayCol = 0;
		int FTHG = 0;
		int FTAG = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			int row = 0;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(split);
				if (row > 0) {
					if (data != null && data.length != 0) {
						temp.add(new MatchData(data[homeCol], data[awayCol], Integer.parseInt(data[FTHG]), Integer.parseInt(data[FTAG])));
					}
//					String hn = data[homeCol];
//					String an = data[awayCol];
//					//int h = Integer.parseInt(data[FTHG]);
//					Integer.parseInt("37575");
//					int a = Integer.parseInt(data[FTAG]);
//					MatchData match = new MatchData(hn, an, 0, a);
//					temp.add(match);
					//temp.add(new MatchData(data[homeCol], data[awayCol], Integer.parseInt(data[FTHG]), Integer.parseInt(data[FTAG])));
				} else {
					homeCol = getColNum(data, "HomeTeam");
					awayCol = getColNum(data, "AwayTeam");
					FTHG = getColNum(data, "FTHG");
					FTAG = getColNum(data, "FTAG");
				}
				row++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
	public static int getColNum(String[] data, String s) {
		for (int i = 0; i < data.length; i++) {
			if (data[i].equals(s))
				return i;
		}
		return -1;
	}

	public Map<String, Map<String, MatchData>> getThisSeasonMatchData() {
		return thisSeasonMatchData;
	}

	public Set<MatchData> getPastMatchData() {
		return pastMatchData;
	}

	public Map<String, TeamData> getTeamMap() {
		return teamMap;
	}

	public AllMatch getAllMatchInfo() {
		return allMatchInfo;
	}
	
}
