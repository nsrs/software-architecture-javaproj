package wwwordz.shared;

import java.io.Serializable;

/**
 * "A row in the ranking table. 
 *  Basically all the data of a player except the password."<br><br>
 *  
 *  Said data is kept in fields, namely the nick, the current points and 
 *  the accumulated points from successive rounds of a player.
 * 
 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/shared/Rank.html
 */
public class Rank implements Serializable {
	private static final long serialVersionUID = 1L;
	String nick;
	int points;
	int accumulated;
	
	/**
	 * Empty constructor.
	 * 
	 */
	public Rank() {}

	/**
	 * Creates an instance of this Class,
	 * 		initializing every field with the given
	 * 		data of a particular player.
	 * 
	 * @param nick - the nickname of the player
	 * @param points - the points of the player
	 * @param accumulated - the accumulated points of the player
	 */
	public Rank(String nick, int points, int accumulated) {
		this.nick 		 = nick;
		this.points 	 = points;
		this.accumulated = accumulated;
	}

	/**
	 * Retrieves the player's nickname stored in this Rank instance.
	 * 
	 * @return a String nickname
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Changes this object's stored nickname.
	 * 
	 * @param nick - the new nickname to store
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * Retrieves the player's current points,
	 * 		stored in this Rank instance.
	 * 
	 * @return the number of points of the player this
	 * 		instance refers to
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Changes the value of <i>points</i> this object stores.
	 * 
	 * @param points - the number of points to store from now on
	 */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * Retrieves the player's accumulated points,
	 * 		stored in this Rank instance.
	 * 
	 * @return the number of accumulated points over
	 * 		successive rounds of the player this
	 * 		instance refers to
	 * 		
	 */
	public int getAccumulated() {
		return accumulated;
	}

	/**
	 * Changes the value of <i>accumulated</i> points this object stores.
	 * 
	 * @param points - the number of accumulated points to store from now on
	 */
	public void setAccumulated(int accumulated) {
		this.accumulated = accumulated;
	}
	
}
