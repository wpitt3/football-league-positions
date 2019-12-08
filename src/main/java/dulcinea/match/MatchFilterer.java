package dulcinea.match;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MatchFilterer {

    /**
     * find all matches between fromWeek and lastWeek
     * from Week of 0 will be from beginning
     *
     */
    public static List<Match> findMatchesForWeekXToWeekY(List<Match> matches, int fromWeek, int lastWeek) {
        List<Match> allMatches = findMatchesUntilWeekX(matches, lastWeek);
        List<Match> tooEarlyMatches = findMatchesUntilWeekX(matches, fromWeek);
        allMatches.removeAll(Lists.newArrayList(tooEarlyMatches));
        return allMatches;
    }

    static List<Match> findMatchesUntilWeekX(List<Match> matches, int noOfMatches) {
        Set<String> teams = calcAllTeams(matches);
        if (teams.size()*noOfMatches/2 > matches.size()) {throw new RuntimeException("Not enough matches to meet requested number");}

        List<Match> matchesSubList = matches.subList(0, teams.size()*noOfMatches/2);

        Map<String, List<Match>> homeMatches = matchesSubList.stream().collect(Collectors.groupingBy(it -> it.getHomeTeam()));
        Map<String, List<Match>> awayMatches = matchesSubList.stream().collect(Collectors.groupingBy(it -> it.getAwayTeam()));

        if(teams.stream().anyMatch(it -> extractNumberOfMatches(it, homeMatches, awayMatches) != noOfMatches)) {
            throw new RuntimeException("Unbalanced team order for matches");
        }
        return matchesSubList;
    }

    static List<Match> filterMatchesByPlayed(List<Match> matches) {
        return matches.stream()
                .filter(match -> match.getHomeTeamScore() != null && match.getAwayTeamScore() != null)
                .collect(Collectors.toList());
    }

    private static int extractNumberOfMatches(String teamName, Map<String, List<Match>> homeMatches, Map<String, List<Match>> awayMatches) {
        return  (homeMatches.get(teamName) != null ? homeMatches.get(teamName).size() : 0) + (awayMatches.get(teamName) != null ? awayMatches.get(teamName).size() : 0);
    }

    private static Set<String> calcAllTeams(List<Match> matches) {
        return matches.stream().map(it -> Lists.newArrayList(it.getHomeTeam(), it.getAwayTeam())).flatMap(List::stream).collect(Collectors.toSet());
    }
}
