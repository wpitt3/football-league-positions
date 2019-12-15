package dulcinea.prediction;

import dulcinea.match.Match;
import dulcinea.match.MatchFilterer;
import dulcinea.match.Table;
import dulcinea.match.LeaguePostion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LeaguePredicter {
    private static final Integer pointsForAWin = 3;

    public static List<LeaguePositionStats> findPossibleLeaguePositions(Table table, List<Match> matches, Integer matchesPlayed, Integer matchesLookAhead) {
        List<Match> futureMatches = MatchFilterer.findMatchesForWeekXToWeekY(matches, matchesPlayed, matchesLookAhead + matchesPlayed);
        Map<String, ArrayList<String>> teamToOpponents = OpponentCalculator.calculateMatchesOpponents(futureMatches);
        System.out.println(teamToOpponents.keySet().stream()
                .map(team -> "\""+team+ "\":[\""+teamToOpponents.get(team).stream().collect(Collectors.joining("\",\""))+"\"]")
                .collect(Collectors.joining(",")));
        return table.getTeams().stream().map( team -> {
            LeaguePositionStats leaguePositionStats = new LeaguePositionStats(team.getName(), team.getIndex());
            for (int i = 1; i<=matchesLookAhead; i++) {
                Integer maxPoints = pointsForAWin * i;
                leaguePositionStats = updateLpsWithCatchableTeams(table, i, maxPoints, teamToOpponents, team, leaguePositionStats);
                leaguePositionStats = updateLpsWithCatchingTeams(table, i, maxPoints, teamToOpponents, team, leaguePositionStats);
            }
            return leaguePositionStats;
        }).collect(Collectors.toList());
    }

    private static LeaguePositionStats updateLpsWithCatchingTeams(Table table, Integer matchesLookAhead, Integer maxPoints, Map<String, ArrayList<String>> teamToOpponents, LeaguePostion team, LeaguePositionStats leaguePositionStats) {
        List<LeaguePostion> teamsCatchingUp = findTeamsWhichCouldCatchUp(table, team.getIndex(), team.getPoints() - maxPoints);
        int lowestPossible = team.getIndex();
        System.out.println(team.getName() + " "+ team.getPoints());
        System.out.println(teamsCatchingUp.stream().map(opponent -> "\"" + opponent.getName() + "\":"+ opponent.getPoints()).collect(Collectors.joining(",")));
        lowestPossible += NearbyTeamsCalculator.calcCatchingTeams(team, teamsCatchingUp, teamToOpponents, matchesLookAhead).size();
        return leaguePositionStats.withLowestPossible(lowestPossible);
    }

    private static LeaguePositionStats updateLpsWithCatchableTeams(Table table, Integer matchesLookAhead, Integer maxPoints, Map<String, ArrayList<String>> teamToOpponents, LeaguePostion team, LeaguePositionStats leaguePositionStats) {
        List<LeaguePostion> catchableTeams = findCatchableTeams(table, team.getIndex(), team.getPoints() + maxPoints);
        int highestPossible = team.getIndex();
        highestPossible -= NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(team, catchableTeams, teamToOpponents, matchesLookAhead).size();
        return leaguePositionStats.withHighestPossible(highestPossible);
    }

    private static List<LeaguePostion> findCatchableTeams(Table table, int currentPosition, int maxPossiblePoints) {
        return filterTeams(table, team -> team.getIndex() < currentPosition && team.getPoints() <= maxPossiblePoints);
    }

    private static List<LeaguePostion> findTeamsWhichCouldCatchUp(Table table, int currentPosition, int minPointsOfCatchingTeam) {
        return filterTeams(table, team -> team.getIndex() > currentPosition && team.getPoints() >= minPointsOfCatchingTeam);
    }

    private static List<LeaguePostion> filterTeams(Table table, Predicate<LeaguePostion> filter) {
        return table.getTeams().stream().filter(filter).collect(Collectors.toList());
    }
}
