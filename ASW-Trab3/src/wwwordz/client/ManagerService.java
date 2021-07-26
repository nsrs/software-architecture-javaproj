package wwwordz.client;

import java.util.List;
import wwwordz.shared.Puzzle;
import wwwordz.shared.Rank;
import wwwordz.shared.WWWordzException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * This is the synchronous service' interface of the class Manager.
 * It is used for the server side implementation of the service.
 * 
 * Check class Manager for details on the methods.
 * 
 * @see wwwordz.game.Manager
 * @see wwwordz.server.ManagerServiceImpl
 */
@RemoteServiceRelativePath("manager")
public interface ManagerService extends RemoteService {
	long timeToNextPlay();

	long register(String nick, String password) throws WWWordzException;

	Puzzle getPuzzle() throws WWWordzException;

	void setPoints(String nick, int points) throws WWWordzException;

	List<Rank> getRanking() throws WWWordzException;
}