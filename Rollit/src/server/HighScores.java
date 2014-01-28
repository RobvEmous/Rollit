package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import parts.Score;
import parts.Scores;

public class HighScores {

private HashMap<Score, Integer> scores = new HashMap<Score, Integer>();
	
	private void Scores() {
		
	}
	
	public void addScore(Score score) {
		scores.put(score,score.getPoints());
	}
	
	public Score getScore(Date date) {
		for (Score s: scores.keySet()) {
			 if (s.getDate().equals(date)) {
				 return s;
			 }
		}
		return null;
	}
	
	public ArrayList<Score> getScores(Player player) {
		ArrayList<Score> subScores = new ArrayList<Score>();
		for (Score s: scores.keySet()) {
			 if (s.getPlayer().equals(player)) {
				 subScores.add(s);
			 }
		}
		return subScores;
	}
	
	public double getAverageScore() {
		double average = 0D;
		for (int point : scores.values()) {
			average += point;
		}
		return average / scores.size();
	}
	
	public ArrayList<Score> getScoresAbove(int value) {	
		ArrayList<Score> subScores = new ArrayList<Score>();
		for (Score s : scores.keySet()) {
			if (s.getPoints() > value) {
				subScores.add(s);
			}
		}
		return subScores;
	}
	
	public ArrayList<Score> getScoresBelow(int value) {	
		ArrayList<Score> subScores = new ArrayList<Score>();
		for (Score s : scores.keySet()) {
			if (s.getPoints() < value) {
				subScores.add(s);
			}
		}
		return subScores;
	}
	
	public ArrayList<Score> getHighestScores(int nrOfScores) {
		if (nrOfScores <= scores.size()) {
			ArrayList<Score> subScoresTemp = new ArrayList<Score>(sortToValueHighLow(scores));
			ArrayList<Score> subScores = new ArrayList<Score>(nrOfScores);
			for (int i = 0; i < nrOfScores ; i++) {
				subScores.add(subScoresTemp.get(i));
			}
			return subScores;
		}
		return null;
	}

	private ArrayList<Score> sortToValueHighLow(HashMap<Score,Integer> theMap) {
		HashMap<Score, Integer> map = new HashMap<Score, Integer>(theMap);
		ArrayList<Score> sortedList = new ArrayList<Score>(map.size());
		int currValue = getHighestValue(map);
		int lowestValue = getLowestValue(map);
		while (currValue >= lowestValue) {
			for (Score score : map.keySet()) {
				if (map.get(score) == currValue) {
					sortedList.add(score);
					map.put(score, -1);
				}
			}
			currValue = getHighestValue(map);	
		}
		return sortedList;	
	}
	
	private int getHighestValue(HashMap<Score,Integer> map) {
		int highest = 0;
		for (int v : map.values()) {
			if (v > highest) {
				highest = v;
			}
		}
		return highest;
	}
	
	private int getLowestValue(HashMap<Score,Integer> map) {
		int lowest = Integer.MAX_VALUE;
		for (int v : map.values()) {
			if (v < lowest) {
				lowest = v;
			}
		}
		return lowest;
	}
	
	@Override
	public String toString() {
		String description = "";
		for (Score s : scores.keySet()) {
			description += s.toString() + ". ";
		}
		return description;
	}
	
	private void printMap(HashMap<Score, Integer> map) {
		for (Score s : map.keySet()) {
			System.out.println(s.toString() + " -- " + map.get(s).toString());
		}
	}
	
	private void printList(List<Score> list) {
		if (list != null) {
			for (Score s : list) {
				System.out.println(s.toString());
			}
		}
	}
	
	private void printList2(List<Integer> list) {
		for (int s : list) {
			System.out.println(s);
		}
	}
	
	public static void main(String[] args) {
		Scores scores = new Scores();

	}
}
