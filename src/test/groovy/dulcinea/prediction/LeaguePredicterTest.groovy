package dulcinea.prediction

import dulcinea.match.Match
import dulcinea.match.Table
import spock.lang.Specification

class LeaguePredicterTest extends Specification {
    
    void "Basic lookup for team which cannot go higher"() {
        given:
          Table table = Table.createTable(matches(), 2)
        
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches(), 2, 1)
        
        then:
          result.size() == 6
          result[0].teamName == "NUFC"
          result[0].currentPosition == 1
          result[0].highestPossible[0] == 1
    }
    
    void "Basic look up for team which cannot go lower"() {
        given:
          Table table = Table.createTable(matches(), 2)
        
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches(), 2, 1)
        
        then:
          result.size() == 6
          result[5].teamName == "BFC"
          result[5].lowestPossible[0] == 6
    }
    
    void "Lookup which teams which cannot catch main team as they are playing each other"() {
        given:
          Table table = Table.createTable(matches(), 2)
        
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches(), 2, 1)
        
        then:
          result.size() == 6
          result[0].teamName == "NUFC"
          result[0].lowestPossible[0] == 3
    }
    
    void "Lowest without large swing is only set is not equal to possible"() {
        given:
          Table table = Table.createTable(matches(), 2)
    
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches(), 2, 1)
        
        then:
          result.size() == 6
          result[3].teamName == "SFC"
          result[3].lowestPossible[0] == 5
          result[4].lowestPossible[0] == 6
    }
    
    void "Lookup which teams are uncatchable as they are playing each other"() {
        given:
          Table table = Table.createTable(matches(), 2)
    
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches(), 2, 1)
        
        then:
          result.size() == 6
          result[2].teamName == "LFC"
          result[2].currentPosition == 3
          result[2].highestPossible[0] == 2
          result[5].teamName == "BFC"
          result[5].currentPosition == 6
          result[5].highestPossible[0] == 4
    }
    
    void "Teams behind by 3 points and 3 goals but with better goals scored are not counted as large swing"() {
        given:
          List<Match> matches = [
              new Match("LFC", "BFC", 6, 4),
              new Match("NUFC", "SFC", 2, 1),
              new Match("LFC", "SFC"),
              new Match("NUFC", "BFC"),
          ]
  
          Table table = Table.createTable(matches, 1)
        
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches, 1, 1)
        
        then:
          result.size() == 4
          result[0].teamName == "LFC"
          result[0].lowestPossible[0] == 4
          result[1].teamName == "NUFC"
          result[1].lowestPossible[0] == 4
          result[2].teamName == "SFC"
          result[2].currentPosition == 3
          result[2].highestPossible[0] == 1
          result[3].teamName == "BFC"
          result[3].currentPosition == 4
          result[3].highestPossible[0] == 1
    }
    
    private List<Match> matches() {
        return [
            new Match("LFC", "BFC", 4, 1),
            new Match("NUFC", "SFC", 6, 1),
            new Match("CFC", "MCFC", 2, 2),
            
            new Match("SFC", "BFC", 3, 1),
            new Match("CFC", "LFC", 2, 1),
            new Match("NUFC", "MCFC", 4, 1),
            
            new Match("LFC", "SFC", null, null),
            new Match("CFC", "NUFC", null, null),
            new Match("MCFC", "BFC", null, null),
        ]
    }
}