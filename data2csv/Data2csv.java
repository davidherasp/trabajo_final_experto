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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 */

public class Data2csv 
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
    	Logger log = Logger.getLogger(Data2csv.class.getName());

        RiotAPI.setAPIKey(args[0]);
        RiotAPI.setRegion(Region.valueOf(args[1]));
        //RiotAPI.printCalls(true);
        RiotAPI.setLoadPolicy(LoadPolicy.LAZY);
        
        Summoner summoner = RiotAPI.getSummonerByName(args[2]);
        new File("csv_data/summoners/" + summoner.getName() + "/games").mkdirs();
        new File("csv_data/summoners/" + summoner.getName() + "/stats").mkdirs();
        CSVWriter writerGames = new CSVWriter(new FileWriter("csv_data/summoners/" + summoner.getName() + "/games/games.csv"));
        String[] headerGames  = ("match ID" + "," + "platform ID" + "," + "queue type" + "," + "season" + "," + "time stamp" + "," + "duration" + "," + "winner(side)" + "," + "champ ID" + "," + "lane" + "," + "role" + "," + "win").split(",");
        writerGames.writeNext(headerGames);
        
        List<MatchReference> matchList = summoner.getMatchList();
        System.out.println("Total ranked games: " + matchList.size());

        int count = 0;

        for(MatchReference m: matchList)
        {    
            try{
                Match match = m.getMatch();
                List<MatchTeam> teams = match.getTeams();
                String[] entries = (m.getID() + "," + m.getPlatformID() + ","
                                 + m.getQueueType() + "," + m.getSeason() + "," 
                                 + m.getTimestamp() + "," + match.getDuration() + ","
                                 + (teams.get(0).getWinner() ? teams.get(0).getSide() : teams.get(1).getSide()) + "," //Returns the side of the team winner of the game
                                 + m.getChampionID() + "," + m.getLane() + "," 
                                 + m.getRole() + "," + "null").split(",");

                List<Participant> participants = match.getParticipants();
                CSVWriter writerStats = new CSVWriter(new FileWriter("csv_data/summoners/" + summoner.getName() + "/stats/" + m.getID() + "_stats.csv"));
                String[] headerStats  = ("matchID" + "," + "summonerID" + "," + "team" + "," + "assists" + "," + "deaths" + "," + "doubleKills" + "," 
                	                  + "firstBloodAssist" + "," + "firstBloodKill" + "," + "firstInhibitorAssist" + "," + "firstInhibitorKill" + "," 
                	                  + "firstTowerAssist" + "," + "firstTowerKill" + "," + "goldEarned" + "," + "goldSpent" + "," + "inhibitorKills" + "," 
                	                  + "item0ID" + "," + "item1ID" + "," + "item2ID" + "," + "item3ID" + "," + "item4ID" + "," + "item5ID" + "," + "item6ID" + "," 
                	                  + "killingSprees" + "," + "kills" + "," + "largestCriticalStrike" + "," + "largestKillingSpree" + "," + "largestMultiKill" + "," 
                	                  + "level" + "," + "magicDamageDealt" + "," + "magicDamageDealtToChampions" + "," + "magicDamageTaken" + "," + "minionsKilled" + "," 
                	                  + "neutralMinionsKilled" + "," + "neutralMinionsKilledEnemyJungle" + "," + "neutralMinionsKilledTeamJungle" + "," + "pentaKills" + "," 
                	                  + "physicalDamageDealt" + "," + "physicalDamageDealtToChampions" + "," + "physicalDamageTaken" + "," + "quadraKills" + "," 
                	                  + "sightWardsBought" + "," + "teamObjectives" + "," + "totalDamageDealt" + "," + "totalDamageDealtToChampions" + "," 
                	                  + "totalDamageTaken" + "," + "totalHealing" + "," + "totalTimeCrowdControlDealt" + "," + "totalUnitsHealed" + "," + "towerKills" + "," 
                	                  + "tripleKills" + "," + "trueDamageDealt" + "," + "trueDamageDealtToChampions" + "," + "trueDamageTaken" + "," + "unrealKills" + "," 
                	                  + "visionWardsBought" + "," + "wardsKilled" + "," + "wardsPlaced" + "," + "winner").split(",");
                writerStats.writeNext(headerStats);

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
                        if (p.getSummonerID() == summoner.getID())
                        {
                            if (stats.getWinner())
                                entries[entries.length - 1] = "true";
                            else
                                entries[entries.length - 1] = "false";
                        }
                    }catch (APIException ex)
                    {
                    	//log.log( Level.SEVERE, ex.toString(), ex );
                        //System.out.println("Stats CSV APIException: " + ex.getMessage());
                    }
                }
                writerGames.writeNext(entries);
                writerStats.close();
            }catch (APIException|IllegalArgumentException ex)
            {
            	//log.log( Level.SEVERE, ex.toString(), ex );
                //System.out.println("Games CSV APIException: " + ex.getMessage());
            }
            count++;
            System.out.print("Progress: " + (count*100/matchList.size()) + "%" + " - game " + count + "/" + matchList.size() + "\r");
        }
        
        writerGames.close();
        System.out.println("Progress complete!                                                                           \r");
    }   
}
