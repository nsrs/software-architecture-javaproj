package wwwordz.game;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import wwwordz.client.ManagerService;
import wwwordz.shared.Puzzle;
import wwwordz.shared.Rank;
import wwwordz.shared.WWWordzException;

/**
 * 	"This class is a singleton 
 * 		and acts as a facade for other classes in this package. 
 * 	Methods in this class are delegated in instances of these classes."
 * 
 *  @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/game/Manager.html
 *  
 */
public class Manager implements ManagerService {
	static final ScheduledExecutorService worker = Executors
												   .newScheduledThreadPool(2);
	private static Manager manager = null;
	Round round;
	
	/**
	 * 	Creates an initial round instance,
	 * 		and proceeds to schedule a period task that will
	 * 		create a new instance whenever the current round finishes.
	 * 
	 *  @see https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ScheduledExecutorService.html#scheduleAtFixedRate(java.lang.Runnable,%20long,%20long,%20java.util.concurrent.TimeUnit)
	 */
	private Manager() {
		round = new Round();
		worker.scheduleAtFixedRate(
			new Runnable() { public void run() { round = new Round(); }},
			Round.getRoundDuration(), 
			Round.getRoundDuration(), 
			TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Accesses the singleton's sole instance,
	 * 	calling the constructor if there is none yet
	 * 
	 * @return a single, permanent instance of Manager
	 */
	public static Manager getInstance() {
		if (manager == null) {
			manager = new Manager();
		}
		return manager;
	}
	
	/**
	 * Retrieves the waiting time for the next PLAY stage
	 * 
	 * @return the waiting time in milliseconds
	 * 		for the next available PLAY stage
	 */
	public long timeToNextPlay() {
		return round.getTimetoNextPlay();
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
		long time;
		try {
			time = round.register(nick, password);
			return time;
		} catch (WWWordzException exception) {
			throw exception;
		}
	}
	
	/**
	 * Retrieves the current round's puzzle
	 * 
	 * @return the Puzzle instance generated for this round
	 * 
	 * @throws WWWordzException - if the method is not called on stage PLAY
	 */
	public Puzzle getPuzzle() throws WWWordzException {
		Puzzle puzzle;
		try {
			puzzle = round.getPuzzle();
			return puzzle;
		} catch (WWWordzException exception) {
			throw exception;
		}
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
		try {
			round.setPoints(nick,  points);
		} catch (WWWordzException exception) {
			throw exception;
		}
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
		List<Rank> list;
		try {
			list = round.getRanking();
			return list;
		} catch (WWWordzException exception) {
			throw exception;
		}
	}
	
}
