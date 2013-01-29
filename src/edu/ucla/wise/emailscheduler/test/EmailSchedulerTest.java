package edu.ucla.wise.emailscheduler.test;

import edu.ucla.wise.emailscheduler.EmailScheduler;

public class EmailSchedulerTest {
	public static void main(String[] args) {

		for (int hour = 1; hour <= 23; hour++) {
			long emailWaitTime = EmailScheduler
					.calculateInitialWaitPeriodInMillis(hour);

			long emailWaitTimeInHours = emailWaitTime
					/ EmailScheduler.MILLISECONDS_IN_AN_HOUR;

			System.out.println(emailWaitTimeInHours);
		}

	}
}
