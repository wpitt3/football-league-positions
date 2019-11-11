package dulcinea;


import java.io.*;
import java.util.List;

class LeaguePositionCalculator {

    public static void main(String[] args) throws IOException  {
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

        List<Match> allMatches = FootballMatchesParser.parse(readFile(args[0]));

        Table table = new Table();
        table.updateTable(allMatches, matchesPlayed);

        System.out.println(table.printTable());

        LeaguePredicter.findPossibleLeaguePositions(table, allMatches, matchesPlayed, matchesLookAhead);
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