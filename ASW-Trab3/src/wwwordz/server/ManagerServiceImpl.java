package wwwordz.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import wwwordz.client.ManagerService;
import wwwordz.game.Manager;
import wwwordz.game.Round;
import wwwordz.shared.Configs;
import wwwordz.shared.Puzzle;
import wwwordz.shared.Rank;
import wwwordz.shared.WWWordzException;

/**
 * The server's implementation of the Manager service implements
 * 		the synchronous interface of the class.
 * 
 * Initially, the server sets a Round's duration with the
 * 		default values in Configs class.
 * 
 * The methods of this class run in the same fashion: 
 * 		get Manager's single instance and call the equivalent method.
 *
 * @see wwwordz.game.Manager
 */
@SuppressWarnings("serial")
public class ManagerServiceImpl extends RemoteServiceServlet implements ManagerService {
	static {
		Round.setJoinStageDuration(Configs.getJoinStageDuration());
		Round.setPlayStageDuration(Configs.getPlayStageDuration());
		Round.setReportStageDuration(Configs.getReportStageDuration());
		Round.setRankingStageDuration(Configs.getRankingStageDuration());
	}

	/**
	 * Retrieves the waiting time for the next PLAY stage
	 * 
	 * @return the waiting time in milliseconds
	 * 		for the next available PLAY stage
	 */
	public long timeToNextPlay() {
		return Manager.getInstance().timeToNextPlay();
	}

	/**
	 * Attempts to register the player in the current round
	 * 
	 * @param nick - the player's nickname
	 * @param password - the player's password
	 * 
	 * @return the remaining time in milliseconds for the next PLAY stage
	 * 
	 * @throws WWWordzException - if player does not exist,
	 * 					or when a player tries to register after stage JOIN
	 */
	public long register(String nick, String password) throws WWWordzException {
		return Manager.getInstance().register(nick, password);
	}

	/**
	 * Retrieves the current round's puzzle
	 * 
	 * @return the Puzzle instance generated for this round
	 * 
	 * @throws WWWordzException - if the method is not called on stage PLAY
	 */
	public Puzzle getPuzzle() throws WWWordzException {
		return Manager.getInstance().getPuzzle();
	}

	/**
	 * Attempts to set a player's points for the current round
	 * 
	 * @param nick - the player's nickname
	 * @param points - the number of points to assign to the player
	 * 
	 * @throws WWWordzException - if player is not on the current round,
	 * 						or if the method is not called on stage REPORT
	 */
	public void setPoints(String nick, int points) throws WWWordzException {
		Manager.getInstance().setPoints(nick, points);
	}

	/**
	 * Attempts to retrieve a list of type Rank,
	 * 	which is sorted by the amount of points of each player 
	 * 	in the current round
	 * 
	 * @return a List<Rank> that contains every player of the current round,
	 * 			sorted by the amount of points each obtained in it
	 * 
	 * @throws WWWordzException - if the method is not called on stage RANKING
	 */
	public List<Rank> getRanking() throws WWWordzException {
		return Manager.getInstance().getRanking();
	}
	
	
}
