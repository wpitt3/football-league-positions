package dulcinea.match;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Table {

    private List<LeaguePostion> teams;

    private Table() {
        teams = new ArrayList<>();
    }

    /**
     * Construct table for all matches
     */
    public static Table createTable(List<Match> matches) {
        Table table = new Table();
        table.addMatchesToTable(MatchFilterer.filterMatchesByPlayed(matches));
        return table;
    }

    /**
     * Construct table for matches up to week x
     */
    public static Table createTable(List<Match> matches, int noOfMatches) {
        List<Match> matchesUpToWeek = MatchFilterer.findMatchesUntilWeekX(
            MatchFilterer.filterMatchesByPlayed(matches),  noOfMatches
        );

        return createTable(matchesUpToWeek);
    }

    public List<LeaguePostion> getTeams() {
        return Lists.newArrayList(teams);
    }

    private void addMatchesToTable(List<Match> matches) {
        for (Match match : matches) {
            getTeam(match.getHomeTeam()).addMatch(match.getHomeTeamScore(), match.getAwayTeamScore());
            getTeam(match.getAwayTeam()).addMatch(match.getAwayTeamScore(), match.getHomeTeamScore());
        }
        updateOrder();
    }

    private LeaguePostion getTeam(String name) {
        return teams.stream().filter(team -> team.getName().equals(name)).findFirst().orElseGet(() -> {
            LeaguePostion team = new LeaguePostion(name);
            teams.add(team);
            return team;
        });
    }

    private void updateOrder() {
        teams = teams.stream().sorted(
            Comparator.comparing(LeaguePostion::getPoints)
                .thenComparing(LeaguePostion::getGoalDifference)
                .thenComparing(LeaguePostion::getGoalsFor)
                .reversed()
        ).collect(Collectors.toList());

        int index = 1;
        for(LeaguePostion team: teams) {
            team.setIndex(index++);
        }
    }

    public String printTable() {
        if(teams.isEmpty()) {
            return "No Teams";
        }

        int maxTeamLength = teams.stream().map(team -> team.getName().length()).reduce(Integer::max).get() + 3;
        String table = StringUtils.rightPad("Team", maxTeamLength) + "  P   W   D   L  GF  GA  GD   P\n";

        for (LeaguePostion team : teams) {
            table += StringUtils.rightPad(team.getName(), maxTeamLength);
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
