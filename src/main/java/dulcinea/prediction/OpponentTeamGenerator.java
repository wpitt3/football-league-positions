package dulcinea.prediction;

import dulcinea.match.LeaguePostion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

class OpponentTeamGenerator {
    static List<Team> generateTeams(int targetPoints, List<LeaguePostion> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        Map<String, Team> nameToTeam = teamsWithinRange.stream().map(leaguePostion ->
                new Team(leaguePostion.getName(), targetPoints - leaguePostion.getPoints())
        ).collect(Collectors.toMap(Team::getName, Function.identity()));

        return nameToTeam.values().stream().map(team -> {
            List<Team> opponents = teamToOpponents.get(team.getName())
                    .stream()
                    .limit(matchesLookAhead)
                    .map(nameToTeam::get)
                    .collect(Collectors.toList());
            team.setOpponents(opponents);
            return team;
        }).collect(Collectors.toList());
    }

    static List<Team> resolveTeamsWhichAreAheadOfMainTeam(List<Team> teams) {
        return resolveTeams(teams, team -> teamWon(team));
    }

    static List<Team> resolveTeamsWhichAreBehindMainTeam(List<Team> teams) {
        return resolveTeams(teams, team -> teamLost(team));
    }

    private static List<Team> resolveTeams(List<Team> teams, Consumer<Team> playMatch) {
        teams.forEach(team -> team.getOpponents().stream().filter(Objects::isNull).forEach(x -> playMatch.accept(team)));
        return teams;
    }

    private static void teamWon(Team team) {
        team.reducePointsOffEqual(3);
        team.removeOpponent(null);
    }
    private static void teamLost(Team team) {
        team.removeOpponent(null);
    }

}
