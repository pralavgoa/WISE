/**
 * 
 */
package edu.ucla.wise.emailscheduler;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import edu.ucla.wise.commons.Data_Bank;
import edu.ucla.wise.commons.Study_Space;

/**
 * This email thread will spawn action of sending reminders.
 * 
 */
public class EmailScheduler {
    public static final String APPLICATION_NAME = "/WISE";

    public static final int DEFAULT_EMAIL_START_TIME = 2;



    private static ScheduledExecutorService executor;

    static Logger LOG = Logger.getLogger(EmailScheduler.class);

    public static void startEmailSendingThreads() {

	List<Study_Space> studySpaceList = StudySpaceFetcher
		.getStudySpaces(APPLICATION_NAME);

	LOG.info("Found " + studySpaceList.size() + " study spaces");

	executor = Executors.newSingleThreadScheduledExecutor();

	for (final Study_Space studySpace : studySpaceList) {

	    long emailStartHour = DEFAULT_EMAIL_START_TIME;
	    try {
		emailStartHour = Long.parseLong(studySpace.emailSendingTime);

		// -1 will indicate that the emails should not be sent
		if (emailStartHour == -1) {
		    // proceed to the next study space
		    continue;
		}

		if (emailStartHour < 0 && emailStartHour > 23) {
		    throw new IllegalArgumentException();
		}

	    } catch (IllegalArgumentException e) {
		LOG.error("Error in start time for " + studySpace.study_name
			+ ", defaulting to time " + DEFAULT_EMAIL_START_TIME, e);
		emailStartHour = DEFAULT_EMAIL_START_TIME;

	    }

	    long initialWaitPeriodInMillis = calculateInitialWaitPeriodInMillis(emailStartHour);

	    LOG.info("Emailer for " + studySpace.id + " will start in "
		    + WISE_TimeUtils
			    .convertMillisToHumanReadableForm(initialWaitPeriodInMillis));
	    executor.scheduleAtFixedRate(new Runnable() {

		@Override
		public void run() {
		    LOG.info("Mail sender for " + studySpace.id
			    + " has started");

		    Data_Bank db = studySpace.db;
		    LOG.info("Study_Space " + studySpace.study_name
			    + " CONNECTING to database: " + db.dbdata);
		    LOG.info(db.send_reminders());

		    LOG.info("Mail sender for " + studySpace.id
			    + " has finished");

		}

		    }, initialWaitPeriodInMillis,
		    WISE_TimeUtils.MILLISECONDS_IN_A_DAY,
		    TimeUnit.MILLISECONDS);
	}
    }

    public static long calculateInitialWaitPeriodInMillis(long emailStartTime) {
	Calendar currentTime = Calendar.getInstance();

	Calendar calendarMidnight = Calendar.getInstance();

	calendarMidnight.set(Calendar.HOUR_OF_DAY, 0);
	calendarMidnight.set(Calendar.MINUTE, 0);
	calendarMidnight.set(Calendar.SECOND, 0);
	calendarMidnight.set(Calendar.MILLISECOND, 0);

	long millisAtMidnight = calendarMidnight.getTimeInMillis();

	LOG.info("Milliseconds at midnight: " + millisAtMidnight);

	long emailStartTimeMillis = emailStartTime
		* WISE_TimeUtils.MILLISECONDS_IN_AN_HOUR;

	LOG.info("Email startTime in millis " + emailStartTimeMillis);

	long currentTimeMillis = currentTime.getTimeInMillis();

	LOG.info("Current time in milliseconds: " + currentTimeMillis);
	// 2. add todays milliseconds to it

	if (emailStartTimeMillis + millisAtMidnight > currentTimeMillis) {
	    return emailStartTimeMillis + millisAtMidnight - currentTimeMillis;
	} else {
	    return WISE_TimeUtils.MILLISECONDS_IN_A_DAY
		    - (currentTimeMillis - emailStartTimeMillis - millisAtMidnight);
	}
    }

    public static boolean sendEmailsInStudySpace(Study_Space ss) {

	return true;
    }

    public static void destroyScheduler() {
	if (executor != null) {
	    executor.shutdown();
	}
    }

}
