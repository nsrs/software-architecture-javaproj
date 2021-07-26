package wwwordz.game;

import java.io.Serializable;

/**
 * "A player of WWWordz, including authentication data (name and password), 
 * current round and accumulated points."
 * 
 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/game/Player.html
 *
 */
public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
	String nick;
	String password;
	int points;
	int accumulated;
	
	/**
	 * Creates an instance of Player with the given nickname and password.
	 * 
	 * @param nick - the player's nickname
	 * @param password - the player's password
	 */
	public Player(String nick, String password) {
		this.nick = nick;
		this.password = password;
	}

	/**
	 * Retrieves the player's nickname.
	 * 
	 * @return the player's nickname as a String
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Changes the player's nickname to the given String.
	 * 
	 * @param nick - the player's new nickname
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * Retrieves the player's password.
	 * 
	 * @return the player's password as a String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Changes the player's password to the given String.
	 * 
	 * @param password - the player's new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Retrieves the player's current points.
	 * 
	 * @return the player's current points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Adds the given points to the player, and also
	 * 		updates its accumulated accordingly.
	 * 
	 * @param points - the number of points to add
	 */
	public void setPoints(int points) {
		this.points = points;
		this.accumulated += points;
	}

	/**
	 * Retrieves the player's total points,
	 * 		accumulated from previous rounds.
	 * 
	 * @return the player's accumulated points
	 */
	public int getAccumulated() {
		return accumulated;
	}

	/**
	 * Changes the accumulated points of a player
	 * 		to the given number.
	 * 
	 * @param accumulated - the new number of accumulated points
	 */
	public void setAccumulated(int accumulated) {
		this.accumulated = accumulated;
	}
}
