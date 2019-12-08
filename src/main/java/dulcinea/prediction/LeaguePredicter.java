package dulcinea.prediction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dulcinea.match.Match;
import dulcinea.match.MatchFilterer;
import dulcinea.match.Table;
import dulcinea.match.LeaguePostion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaguePredicter {
    private static final Integer pointsForAWin = 3;

    public static List<LeaguePositionStats> findPossibleLeaguePositions(Table table, List<Match> matches, Integer matchesPlayed, Integer matchesLookAhead) {
        Map<String, ArrayList<String>> teamToOpponents = calculateTeamToOpponents(matches, matchesPlayed, matchesLookAhead);

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
        if (teamsCatchingUp.size() != 0) {
            lowestPossible += teamsCatchingUp.size() - NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(team, teamsCatchingUp, teamToOpponents, matchesLookAhead);
        }
        return leaguePositionStats.withLowestPossible(lowestPossible);
    }

    private static LeaguePositionStats updateLpsWithCatchableTeams(Table table, Integer matchesLookAhead, Integer maxPoints, Map<String, ArrayList<String>> teamToOpponents, LeaguePostion team, LeaguePositionStats leaguePositionStats) {
        List<LeaguePostion> catchableTeams = findCatchableTeams(table, team.getIndex(), team.getPoints() + maxPoints);
        int highestPossible = team.getIndex();
        if (catchableTeams.size() != 0) {
            highestPossible -= catchableTeams.size() - NearbyTeamsCalculator.teamsWhichAreNotCatchablebyMainTeam(team, catchableTeams, teamToOpponents, matchesLookAhead);
        }
        return leaguePositionStats.withHighestPossible(highestPossible);
    }

    private static List<LeaguePostion> findCatchableTeams(Table table, int currentPosition, int maxPossiblePoints) {
        return table.getTeams().stream()
            .filter(team -> team.getIndex() < currentPosition && team.getPoints() <= maxPossiblePoints)
            .collect(Collectors.toList());
    }

    private static List<LeaguePostion> findTeamsWhichCouldCatchUp(Table table, int currentPosition, int minPointsOfCatchingTeam) {
        return table.getTeams().stream()
            .filter(team -> team.getIndex() > currentPosition && team.getPoints() >= minPointsOfCatchingTeam)
            .collect(Collectors.toList());
    }

    //TODO: opponent finder class
    private static Map<String, ArrayList<String>> calculateTeamToOpponents(List<Match> matches, Integer matchesPlayed, Integer matchesLookAhead) {
        List<Match> futureMatches = MatchFilterer.findMatchesForWeekXToWeekY(matches, matchesPlayed, matchesLookAhead + matchesPlayed);
        return futureMatches.stream()
            .map(match -> Maps.newHashMap(ImmutableMap.of(
                match.getHomeTeam(), Lists.newArrayList(match.getAwayTeam()),
                match.getAwayTeam(), Lists.newArrayList(match.getHomeTeam()))))
            .reduce((map1, map2) -> {
                map2.forEach((key, subset) -> {
                    ArrayList<String> x = map1.get(key) != null ? map1.get(key) : new ArrayList<>();
                    x.addAll(subset);
                    map1.put(key, x);
                });
                return map1;
            }).get();
    }


}
