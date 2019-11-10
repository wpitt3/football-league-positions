package dulcinea;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Table {

    private List<TeamStatus> teams;

    Table() {
        teams = new ArrayList<TeamStatus>();
    }

    /**
     * Construct table for all matches
     */
    public void updateTable(List<Match> matches) {
        addMatchesToTable(filterMatchesByPlayed(matches));
    }

    /**
     * Construct table for matches up to week x
     */
    public void updateTable(List<Match> matches, int noOfMatches) {
        Map<String, Integer> teamFrequency = new HashMap<>();
        List<Match> matchesUpToWeek = new ArrayList<>();

        for (Match match : filterMatchesByPlayed(matches)) {
            Integer homeTeamCount = getTeamFrequency(match.getHomeTeam(), teamFrequency);
            Integer awayTeamCount = getTeamFrequency(match.getAwayTeam(), teamFrequency);
            if (homeTeamCount <= noOfMatches && awayTeamCount <= noOfMatches) {
                matchesUpToWeek.add(match);
                teamFrequency.put(match.getHomeTeam(), homeTeamCount);
                teamFrequency.put(match.getAwayTeam(), awayTeamCount);
            }
        }

        if (teamFrequency.keySet().stream().anyMatch(team -> teamFrequency.get(team) != noOfMatches )) {
            throw new RuntimeException("Not enough matches to meet requested number");
        }
        addMatchesToTable(matchesUpToWeek);
    }

    public TeamStatus getTeam(int index) {
        return teams.get(index);
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

    private void addMatchesToTable(List<Match> matches) {
        for (Match match : matches) {
            getTeam(match.getHomeTeam()).addMatch(match.getHomeTeamScore(), match.getAwayTeamScore());
            getTeam(match.getAwayTeam()).addMatch(match.getAwayTeamScore(), match.getHomeTeamScore());
        }
        updateOrder();
    }

    private List<Match> filterMatchesByPlayed(List<Match> matches) {
        return matches.stream()
                .filter(match -> match.getHomeTeamScore() != null && match.getAwayTeamScore() != null)
                .collect(Collectors.toList());
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

    private String padNumber(int value) {
        return StringUtils.leftPad(String.valueOf(value), 4);
    }

    private Integer getTeamFrequency(String team, Map<String, Integer> teamFrequency) {
        return teamFrequency.get(team) != null ? teamFrequency.get(team)+1 : 1;
    }

}
