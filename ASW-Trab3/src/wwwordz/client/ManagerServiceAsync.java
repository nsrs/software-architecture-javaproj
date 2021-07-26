package wwwordz.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import wwwordz.shared.Puzzle;
import wwwordz.shared.Rank;

/**
 * This is the asynchronous service used by the client to
 * 		access class Manager's methods.
 * The methods themselves cannot return values, so the
 * 		callback, passed as a parameter, provides
 * 		a way to retrieve the returned value.
 * 
 * Check class Manager for details on the methods.
 * 
 * @see wwwordz.game.Manager
 * @see wwwordz.client.ManagerService
 */
public interface ManagerServiceAsync {
	void timeToNextPlay(AsyncCallback<Long> callback);

	void register(String nick, String password, AsyncCallback<Long> callback);

	void getPuzzle(AsyncCallback<Puzzle> callback);

	void setPoints(String nick, int points, AsyncCallback<Void> callback);

	void getRanking(AsyncCallback<List<Rank>> callback);
}
