package dulcinea;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Table {

    private List<TeamStatus> teams;

    Table() {
        teams = new ArrayList<TeamStatus>();
    }

    public void update(List<Match> matches) {
        List<Match> playedMatches = matches.stream()
            .filter(match -> match.getHomeTeamScore() != null && match.getAwayTeamScore() != null )
            .collect(Collectors.toList());
        for (Match match : playedMatches) {
            getTeam(match.getHomeTeam()).addMatch(match.getHomeTeamScore(), match.getAwayTeamScore());
            getTeam(match.getAwayTeam()).addMatch(match.getAwayTeamScore(), match.getHomeTeamScore());
        }
        updateOrder();
    }

    public void update(String team, Integer teamScore, Integer opponentScore) {
        if (teamScore != null && opponentScore != null) {
            getTeam(team).addMatch(teamScore, opponentScore);
        }
        updateOrder();
    }

    public TeamStatus getTeam(int index) {
        return teams.get(index);
    }

    private TeamStatus getTeam(String name) {
        return teams.stream().filter(team -> team.getName().equals(name)).findFirst().orElseGet(() -> {
            TeamStatus team = new TeamStatus(name);
            teams.add(team);
            return team;
        });
    }

    private void updateOrder() {
        teams = teams.stream().sorted(
            Comparator.comparing(TeamStatus::getPoints)
            .thenComparing(TeamStatus::getGoalDifference)
            .thenComparing(TeamStatus::getGoalsFor)
            .reversed()
        ).collect(Collectors.toList());

    }

    public String printTable() {
        String table = "";

        for (TeamStatus team : teams) {
            table += StringUtils.rightPad(team.getName(), 8);
            table += padNumber(team.getPlayed());
            table += padNumber(team.getWon());
            table += padNumber(team.getDrawn());
            table += padNumber(team.getLost());
            table += padNumber(team.getGoalsFor());
            table += padNumber(team.getGoalsAgainst());
            table += padNumber(team.getGoalDifference());
            table += padNumber(team.getPoints());
            table += "\n";
        }

        return table;
    }

    private String padNumber(int value) {
        return StringUtils.leftPad(String.valueOf(value), 4);
    }

}
