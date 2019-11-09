package dulcinea

import spock.lang.Specification

class FootballMatchesParserTest extends Specification {
    
    
    void "Parse matches"() {
        given:
          String matchJson = getClass().getResource('/matches.json').readLines().join()
        
        when:
          List<Match> matches = FootballMatchesParser.parse(matchJson)
        
        then:
          matches.size() == 380
          matches[0].homeTeam == "LFC"
          matches[0].awayTeam == "NCFC"
          matches[0].homeTeamScore == 4
          matches[0].awayTeamScore == 1
  
          matches[200].homeTeam == "BFC"
          matches[200].awayTeam == "AVFC"
          matches[200].homeTeamScore == null
          matches[200].awayTeamScore == null
    }
}