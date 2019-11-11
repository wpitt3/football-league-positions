package dulcinea

import org.junit.Ignore
import spock.lang.Specification

class LeaguePredicterTest extends Specification {
    
    void "Basic lookup for team which cannot go higher"() {
        given:
          Table table = new Table()
          table.updateTable(matches(), 2)
        
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches(), 2, 1)
        
        then:
          result.size() == 6
          result[0].teamName == "NUFC"
          result[0].currentPosition == 1
          result[0].highestImpossible == null
          result[0].highestPossible == 1
          result[0].highestWithoutLargeSwing == null
    }
    
    void "Lookup which teams could catch without large goal swing"() {
        given:
          Table table = new Table()
          table.updateTable(matches(), 2)
        
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches(), 2, 1)
        
        then:
          result.size() == 6
          result[0].teamName == "NUFC"
          result[0].lowestWithoutLargeSwing == 2
    }
    
    void "Lookup which teams which are not possible as they are playing each other"() {
        given:
          Table table = new Table()
          table.updateTable(matches(), 2)
        
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches(), 2, 1)
        
        then:
          result.size() == 6
          result[0].teamName == "NUFC"
          result[0].lowestPossible == 3
          result[0].lowestImpossible == 4
    }
    
    void "Lowest without large swing must take into account possible"() {
        given:
          Table table = new Table()
          table.updateTable(matches(), 2)
    
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches(), 2, 1)
        
        then:
          result.size() == 6
          result[3].teamName == "SFC"
          result[3].lowestWithoutLargeSwing == 5
          result[3].lowestPossible == 5
          result[3].lowestImpossible == 6
          result[4].lowestWithoutLargeSwing == 6
          result[4].lowestPossible == 6
    }
    
    void "two teams are drawing and behind a team with a better goal difference by 4"() {
        given:
          Table table = new Table()
          table.updateTable(matches(), 2)
    
        when:
          List<LeaguePositionStats> result = LeaguePredicter.findPossibleLeaguePositions(table, matches(), 2, 1)

        then:
          result.size() == 6
          result[1].teamName == "CFC"
//          result[1].lowestWithoutLargeSwing == 4  need to fix this
          result[1].lowestPossible == 5
    }
    
    
    private List<Match> matches() {
        return [
            new Match("LFC", "BFC", 4, 1),
            new Match("NUFC", "SFC", 6, 1),
            new Match("CFC", "MCFC", 2, 2),
            
            new Match("SFC", "BFC", 3, 1),
            new Match("CFC", "LFC", 2, 1),
            new Match("NUFC", "MCFC", 2, 1),
            
            new Match("LFC", "SFC", 2, 2),
            new Match("CFC", "NUFC", 1, 0),
            new Match("MCFC", "BFC", 1, 0),
        ]
    }
}