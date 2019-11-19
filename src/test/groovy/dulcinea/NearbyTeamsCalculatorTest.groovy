package dulcinea

import spock.lang.Ignore
import spock.lang.Specification

class NearbyTeamsCalculatorTest extends Specification {
    private static final String TEAM_A = "TeamA"
    private static final String TEAM_B = "TeamB"
    private static final String TEAM_C = "TeamC"
    private static final String TEAM_D = "TeamD"
    private static final String TEAM_E = "TeamE"
    private static final String TEAM_F = "TeamF"
    
    
    void "Both teams are on the same points as TeamD and are catchable"() {
        given:
          Table table = basicTable()
          table.teams[0].won = 5
          table.teams[1].goalsFor = 1
          table.teams[2].goalsFor = 1
        
        when:
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C], (TEAM_C): [TEAM_B]]
        
          int result = NearbyTeamsCalculator.teamsWhichAreNotCatchablebyMainTeam(table.teams[3], teamsPlayingEachOther, teamToOpponents, 1)
        
        then:
          result == 0
    }
    
    void "Both teams are two points more than TeamD and are catchable"() {
        given:
          Table table = basicTable()
          table.teams[0].won = 5
          table.teams[1].drawn = 3
          table.teams[2].drawn = 3
        
        when:
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C], (TEAM_C): [TEAM_B]]
          
          int result = NearbyTeamsCalculator.teamsWhichAreNotCatchablebyMainTeam(table.teams[3], teamsPlayingEachOther, teamToOpponents, 1)
        
        then:
          result == 0
    }
    
    void "One team is 3 points ahead and one is 0 ahead of TeamD and both are catchable"() {
        given:
          Table table = basicTable()
          table.teams[0].won = 5
          table.teams[1].won = 1
        
        when:
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C], (TEAM_C): [TEAM_B]]
          
          int result = NearbyTeamsCalculator.teamsWhichAreNotCatchablebyMainTeam(table.teams[3], teamsPlayingEachOther, teamToOpponents, 1)
        
        then:
          result == 0
    }
    
    void "One team is 3 points ahead and one is 1 ahead of TeamD so one is catchable"() {
        given:
          Table table = basicTable()
          table.teams[0].won = 5
          table.teams[1].won = 1
          table.teams[2].drawn = 2
        
        when:
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C], (TEAM_C): [TEAM_B]]
          
          int result = NearbyTeamsCalculator.teamsWhichAreNotCatchablebyMainTeam(table.teams[3], teamsPlayingEachOther, teamToOpponents, 1)
        
        then:
          result == 1
    }
    
    // TEAM being caught  |
    //                    v
    
    void "Both teams are on the same points as TeamA and can catch"() {
        given:
          Table table = basicTable()
          table.teams[0].goalsFor = 2
        
        when:
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C], (TEAM_C): [TEAM_B]]
          
          int result = NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(table.teams[0], teamsPlayingEachOther, teamToOpponents, 1)
        
        then:
          result == 0
    }
    
    void "Both teams are one point less than TeamA and can catch"() {
        given:
          Table table = basicTable()
          table.teams[0].drawn += 1
        
        when:
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C], (TEAM_C): [TEAM_B]]
          
          int result = NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(table.teams[0], teamsPlayingEachOther, teamToOpponents, 1)
        
        then:
          result == 0
    }
    
    void "One team is 3 points behind and one is 0 behind of TeamA and both can catch"() {
        given:
          Table table = basicTable()
          table.teams[0].won += 1
          table.teams[1].won += 1
        
        when:
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C], (TEAM_C): [TEAM_B]]
          
          int result = NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(table.teams[0], teamsPlayingEachOther, teamToOpponents, 1)
        
        then:
          result == 0
    }
    
    void "One team is 3 points behind and one is 1 behind of TeamA so only one can catch"() {
        given:
          Table table = basicTable()
          table.teams[0].won += 1
          table.teams[1].drawn += 2
        
        when:
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C], (TEAM_C): [TEAM_B]]
          
          int result = NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(table.teams[0], teamsPlayingEachOther, teamToOpponents, 1)
        
        then:
          result == 1
    }
    
    void "2 teams 2 games both 3 points off"() {
        given:
          Table table = basicTable()
          table.teams[0].won += 1
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C, TEAM_C], (TEAM_C): [TEAM_B, TEAM_B]]
        
        when:
          int result = NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(table.teams[0], teamsPlayingEachOther, teamToOpponents, 2)
        
        then:
          result == 0
    }
    
    void "2 teams 2 games one 4 points off, one 1 point off"() {
        given:
          Table table = basicTable()
          table.teams[0].won += 1
          table.teams[0].drawn += 1
          table.teams[1].won += 1
        
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C, TEAM_C], (TEAM_C): [TEAM_B, TEAM_B]]
    
        when:
          int result = NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(table.teams[0], teamsPlayingEachOther, teamToOpponents, 2)
    
        then:
          result == 0
    }
    
    void "2 teams 2 games one 6 points off, one 7 points off"() {
        given:
          Table table = basicTable()
          table.teams[0].won += 2
          table.teams[0].drawn += 1
          table.teams[1].drawn += 1
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C, TEAM_C], (TEAM_C): [TEAM_B, TEAM_B]]
    
        when:
          int result = NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(table.teams[0], teamsPlayingEachOther, teamToOpponents, 2)
    
        then:
          result == 1
    }
    
    void "3 teams 3 games 1 team 6 off, 2 teams 4 off"() {
        given:
          Table table = basicTable()
          table.teams[0].won += 2
          table.teams[1].drawn += 2
          table.teams[2].drawn += 2
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2], table.teams[3]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C, TEAM_D], (TEAM_D): [TEAM_B, TEAM_C], (TEAM_C): [TEAM_B, TEAM_D]]
    
        when:
          int result = NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(table.teams[0], teamsPlayingEachOther, teamToOpponents, 3)
    
        then:
          result == 1
    }
    
    @Ignore
    void "All three teams can beat top of table, but a points must be perfectly distributed"() {
        given:
          Table table = basicTable()
          table.updateTable([new Match(TEAM_E, TEAM_F, 0, 0)])
          table.teams[0].won += 2
          table.teams[1].won += 1
          table.teams[2].drawn += 1
          table.teams[3].drawn += 1
        
          List<TeamStatus> teamsPlayingEachOther = [table.teams[1], table.teams[2], table.teams[3], table.teams[4]]
          Map<String, ArrayList<String>> teamToOpponents =
                  [(TEAM_B): [TEAM_E, TEAM_E, TEAM_C],
                    (TEAM_C): [TEAM_B, TEAM_D, TEAM_D, TEAM_D, TEAM_D],
                    (TEAM_D): [TEAM_C, TEAM_C, TEAM_C, TEAM_C],
                    (TEAM_E): [TEAM_B, TEAM_B]]
        
        when:
          int result = NearbyTeamsCalculator.teamsWhichCannotCatchMainTeam(table.teams[0], teamsPlayingEachOther, teamToOpponents, 3)
        
        then:
          result == 0
    }
    
    private Table basicTable() {
        Table table = new Table()
        table.updateTable([new Match(TEAM_A, TEAM_B, 0, 0), new Match(TEAM_C, TEAM_D, 0, 0)], 1)
        return table
    }
}