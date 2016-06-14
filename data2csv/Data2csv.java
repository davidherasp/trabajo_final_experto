import com.opencsv.CSVWriter;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.api.LoadPolicy;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.match.Match;
import com.robrua.orianna.type.core.match.MatchTeam;
import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.match.ParticipantStats;
import com.robrua.orianna.type.core.matchlist.MatchReference;
import com.robrua.orianna.type.core.summoner.Summoner;
import com.robrua.orianna.type.exception.APIException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author david
 */
public class Data2csv {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException
    {
        RiotAPI.setAPIKey(args[0]);
        RiotAPI.setRegion(Region.valueOf(args[1]));
        //RiotAPI.printCalls(true);
        RiotAPI.setLoadPolicy(LoadPolicy.LAZY);
        
        Summoner summoner = RiotAPI.getSummonerByName(args[2]);
        new File("csv_data/summoners/" + summoner.getName() + "/games").mkdirs();
        new File("csv_data/summoners/" + summoner.getName() + "/stats").mkdirs();
        CSVWriter writerGames = new CSVWriter(new FileWriter("csv_data/summoners/" + summoner.getName() + "/games/games.csv"));
        
        List<MatchReference> matchList = summoner.getMatchList();
        System.out.println("Total ranked games: " + matchList.size());

        for(MatchReference m: matchList)
        {    
            try{
                Match match = m.getMatch();
                List<MatchTeam> teams = match.getTeams();
                String[] entries = (m.getID() + "," + m.getPlatformID() + ","
                                 + m.getQueueType() + "," + m.getSeason() + "," 
                                 + m.getTimestamp() + "," + match.getDuration() + ","
                                 + (teams.get(0).getWinner() ? teams.get(0).getSide() : teams.get(1).getSide()) //Returns the side of the winner of the game
                                 ).split(",");
                writerGames.writeNext(entries);
                
                List<Participant> participants = match.getParticipants();
                CSVWriter writerStats = new CSVWriter(new FileWriter("csv_data/summoners/" + summoner.getName() + "/stats/" + m.getID() + "_stats.csv"));
                for(Participant p: participants)
                {
                    try{
                        ParticipantStats stats = p.getStats();

                        String[] entries2 = (m.getID() + "," + p.getSummonerID() + ","
                                          +  p.getTeam() + "," + stats.getAssists() + ","
                                          +  stats.getDeaths() + "," + stats.getDoubleKills() + ","
                                          +  stats.getFirstBloodAssist() + "," + stats.getFirstBloodKill() + ","
                                          +  stats.getFirstInhibitorAssist() + "," + stats.getFirstInhibitorKill() + ","
                                          +  stats.getFirstTowerAssist() + "," + stats.getFirstTowerKill() + ","
                                          +  stats.getGoldEarned() + "," + stats.getGoldSpent() + ","
                                          +  stats.getInhibitorKills() + "," 
                                          +  stats.getItem0ID() + "," + stats.getItem1ID() + "," + stats.getItem2ID() + "," + stats.getItem3ID() + "," + stats.getItem4ID() + "," + stats.getItem5ID()+ "," + stats.getItem6ID() + ","
                                          +  stats.getKillingSprees() + "," + stats.getKills() + ","
                                          +  stats.getLargestCriticalStrike() + "," + stats.getLargestKillingSpree() + "," + stats.getLargestMultiKill() + ","
                                          +  stats.getLevel() + ","
                                          +  stats.getMagicDamageDealt() + "," + stats.getMagicDamageDealtToChampions() + "," + stats.getMagicDamageTaken() + ","
                                          +  stats.getMinionsKilled() + "," + stats.getNeutralMinionsKilled() + "," + stats.getNeutralMinionsKilledEnemyJungle() + "," + stats.getNeutralMinionsKilledTeamJungle() + ","
                                          +  stats.getPentaKills() + "," 
                                          +  stats.getPhysicalDamageDealt() + "," + stats.getPhysicalDamageDealtToChampions() + "," + stats.getPhysicalDamageTaken() + ","
                                          +  stats.getQuadraKills() + "," + stats.getSightWardsBought() + ","
                                          +  stats.getTeamObjectives() + ","
                                          +  stats.getTotalDamageDealt() + "," + stats.getTotalDamageDealtToChampions() + "," + stats.getTotalDamageTaken() + ","
                                          +  stats.getTotalHealing() + "," + stats.getTotalTimeCrowdControlDealt() + ","
                                          +  stats.getTotalUnitsHealed() + "," + stats.getTowerKills() + ","
                                          +  stats.getTripleKills() + ","
                                          +  stats.getTrueDamageDealt() + "," + stats.getTrueDamageDealtToChampions() + "," + stats.getTrueDamageTaken() + ","
                                          +  stats.getUnrealKills() + "," + stats.getVisionWardsBought() + "," 
                                          +  stats.getWardsKilled() + "," + stats.getWardsPlaced() + ","
                                          +  stats.getWinner()
                                          ).split(",");
                        writerStats.writeNext(entries2);
                    }catch (APIException ex)
                    {
                        System.out.println("Stats CSV APIException: " + ex.getMessage());
                    }
                }
                writerStats.close();
            }catch (APIException ex)
            {
                System.out.println("Games CSV APIException: " + ex.getMessage());
            }
        }
        
        writerGames.close();
    }   
}
//        int won = 0;
//        Side side = null;

//        for(MatchReference m: matchList)
//        {
//            Match match = m.getMatch();
//            List<Participant> participants = match.getParticipants();
//            participants.
//            for(Participant p: participants)
//            {
//                if (summoner == p.getSummoner())
//                {
//                    side = p.getTeam();
//                }
//            }
//            List<MatchTeam> teams = match.getTeams();
//            for(MatchTeam team: teams)
//            {
//                if(team.getWinner())
//                {
//                    if(side == team.getSide())
//                    {
//                        won++;
//                    }
//                }
//            }
//        }
