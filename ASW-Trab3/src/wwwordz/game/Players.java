package wwwordz.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import wwwordz.shared.WWWordzException;

/**
 * "Persistent collection of players indexed by nick.<br><br>
 *
 * Each player has nick, password, points and accumulated points. 
 * Data is persisted using serialization 
 * and backup each time a new user is created or points are changed.<br><br>
 *
 * This class is a singleton."<br><br>
 * 
 * The default name of the .ser file is "players".
 *
 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/game/Players.html
 */
public class Players implements Serializable {
	private static final long   serialVersionUID = 1L;
	private static Players      players          = null;
	private static Map<String, Player> playersMap;
	transient private static File home = null,
								  file = null;
	
	/**
	 * Simply initializes an empty collection of players
	 * 	at the creation of the only instance of this class.
	 * 
	 */
	private Players() {
			playersMap = new HashMap<String, Player>();
	}
	
	/**
	 * Retrieves the location of the file
	 * 	where the serialized content is kept,
	 *  store in the field <i>home</i> of this class
	 * 
	 * @return an "abstract" file with the location of the .ser file
	 */
	public static File getHome() {
		return home;
	}
	
	/**
	 * Changes the location of the .ser file,
	 * 		while also moving the file to the
	 * 		new home location.<br>
	 * The new home location must be valid,
	 * 		so that the current .ser file
	 * 		can be moved to it.
	 * 
	 * @param home - an "abstract" file with the new location of the .ser file
	 */
	public static void setHome(File home) {
		try {
			Files.move(file.toPath(), home.toPath());
			Players.home = home;
		} catch (IOException cause) {
			cause.printStackTrace();
		}
	}
	
	/**
	 * Accesses the internal, single instance of this class.
	 * Calling this method for the first time will create the
	 * 		instance, initialize the default location of the .ser file,
	 *  	and possibly restoring an existing version of it.
	 * 
	 * @return the single instance of Players class
	 */
	public static Players getInstance() {
		if (players == null) {
			home = new File(System.getProperty("user.dir"));
			file = new File(home + "/players.ser");
			if (file.canRead()) {
				players = restore();
			} else {
				players = new Players();
			}
		}
		return players;
	}
	
	/**
	 * Checks if the given credentials of a player are correct
	 * 		i.e. if the player exists.<br>
	 * If the player's nickname is correct but the password
	 * 		does not match the existing one, it will "pass"
	 * 		the verification.
	 * If the nickname cannot be found, the player will be
	 * 		created in the database.<br>
	 * Calls for a backup on success.
	 * 
	 * @param nick - the player's nickname, as a String
	 * @param password - the player's password, as a String
	 * 
	 * @return a boolean value to determine if the player's
	 * 		credentials match an existing player; also returns
	 * 		true if the non-existing player was created in the process
	 */
	public boolean verify(String nick, String password) {
		Player player = getPlayer(nick);
		if (player == null) {
			playersMap.put(nick, new Player(nick, password));
			backup();
			return true;
		} else if (password.equals(player.getPassword())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Resets the player's points back to zero.<br>
	 * Calls for a backup on success.
	 * 
	 * @param nick - the player's nickname
	 * 
	 * @throws WWWordzException - if the given nickname 
	 * 							  does not belong to any stored Player instance
	 */
	public void resetPoints(String nick) throws WWWordzException {
		Player player = getPlayer(nick);
		if (player == null) {
			throw new WWWordzException("Player not found\n");
		} else {
			player.setPoints(0);
			backup();
		}
		
	}
	
	/**
	 * Adds a number of points to a player.<br>
	 * Calls for a backup on success.
	 * 
	 * @param nick - the player's nickname
	 * @param points - the number of points to be added
	 * 
	 * @throws WWWordzException - if the given nickname
	 * 							  does not belong to any stored Player instance
	 */
	public void addPoints(String nick, int points) throws WWWordzException {
		Player player = getPlayer(nick);
		if (player == null) {
			throw new WWWordzException("Player not found\n");
		} else {
			player.setPoints(points);
			backup();
		}
	}
	
	/**
	 * Retrieves a Player instance from the internal
	 * 		Collection <i>playersMap</i> through a given
	 * 		nickname.
	 * 
	 * @param nick - the player's nickname
	 * 
	 * @return a Player instance with said nickname,
	 * 		   or null if none is found
	 */
	public Player getPlayer(String nick) {
		return playersMap.get(nick);
	}
	
	/**
	 * Clears the internal Collection of Player instances.
	 * 
	 */
	public void cleanup() {
		if (playersMap != null) {
			playersMap.clear();
		}
	}
	
	/**
	 * Attempts to restore an existing instance of this class,
	 * 		serialized and stored in a .ser file at the <i>home</i>
	 * 		field location.
	 * 
	 * @return an instance of Players, previously serialized,
	 * 		   or null if the file cannot be found
	 */
	private static Players restore() {
		if (file.canRead()) {
			try (
			 FileInputStream stream = new FileInputStream(file);
			 ObjectInputStream deserializer = new ObjectInputStream(stream))
			{
				playersMap = new HashMap<String, Player>();
				return (Players) deserializer.readObject();
			} catch (IOException | ClassNotFoundException cause) {
				cause.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * Serializes this singleton's instance into a .ser file
	 * 		at the <i>home</i> field location.
	 * 
	 */
	private static void backup() {
		try (
		 FileOutputStream stream = new FileOutputStream(file);
		 ObjectOutputStream serializer = new ObjectOutputStream(stream)) {
			serializer.writeObject(players);
		} catch (IOException cause) {
			cause.printStackTrace();
		}
	}

}