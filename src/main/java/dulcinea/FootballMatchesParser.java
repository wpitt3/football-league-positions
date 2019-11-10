package dulcinea;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;

public class FootballMatchesParser {

    public static List<Match> parse(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode jsonNode = mapper.readTree(json);
        List<Match> matches = Streams.stream(jsonNode.get("matches").elements())
            .map(match -> extractMatchFromJsonNode(match)).collect(Collectors.toList());
        return matches;
    }

    private static Match extractMatchFromJsonNode(JsonNode match) {
        JsonNode result = match.get("score").get("fullTime");
        return new Match(
            match.get("homeTeam").get("name").textValue(),
            match.get("awayTeam").get("name").textValue(),
            result.get("homeTeam").isIntegralNumber() ? result.get("homeTeam").intValue() : null,
            result.get("awayTeam").isIntegralNumber() ? result.get("awayTeam").intValue() : null
        );
    }

}
