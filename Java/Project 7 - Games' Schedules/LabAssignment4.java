import java.lang.*;
public class LabAssignment4
{
	public static void main(String[] args) 
	{
		
		int topScore = 0;
		Schedule seasonSchedule = new Schedule();	// a new schedule object that store results for GAMES games
		int numOfGamesInSchedule = seasonSchedule.GAMES; // number of games in the schedule

		String [] teamsInSeason = seasonSchedule.getTeams(); // Names of teams in schedule
		teamData[] teamRecord = new teamData [teamsInSeason.length]; //object to store "database for each team
		for (int i=0;i<teamRecord.length && i<teamsInSeason.length ;i++ ) //Process to store data in teamRecord
		{
			teamRecord[i] = new teamData();
			teamRecord[i].teamName = teamsInSeason[i];
			teamRecord[i].numOfWins = 0;
			teamRecord[i].numOfLosses = 0;
			teamRecord[i].numOfTies = 0;
			teamRecord[i].totalPoints = 0;
		}
		
		Game [] gameResults = new Game [seasonSchedule.GAMES]; // print outs all games results
		for (int i =0; i < seasonSchedule.GAMES; i++)
		{
			gameResults[i] = seasonSchedule.getGame(i);
			String homeTeam = gameResults[i].getHomeTeam();
			int homeScore = gameResults[i].getHomeScore();
			String awayTeam = gameResults[i].getAwayTeam();
			int awayScore = gameResults[i].getAwayScore();

			if (homeScore > awayScore)
			{
				{
					int k = 0;
					while (homeTeam != teamRecord[k].teamName)
					k++;
					teamRecord[k].numOfWins += 1;
				}
				{
					int k = 0;
					while (awayTeam != teamRecord[k].teamName)
					k++;
					teamRecord[k].numOfLosses += 1;
				}

			}
			else if (homeScore == awayScore)
			{
				{
					int k = 0;
					while (homeTeam != teamRecord[k].teamName)
					k++;
					teamRecord[k].numOfTies += 1;
				}
				{
					int k = 0;
					while (awayTeam != teamRecord[k].teamName)
					k++;
					teamRecord[k].numOfTies += 1;
				}
			}
			else
			{
				{
					int k = 0;
					while (awayTeam != teamRecord[k].teamName)
					k++;
					teamRecord[k].numOfWins += 1;
				}
				{
					int k = 0;
					while (homeTeam != teamRecord[k].teamName)
					k++;
					teamRecord[k].numOfLosses += 1;
				}
			}
		}
		for (int i=0;i<teamRecord.length ;i++ )// calculating total points
		{
			teamRecord[i].totalPoints = (2*teamRecord[i].getNumOfWins()) + (teamRecord[i].getNumOfTies());
		}
		for (int i=0;i<teamRecord.length ;i++ )
		{
			if (teamRecord[i].getTotalPoints() > topScore)
			{
				topScore = teamRecord[i].getTotalPoints();
			}
		}
		for (int i=0;i<teamRecord.length ;i++ )
		{
			System.out.println(teamRecord[i].getTeamName() + " - " + teamRecord[i].getNumOfWins() + " wins, " 
				+ teamRecord[i].getNumOfLosses() + " losses, " + teamRecord[i].getNumOfTies() + " ties = "
				+ teamRecord[i].getTotalPoints() + " total points");
		}
				
		System.out.println("The season winner(s) with " + topScore+ " points");
		for (int i=0; i<teamRecord.length;i++ )
		{
			teamRecord[i].getTotalPoints();
			if (teamRecord[i].getTotalPoints() == topScore)
			{
				System.out.println(teamRecord[i].getTeamName());
			}
		}
	}
}