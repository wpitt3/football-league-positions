package dulcinea


import spock.lang.Specification

class TableTest extends Specification {
    
    void "Don't include non played matches in table"() {
        given:
          List matches = [
                  new Match("EFC", "WFC", null, null),
          ]
        
        when:
          Table table = new Table()
          table.update(matches)
        
        then:
          table.teams.size() == 0
    }
    
    void "A win gets more points then a draw and loss"() {
        given:
          List matches = [
                  new Match("LFC", "NCFC", 4, 1),
                  new Match("CFC", "WWWFC", 2, 2),
          ]
        
        when:
          Table table = new Table()
          table.update(matches)
        
        then:
          table.teams.size() == 4
          table.teams[0].getName() == "LFC"
          table.teams[0].getPoints() == 3
          table.teams[0].getWon() == 1
          table.teams[1].getName() == "CFC"
          table.teams[1].getPoints() == 1
          table.teams[1].getDrawn() == 1
          table.teams[3].getName() == "NCFC"
          table.teams[3].getPoints() == 0
          table.teams[3].getLost() == 1
    }
    
    void "The table is ordered by points then gd the gf"() {
        given:
          List matches = [
                  new Match("LFC", "NCFC", 4, 1),
                  new Match("NUFC", "SFC", 0, 3),
                  new Match("MUFC", "AFC", 2, 1),
          ]
        
        when:
          Table table = new Table()
          table.update(matches)
    
        then:
          table.teams.size() == 6
          table.teams[0].getName() == "LFC"
          table.teams[0].getPoints() == 3
          table.teams[0].getGoalDifference() == 3
          table.teams[0].getGoalsFor() == 4
          table.teams[1].getName() == "SFC"
          table.teams[1].getPoints() == 3
          table.teams[1].getGoalDifference() == 3
          table.teams[1].getGoalsFor() == 3
          table.teams[2].getName() == "MUFC"
          table.teams[2].getPoints() == 3
          table.teams[2].getGoalDifference() == 1
          table.teams[2].getGoalsFor() == 2
    }
    
    void "table prints correctly"() {
        given:
          List matches = [
              new Match("LFC", "NCFC", 4, 1),
              new Match("CFC", "WWWFC", 2, 2),
          ]
        
        when:
          Table table = new Table()
          table.update(matches)
          String result = table.printTable()
        
        then:
          result ==
              "LFC        1   1   0   0   4   1   3   3\n" +
              "CFC        1   0   1   0   2   2   0   1\n" +
              "WWWFC      1   0   1   0   2   2   0   1\n" +
              "NCFC       1   0   0   1   1   4  -3   0\n"
    
    }
}