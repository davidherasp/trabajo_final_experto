import com.opencsv.CSVWriter;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.api.LoadPolicy;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.common.Side;
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
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author david
 */

public class Data2csv 
{
    private static String[] headerGames = 
            ( "matchID" + "," + "platformID" + ","
            + "queueType" + "," + "season" + "," + "timestamp" + "," 
            + "duration" + "," + "winner(side)" + "," + "champ" + "," 
            + "lane" + "," + "role" + "," + "summoner1" + "," + "summoner2" + "," + "matchup1" + "," + "matchup2" + ","
            + "kills" + "," + "deaths" + ","
            + "assists" + "," + "minionsKilled" + "," + "goldEarned" + ","
 //           + "item0ID" + "," + "item1ID" + "," + "item2ID" + "," + "item3ID" + "," + "item4ID" + "," + "item5ID" + "," + "item6ID" + ","
            + "totalDmgDealtToChamps" + "," + "totalCCDealt" + "," 
            + "wardsPlaced" + "," 
            + "doubleKills" + "," + "tripleKills" + "," + "quadraKills" + "," +"pentaKills" + ","
            + "win"
            ).split(",");
    
//    private static String[] headerStats  = 
//            ( "matchID" + "," + "summonerID" + "," + "team" + "," + "assists" + "," + "deaths" + "," + "doubleKills" + "," 
//            + "firstBloodAssist" + "," + "firstBloodKill" + "," + "firstInhibitorAssist" + "," + "firstInhibitorKill" + "," 
//            + "firstTowerAssist" + "," + "firstTowerKill" + "," + "goldEarned" + "," + "goldSpent" + "," + "inhibitorKills" + "," 
//            + "item0ID" + "," + "item1ID" + "," + "item2ID" + "," + "item3ID" + "," + "item4ID" + "," + "item5ID" + "," + "item6ID" + "," 
//            + "killingSprees" + "," + "kills" + "," + "largestCriticalStrike" + "," + "largestKillingSpree" + "," + "largestMultiKill" + "," 
//            + "level" + "," + "magicDamageDealt" + "," + "magicDamageDealtToChampions" + "," + "magicDamageTaken" + "," + "minionsKilled" + "," 
//            + "neutralMinionsKilled" + "," + "neutralMinionsKilledEnemyJungle" + "," + "neutralMinionsKilledTeamJungle" + "," + "pentaKills" + "," 
//            + "physicalDamageDealt" + "," + "physicalDamageDealtToChampions" + "," + "physicalDamageTaken" + "," + "quadraKills" + "," 
//            + "sightWardsBought" + "," + "teamObjectives" + "," + "totalDamageDealt" + "," + "totalDamageDealtToChampions" + "," 
//            + "totalDamageTaken" + "," + "totalHealing" + "," + "totalTimeCrowdControlDealt" + "," + "totalUnitsHealed" + "," + "towerKills" + "," 
//            + "tripleKills" + "," + "trueDamageDealt" + "," + "trueDamageDealtToChampions" + "," + "trueDamageTaken" + "," + "unrealKills" + "," 
//            + "visionWardsBought" + "," + "wardsKilled" + "," + "wardsPlaced" + "," + "winner"
//            ).split(",");
    
    public static void main(String[] args) throws IOException, InterruptedException
    {
        long tStart = System.currentTimeMillis();
//    	Logger log = Logger.getLogger(Data2csv.class.getName());

        RiotAPI.setAPIKey(args[0]);
        RiotAPI.setRegion(Region.valueOf(args[1]));
        //RiotAPI.printCalls(true);
        RiotAPI.setLoadPolicy(LoadPolicy.LAZY);
        Summoner summoner = null;
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        
        try
        {
        	summoner = RiotAPI.getSummonerByName(args[2]);

        	new File("csv_data/summoners/" + summoner.getName() + "/games").mkdirs();
	        new File("csv_data/summoners/" + summoner.getName() + "/stats").mkdirs();
	        
	        CSVWriter writerGames = new CSVWriter(new FileWriter("csv_data/summoners/" + summoner.getName() + "/games/games.csv"));
	        writerGames.writeNext(headerGames);
	        
	        List<MatchReference> matchList = summoner.getMatchList();
	        System.out.println("Total ranked games: " + matchList.size());
	        
	        int     count = 0;
	        long    kills = 0, deaths = 0, assists = 0, minionsKilled = 0,
	                item0ID = 0, item1ID = 0, item2ID = 0, item3ID = 0, item4ID = 0, item5ID = 0, item6ID = 0,
	                wardsPlaced = 0, doubleKills = 0, 
	                tripleKills = 0, quadraKills = 0, pentaKills = 0,
	                goldEarned = 0, ttlDmgChamps = 0, ttlCCDealt = 0;
	        boolean win = true;
	        String summoner1 = "", summoner2 = "", enemy1 = "", enemy2 = "";
	        Side side = null;

	        for(MatchReference m: matchList)
	        {    
	            try/*(CSVWriter writerStats = new CSVWriter(new FileWriter("csv_data/summoners/" + summoner.getName() + "/stats/" + m.getID() + "_stats.csv")))*/
	            {
	                Match match = m.getMatch();
	                List<MatchTeam> teams = match.getTeams();

	                List<Participant> participants = match.getParticipants();     

	                //writerStats.writeNext(headerStats);

	                for(Participant p: participants)
	                {
	                    try{
	                        ParticipantStats stats = p.getStats();
	//                        String[] entries2 = (m.getID() + "," + p.getSummonerID() + ","
	//                                          +  p.getTeam() + "," + stats.getAssists() + ","
	//                                          +  stats.getDeaths() + "," + stats.getDoubleKills() + ","
	//                                          +  stats.getFirstBloodAssist() + "," + stats.getFirstBloodKill() + ","
	//                                          +  stats.getFirstInhibitorAssist() + "," + stats.getFirstInhibitorKill() + ","
	//                                          +  stats.getFirstTowerAssist() + "," + stats.getFirstTowerKill() + ","
	//                                          +  stats.getGoldEarned() + "," + stats.getGoldSpent() + ","
	//                                          +  stats.getInhibitorKills() + "," 
	//                                          +  stats.getItem0ID() + "," + stats.getItem1ID() + "," + stats.getItem2ID() + "," + stats.getItem3ID() + "," + stats.getItem4ID() + "," + stats.getItem5ID()+ "," + stats.getItem6ID() + ","
	//                                          +  stats.getKillingSprees() + "," + stats.getKills() + ","
	//                                          +  stats.getLargestCriticalStrike() + "," + stats.getLargestKillingSpree() + "," + stats.getLargestMultiKill() + ","
	//                                          +  stats.getLevel() + ","
	//                                          +  stats.getMagicDamageDealt() + "," + stats.getMagicDamageDealtToChampions() + "," + stats.getMagicDamageTaken() + ","
	//                                          +  stats.getMinionsKilled() + "," + stats.getNeutralMinionsKilled() + "," + stats.getNeutralMinionsKilledEnemyJungle() + "," + stats.getNeutralMinionsKilledTeamJungle() + ","
	//                                          +  stats.getPentaKills() + "," 
	//                                          +  stats.getPhysicalDamageDealt() + "," + stats.getPhysicalDamageDealtToChampions() + "," + stats.getPhysicalDamageTaken() + ","
	//                                          +  stats.getQuadraKills() + "," + stats.getSightWardsBought() + ","
	//                                          +  stats.getTeamObjectives() + ","
	//                                          +  stats.getTotalDamageDealt() + "," + stats.getTotalDamageDealtToChampions() + "," + stats.getTotalDamageTaken() + ","
	//                                          +  stats.getTotalHealing() + "," + stats.getTotalTimeCrowdControlDealt() + ","
	//                                          +  stats.getTotalUnitsHealed() + "," + stats.getTowerKills() + ","
	//                                          +  stats.getTripleKills() + ","
	//                                          +  stats.getTrueDamageDealt() + "," + stats.getTrueDamageDealtToChampions() + "," + stats.getTrueDamageTaken() + ","
	//                                          +  stats.getUnrealKills() + "," + stats.getVisionWardsBought() + "," 
	//                                          +  stats.getWardsKilled() + "," + stats.getWardsPlaced() + ","
	//                                          +  stats.getWinner()
	//                                          ).split(",");
	//                        writerStats.writeNext(entries2);
	                        if (p.getSummonerID() == summoner.getID())
	                        {  
	                            kills = stats.getKills(); deaths = stats.getDeaths(); assists = stats.getAssists();
	                            minionsKilled = stats.getMinionsKilled(); goldEarned = stats.getGoldEarned();
	                            summoner1 = p.getSummonerSpell1().getName(); summoner2 = p.getSummonerSpell2().getName();
	                            item0ID = stats.getItem0ID(); item1ID = stats.getItem1ID(); item2ID = stats.getItem2ID();
	                            item3ID = stats.getItem3ID(); item4ID = stats.getItem4ID(); item5ID = stats.getItem5ID();
	                            item6ID = stats.getItem6ID(); ttlDmgChamps = stats.getTotalDamageDealtToChampions();
	                            ttlCCDealt = stats.getTotalTimeCrowdControlDealt();
	                            wardsPlaced = stats.getWardsPlaced(); doubleKills = stats.getDoubleKills();
	                            tripleKills = stats.getTripleKills(); quadraKills = stats.getQuadraKills();
	                            pentaKills = stats.getPentaKills(); win = stats.getWinner();
	                            side = p.getTeam();
	                        }

	                    }catch (APIException  ex)
	                    {
	                    	//log.log( Level.SEVERE, ex.toString(), ex );
	                        //System.out.println("Stats CSV APIException: " + ex.getMessage());
	                    }
	                }

	                for(Participant p: participants)
	                {
	                    try
	                    {
	                        if (p.getTeam() != side && p.getTimeline().getLane() == m.getLane())
	                        {
	                                if (enemy1.isEmpty())
	                                {
	                                        enemy1 = p.getChampion().getName();
	                                }else
	                                {
	                                        enemy2 = p.getChampion().getName();
	                                }
	                        }
	                    }catch(APIException ex)
	                    {

	                    }	
	                }

	                String[] entries = 
	                        ( m.getID() + "," + m.getPlatformID() + ","
	                        + m.getQueueType() + "," + m.getSeason() + "," 
	                        + formatter.format(m.getTimestamp()) + "," + match.getDuration() + ","
	                        + (teams.get(0).getWinner() ? teams.get(0).getSide() : teams.get(1).getSide()) + "," //Returns the side of the team winner of the game
	                        + m.getChampion().getName() + "," + m.getLane() + "," + m.getRole() + "," 
	                        + summoner1 + "," + summoner2 + "," + enemy1 + "," + enemy2 + ","
	                        + kills + "," + deaths + "," + assists + ","
	                        + minionsKilled + "," + goldEarned + ","
//	                        + item0ID + "," + item1ID + "," + item2ID + "," + item3ID + ","
//	                        + item4ID + "," + item5ID + "," + item6ID + ","
	                        + ttlDmgChamps + "," + ttlCCDealt + ","
	                        + wardsPlaced + "," + doubleKills + "," + tripleKills + ","
	                        + quadraKills + "," + pentaKills + "," + win
	                        ).split(",");
	                writerGames.writeNext(entries);
	                enemy1 = ""; enemy2 = "";
	//                writerStats.close();
	            }catch (APIException | IllegalArgumentException ex)
	            {
	            	//log.log( Level.SEVERE, ex.toString(), ex );
	                //System.out.println("Games CSV APIException: " + ex.getMessage());
	            }
	            count++;
	            System.out.print("Progress: " + (count*100/matchList.size()) + "%" + " - game " + count + "/" + matchList.size() + "\r");
	        }
	        
	        long tEnd = System.currentTimeMillis();
	        long tDelta = tEnd - tStart;
	        double elapsedSeconds = tDelta / 1000.0;

	        long second = (tDelta / 1000) % 60;
			long minute = (tDelta / (1000 * 60)) % 60;
			long hour =   (tDelta / (1000 * 60 * 60)) % 24;
	        
	        writerGames.close();
	        System.out.println("Progress completed in " + hour + "h " + minute + "m " + second + "s\r");
        }catch(APIException ex)
        {
        	System.out.println("ERROR: There are no results for the summoner name " + args[2]);
        }
    }   
}
