package dulcinea;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaguePredicter {
    private static final Integer pointsForAWin = 3;
    private static final Integer goalDifferenceForBigSwing = 4;


    public static List<LeaguePositionStats> findPossibleLeaguePositions(Table table, List<Match> matches, Integer matchesPlayed, Integer matchesLookAhead) {
        Integer maxPoints = pointsForAWin * matchesLookAhead;
        Map<String, ArrayList<String>> teamToOpponents = calculateTeamToOpponents(matches, matchesPlayed, matchesLookAhead);

        return table.getTeams().stream().map( team -> {
            LeaguePositionStats leaguePositionStats = new LeaguePositionStats(team.getName(), team.getPosition());

            List<TeamStatus> catchableTeams = findCatchableTeams(table, team.getPosition(), team.getPoints() + maxPoints);
            if (catchableTeams.size() == 0) {
                leaguePositionStats.setHighestPossible(team.getPosition());
            } else {
                int teamsWhichCanCatchMainTeam = catchableTeams.size() - NearbyTeamsCalculator.teamsWhichAreNotCatchablebyMainTeam(team, catchableTeams, teamToOpponents, matchesLookAhead);
                int teamsWithinPointsAndGoalDifference = calcTeamsAheadWithinPointsAndGoalDiff(catchableTeams, maxPoints, team).size();

                if (teamsWhichCanCatchMainTeam != catchableTeams.size()) {
                    leaguePositionStats.setHighestPossible(team.getPosition() -  teamsWhichCanCatchMainTeam);
                    leaguePositionStats.setHighestImpossible(team.getPosition() - catchableTeams.size());
                    if (teamsWithinPointsAndGoalDifference < teamsWhichCanCatchMainTeam) {
                        teamsWithinPointsAndGoalDifference = teamsWhichCanCatchMainTeam;
                    }
                } else {
                    leaguePositionStats.setHighestPossible(team.getPosition() - catchableTeams.size());
                }
                if (team.getPosition() - teamsWithinPointsAndGoalDifference != leaguePositionStats.getHighestPossible()) {
                    leaguePositionStats.setHighestWithoutLargeSwing(team.getPosition() - teamsWithinPointsAndGoalDifference);
                }
            }

            List<TeamStatus> teamsCatchingUp = findTeamsWhichCouldCatchUp(table, team.getPosition(), team.getPoints() - maxPoints);
            if (teamsCatchingUp.size() == 0) {
                leaguePositionStats.setLowestPossible(team.getPosition());
            } else {
                int teamsWhichCanCatchMainTeam = teamsCatchingUp.size() - NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(team, teamsCatchingUp, teamToOpponents, matchesLookAhead);
                int teamsWithinPointsAndGoalDifference = calcTeamsBehindWithinPointsAndGoalDiff(teamsCatchingUp, maxPoints, team).size();

                if (teamsWhichCanCatchMainTeam != teamsCatchingUp.size()) {
                    leaguePositionStats.setLowestPossible(team.getPosition() + teamsWhichCanCatchMainTeam);
                    leaguePositionStats.setLowestImpossible(team.getPosition() + teamsCatchingUp.size());
                    if (teamsWithinPointsAndGoalDifference > teamsWhichCanCatchMainTeam) {
                        teamsWithinPointsAndGoalDifference = teamsWhichCanCatchMainTeam;
                    }
                } else {
                    leaguePositionStats.setLowestPossible(team.getPosition() + teamsCatchingUp.size());
                }
                if (team.getPosition() + teamsWithinPointsAndGoalDifference != leaguePositionStats.getLowestPossible()) {
                    leaguePositionStats.setLowestWithoutLargeSwing(team.getPosition() + teamsWithinPointsAndGoalDifference);
                }
            }
            return leaguePositionStats;
        }).collect(Collectors.toList());
    }

    private static List<TeamStatus> findCatchableTeams(Table table, int currentPosition, int maxPossiblePoints) {
        return table.getTeams().stream()
            .filter(team -> team.getPosition() < currentPosition && team.getPoints() <= maxPossiblePoints)
            .collect(Collectors.toList());
    }

    private static List<TeamStatus> findTeamsWhichCouldCatchUp(Table table, int currentPosition, int minPointsOfCatchingTeam) {
        return table.getTeams().stream()
            .filter(team -> team.getPosition() > currentPosition && team.getPoints() >= minPointsOfCatchingTeam)
            .collect(Collectors.toList());
    }

    private static List<TeamStatus> calcTeamsAheadWithinPointsAndGoalDiff(List<TeamStatus> teamsAhead, Integer maxPoints, TeamStatus team) {
        return teamsAhead.stream()
            .filter(aheadTeam ->
                aheadTeam.getPoints() < team.getPoints() + maxPoints
                    || aheadTeam.getGoalDifference() < team.getGoalDifference() + 3
                    || (aheadTeam.getGoalDifference() == team.getGoalDifference() + 3 && aheadTeam.getGoalsFor() <= team.getGoalsFor())
            )
            .collect(Collectors.toList());
    }

    private static List<TeamStatus> calcTeamsBehindWithinPointsAndGoalDiff(List<TeamStatus> teamsCatchingUp, Integer maxPoints, TeamStatus team) {
        return teamsCatchingUp.stream()
            .filter(catchingTeam ->
                catchingTeam.getPoints() + maxPoints > team.getPoints()
                    || catchingTeam.getGoalDifference() + 3 > team.getGoalDifference()
                    || (catchingTeam.getGoalDifference() + 3 == team.getGoalDifference() && catchingTeam.getGoalsFor() >= team.getGoalsFor()))
            .collect(Collectors.toList());
    }

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
