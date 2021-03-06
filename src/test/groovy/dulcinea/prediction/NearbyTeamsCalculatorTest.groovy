package dulcinea.prediction

import dulcinea.match.Match
import dulcinea.match.Table
import dulcinea.match.LeaguePostion
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
          Table table = basicTable(10)
          table.teams[1].goalsFor = 1
          table.teams[2].goalsFor = 1
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(table.teams[3], midTwoTeams(table), midTeamOpponents(), 1)
        
        then:
          result.size() == 2
    }
    
    void "Both teams are two points more than TeamD and are catchable"() {
        given:
          Table table = basicTable(10,2,2)
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(table.teams[3], midTwoTeams(table), midTeamOpponents(), 1)
        
        then:
          result.size() == 2
    }
    
    void "One team is 3 points ahead and one is 0 ahead of TeamD and both are catchable"() {
        given:
          Table table = basicTable(10,3)
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(table.teams[3], midTwoTeams(table), midTeamOpponents(), 1)
        
        then:
          result.size() == 2
    }
    
    void "One team is 3 points ahead and one is 1 ahead of TeamD so one is catchable"() {
        given:
          Table table = basicTable(10,3,2)
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(table.teams[3], midTwoTeams(table), midTeamOpponents(), 1)
        
        then:
          result.size() == 1
    }
    
    void "2 teams 2 games both 3 points ahead"() {
        given:
          Table table = basicTable(10,3,3)
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(table.teams[3], midTwoTeams(table), midTeamOpponents(2), 2)
        
        then:
          result.size() == 2
    }
    
    void "2 teams 2 games one 5 points ahead, one 2 point ahead"() {
        given:
          Table table = basicTable(10,5,2)
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(table.teams[3], midTwoTeams(table), midTeamOpponents(2), 2)
        
        then:
          result.size() == 2
    }
    
    void "2 teams 2 games one 5 points ahead, one 6 points ahead"() {
        given:
          Table table = basicTable(10,6,5)
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(table.teams[3], midTwoTeams(table), midTeamOpponents(2), 2)
        
        then:
          result.size() == 1
    }
    
    void "3 teams 2 games 1 team 6 ahead, 2 teams 5 ahead"() {
        given:
          Table table = basicTable(5,3,3)
        
          List<LeaguePostion> teamsPlayingEachOther = [table.teams[0], table.teams[1], table.teams[2]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C, TEAM_A], (TEAM_A): [TEAM_B, TEAM_C], (TEAM_C): [TEAM_B, TEAM_A]]
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(table.teams[3], teamsPlayingEachOther, teamToOpponents, 2)
        
        then:
          result.size() == 2
    }
    
    void "All four teams can be beated by fifth place, but a points must be perfectly distributed"() {
        given:
          Table table = basicTable(6,6,6,0)
          table.addMatchesToTable([new Match(TEAM_E, TEAM_F, 0, 0)])

          List<LeaguePostion> teamsPlayingEachOther = [table.teams[0], table.teams[1], table.teams[2], table.teams[3]]
          Map<String, ArrayList<String>> teamToOpponents =
                  [
                    (TEAM_A): [TEAM_B, TEAM_B, TEAM_D],
                    (TEAM_B): [TEAM_A, TEAM_A, TEAM_C],
                    (TEAM_C): [TEAM_B, TEAM_D, TEAM_D],
                    (TEAM_D): [TEAM_C, TEAM_C, TEAM_A]
                  ]

        when:
          List<Team> result = NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(table.teams[4], teamsPlayingEachOther, teamToOpponents, 3)

        then:
          result.size() == 4
    }
    
    void "Difficult example"() {
        given:
          Map teamToOpponents = ["Liverpool FC":["AFC Bournemouth", "Watford FC", "West Ham United FC"], "Aston Villa FC":["Leicester City FC", "Sheffield United FC", "Southampton FC"], "Tottenham Hotspur FC":["Burnley FC", "Wolverhampton Wanderers FC", "Chelsea FC"], "Wolverhampton Wanderers FC":["Brighton & Hove Albion FC", "Tottenham Hotspur FC", "Norwich City FC"], "Manchester City FC":["Manchester United FC", "Arsenal FC", "Leicester City FC"], "Southampton FC":["Newcastle United FC", "West Ham United FC", "Aston Villa FC"], "West Ham United FC":["Arsenal FC", "Southampton FC", "Liverpool FC"], "Chelsea FC":["Everton FC", "AFC Bournemouth", "Tottenham Hotspur FC"], "Manchester United FC":["Manchester City FC", "Everton FC", "Watford FC"], "Leicester City FC":["Aston Villa FC", "Norwich City FC", "Manchester City FC"], "AFC Bournemouth":["Liverpool FC", "Chelsea FC", "Burnley FC"], "Burnley FC":["Tottenham Hotspur FC", "Newcastle United FC", "AFC Bournemouth"], "Sheffield United FC":["Norwich City FC", "Aston Villa FC", "Brighton & Hove Albion FC"], "Norwich City FC":["Sheffield United FC", "Leicester City FC", "Wolverhampton Wanderers FC"], "Everton FC":["Chelsea FC", "Manchester United FC", "Arsenal FC"], "Watford FC":["Crystal Palace FC", "Liverpool FC", "Manchester United FC"], "Crystal Palace FC":["Watford FC", "Brighton & Hove Albion FC", "Newcastle United FC"], "Arsenal FC":["West Ham United FC", "Manchester City FC", "Everton FC"], "Newcastle United FC":["Southampton FC", "Burnley FC", "Crystal Palace FC"], "Brighton & Hove Albion FC":["Wolverhampton Wanderers FC", "Crystal Palace FC", "Sheffield United FC"]]
          List<LeaguePostion> teamsPlayingEachOther = ["Wolverhampton Wanderers FC": 23, "Manchester United FC": 21, "Crystal Palace FC": 21, "Tottenham Hotspur FC": 20, "Sheffield United FC": 19, "Arsenal FC": 19, "Newcastle United FC": 19, "Burnley FC": 18, "Brighton & Hove Albion FC": 18, "AFC Bournemouth": 16, "West Ham United FC": 16, "Aston Villa FC": 15].collect{ k, v -> LeaguePostion team = new LeaguePostion(k); team.drawn = v; return team}
          LeaguePostion mainTeam = new LeaguePostion("Southampton FC")
          mainTeam.drawn = 15
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcTeamsThatCanBeOvertaken(mainTeam, teamsPlayingEachOther, teamToOpponents, 3)
        
        then:
          result.size() == 12
    }
    
    
    // TEAM being caught  |
    //                    v
    
    void "Both teams are on the same points as TeamA and can catch"() {
        given:
          Table table = basicTable()
          table.teams[0].goalsFor = 2
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcCatchingTeams(table.teams[0], midTwoTeams(table), midTeamOpponents(), 1)
        
        then:
          result.size() == 2
    }
    
    void "Both teams are one point less than TeamA and can catch"() {
        given:
          Table table = basicTable(1)
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcCatchingTeams(table.teams[0], midTwoTeams(table), midTeamOpponents(), 1)
        
        then:
          result.size() == 2
    }
    
    void "One team is 3 points behind and one is 0 behind of TeamA and both can catch"() {
        given:
          Table table = basicTable(3,3)
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcCatchingTeams(table.teams[0], midTwoTeams(table), midTeamOpponents(), 1)
        
        then:
          result.size() == 2
    }
    
    void "One team is 3 points behind and one is 1 behind of TeamA so only one can catch"() {
        given:
          Table table = basicTable(3,2)
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcCatchingTeams(table.teams[0], midTwoTeams(table), midTeamOpponents(), 1)
        
        then:
          result.size() == 1
    }
    
    void "2 teams 2 games both 3 points off"() {
        given:
          Table table = basicTable(3)
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcCatchingTeams(table.teams[0], midTwoTeams(table), midTeamOpponents(2), 2)
        
        then:
          result.size() == 2
    }
    
    void "2 teams 2 games one 4 points off, one 1 point off"() {
        given:
          Table table = basicTable(4,3)
    
        when:
          List<Team> result = NearbyTeamsCalculator.calcCatchingTeams(table.teams[0], midTwoTeams(table), midTeamOpponents(2), 2)
    
        then:
          result.size() == 2
    }
    
    void "2 teams 2 games one 6 points off, one 7 points off"() {
        given:
          Table table = basicTable(7,1)
          
        when:
          List<Team> result = NearbyTeamsCalculator.calcCatchingTeams(table.teams[0], midTwoTeams(table), midTeamOpponents(2), 2)
    
        then:
          result.size() == 1
    }
    
    void "3 teams 3 games 1 team 6 off, 2 teams 4 off"() {
        given:
          Table table = basicTable(6,2,2)
          List<LeaguePostion> teamsPlayingEachOther = [table.teams[1], table.teams[2], table.teams[3]]
          Map<String, ArrayList<String>> teamToOpponents = [(TEAM_B): [TEAM_C, TEAM_D], (TEAM_D): [TEAM_B, TEAM_C], (TEAM_C): [TEAM_B, TEAM_D]]
    
        when:
          List<Team> result = NearbyTeamsCalculator.calcCatchingTeams(table.teams[0], teamsPlayingEachOther, teamToOpponents, 2)
    
        then:
          result.size() == 2
    }
    
    void "All three teams can beat top of table, but a points must be perfectly distributed"() {
        given:
          Table table = basicTable(9,6,4,1)
          table.addMatchesToTable([new Match(TEAM_E, TEAM_F, 0, 0)])
        
          List<LeaguePostion> teamsPlayingEachOther = [table.teams[1], table.teams[2], table.teams[3], table.teams[4]]
          Map<String, ArrayList<String>> teamToOpponents =
                  [(TEAM_B): [TEAM_E, TEAM_E, TEAM_C],
                    (TEAM_C): [TEAM_B, TEAM_D, TEAM_D],
                    (TEAM_D): [TEAM_C, TEAM_C, TEAM_A],
                    (TEAM_E): [TEAM_B, TEAM_B, TEAM_A]]
        
        when:
          List<Team> result = NearbyTeamsCalculator.calcCatchingTeams(table.teams[0], teamsPlayingEachOther, teamToOpponents, 3)
        
        then:
          result.size() == 3
    }
    
//    void "Difficult catching"() {
//        given:
//          Map teamToOpponents = ["Aston Villa FC":["AFC Bournemouth","Everton FC","Crystal Palace FC"],"Liverpool FC":["Southampton FC","Arsenal FC","Burnley FC"],"Tottenham Hotspur FC":["Manchester City FC","Newcastle United FC","Arsenal FC"],"Wolverhampton Wanderers FC":["Manchester United FC","Burnley FC","Everton FC"],"Manchester City FC":["Tottenham Hotspur FC","AFC Bournemouth","Brighton & Hove Albion FC"],"Southampton FC":["Liverpool FC","Brighton & Hove Albion FC","Manchester United FC"],"West Ham United FC":["Brighton & Hove Albion FC","Watford FC","Norwich City FC"],"Chelsea FC":["Leicester City FC","Norwich City FC","Sheffield United FC"],"Leicester City FC":["Chelsea FC","Sheffield United FC","AFC Bournemouth"],"Manchester United FC":["Wolverhampton Wanderers FC","Crystal Palace FC","Southampton FC"],"AFC Bournemouth":["Aston Villa FC","Manchester City FC","Leicester City FC"],"Burnley FC":["Arsenal FC","Wolverhampton Wanderers FC","Liverpool FC"],"Sheffield United FC":["Crystal Palace FC","Leicester City FC","Chelsea FC"],"Norwich City FC":["Newcastle United FC","Chelsea FC","West Ham United FC"],"Everton FC":["Watford FC","Aston Villa FC","Wolverhampton Wanderers FC"],"Watford FC":["Everton FC","West Ham United FC","Newcastle United FC"],"Crystal Palace FC":["Sheffield United FC","Manchester United FC","Aston Villa FC"],"Arsenal FC":["Burnley FC","Liverpool FC","Tottenham Hotspur FC"],"Newcastle United FC":["Norwich City FC","Tottenham Hotspur FC","Watford FC"],"Brighton & Hove Albion FC":["West Ham United FC","Southampton FC","Manchester City FC"]]
//          List<LeaguePostion> teamsPlayingEachOther = ["AFC Bournemouth":1,"Sheffield United FC":1,"Crystal Palace FC":1,"Everton FC":1,"Leicester City FC":1,"Wolverhampton Wanderers FC":1,"Newcastle United FC":0,"Aston Villa FC":0,"Norwich City FC":0,"Southampton FC":0,"Watford FC":0,"Chelsea FC":0,"West Ham United FC":0].collect{ k, v -> LeaguePostion team = new LeaguePostion(k); team.drawn = v; return team}
//          LeaguePostion mainTeam = new LeaguePostion("Arsenal FC")
//          mainTeam.drawn = 3
//
//        when:
//          List<Team> result = NearbyTeamsCalculator.calcCatchingTeams(mainTeam, teamsPlayingEachOther, teamToOpponents, 2)
//
//        then:
//          result.size() == 12
//    }
//
    private static Table basicTable(int...teamPoints) {
        Table table = Table.createTable([new Match(TEAM_A, TEAM_B, 0, 0), new Match(TEAM_C, TEAM_D, 0, 0)], 1)
        for(int i=0;i<teamPoints.length;i++) {
            table.teams[i].drawn += teamPoints[i]
        }
        return table
    }
    
    private static List midTwoTeams(Table table) {
        return [table.teams[1], table.teams[2]]
    }
    
    private static Map<String, ArrayList<String>> midTeamOpponents(int times = 1) {
        return [(TEAM_B): [TEAM_C]*times, (TEAM_C): [TEAM_B]*times]
    }
}