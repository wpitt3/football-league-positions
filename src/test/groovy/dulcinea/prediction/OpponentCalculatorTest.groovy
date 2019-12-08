package dulcinea.prediction

import dulcinea.match.Match
import spock.lang.Specification;

class OpponentCalculatorTest extends Specification {
    private static final String TEAM_A = "TeamA"
    private static final String TEAM_B = "TeamB"
    private static final String TEAM_C = "TeamC"
    
    void "Simple one match"() {
        given:
          List matches = [new Match(TEAM_A, TEAM_B)]
        
        when:
          Map result = OpponentCalculator.calculateOpponents(matches)
        
        then:
          result == [(TEAM_A):[TEAM_B], (TEAM_B): [TEAM_A]]
    }
    
    void "Include duplicates"() {
        given:
          List matches = [new Match(TEAM_A, TEAM_B), new Match(TEAM_A, TEAM_B)]
        
        when:
          Map result = OpponentCalculator.calculateOpponents(matches)
        
        then:
          result == [(TEAM_A):[TEAM_B, TEAM_B], (TEAM_B): [TEAM_A, TEAM_A]]
    }
    
    void "Multiple different teams"() {
        given:
          List matches = [new Match(TEAM_A, TEAM_B), new Match(TEAM_A, TEAM_C)]
        
        when:
          Map result = OpponentCalculator.calculateOpponents(matches)
        
        then:
          result == [(TEAM_A):[TEAM_B, TEAM_C], (TEAM_B): [TEAM_A], (TEAM_C): [TEAM_A]]
    }
    
}
