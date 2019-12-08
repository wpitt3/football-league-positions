package dulcinea.match

import spock.lang.Specification

class MatchFiltererTest extends Specification {
    
    void "Filter unplayed matches"() {
        given:
          List matches = [new Match("EFC", "WFC", null, null),]
        
        when:
          List<Match> result = MatchFilterer.filterMatchesByPlayed(matches)
        
        then:
          result.size() == 0
    }
    
    void "If there not enough matches to meet week specified throw exception"() {
        when:
          MatchFilterer.findMatchesUntilWeekX(matches(), 3)
        
        then:
          thrown RuntimeException
    }
    
    void "The matches are filtered up until match 2 when there are 2 are requested"() {
        when:
          List<Match> result = MatchFilterer.findMatchesUntilWeekX(matches(), 2)
    
        then:
          result.size() == 4
    }
    
    void "The matches are filtered up until match 1 when there are 1 are requested"() {
        when:
          List<Match> result = MatchFilterer.findMatchesUntilWeekX(matches(), 1)
        
        then:
          result.size() == 2
          result[0].homeTeam == "LFC"
          result[0].awayTeam == "NCFC"
    }
    
    void "The matches from week 2 are given if requested"() {
        when:
          List<Match> result = MatchFilterer.findMatchesForWeekXToWeekY(matches(), 1, 2)
        
        then:
          result.size() == 2
          result[0].homeTeam == "SFC"
          result[0].awayTeam == "NCFC"
          result[1].homeTeam == "NUFC"
          result[1].awayTeam == "LFC"
    }
    
    void "If matches have unbalanced number of games for each team then throw exception"() {
        given:
          List<Match> matches = matches() + [new Match("NUFC", "LFC", 2, 1), new Match("SFC", "LFC", 2, 1)]
        
        when:
          MatchFilterer.findMatchesUntilWeekX(matches, 3)
        
        then:
          thrown RuntimeException
    }
    
    private List<Match> matches() {
        return [
                new Match("LFC", "NCFC", 4, 1),
                new Match("NUFC", "SFC", 0, 3),
                new Match("SFC", "NCFC", 4, 1),
                new Match("NUFC", "LFC", 2, 1),
        ]
    }
}