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
        System.out.println(teamToOpponents);

        return table.getTeams().stream().map( team -> {
            List<String> upcomingFixtures = teamToOpponents.get(team.getName());
            LeaguePositionStats leaguePositionStats = new LeaguePositionStats(team.getName(), team.getPosition());
            List<TeamStatus> catchableTeams = findCatchableTeams(table, team.getPosition(), team.getPoints() + maxPoints);
            if (catchableTeams.size() == 0) {
                leaguePositionStats.setHighestPossible(team.getPosition());
            }
            List<TeamStatus> teamsCatchingUp = findTeamsWhichCouldCatchUp(table, team.getPosition(), team.getPoints() - maxPoints);
            if (teamsCatchingUp.size() == 0) {
                leaguePositionStats.setLowestPossible(team.getPosition());
            } else {
                List<TeamStatus> teamsPlayingEachOther = findTeamsPlayingEachOther(teamsCatchingUp, teamToOpponents);
                int teamsWhichCannotCatchMainTeam = teamsWhichCannotCatchMainTeam(team, teamsPlayingEachOther, teamToOpponents, matchesLookAhead);
                int teamsWithinPointsAndGoalDifference = calcTeamsWithinPointsAndGoalDiff(teamsCatchingUp, maxPoints, team).size();

                if (teamsWhichCannotCatchMainTeam != 0) {
                    leaguePositionStats.setLowestPossible(team.getPosition() + teamsCatchingUp.size() - teamsWhichCannotCatchMainTeam);
                    leaguePositionStats.setLowestImpossible(team.getPosition() + teamsCatchingUp.size());
                    if (teamsWithinPointsAndGoalDifference > teamsCatchingUp.size() - teamsWhichCannotCatchMainTeam) {
                        teamsWithinPointsAndGoalDifference = teamsCatchingUp.size() - teamsWhichCannotCatchMainTeam;
                    }
                } else {
                    leaguePositionStats.setLowestPossible(team.getPosition() + teamsCatchingUp.size());
                }

                leaguePositionStats.setLowestWithoutLargeSwing(team.getPosition() + teamsWithinPointsAndGoalDifference);
            }
            return leaguePositionStats;
        }).collect(Collectors.toList());
    }

    private static List<TeamStatus> findTeamsPlayingEachOther(List<TeamStatus> teamsCatchingUp, Map<String, ArrayList<String>> teamToOpponents) {
        List<String> teamNames = teamsCatchingUp.stream().map(TeamStatus::getName).collect(Collectors.toList());
        return teamsCatchingUp.stream().filter(teamStatus -> teamToOpponents.get(teamStatus.getName()).stream().anyMatch(teamName -> teamNames.contains(teamName))).collect(Collectors.toList());
    }

    private static int teamsWhichCannotCatchMainTeam(TeamStatus mainTeam, List<TeamStatus> teamsPlayingEachOther, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        int targetPoints = mainTeam.getPoints();
        if (matchesLookAhead == 1 && teamsPlayingEachOther.size() == 2 && teamsPlayingEachOther.stream().allMatch(team -> team.getPoints() + 1 < targetPoints)) {
            return teamsPlayingEachOther.size() - 1;
        }
        return 0;
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

    private static List<TeamStatus> calcTeamsWithinPointsAndGoalDiff(List<TeamStatus> teamsCatchingUp, Integer maxPoints, TeamStatus team) {
        return teamsCatchingUp.stream()
                .filter(catchingTeam ->
                        catchingTeam.getPoints() + maxPoints > team.getPoints()
                                || catchingTeam.getGoalDifference() + 3 > team.getGoalDifference()
                                || (catchingTeam.getGoalDifference() + 3 == team.getGoalDifference() && catchingTeam.getGoalsFor() >= team.getGoalsFor())
                )
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
