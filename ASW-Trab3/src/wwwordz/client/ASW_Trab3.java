package wwwordz.client;

import wwwordz.shared.Configs;
import wwwordz.shared.Puzzle;
import wwwordz.shared.Puzzle.Solution;
import wwwordz.shared.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This is the entry point class of the clientside,
 * 		which overrides the onModuleLoad method
 * 		and "presents" the web app for the client.
 * <br>
 * The page's main layout used a DeckPanel to
 * 		show/hide the containers of the game's
 * 		stages, and the internal clock, which
 * 		is updated systematically through a
 * 		call to Manager's asynchronous
 * 		service getTimeToNextPlay method,
 * 		regulates when the front page should
 * 		be updated.
 * <br>
 * The class contains a lot of information,
 * 		since it handles the logic and the
 * 		graphical dynamics of the game,
 * 		but it is divided in documented sections.
 * <br><br>
 * The documented sections are:<br>
 * 		-> PREPARING - preparing the widgets and all unchangeable functionality<br>
 * 		-> UPDATING  - methods called by the game's clock<br>
 * 		-> DRAWING   - methods used to switch the DeckPanel's presented widget<br>
 * 		-> UTILITIES - methods used by the asynchronous callbacks<br>
 * 		-> RPC 		 - methods that use Manager's asynchronous service<br>
 * 	    -> OBJECTS	 - custom class members used throughout this class<br>
 * 
 */
public class ASW_Trab3 implements EntryPoint {
	private static final String SERVER_ERROR    = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	
	/**
	 * Periodic updates to the internal game clock have this frequency
	 * (given in milliseconds).
	 */
	private static final int    CLOCK_INTERVAL  = 100;
	
	/**
	 * Expected size of the puzzle grid.
	 */
	private static final int    MATRIX_SIZE	    = 4;
	
	/**
	 * Asynchronous service of Manager's implementation
	 */
	private final ManagerServiceAsync 	managerService;
	
	/**
	 * The client's timer to keep track of the game's stages 
	 */
	private Timer						gameClock;
	
	/**
	 * An enumerator instance to most easily identify
	 * 		the current stage of the game.<br>
	 * @see ASW_Trab3.Panels
	 */
	private Panels						gameStage;
	
	/**
	 * The player's name
	 */
	private String						playerName;
	
	/**
	 * Player's current round's points
	 */
	private int							playerPoints;
	
	/**
	 * Unsorted list of words found this round by the player
	 */
	private List<String> 				wordsFound;


	//	The commonly referred widgets' declarations
	//are listed below
	
	/**
	 * The core widget of the website, whose direct
	 * 		children are vertical panels that
	 * 		represent each one of the game's stages
	 */
	private final DeckPanel 			deck;
  
    /**
	 * An isolated widget, kept from the default "GreetingService"
	 * 		to print useful error messages.
	 * <br>
	 * It is referred to as "informer", but, unlike the rest
	 * 		of the major panels, this is not contained in
	 * 		the core DeckPanel of this class, since it
	 * 		doesn't represent any stage of the game.
	 */
	private final DialogBox 			informerDialogBox;
	
	/**
	 * Panel to sort out the informer's content
	 */
    private final VerticalPanel			informerPanel;
    
    /**
     * Informer's title
     */
	private final Label					informerLabel;
	
	/**
	 * Informer's message
	 */
	private final HTML  				informerHtml;
	
	/**
	 * Informer's close button, necessary because the
	 * 		dialog box, when shown, prevents input 
	 * 		on other widgets.
	 */
	private final Button				informerButton;
	
	/**
	 * LOGIN<br>
	 * The initial "page" of the website.
	 * <br>
	 * It contains a simple form for the user to
	 * 		register/login, not only as an
	 * 		authentication method, but also to
	 * 		register for the next round.
	 * <br>
	 * It is referred to as "login".
	 */
	private final VerticalPanel 		loginPanel;
	
	/**
	 * Login form's header, informing the user when to register
	 * 		or the remaining time for the next round.
	 */
	private final Label 				loginClockLabel;
	
	/**
	 * Takes user's nickname (any or no name accepted)
	 */
	private final TextBox   			formName;
	
	/**
	 * Takes user's password (any or no password accepted)
	 */
	private final PasswordTextBox		formPassword;
	
	/**
	 * Login form's submit button
	 */
	private final Button				loginButton;
	
	/**
	 * WAITING<br>
	 * An optional window appears between the register and
	 * 		the play stages. 
	 * <br>
	 * Though not necessary, it is not harmless in any way, 
	 * 		and attempts to avoid multiple registers, 
	 * 		since only one player is able to play from a
	 * 		single instance of the webapp (i.e. browser tab).
	 * <br>
	 * It is referred to as "waiting".
	 */
	private final VerticalPanel 		waitingPanel;
	
	/**
	 * Waiting message for the player before actually playing.
	 */
	private final Label 				waitingLabel;
	
	/**
	 * PUZZLE<br>
	 * The play stage panel, with the puzzle, a timer
	 * 		and a few more information for the player.
	 * <br>
	 * It is referred to as "puzzle".
	 */
	private final VerticalPanel			puzzlePanel;
	
	/**
	 * The time left for the play stage is given in this label,
	 * 		above the actual puzzle.
	 */
	private final Label 				puzzleHeader;
	
	/**
	 * Custom widget for Puzzle, whose class is a member
	 * 		of this class.
	 * 
	 * @see ASW_Trab3.Panels
	 */
	private final PuzzleGrid			puzzleGrid;
	
	/**
	 * Shows the player's name during the puzzle.
	 */
	private final Label					puzzlePlayerName;
	
	/**
	 * Shows the player's current points obtained so far
	 * 		on the current puzzle.
	 */
	private final Label					puzzlePoints;
	
	/**
	 * Shows the number of words the player found so far
	 * 		on the current puzzle.
	 */
	private final Label					puzzleWordsFound;

	/**
	 * REPORT<br>
	 * The report panel presents the player's points
	 * 		and a list of words found by the player
	 * 		on the current round.
	 * <br>
	 * It is referred to as "report".
	 */
	private final VerticalPanel 		reportPanel;

	/**
	 * Shows the player's points on this round,
	 * 		during the report stage.
	 */
	private final Label					reportLabel;
	
	/**
	 * Presents every word the player found on this
	 * 		round, during the report stage.
	 * <br><br>
	 * The words are presented in a sort of grid,
	 * 		with 2 rows and 4 columns. Each column
	 * 		divides the words found as follows:<br><br>
	 * 		
	 * 		-> 3   letters<br>
	 * 		-> 4-5 letters<br>
	 * 		-> 6-7 letters<br>
	 * 		-> 8+  letters<br>
	 * 		<br>
	 * The first row contains the headers of the list,
	 * 		whereas the second row are single HTML
	 * 		objects with the sorted lists.
	 * <br><br>
	 * This field is initialized to point out the
	 * 		meaning of the arguments it receives.
	 */
	private final Grid					reportWordsGrid = new Grid(2, 4);
	

	/**
	 * RANKING<br>
	 * The ranking table of the current round,
	 * 		listing the players by the points
	 * 		obtained in this round.
	 * <br>
	 * It is referred to as "ranking".
	 * 
	 * @see RankingRow
	 */
	private final VerticalPanel 		rankingPanel;

	
	
	
	
/*
 * ###################################
 * 
 * 				PREPARING
 * 
 * ###################################
 */
	
	/**
	 * The constructor is in charge of assigning values to
	 * 		all of the fields, all previously declared.
	 * <br>
	 * It also sets the current stage of the game to the
	 * 		initial LOGIN stage (equivalent to JOIN stage),
	 * 		and creating the game's clock for the client.
	 */
	ASW_Trab3() {
		managerService		= GWT.create(ManagerService.class);
		
		deck				= new DeckPanel();
		
		formName 			= new TextBox();
		formPassword 		= new PasswordTextBox();
		
		loginButton 		= new Button("Login");
		informerButton 		= new Button("Close");
		
		informerLabel 		= new Label();
		loginClockLabel 	= new Label();
		waitingLabel		= new Label();
		puzzleHeader		= new Label();
		puzzlePlayerName	= new Label();
		puzzlePoints		= new Label();
		puzzleWordsFound	= new Label();
		reportLabel			= new Label();
		
		informerHtml 		= new HTML();

		informerDialogBox 	= new DialogBox();
		
		puzzleGrid			= new PuzzleGrid();

		informerPanel 		= new VerticalPanel();
		loginPanel			= new VerticalPanel();
		waitingPanel		= new VerticalPanel();
		puzzlePanel			= new VerticalPanel();
		reportPanel			= new VerticalPanel();
		rankingPanel		= new VerticalPanel();
		
		wordsFound  = new ArrayList<String>();
		gameStage	= Panels.LOGIN;
		
		gameClock 	= new Timer() {
			public void run() {
				managerService.timeToNextPlay(new AsyncCallback<Long>() {

					@Override
					public void onFailure(Throwable caught) {
						informError("Remote Procedure Call - Failure",
								SERVER_ERROR + "<br>Error message: " 
											 + caught.getMessage());
					}

					@Override
					public void onSuccess(Long time) {
						switch(gameStage) {
							case LOGIN:   {updateLoginTime(time); break;}
							case WAITING: {updateTimeToPlay(time); break;}
							case PUZZLE:  {updatePuzzleTime(time); break;}
							case REPORT:  {updateTimeToRank(time); break;}
							case RANKING: {updateTimeToEnd(time); break;}
						}
					}
				});
			}
		};
	}


	/**
	 * Runs the initial configurations for the web app,
	 * 		namely the widget's configurations and
	 * 		the creation of handlers.
	 */
	@Override
	public void onModuleLoad() {
		setupInformer();
		setupLogin();
		setupWaiting();
		setupPuzzle();
		setupReport();
		
		deck.add(loginPanel);
		deck.add(waitingPanel);
		deck.add(puzzlePanel);
		deck.add(reportPanel);
		deck.add(rankingPanel);
		
		deck.addStyleName("deck");
		
		RootPanel.get().add(deck);

		informerButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				informerDialogBox.hide();
				RootPanel.get().removeStyleName("standby");
				loginButton.setEnabled(true);
			}
		});

		loginButton.addStyleName("button-form");
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				register();
			}
		});

		showLoginPanel();

		gameClock.scheduleRepeating(CLOCK_INTERVAL);
	}
	
	/**
	 * Configures the informer panel, based off from
	 * 		the default GreetingService's implementation.
	 */
	private void setupInformer() {
		RootPanel.get("informerContainer")
		 .add(informerLabel);

		informerDialogBox.setText("Remote Procedure Call");
		informerDialogBox.setAnimationEnabled(true);
		
		informerButton.getElement().setId("closeButton");
		
		informerPanel.addStyleName("dialogVPanel");
		informerPanel.add(informerHtml);
		informerPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		informerPanel.add(informerButton);
		informerDialogBox.setWidget(informerPanel);
	}
	
	/**
	 * Configures the login panel, which contains a simple
	 * 		form with two fields and one submit button,
	 * 		along with a header which shows the clock's
	 * 		time to the user.
	 */
	private void setupLogin() {
		HorizontalPanel loginRow1 = new HorizontalPanel(),
						loginRow2 = new HorizontalPanel(),
						loginRow3 = new HorizontalPanel();

		loginRow1.add(new Label("Register below "));
		loginRow1.add(loginClockLabel);
		
		loginRow2.add(new Label("Username: "));
		loginRow2.add(formName);
		
		loginRow3.add(new Label("Password: "));
		loginRow3.add(formPassword);
		
		loginButton.addStyleName("button-form");
		
		loginPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		
		loginPanel.add(loginRow1);
		loginPanel.add(loginRow2);
		loginPanel.add(loginRow3);
		loginPanel.add(loginButton);
	}
	
	/**
	 * Configures the waiting panel, which simply shows how
	 * 		much time the player must wait to receive the
	 * 		puzzle (i.e. for the PLAY stage to begin).
	 */
	private void setupWaiting() {
		waitingPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		waitingPanel.add(new Label("Waiting for game to start..."));
		waitingPanel.add(waitingLabel);
	}
	
	/**
	 * Configures the puzzle panel in a vertical fashion, 
	 * 		providing, from top to bottom, a clock,
	 * 		the puzzle grid, the name of the player,
	 * 		the points it obtained so far and the
	 * 		number of words the player found in the
	 * 		current round.	
	 */
	private void setupPuzzle() {
		puzzlePanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		puzzleHeader.addStyleName("label-strong");
		puzzleGrid.addStyleName("puzzle-grid");
		puzzlePanel.add(puzzleHeader);
		puzzlePanel.add(puzzleGrid);
		puzzlePanel.add(puzzlePlayerName);
		puzzlePanel.add(puzzlePoints);
		puzzlePanel.add(puzzleWordsFound);
	}
	
	/**
	 * Configures the report panel by first setting the headers of the
	 * 		table of found words, after which it will create HTML
	 * 		widgets to posteriorly show the actual words.
	 * <br>
	 * The player's points are shown before the table of words found.
	 */
	private void setupReport() {
		reportPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		reportWordsGrid.setWidget(0, 0, new Label("3 letters"));
		reportWordsGrid.setWidget(0, 1, new Label("4-5 letters"));
		reportWordsGrid.setWidget(0, 2, new Label("6-7 letters"));
		reportWordsGrid.setWidget(0, 3, new Label("8+ letters"));

		for(int i = 0; i < 4; i++) {
			reportWordsGrid.setWidget(1, i, new HTML());
			((HTML) (reportWordsGrid.getWidget(1, i))).addStyleName("ranking-cell");
			((Label) (reportWordsGrid.getWidget(0, i))).addStyleName("ranking-cell");
		}

		reportPanel.add(reportLabel);
		reportPanel.add(reportWordsGrid);
	}
	
	
	
	
	
	
	/*
	 * ###################################
	 * 
	 * 				UPDATING
	 * 
	 * ###################################
	 */
	
	
	/**
	 * Obtains the time left for the next PLAY stage,
	 * 		in order to update the login panel's
	 * 		timer.
	 * 
	 * @param time - milliseconds left for the next Puzzle instance
	 */
	private void updateLoginTime(Long time) {
		long timeinseconds = time/1000,
			 remainingsecs = (time-Configs.getJoinStageDuration())
				 			 / 1000;
		if (time > Configs.getJoinStageDuration()) {
			loginClockLabel.setText("(available in " 
								+ remainingsecs
								+ " seconds)");
		}
		else {
			loginClockLabel.setText("(register within the next " 
								+ timeinseconds 
								+ " seconds)");
		}
	}
	
	/**
	 * Checks the game's clock while on the waiting panel,
	 * 		updating the time left and even switching
	 * 		to the next panel when appropriate.
	 * 
	 * @param time - milliseconds left for the next Puzzle instance 
	 */
	private void updateTimeToPlay(Long time) {
		if (time <= 0
		||  time >  Configs.getReportStageDuration() 
				  + Configs.getRankingStageDuration()
				  + Configs.getJoinStageDuration()) 
		{
			gameStage = Panels.PUZZLE;
			showPuzzlePanel();
		} else {
			waitingLabel.setText("(" + time/1000 + " seconds remaining)");
		}
	}
	
	/**
	 * Checks the game's clock while on the puzzle panel,
	 * 		updating its timer and switching to
	 * 		the next panel when the PLAY stage ends.
	 * 
	 * @param time - milliseconds left for the next Puzzle instance
	 */
	private void updatePuzzleTime(Long time) {
		long remainingtime = time - (Configs.getReportStageDuration() 
				  				   + Configs.getRankingStageDuration()
				  				   + Configs.getJoinStageDuration());

		if (remainingtime <= 0) {
			gameStage = Panels.REPORT;
			showReportPanel();
			
		} else {
			puzzleHeader.setText("Seconds left: " + remainingtime/1000);
		}
	}

	/**
	 * Checks the game's clock during the report panel,
	 * 		switching to the next panel when the
	 * 		REPORT stage ends.
	 * 
	 * @param time - milliseconds left for the next Puzzle instance
	 */
	private void updateTimeToRank(Long time) {
		if (time <=   Configs.getRankingStageDuration()
					+ Configs.getJoinStageDuration()) 
		{
			gameStage = Panels.RANKING;
			ranking();
		}
	}

	/**
	 * Checks the time during the ranking panel,
	 * 		switching back to the login panel when the
	 * 		round ends, since no players are kept to
	 * 		play from one round to another.
	 * 
	 * @param time - milliseconds left for the next Puzzle instance
	 */
	private void updateTimeToEnd(Long time) {
		if (time <= Configs.getJoinStageDuration()) {
			gameStage = Panels.LOGIN;
			showLoginPanel();
			rankingPanel.clear();
		}
	}



	
	
	
	
	
	/*
	 * ###################################
	 * 
	 * 				DRAWING
	 * 
	 * ###################################
	 */

	/**
	 * Draws the login panel on the core DeckPanel instance.
	 * 
	 */
	private void showLoginPanel() {
		deck.showWidget(Panels.getStageIndex(gameStage));
		formName.setFocus(true);
	}
	
	/**
	 * Retrieves the round's generated puzzle instance,
	 * 		while also reseting the puzzle and the
	 * 		labels of the puzzle panel with stats.
	 * It then draws the puzzle panel on the core 
	 * 		DeckPanel instance.
	 * 
	 */
	private void showPuzzlePanel() {
		play();
		playerPoints = 0;
		wordsFound.clear();
		updatePuzzleStats();
		deck.showWidget(Panels.getStageIndex(gameStage));
	}

	/**
	 * Reports the points obtained this round to the server
	 * 		and fills the table with found words just before
	 * 		showing the report panel.
	 */
	private void showReportPanel() {
		report();
		reportLabel.setText("Your score for this round is " + playerPoints);
		((HTML) (reportWordsGrid.getWidget(1, 0))).setHTML("");
		((HTML) (reportWordsGrid.getWidget(1, 1))).setHTML("");
		((HTML) (reportWordsGrid.getWidget(1, 2))).setHTML("");
		((HTML) (reportWordsGrid.getWidget(1, 3))).setHTML("");

		getWordsFound();
		deck.showWidget(Panels.getStageIndex(gameStage));
	}
	
	/**
	 * Though it is not a recommendable approach, 
	 * 		the ranking entries are created dynamically
	 * 		here, after the ranking list is obtained from
	 * 		the server.
	 * <br>
	 *
	 * @param rankingList - the ranking of this round
	 */
	private void showRankingPanel(List<Rank> rankingList) {
		String nick,
			   points,
			   accumulated;
		RankingRow header = new RankingRow();
		header.setFields("Nickname",
						 "Round Points",
						 "Accumulated Points");
		
		rankingPanel.add(header);

		if (! rankingList.isEmpty()) {
			for(wwwordz.shared.Rank rank: rankingList) {
				nick 		= rank.getNick();
				points 		= ((Integer) rank.getPoints()).toString();
				accumulated = ((Integer) rank.getAccumulated()).toString();
				
				RankingRow rankEntry = new RankingRow();
				rankEntry.setFields(nick, points, accumulated);
				rankingPanel.add(rankEntry);
			}
		}
		deck.showWidget(Panels.getStageIndex(gameStage));
	}



	
	
	
	
	
	
	/*
	 * ###################################
	 * 
	 * 				UTILITIES
	 * 
	 * ###################################
	 */

	/**
	 * Updates the labels with points and number of words found
	 * 		in the puzzle panel.
	 */
	private void updatePuzzleStats() {
		puzzlePoints.setText("Points: " + playerPoints);
		puzzleWordsFound.setText("Words found: " + wordsFound.size());
	}

	/**
	 * Provides the custom PuzzleGrid widget with the Puzzle instance
	 * 		obtained from the async callback, thus
	 * 		avoiding any lost references while
	 * 		passing the Puzzle instance to the
	 * 		enclosing class.
	 * 
	 * @param puzzle - the Puzzle instance generated for the current round
	 */
	private void setPuzzleGrid(Puzzle puzzle) {
		puzzleGrid.setPuzzle(puzzle);
	}

	/**
	 * Sorts the list of words found on the current round and
	 * 		fills the table of words during the report stage
	 * 		accordingly by only updating the content, instead
	 * 		of creating any new widgets.
	 */
	private void getWordsFound() {
		Collections.sort(wordsFound);
		HTML wordList;
		for(String word: wordsFound) {
			if (word.length() == 3) {
				wordList = ((HTML) (reportWordsGrid.getWidget(1, 0)));
			} else if (word.length() == 4
				    || word.length() == 5) {
				wordList = ((HTML) (reportWordsGrid.getWidget(1, 1)));
			} else if (word.length() == 6
					|| word.length() == 7) {
				wordList = ((HTML) (reportWordsGrid.getWidget(1, 2)));
			} else {
				wordList = ((HTML) (reportWordsGrid.getWidget(1, 3)));
			}
			wordList.setHTML(wordList.getHTML() + "<br>" + word);
		}
	}

	/**
	 * A synthetic call of the informer panel, which receives
	 * 		a title and a message to present.
	 * <br>
	 * When called, the informer must be closed, or else no
	 * 		other input will be received.
	 * 
	 * @param title - the informer's title, a
	 * 				short description of the error
	 * 
	 * @param message - the informer's error message
	 */
	private void informError(String title, String message) {
		informerDialogBox.setText(title);
		informerHtml.addStyleName("label-error");
		informerHtml.setHTML(message);
		informerDialogBox.center();
		RootPanel.get().addStyleName("standby");
	}


	
	
	
	
	
	/*
	 * ###################################
	 * 
	 * 				  RPC
	 * 
	 * ###################################
	 */

	/**
	 * Retrieves the submitted form's fields
	 * 		and passes it to the register method
	 * 		of Manager.
	 * <br>
	 * On success, it will effectively register the player 
	 * 		and update the game's stage to WAITING
	 * 		(equivalent to JOIN, but without the ability
	 * 		to login again).
	 */
	private void register() {
		final String name 	  = formName.getText(),
				 	 password = formPassword.getText();
	
		informerLabel.setText("");

		managerService.register(name, 
						 	    password, 
						 	    new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				informError("Remote Procedure Call - Failure",
						SERVER_ERROR + "<br>Error message: " 
									 + caught.getMessage());
				loginButton.setEnabled(false);
			}
			
			@Override
			public void onSuccess(Long time) {
				playerName = name;
				puzzlePlayerName.setText(playerName);
				//formPassword.setText("");
				gameStage = Panels.WAITING;
				deck.showWidget(1);
			}
		});
	}
	
	/**
	 * Returns the generated puzzle instance for the current
	 * 		round, on success, after calling the getPuzzle
	 * 		method of Manager.
	 */
	private void play() {
		managerService.getPuzzle(new AsyncCallback<Puzzle>() {

			@Override
			public void onFailure(Throwable caught) {
				informError("Remote Procedure Call - Failure",
						SERVER_ERROR + "<br>Error message: " 
									 + caught.getMessage());
			}

			@Override
			public void onSuccess(Puzzle puzzle) {
				setPuzzleGrid(puzzle);
			}
			
		});
	}
	
	/**
	 * Reports back to the server the player's points, along
	 * 		with its nickname, through Manager's setPoints
	 * 		method.
	 */
	private void report() {
		managerService.setPoints(playerName,
								 playerPoints,
								 new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				informError("Remote Procedure Call - Failure",
						SERVER_ERROR + "<br>Error message: " 
									 + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
			}
			
		});
	}
	
	/**
	 * Switches the current panel to the ranking panel,
	 * 		passing the list of Rank instances obtained
	 * 		from Manager's getRanking method.
	 */
	private void ranking() {
		managerService.getRanking(new AsyncCallback<List<Rank>>() {

			@Override
			public void onFailure(Throwable caught) {
				informError("Remote Procedure Call - Failure",
							SERVER_ERROR + "<br>Error message: " 
										 + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Rank> ranks) {
				showRankingPanel(new ArrayList<Rank>(ranks));
			}
			
		});
	}

	
	
	
	
	
	
	/*
	 * ###################################
	 * 
	 * 				 OBJECTS
	 * 
	 * ###################################
	 */
	
	
/**
 * An extension of the Grid widget to represent the Puzzle,
 * 		while also keeping useful information and methods
 * 		to use while playing.
 * <br><br>
 * It keeps the current word the player is forming,
 * 		a collection of the cells currently selected,
 * 		a reference to the last clicked cell,
 * 		and the puzzle instance generated for the
 * 		current round.
 * <br>
 * It also contains a timer, used to creating a
 * 		blinking effect when a word is either correct
 * 		or incorrect.
 *
 */
	public class PuzzleGrid extends Grid {
		private static final int	BLINK_DURATION = 500;
		StringBuilder 				word;
		List<Widget>  				selectedCells;
		wwwordz.shared.Table.Cell	lastClickedCell;
		Puzzle						puzzle;

		private final Timer blink;
		
		/**
		 * Creates an instance of this widget while
		 * 		setting the internal PuzzleCell
		 * 		instances it contains and providing
		 * 		them unique handlers.
		 * <br>
		 * This is only performed once, since the widget
		 * 		is fixed throughout the whole run.
		 */
		PuzzleGrid() {
			selectedCells = new ArrayList<Widget>();
			word		  = new StringBuilder();
			puzzle 		  = null;

			blink = new Timer() {
				@Override
				public void run() {
					resetSelection();
				}
			};
			
			this.resize(MATRIX_SIZE, MATRIX_SIZE);
			for(int row = 0; row < MATRIX_SIZE; row++) {
				for (int column = 0; column < MATRIX_SIZE; column++) {
					PuzzleCell cell = new PuzzleCell(row, column);
					cell.addStyleName("puzzle-cell");
					cell.addStyleName("puzzle-cell-ready");
					CellClickHandler handler = new CellClickHandler(cell);
					cell.addClickHandler(handler);
					cell.addDoubleClickHandler(handler);
					this.setWidget(row, column, cell);
				}
			}
		}
		
		/**
		 * Given a Puzzle instance, this method fills the
		 * 		PuzzleGrid with the letters of each and
		 * 		resets it to the default state.
		 * 
		 * @param tempPuzzle
		 */
		public void setPuzzle(Puzzle tempPuzzle) {
			lastClickedCell = null;
			puzzle 			= new Puzzle();
			puzzle.setTable(tempPuzzle.getTable());
			puzzle.setSolutions(tempPuzzle.getSolutions());
			
			wwwordz.shared.Table table = puzzle.getTable();
			
			for(int row = 0; row < MATRIX_SIZE; row++) {
				for (int column = 0; column < MATRIX_SIZE; column++) {
					((PuzzleCell) this.getWidget(row, column))
					.setText("" 
						   + table.getCell(row+1, column+1).getLetter());
				}
			}
			puzzleGrid.resetSelection();
		}
		
		/**
		 * Resets the widget's appearance and functionality
		 * 		to a "no cells selected" state.
		 */
		public void resetSelection() {
			while(! selectedCells.isEmpty()) {
				selectedCells.get(0).removeStyleName("puzzle-cell-correct");
				selectedCells.get(0).removeStyleName("puzzle-cell-wrong");
				selectedCells.get(0).removeStyleName("puzzle-cell-active");
				selectedCells.get(0).addStyleName("puzzle-cell-ready");
				((PuzzleCell) selectedCells.get(0)).reset();
				selectedCells.remove(0);
			}
			word.setLength(0);
			lastClickedCell = null;
		}

		/**
		 * Attempts to select a cell on the grid and build
		 * 		the current word,
		 * 		but it will fail if the cell is not adjacent
		 * 		to a previously selected one, thus returning
		 * 		a boolean to represent such.
		 * 
		 * @param row - the row of the PuzzleCell to be selected
		 * @param column - the column of the PuzzleCell to be selected
		 * @return a boolean value, signaling if the cell was selected
		 * 			or not
		 */
		public boolean selectCell(int row, int column) {
			wwwordz.shared.Table table = puzzle.getTable();

			if (lastClickedCell == null
			||	table.getNeighbors(lastClickedCell)
					 .contains(table.getCell(row+1, column+1)) ) 
			{
				selectedCells.add(this.getWidget(row, column));
				this.getWidget(row, column).removeStyleName("puzzle-cell-ready");
				this.getWidget(row, column).addStyleName("puzzle-cell-active");
				lastClickedCell = table.getCell(row+1, column+1);
				word.append(lastClickedCell.getLetter());
				return true;
			}

			return false;
		}
		
		/**
		 * Checks if the formed word is a valid solution for
		 * 		the Puzzle instance of the current round.
		 * 
		 * @return a boolean value, signaling if the selected word
		 * 			is a valid solution
		 */
		public boolean isWord() {
			List<Solution> puzzleSolutions = puzzle.getSolutions();
				for(Solution solution: puzzleSolutions) {
					if (solution.getWord().equals(word.toString())) {
						wordsFound.add(word.toString());
						playerPoints += solution.getPoints();
						return true;
					}
				}
			return false;
		}
		
		/**
		 * Checks if the formed word was already found on the current round
		 * 		by the player, thus avoiding duplicates.
		 * 
		 * @return a boolean value, signaling if the selected word
		 * 			was already found by the player on the current round
		 */
		private boolean containsWord() {
			String string = word.toString();
			for(String found: wordsFound) {
				if (string.equals(found)) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * "Flashes" the selected cells to signal the
		 * 		word was accepted and the player will
		 *		receive points for it.
		 */
		public void paintCellsCorrect() {
			if (! selectedCells.isEmpty()) {
				for(Widget cell: selectedCells) {
					cell.removeStyleName("puzzle-cell-active");
					cell.addStyleName("puzzle-cell-correct");
				}
			}
			blink.schedule(BLINK_DURATION);
		}
		
		/**
		 * "Flashes" the selected cells signaling the
		 * 		word is not valid.
		 */
		public void paintCellsWrong() {
			if (! selectedCells.isEmpty()) {
				for(Widget cell: selectedCells) {
					cell.removeStyleName("puzzle-cell-active");
					cell.addStyleName("puzzle-cell-wrong");
				}
			}
			blink.schedule(BLINK_DURATION);
		}
	}




	/**
	 * A PuzzleCell is an extension of a HTML, used to
	 * 		represent a cell on the PuzzleGrid instance.
	 * <br>
	 * This class has the functionality of a button,
	 * 		but because the default Button widget
	 * 		has its background-color style attribute
	 * 		already set, it could not be changed
	 * 		dynamically.
	 * <br>
	 * Each PuzzleCell widget has a handler assigned to it,
	 * 		a custom handler named CellClickHandler.
	 * 		The handler calls methods in this class
	 * 		accordingly.
	 * <br>
	 * Instances have a field, to work similarly to
	 * 		a toggle button. If the cell is selected,
	 * 		it should not trigger the click event. 
	 */
	public class PuzzleCell extends HTML {
		private final int 		cellRow,
		 				  	  	cellColumn;
		private boolean 		selected;

		/**
		 * Creates an instance of this class,
		 * 		assigning the row and column
		 * 		of the widget in the containing
		 * 		PuzzleGrid.
		 * 
		 * @param row - index of the row of the widget on PuzzleGrid
		 * @param column - index of the column of the widget on PuzzleGrid
		 */
		PuzzleCell(int row, int column) {
			cellRow    	= row;
			cellColumn 	= column;
			selected	= false;
		}

		/**
		 * Follows up a click event, effectively switching the
		 * 		state of this widget to selected if it is
		 * 		a valid move.
		 */
		public void doClick() {
			if (! puzzleGrid.selectCell(cellRow, cellColumn)) {
				informError("Illegal move",
							"A new selected cell must be adjacent " 
								   + "to the last selected cell!");
			} else {
				selected = true;
			}
		}

		/**
		 * Follows up a double click event, checking if the
		 * 		current word is valid.
		 */
		public void doDoubleClick() {
			if (! puzzleGrid.selectCell(cellRow, cellColumn)) {
				informError("Illegal move",
							"A new selected cell must be adjacent " 
									+ "to the last selected cell!");
				puzzleGrid.paintCellsWrong();
			} else if (puzzleGrid.containsWord()) {
				informError("Illegal word",
							"You've already found this word!");
				puzzleGrid.paintCellsWrong();
			} else if (! puzzleGrid.isWord()) {
				informError("Illegal word",
							"The formed word is not acceptable!");
				puzzleGrid.paintCellsWrong();
			} else {
				puzzleGrid.paintCellsCorrect();
				updatePuzzleStats();
			}
		}

		/**
		 * Checks if the last selected PuzzleCell instance
		 * 		on the PuzzleGrid is this one.
		 * 
		 * @return a boolean to check such value
		 */
		public boolean isLastSelectedCell() {
			return puzzleGrid.puzzle
					  		 .getTable()
					  		 .getCell(cellRow+1, 
					  				  cellColumn+1).equals(puzzleGrid
							  					          .lastClickedCell);
		}

		/**
		 * Checks if this PuzzleCell instance is currently selected
		 * 
		 * @return a boolean to check if the cell is currently selected
		 */
		public boolean isSelected() {
			return selected;
		}
		
		/**
		 * Resets the state of this cell back to not selected
		 */
		public void reset() {
			selected = false;
		}
	}



	/**
	 * A custom handler for click and double click events on
	 * 		a specific PuzzleCell instance.
	 * <br>
	 * A handler is created for each PuzzleCell, though a
	 * 		similar functionality could be created with a
	 * 		single handler for all cells, following an
	 * 		Observer pattern.
	 * <br>
	 * In order to avoid confusion between click and
	 * 		double click events, a brief timeout starts
	 * 		whenever a click event is listened, to
	 * 		cancel the actual click functionality
	 * 		if another follows on the same widget.
	 */
	public class CellClickHandler implements ClickHandler,
											 DoubleClickHandler {
		private static final int DBLCLK_TIMEOUT = 350;
		private final PuzzleCell puzzleCell;
		private final Timer		 doubleClickTimer;

		/**
		 * Creates an handler for a specific PuzzleCell instance.
		 * 
		 * @param cell - a reference to a PuzzleCell instance
		 */
		CellClickHandler(PuzzleCell cell) {
			puzzleCell = cell;
			doubleClickTimer = new Timer() {
				@Override
				public void run() {
					puzzleCell.doClick();
				}
			};
		}
		
		@Override
		public void onClick(ClickEvent event) {
			if (doubleClickTimer.isRunning()) {
				doubleClickTimer.cancel();
			} else {
				if (puzzleCell.isSelected()) {
					informError("Illegal move",
								"A cell can only be selected once per word!");
				} else {
					doubleClickTimer.schedule(DBLCLK_TIMEOUT);
				}
				
			}
		}
		
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			if (  puzzleCell.isSelected()
			&&  ! puzzleCell.isLastSelectedCell()) {
				informError("Illegal move",
							"The last letter of a word must be "
								+ "the last one selected or "
								+ "not selected at all!");
				puzzleGrid.paintCellsWrong();
			} else {
				puzzleCell.doDoubleClick();
			}
		}
	}
	
	
	
	
	/**
	 * A custom widget used on the ranking panel,
	 * 		with an horizontal layout of the
	 * 		3 fields that a Rank object contains.
	 */
	public class RankingRow extends HorizontalPanel {
		private HTML nick,
					 points,
					 accumulated;
		
		/**
		 * Creates an empty instance of this class,
		 * 		already styled.
		 */
		RankingRow() {
			nick 		= new HTML();
			points 		= new HTML();
			accumulated = new HTML();
			nick.addStyleName("ranking-cell");
			points.addStyleName("ranking-cell");
			accumulated.addStyleName("ranking-cell");
			this.add(nick);
			this.add(points);
			this.add(accumulated);
		}
		
		/**
		 * Sets the attributes of the Rank instance
		 * 		associated with this widget.
		 * 
		 * @param nickname    - player's nickname
		 * @param roundPoints - player's points
		 * @param accPoints   - player's accumulated points
		 */
		public void setFields(String nickname,
							  String roundPoints,
							  String accPoints) {
			nick.setText(nickname);
			points.setText(roundPoints);
			accumulated.setText(accPoints);
		}

	}
	
	/**
	 * This enumerator keeps track of which panel should
	 * 		be "drawn" on the web app (i.e. which widget
	 * 		should be shown by the core DeckPanel widget).
	 * <br><br>
	 * Each stage is equivalent to the stage of the round,
	 * 		except that LOGIN and WAITING are subdivisions
	 * 		of the JOIN stage, and PUZZLE is the equivalent
	 * 		of PLAY stage.
	 */
	static enum Panels {
		LOGIN,
		WAITING,
		PUZZLE,
		REPORT,
		RANKING;

		/**
		 * Returns the index of the panel on the core DeckPanel
		 * 		instance according to the given stage.
		 * 
		 * @param stage - a Panels enumerator constant
		 * @return the index of the given stage
		 */
		public static int getStageIndex(Panels stage) {
			switch (stage) {
				case LOGIN:   return 0;
				case WAITING: return 1;
				case PUZZLE:  return 2;
				case REPORT:  return 3;
				case RANKING: return 4;
				default:	  return -1;
			}
		}
	}

}
