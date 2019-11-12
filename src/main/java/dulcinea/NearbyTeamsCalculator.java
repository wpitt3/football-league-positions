package dulcinea;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NearbyTeamsCalculator {

    //this needs a second pass for the goal difference
    public static int teamsWhichAreNotCatchablebyMainTeam(TeamStatus mainTeam, List<TeamStatus> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        int targetPoints = mainTeam.getPoints() + 3 * matchesLookAhead;
        Map<String, TeamToBeat> teamToBeats = calcTeamsToBeat(targetPoints, teamsWithinRange, teamToOpponents, matchesLookAhead);

        for (String teamName : teamToBeats.keySet()) {
            TeamToBeat teamToBeat = teamToBeats.get(teamName);
            if (teamToBeat.gamesToPlay == 1) {
                TeamToBeat opponent = teamToBeats.get(teamToBeat.getOpponents().get(0));
                // -1?
                if (teamToBeat.pointsOffEqual == 0) {
                    aBeatB(opponent, teamToBeat);
                } else if (teamToBeat.pointsOffEqual == 1 || teamToBeat.pointsOffEqual == 2) {
                    aDrewWithB(teamToBeat, opponent);
                } else {
                    aBeatB(teamToBeat, opponent);
                }
            }
        }
        // assign obvious results
        // assign all external games as wins && remove all teams which are over threshold -> repeat until done

        // work out greedy picking, choose the top team until over threshold order opponents by points (-games????)
        return (int)teamToBeats.values().stream().filter(team -> team.pointsOffEqual < 0).count();
    }

    public static int teamsWhichCannotCatchMainTeam(TeamStatus mainTeam, List<TeamStatus> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        int targetPoints = mainTeam.getPoints();

        Map<String, TeamToBeat> teamToBeats = calcTeamsToBeat(targetPoints, teamsWithinRange, teamToOpponents, matchesLookAhead);

        for (String teamName : teamToBeats.keySet()) {
            TeamToBeat teamToBeat = teamToBeats.get(teamName);
            if (teamToBeat.gamesToPlay == 1) {
                TeamToBeat opponent = teamToBeats.get(teamToBeat.getOpponents().get(0));
                if (teamToBeat.pointsOffEqual == 3 || teamToBeat.pointsOffEqual == 2) {
                    aBeatB(teamToBeat, opponent);
                } else if (teamToBeat.pointsOffEqual == 1) {
                    aDrewWithB(teamToBeat, opponent);
                } else {
                    aBeatB(opponent, teamToBeat);
                }
            }
        }


        // assign obvious results
        // assign all external games as wins && remove all teams which are over threshold -> repeat until done

        // work out greedy picking, choose the top team until over threshold order opponents by points (-games????)

        return (int)teamToBeats.values().stream().filter(team -> team.pointsOffEqual > 0).count();
    }

    private static List<TeamStatus> findTeamsPlayingEachOther(List<TeamStatus> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer gamesInRange) {
        List<String> teamNames = teamsWithinRange.stream().map(TeamStatus::getName).collect(Collectors.toList());
        return teamsWithinRange.stream().filter(teamStatus -> teamToOpponents.get(teamStatus.getName()).stream().limit(gamesInRange).anyMatch(teamName -> teamNames.contains(teamName))).collect(Collectors.toList());
    }

    private static Map<String, TeamToBeat> calcTeamsToBeat(int targetPoints, List<TeamStatus> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        return teamsWithinRange.stream().map(teamStatus -> {
            List<String> opponents = teamToOpponents.get(teamStatus.getName()).stream().limit(matchesLookAhead).collect(Collectors.toList());
            return new TeamToBeat(teamStatus.getName(), matchesLookAhead, opponents, targetPoints - teamStatus.getPoints());
        }).collect(Collectors.toMap(TeamToBeat::getName, Function.identity()));
    }

    private static void aBeatB(TeamToBeat teamA, TeamToBeat teamB) {
        if (teamA != null) {
            teamA.gamesToPlay -= 1;
            teamA.pointsOffEqual -= 3;
            if (teamB != null) {
                teamA.opponents.remove(teamB.getName());
                teamB.opponents.remove(teamA.getName());
            }
        }
        if (teamB != null) {
            teamB.gamesToPlay -= 1;
        }
    }

    private static void aDrewWithB(TeamToBeat teamA, TeamToBeat teamB) {
        if (teamA != null) {
            teamA.gamesToPlay -= 1;
            teamA.pointsOffEqual -= 1;
            if (teamB != null) {
                teamA.opponents.remove(teamB.getName());
                teamB.opponents.remove(teamA.getName());
            }
        }
        if (teamB != null) {
            teamB.gamesToPlay -= 1;
            teamB.pointsOffEqual -= 1;
        }
    }

    private static class TeamToBeat {
        String name;
        int gamesToPlay;
        List<String> opponents;
        int pointsOffEqual;

        public TeamToBeat(String name, int gamesToPlay, List<String> opponents, int pointsOffEqual) {
            this.name = name;
            this.gamesToPlay = gamesToPlay;
            this.opponents = opponents;
            this.pointsOffEqual = pointsOffEqual;
        }

        public String getName() {
            return name;
        }

        public int getGamesToPlay() {
            return gamesToPlay;
        }

        public List<String> getOpponents() {
            return opponents;
        }

        public int getPointsOffEqual() {
            return pointsOffEqual;
        }
    }

}
