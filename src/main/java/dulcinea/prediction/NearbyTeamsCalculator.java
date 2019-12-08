package dulcinea.prediction;

import dulcinea.match.TeamStatus;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NearbyTeamsCalculator {

    private boolean debug = true;

    public static int teamsWhichAreNotCatchablebyMainTeam(TeamStatus mainTeam, List<TeamStatus> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        int targetPoints = mainTeam.getPoints() + 3 * matchesLookAhead;
        List<Team> teams = calcTeams(targetPoints, teamsWithinRange, teamToOpponents, matchesLookAhead);

        // todo extract
        teams.forEach(team -> team.getOpponents().stream().filter(Objects::isNull).forEach( opponent -> {
            aBeatB(opponent, team);
        }));

        int previousGamesRemaining = 0;
        // todo while
        for (int i=0; i<120;i++) {
            teams = sortTeamsOpponents(teams);
            int gamesRemaining = teams.stream().map(Team::getGamesToPlay).mapToInt(Integer::intValue).sum();
            if (previousGamesRemaining == gamesRemaining) {
                teams.stream().filter(team -> team.getGamesToPlay() > 0).findFirst().ifPresent( topTeam ->
                    aBeatB(topTeam, topTeam.getOpponents().get(0))
                );
            }
            previousGamesRemaining = gamesRemaining;
            for( Team team : teams) {
                resolveCatchableTeams(team);
            }
        }

        if( teams.stream().anyMatch(team -> team.getGamesToPlay() > 0)) {
            throw new RuntimeException("Not fully resolved");
        }

        return (int)teams.stream().filter(team -> team.getPointsOffEqual() < 0).count();
    }

    public static int teamsWhichCannotCatchMainTeam(TeamStatus mainTeam, List<TeamStatus> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        int targetPoints = mainTeam.getPoints();
        List<Team> teams = calcTeams(targetPoints, teamsWithinRange, teamToOpponents, matchesLookAhead);

        // todo extract
        teams.forEach(team -> team.getOpponents().stream().filter(Objects::isNull).forEach( opponent -> {
            aBeatB(team, opponent);
        }));

        int previousGamesRemaining = 0;
        // todo while
        for (int i = 0; i < 120; i++) {
            teams = sortTeamsOpponents(teams);
            int gamesRemaining = teams.stream().map(Team::getGamesToPlay).mapToInt(Integer::intValue).sum();
            if (previousGamesRemaining == gamesRemaining) {
                teams.stream().filter(team -> team.getGamesToPlay() > 0).findFirst().ifPresent( topTeam ->
                    aBeatB(topTeam, sortTeamsByPoints(topTeam.getOpponents()).get(0))
                );
            }
            previousGamesRemaining = gamesRemaining;

            for(Team team : teams) {
                resolveCatchingTeams(team);
            }
        }

        if( teams.stream().anyMatch(team -> team.getGamesToPlay() > 0)) {
            throw new RuntimeException("Not fully resolved");
        }
        return (int)teams.stream().filter(team -> team.getPointsOffEqual() > 0).count();
    }

    private static void resolveCatchableTeams(Team team) {
        if ((team.getGamesToPlay() * 3) <= team.getPointsOffEqual() || team.getPointsOffEqual() < 0) {
            for (Team opponent : team.getOpponents()) {
                aBeatB(team, opponent);
            }
        }
        if (team.getGamesToPlay() == 1) {
            Team opponent = team.getOpponents().get(0);
            if (team.getPointsOffEqual() == 0) {
                aBeatB(opponent, team);
            } else if (team.getPointsOffEqual() == 1 || team.getPointsOffEqual() == 2) {
                aDrewWithB(team, opponent);
            } else if (team.getPointsOffEqual() < 0 || team.getPointsOffEqual() > 2) {
                aBeatB(team, opponent);
            }
        }
    }

    private static void resolveCatchingTeams(Team team) {
        if (team.getPointsOffEqual() <= 0 || team.getGamesToPlay() * 3 < team.getPointsOffEqual()) {
            for (Team opponent : team.getOpponents()) {
                aBeatB(opponent, team);
            }
        }
        if (team.getGamesToPlay() > 0) {
            Team opponent = team.getOpponents().get(0);
            if (team.getPointsOffEqual() == 3 || team.getPointsOffEqual() == 2) {
                aBeatB(team, opponent);
            } else if (team.getPointsOffEqual() == 1) {
                aDrewWithB(team, opponent);
            } else if (team.getGamesToPlay() == 1 && (team.getPointsOffEqual() > 3 || team.getPointsOffEqual() < 1)) {
                aBeatB(opponent, team);
            }
        }
    }

    private static List<Team> calcTeams(int targetPoints, List<TeamStatus> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        Map<String, Team> nameToTeam = teamsWithinRange.stream().map(teamStatus ->
            new Team(teamStatus.getName(), targetPoints - teamStatus.getPoints())
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

    private static List<Team> sortTeamsOpponents(List<Team> teams) {
        // this is an estimate
        int pointRequired = teams.stream().map(Team::getPointsOffEqual).filter(x -> x > 0).reduce(0, (a, b) -> a + b);
        int pointsRemaining = teams.stream().map(Team::getGamesToPlay).reduce(0, (a, b) -> a + b) / 2 * 3;
        teams.forEach(team -> {
            List<Team> opponents = sortTeamsByPoints(team.getOpponents());
            if (pointRequired > pointsRemaining) {
                Collections.reverse(opponents);
            }
            team.setOpponents(opponents);
        });

        teams = sortTeamsByPoints(teams);
        if (pointRequired > pointsRemaining) {
            Collections.reverse(teams);
        }
        return teams;
    }

    private static List<Team> sortTeamsByPoints(List<Team> teams) {
        teams.sort((o1, o2) -> {
            int team1Points = o1 != null ? o1.getPointsOffEqual() : 1000;
            int team2Points = o2 != null ? o2.getPointsOffEqual() : 1000;
            return team2Points - team1Points;
        });
        return teams;
    }

    private static void aBeatB(Team teamA, Team teamB) {
        if (teamA != null) {
            teamA.reducePointsOffEqual(3);
            teamA.removeOpponent(teamB);
        }
        if (teamB != null) {
            teamB.removeOpponent(teamA);
        }
    }

    private static void aDrewWithB(Team teamA, Team teamB) {
        if (teamA != null) {
            teamA.reducePointsOffEqual(1);
            teamA.removeOpponent(teamB);
        }
        if (teamB != null) {
            teamB.reducePointsOffEqual(1);
            teamB.removeOpponent(teamA);
        }
    }
}
