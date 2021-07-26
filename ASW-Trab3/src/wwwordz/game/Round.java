

package wwwordz.game;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import wwwordz.shared.*;
import wwwordz.puzzle.*;

/**
 * "A round has 4 sequential stages<br>
 *	<ul>
 *   <li> <b>join</b> - client join the round</li>
 *   <li> <b>play</b> - client retrieves puzzle and player solve puzzle</li>
 *   <li> <b>report</b> - clients report points back to server</li>
 *   <li> <b>ranking</b> - clients ask for rankings</li>
 *  </ul>
 * Each stage has a specific duration and the round method 
 * 		can only be executed within a limited time frame. 
 * The duration of each stage can be checked 
 * 		or changed with static setters and getters.<br>
 * The following method should be executed in the associated stages.<br>
 *	<ol>
 *   <li> register() - join</li>
 *   <li> getPuzzle() - play</li>
 *   <li> setPoints() - register</li>
 *   <li> getRanking() - ranking</li>
 *  </ol>
 *  When executed outside their stages 
 *  	these methods raise a WWWordzException."<br><br>
 * The start times of each stage, including the end of the round,
 * 		are being stored in static fields, assuming that there will
 * 		only be one active round at any moment.<br>
 * Each instance also contains its own puzzle instance,
 * 		a Map of players indexed by their nickname
 *  	and a List of Rank objects for the final ranking stage.
 * 
 *  @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/game/Round.html
 */
public class Round {
	static Date   	   		   join;
	static Date   	   		   play;
	static Date   	   		   report;
	static Date   	   		   ranking;
	static Date   	   		   end;
	private Puzzle 		  	   puzzle;
	private Map<String,Player> roundPlayers;
	private List<Rank> 		   rankList;
	
	/**
	 * Creates a Round instance, setting every
	 * 		Date field accordingly to the current time
	 *  	while also preparing the round's private fields
	 *  	for future usage.
	 * 
	 */
	public Round() {
		
		long joinDuration    = getJoinStageDuration(),
			 playDuration    = getPlayStageDuration(),
			 reportDuration  = getReportStageDuration(),
			 rankingDuration = getRankingStageDuration();
		
		setJoinStageDuration(joinDuration);
		setPlayStageDuration(playDuration);
		setReportStageDuration(reportDuration);
		setRankingStageDuration(rankingDuration);
		
		
		roundPlayers = new HashMap<String,Player>();
		puzzle 		 = new Generator().generate();
		rankList 	 = null;
	}
	
	/**
	 * Calculates the time remaining of stage JOIN
	 * 		in a difference with the next stage's start time.
	 * 
	 * @return the remaining duration of stage JOIN,
	 * 		in milliseconds
	 */
	public static long getJoinStageDuration() {
        return play.getTime() - join.getTime();
    }

	/**
	 * Sets the duration of the stage JOIN by changing
	 * 		the next stage's start time.
	 * 
	 * @param joinStageDuration - the new duration set for stage JOIN,
	 * 		in milliseconds
	 */
    public static void setJoinStageDuration(long joinStageDuration) {
    	join = new Date();
        play = new Date(join.getTime() + joinStageDuration);
    }

    /**
	 * Calculates the time remaining of stage PLAY
	 * 		in a difference with the next stage's start time.
	 * 
	 * @return the remaining duration of stage PLAY,
	 * 		in milliseconds
	 */
    public static long getPlayStageDuration() {
        return report.getTime() - play.getTime();
    }

    /**
	 * Sets the duration of the stage PLAY by changing
	 * 		the next stage's start time.
	 * 
	 * @param joinStageDuration - the new duration set for stage PLAY,
	 * 		in milliseconds
	 */
    public static void setPlayStageDuration(long playStageDuration) {
        report = new Date(play.getTime() + playStageDuration);
    }

    /**
	 * Calculates the time remaining of stage REPORT
	 * 		in a difference with the next stage's start time.
	 * 
	 * @return the remaining duration of stage REPORT,
	 * 		in milliseconds
	 */
    public static long getReportStageDuration() {
        return ranking.getTime() - report.getTime();
    }

    /**
	 * Sets the duration of the stage REPORT by changing
	 * 		the next stage's start time.
	 * 
	 * @param joinStageDuration - the new duration set for stage REPORT,
	 * 		in milliseconds
	 */
    public static void setReportStageDuration(long reportStageDuration) {
        ranking = new Date(report.getTime() + reportStageDuration);
    }

    /**
	 * Calculates the time remaining of stage RANKING
	 * 		in a difference with the end of the round time.
	 * 
	 * @return the remaining duration of stage RANKING,
	 * 		in milliseconds
	 */
    public static long getRankingStageDuration() {
        return end.getTime() - ranking.getTime();
    }

    /**
	 * Sets the duration of the stage RANKING by changing
	 * 		the next stage's start time.
	 * 
	 * @param joinStageDuration - the new duration set for stage RANKING,
	 * 		in milliseconds
	 */
    public static void setRankingStageDuration(long rankingStageDuration) {
        end = new Date(ranking.getTime() + rankingStageDuration);
    }
	
    /**
     * Calculates the duration of a round
     * 		by summing the durations of all its four stages.
     * 
     * @return the round's total duration, in milliseconds
     */
	public static long getRoundDuration() {
		return getJoinStageDuration()
			  +getPlayStageDuration()
			  +getReportStageDuration()
			  +getRankingStageDuration();
	}
	
	/**
	 * Calculates the waiting time for the next available PLAY stage
	 * 		whether it is in this round,
	 * 	    or the next if this one's JOIN stage is over.
	 * 
	 * @return the time, in milliseconds for the next PLAY stage of a round 
	 * 		  available for registration
	 */
	public long getTimetoNextPlay() {
		Date now = new Date();
		
		if(now.before(play)) {
			return play.getTime() - now.getTime();
		} else {
			return end.getTime() - now.getTime() + getJoinStageDuration();
		}
	}
	
	/**
	 * Joins a player to this round's collection of players, 
	 * 		but only if the player's credentials prove its existence
	 * 		and if the round is currently at stage JOIN.
	 * 
	 * @param nick - the player's nickname
	 * @param password - the player's password
	 * 
	 * @return the time left for the beginning of stage PLAY, in milliseconds
	 * 
	 * @throws WWWordzException - if the round is not on stage JOIN
	 * 				or if the player's credentials are not found
	 */
	public long register(String nick, String password) throws WWWordzException {
		if ((!Stage.onStage(Stage.JOIN) )) {
			throw new WWWordzException("Cannot join ongoing game\n");
		} else if (Players.getInstance().verify(nick, password) == false){
			throw new WWWordzException("Wrong password for this player\n");
		} else {
			Player player = Players.getInstance().getPlayer(nick);
			if (!roundPlayers.containsValue(player)) {
				roundPlayers.put(nick, player);
			}
			return getTimetoNextPlay();
		}
	}
	
	/**
	 * Retrieves this round's Puzzle instance if its current stage is PLAY.
	 * 
	 * @return this round's Puzzle instance
	 * 
	 * @throws WWWordzException - if the round is not on stage PLAY
	 */
	public Puzzle getPuzzle() throws WWWordzException {
		if (!Stage.onStage(Stage.PLAY)) {
			throw new WWWordzException("Cannot play right now\n");
		} else {
			return puzzle;
		}
	}
	
	/**
	 * Sets this round's points of a player. 
	 * The player must be registered
	 * and the round's stage must be REPORT for this method to succeed.
	 * 
	 * @param nick - the player's nickname
	 * @param points - the number of points to assign to the player
	 * 
	 * @throws WWWordzException - if player is not on this round,
	 * 						or if the method is not called on stage REPORT
	 */
	public void setPoints(String nick, int points) throws WWWordzException {
		Player player = roundPlayers.get(nick);
		if (player == null) {
			throw new WWWordzException("Player not found\n");
		} else if (!Stage.onStage(Stage.REPORT)) {
			throw new WWWordzException("Cannot report points right now\n");
		} else {
			player.setPoints(points);
		}
	}
	
	/**
	 * Creates the ranking list the first time is called
	 * 		during the stage RANKING. It will also sort it
	 * 		by the players' points.
	 * Afterwards, the field <i>rankList</i>, which will
	 * 		store said list, will be returned.
	 * Any other call to this method will simply return this field.
	 * 
	 * @return a list of this round's players, 
	 * 		sorted by the points each obtained
	 * 
	 * @throws WWWordzException - if the round is not on stage RANKING
	 */
	public List<Rank> getRanking() throws WWWordzException {
		if (!Stage.onStage(Stage.RANKING)) {
			throw new WWWordzException("Cannot display ranking right now\n");
		} else {
			if (rankList == null) {
				List<Player> playerList = new ArrayList<>(roundPlayers.values());
				rankList 				= new ArrayList<Rank>();
				Collections.sort(playerList,
							 	 (e1, e2) -> sortRanks(e1, e2));
			
				for(Player player: playerList) {
					rankList.add(new Rank(player.getNick(),
							 		  	  player.getPoints(),
							 		  	  player.getAccumulated()));
				}
			}
			return rankList;
		}
	}
	
	/**
	 * Sorts two players by the points they obtain in this round.
	 * Two players with the same points are sorted by their accumulated
	 * 		points, and the last tie break sorts them lexicographically.
	 * 
	 * @param p1 - a Player instance
	 * @param right - a different Player instance
	 * @return an integer, used for Collections.sort() call in
	 * 			getRanking() method of this class
	 */
	public int sortRanks(Player p1, Player p2) {
		if (p1.getPoints() ==  p2.getPoints()) {
			return p2.getAccumulated() - p1.getAccumulated();
		} else {
			return p2.getPoints() - p1.getPoints();
		}
	}
	
	/**
	 * An enumerator used to distinguish each stage of a round.
	 * 
	 * @see https://coderanch.com/t/656661/java/Error-enum-defines-method-implicitly
	 */
	static enum Stage {
		JOIN,
		PLAY,
		REPORT,
		RANKING;

		/**
		 * A simple method to centralize the task of knowing
		 * 		if the current round is on a given stage (or not).
		 * 
		 * @param stage - the Stage enum constant to be evaluated
		 * 
		 * @return a boolean value to represent if the current round is
		 * 		at the given stage
		 */
		static boolean onStage(Stage stage) {
			Date now = new Date();
			switch(stage) {
				case JOIN:    return now.before(play) 	 && now.after(join);
				case PLAY:    return now.before(report)  && now.after(play);
				case REPORT:  return now.before(ranking) && now.after(report);
				case RANKING: return now.before(end) 	 && now.after(ranking);
				default:	  return false;
			}
		}
	}
}
