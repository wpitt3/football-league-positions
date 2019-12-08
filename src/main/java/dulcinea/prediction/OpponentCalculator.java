package dulcinea.prediction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dulcinea.match.Match;
import dulcinea.match.MatchFilterer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpponentCalculator {

    static Map<String, ArrayList<String>> calculateOpponents(List<Match> matches) {
        return matches.stream()
                .map(match -> Maps.newHashMap(ImmutableMap.of(
                        match.getHomeTeam(), Lists.newArrayList(match.getAwayTeam()),
                        match.getAwayTeam(), Lists.newArrayList(match.getHomeTeam()))))
                .reduce(OpponentCalculator::mergeTeamOpponents).get();
    }

    private static <String> HashMap<String, ArrayList<String>> mergeTeamOpponents(
            HashMap<String, ArrayList<String>> result,
            HashMap<String, ArrayList<String>> teamToOpponents) {
        teamToOpponents.forEach((team, opponents) -> {
            ArrayList<String> opponentsSoFar = result.get(team) != null ? result.get(team) : new ArrayList<>();
            opponentsSoFar.addAll(opponents);
            result.put(team, opponentsSoFar);
        });
        return result;
    }
}
