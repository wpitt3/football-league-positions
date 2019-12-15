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
    static List<Opponent> generateTeams(int targetPoints, List<LeaguePostion> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        Map<String, Opponent> nameToTeam = teamsWithinRange.stream().map(leaguePostion ->
                new Opponent(leaguePostion.getName(), targetPoints - leaguePostion.getPoints())
        ).collect(Collectors.toMap(Opponent::getName, Function.identity()));

        return nameToTeam.values().stream().map(team -> {
            List<Opponent> opponents = teamToOpponents.get(team.getName())
                    .stream()
                    .limit(matchesLookAhead)
                    .map(nameToTeam::get)
                    .collect(Collectors.toList());
            team.setOpponents(opponents);
            return team;
        }).collect(Collectors.toList());
    }

    static List<Opponent> resolveTeamsWhichAreAheadOfMainTeam(List<Opponent> teams) {
        return resolveTeams(teams, team -> teamWon(team));
    }

    static List<Opponent> resolveTeamsWhichAreBehindMainTeam(List<Opponent> teams) {
        return resolveTeams(teams, team -> teamLost(team));
    }

    private static List<Opponent> resolveTeams(List<Opponent> teams, Consumer<Opponent> playMatch) {
        teams.forEach(team -> team.getOpponents().stream().filter(Objects::isNull).forEach(x -> playMatch.accept(team)));
        return teams;
    }

    private static void teamWon(Opponent team) {
        team.reducePointsOffEqual(3);
        team.removeOpponent(null);
    }
    private static void teamLost(Opponent team) {
        team.removeOpponent(null);
    }

}
