package dulcinea;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MatchFilterer {

    public static List<Match> filterMatchesByPlayed(List<Match> matches) {
        return matches.stream()
            .filter(match -> match.getHomeTeamScore() != null && match.getAwayTeamScore() != null)
            .collect(Collectors.toList());
    }

    public static List<Match> findMatchesUntilWeekX(List<Match> matches, int lastWeek) {
        return findMatchesForWeekXToWeekY(matches, 0, lastWeek);
    }

    /**
     * find all matches between fromWeek and lastWeek
     * from Week of 0 will be from beginning
     *
     */
    public static List<Match> findMatchesForWeekXToWeekY(List<Match> matches, int fromWeek, int lastWeek) {
        Map<String, Integer> teamFrequency = new HashMap<>();
        List<Match> matchesUpToWeek = new ArrayList<>();

        for (Match match : matches) {
            Integer homeTeamCount = getTeamFrequency(match.getHomeTeam(), teamFrequency);
            Integer awayTeamCount = getTeamFrequency(match.getAwayTeam(), teamFrequency);
            if (homeTeamCount <= lastWeek && awayTeamCount <= lastWeek) {
                teamFrequency.put(match.getHomeTeam(), homeTeamCount);
                teamFrequency.put(match.getAwayTeam(), awayTeamCount);
                if (homeTeamCount > fromWeek && awayTeamCount > fromWeek) {
                    matchesUpToWeek.add(match);
                }
            }
        }

        if (teamFrequency.keySet().stream().anyMatch(team -> teamFrequency.get(team) != lastWeek)) {
            throw new RuntimeException("Not enough matches to meet requested number");
        }

        if (fromWeek != 0) {
            Map<String, List<String>> teamToMatches = matchesUpToWeek.stream()
                    .map(match -> Lists.newArrayList(match.getHomeTeam(), match.getAwayTeam()))
                    .flatMap(List::stream)
                    .collect(Collectors.groupingBy(Function.identity()));
            if (teamToMatches.keySet().stream().anyMatch(team -> teamToMatches.get(team).size() != lastWeek - fromWeek)) {
                throw new RuntimeException("Not enough matches to meet requested number");
            }
        }

        return matchesUpToWeek;
    }


    private static Integer getTeamFrequency(String team, Map<String, Integer> teamFrequency) {
        return teamFrequency.get(team) != null ? teamFrequency.get(team)+1 : 1;
    }
}
