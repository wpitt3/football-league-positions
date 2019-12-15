package dulcinea.prediction;

import dulcinea.match.LeaguePostion;

import java.util.*;
import java.util.stream.Collectors;

public class NearbyTeamsCalculator {

    public static List<Opponent> calcTeamsThatCanBeOvertaken(LeaguePostion mainTeam, List<LeaguePostion> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        int targetPoints = mainTeam.getPoints() + 3 * matchesLookAhead;
        List<Opponent> teams = OpponentTeamGenerator.generateTeams(targetPoints, teamsWithinRange, teamToOpponents, matchesLookAhead);
        teams = OpponentTeamGenerator.resolveTeamsWhichAreBehindMainTeam(teams);

        int previousGamesRemaining = 0;
        // todo while
        for (int i=0; i<120;i++) {
            teams = sortTeamsOpponents(teams);
            int gamesRemaining = teams.stream().map(Opponent::getGamesToPlay).mapToInt(Integer::intValue).sum();
            if (previousGamesRemaining == gamesRemaining) {
                teams.stream().filter(team -> team.getGamesToPlay() > 0).findFirst().ifPresent( topTeam ->
                    aBeatB(topTeam, topTeam.getOpponents().get(0))
                );
            }
            previousGamesRemaining = gamesRemaining;
            for( Opponent team : teams) {
                resolveCatchableTeams(team);
            }
        }

        if( teams.stream().anyMatch(team -> team.getGamesToPlay() > 0)) {
            throw new RuntimeException("Not fully resolved");
        }

        return teams.stream().filter(team -> team.getPointsOffEqual() >= 0).collect(Collectors.toList());
    }

    public static List<Opponent> calcCatchingTeams(LeaguePostion mainTeam, List<LeaguePostion> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        int targetPoints = mainTeam.getPoints();
        List<Opponent> teams = OpponentTeamGenerator.generateTeams(targetPoints, teamsWithinRange, teamToOpponents, matchesLookAhead);
        teams = OpponentTeamGenerator.resolveTeamsWhichAreAheadOfMainTeam(teams);

        int previousGamesRemaining = 0;
        while (teams.stream().anyMatch(team -> team.getGamesToPlay() > 0)) {
            teams = sortTeamsOpponents(teams);
            int gamesRemaining = teams.stream().map(Opponent::getGamesToPlay).mapToInt(Integer::intValue).sum();
            if (previousGamesRemaining == gamesRemaining) {
                teams.stream().filter(team -> team.getGamesToPlay() > 0).findFirst().ifPresent( topTeam ->
                    aBeatB(topTeam, sortTeamsByPoints(topTeam.getOpponents()).get(0))
                );
            }
            previousGamesRemaining = gamesRemaining;

            for(Opponent team : teams) {
                resolveCatchingTeams(team);
            }
        }

        return teams.stream().filter(team -> team.getPointsOffEqual() <= 0).collect(Collectors.toList());
    }

    private static void resolveCatchableTeams(Opponent team) {
        if ((team.getGamesToPlay() * 3) <= team.getPointsOffEqual() || team.getPointsOffEqual() < 0) {
            for (Opponent opponent : team.getOpponents()) {
                aBeatB(team, opponent);
            }
        }
        if (team.getGamesToPlay() == 1) {
            Opponent opponent = team.getOpponents().get(0);
            if (team.getPointsOffEqual() == 0) {
                aBeatB(opponent, team);
            } else if (team.getPointsOffEqual() == 1 || team.getPointsOffEqual() == 2) {
                aDrewWithB(team, opponent);
            } else if (team.getPointsOffEqual() < 0 || team.getPointsOffEqual() > 2) {
                aBeatB(team, opponent);
            }
        }
    }

    private static void resolveCatchingTeams(Opponent team) {
        if (team.getPointsOffEqual() <= 0 || team.getGamesToPlay() * 3 < team.getPointsOffEqual()) {
            for (Opponent opponent : team.getOpponents()) {
                aBeatB(opponent, team);
            }
        }
        if (team.getGamesToPlay() > 0) {
            Opponent opponent = team.getOpponents().get(0);
            if (team.getPointsOffEqual() == 3 || team.getPointsOffEqual() == 2) {
                aBeatB(team, opponent);
            } else if (team.getPointsOffEqual() == 1) {
                aDrewWithB(team, opponent);
            } else if (team.getGamesToPlay() == 1 && (team.getPointsOffEqual() > 3 || team.getPointsOffEqual() < 1)) {
                aBeatB(opponent, team);
            }
        }
    }

    private static List<Opponent> sortTeamsOpponents(List<Opponent> teams) {
        // this is an estimate
        int pointRequired = teams.stream().map(Opponent::getPointsOffEqual).filter(x -> x > 0).reduce(0, (a, b) -> a + b);
        int pointsRemaining = teams.stream().map(Opponent::getGamesToPlay).reduce(0, (a, b) -> a + b) / 2 * 3;
        teams.forEach(team -> {
            List<Opponent> opponents = sortTeamsByPoints(team.getOpponents());
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

    private static List<Opponent> sortTeamsByPoints(List<Opponent> teams) {
        teams.sort((o1, o2) -> {
            int team1Points = o1 != null ? o1.getPointsOffEqual() : 0;
            int team2Points = o2 != null ? o2.getPointsOffEqual() : 0;
            return team2Points - team1Points;
        });
        return teams;
    }

    private static void aBeatB(Opponent teamA, Opponent teamB) {
        if (teamA != null && teamB != null){
            System.out.println(teamA.getName() + " beat " + teamB.getName());
        }
        if (teamA != null) {
            teamA.reducePointsOffEqual(3);
            teamA.removeOpponent(teamB);
        }
        if (teamB != null) {
            teamB.removeOpponent(teamA);
        }
    }

    private static void aDrewWithB(Opponent teamA, Opponent teamB) {
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
