package dulcinea;


import dulcinea.match.FootballMatchesParser;
import dulcinea.match.Match;
import dulcinea.match.Table;
import dulcinea.prediction.LeaguePositionStats;
import dulcinea.prediction.LeaguePredicter;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

class LeaguePositionCalculator {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Missing filename");
            return;
        }
        if (args.length < 2) {
            System.out.println("Missing matches played");
            return;
        }
        if (args.length < 3) {
            System.out.println("Missing future matches look ahead");
            return;
        }

        Integer matchesPlayed = Integer.parseInt(args[1]);
        Integer matchesLookAhead = Integer.parseInt(args[2]);
//        String teamName = args[3];

//        System.out.println(table.printTable());

        for(int i = 1; i<3; i++) {
            List<Match> allMatches = FootballMatchesParser.parse(readFile(args[0]));
            matchesPlayed = i;
            Table table = Table.createTable(allMatches, matchesPlayed);
            List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, allMatches, matchesPlayed, matchesLookAhead);
//            System.out.println("{");
//            System.out.println("teams: [\"" + result.stream().map(stats -> stats.getTeamName()).collect(Collectors.joining("\",\"")) + "\"],");
//            System.out.println("teamData: [");
//            for (LeaguePositionStats stats : result) {
//                System.out.println(stats);
//            }
//            System.out.println("]},");
        }
    }

    private static String readFile(String filename) throws IOException {
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String result = "";
        String line;
        while ((line = br.readLine()) != null) {
            result += line;
        }
        return result;
    }

}