package wwwordz.shared;

/**
 * "Default configurations used in WWWordz"<br><br>
 * 
 * These durations, in milliseconds, are supposed to be obtained
 * 		from the designated getters of this class.
 * 
 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/shared/Configs.html
 */
public class Configs {
	public static final long JOIN_STAGE_DURATION    = 5000L,
							 PLAY_STAGE_DURATION    = 30000L,
							 REPORT_STAGE_DURATION  = 5000L,
							 RANKING_STAGE_DURATION = 5000L;
	/**
	 * Empty constructor.
	 * 
	 */
	public Configs() {}

	/**
	 * Returns the default duration of stage JOIN
	 * 
	 * @return the duration of stage JOIN, in milliseconds
	 */
	public static long getJoinStageDuration() {
		return JOIN_STAGE_DURATION;
	}

	/**
	 * Returns the default duration of stage PLAY
	 * 
	 * @return the duration of stage PLAY, in milliseconds
	 */
	public static long getPlayStageDuration() {
		return PLAY_STAGE_DURATION;
	}

	/**
	 * Returns the default duration of stage REPORT
	 * 
	 * @return the duration of stage REPORT, in milliseconds
	 */
	public static long getReportStageDuration() {
		return REPORT_STAGE_DURATION;
	}

	/**
	 * Returns the default duration of stage JOIN
	 * 
	 * @return the duration of stage REPORT, in milliseconds
	 */
	public static long getRankingStageDuration() {
		return RANKING_STAGE_DURATION;
	}
}
