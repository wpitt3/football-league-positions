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
            List<String> opponents = new ArrayList<>(teamToBeat.opponents);
            for (String opponent : opponents) {
                if (teamToBeats.get(opponent) == null) {
                    aBeatB(opponent, teamToBeat);
                }
            }
        }
        for (int i =0; i<10;i++) {
            for (String teamName : teamToBeats.keySet()) {
                TeamToBeat teamToBeat = teamToBeats.get(teamName);
                if (teamToBeat.pointsOffEqual >= 0) {
                    List<String> opponents = new ArrayList<>(teamToBeat.opponents);
                    for (String opponent : opponents) {
                        aBeatB(teamToBeat, teamToBeats.get(opponent));
                    }
                }

                if (teamToBeat.gamesToPlay == 1) {
                    TeamToBeat opponent = teamToBeats.get(teamToBeat.getOpponents().get(0));
                    if (teamToBeat.pointsOffEqual == 0) {
                        aBeatB(opponent, teamToBeat);
                    } else if (teamToBeat.pointsOffEqual == 1 || teamToBeat.pointsOffEqual == 2) {
                        aDrewWithB(teamToBeat, opponent);
                    } else {
                        aBeatB(teamToBeat, opponent);
                    }
                }
            }
        }

        List<TeamToBeat> teamsLeft = teamToBeats.values().stream().filter(team -> team.gamesToPlay > 0).collect(Collectors.toList());
        if( teamsLeft.size() > 0) {
            System.out.println("v Cool " + mainTeam.getName());
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
            List<String> opponents = new ArrayList<>(teamToBeat.opponents);
            for (String opponent : opponents) {
                if (teamToBeats.get(opponent) == null) {
                    aBeatB(teamToBeat, opponent);
                }
            }
        }
        for (int i =0; i<10;i++) {
            for (String teamName : teamToBeats.keySet()) {
                TeamToBeat teamToBeat = teamToBeats.get(teamName);
                if (teamToBeat.pointsOffEqual <= 0 || teamToBeat.gamesToPlay * 3 < teamToBeat.pointsOffEqual) {
                    List<String> opponents = new ArrayList<>(teamToBeat.opponents);
                    for (String opponent : opponents) {
                        aBeatB(teamToBeats.get(opponent), teamToBeat);
                    }
                }

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
        }

        List<TeamToBeat> teamsLeft = teamToBeats.values().stream().filter(team -> team.gamesToPlay > 0).collect(Collectors.toList());
        if( teamsLeft.size() > 0) {
            System.out.println("Cool " + mainTeam.getName());
        }
        // assign obvious results
        // assign all external games as wins && remove all teams which are over threshold -> repeat until done

        // work out greedy picking, choose the top team until over threshold order opponents by points (-games????)

        return (int)teamToBeats.values().stream().filter(team -> team.pointsOffEqual > 0).count();
    }

    private static Map<String, TeamToBeat> calcTeamsToBeat(int targetPoints, List<TeamStatus> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        return teamsWithinRange.stream().map(teamStatus -> {
            List<String> opponents = teamToOpponents.get(teamStatus.getName()).stream().limit(matchesLookAhead).collect(Collectors.toList());
            return new TeamToBeat(teamStatus.getName(), matchesLookAhead, opponents, targetPoints - teamStatus.getPoints());
        }).collect(Collectors.toMap(TeamToBeat::getName, Function.identity()));
    }

    private static void aBeatB(TeamToBeat teamA, TeamToBeat teamB) {
        teamA.gamesToPlay -= 1;
        teamA.pointsOffEqual -= 3;
        teamA.opponents.remove(teamB.getName());
        teamB.opponents.remove(teamA.getName());
        teamB.gamesToPlay -= 1;
    }

    private static void aBeatB(TeamToBeat teamA, String loserName) {
        teamA.gamesToPlay -= 1;
        teamA.pointsOffEqual -= 3;
        teamA.opponents.remove(loserName);
    }

    private static void aBeatB(String winnerName, TeamToBeat teamB) {
        teamB.opponents.remove(winnerName);
        teamB.gamesToPlay -= 1;
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
