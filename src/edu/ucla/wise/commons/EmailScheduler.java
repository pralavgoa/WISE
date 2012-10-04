/**
 * 
 */
package edu.ucla.wise.commons;

import java.io.IOException;

/**
 * This email thread will spawn action of sending reminders.
 * 
 */
public class EmailScheduler {

    public static void main(String[] args) {
	// start the email sending procedure
	java.util.Date today = new java.util.Date();
	if (args.length < 1) {
	    System.out.println("Usage: EmailScheduler [application_name]");
	    return;
	}
	String appName = args[0];
	System.out.print("Launching Email Manager on " + today.toString()
		+ " for studies assigned to " + appName + " on this server.");

	try {
	    AdminInfo.check_init(appName);
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	Study_Space[] allSpaces = new Study_Space[0];
	try {
	    allSpaces = Study_Space.get_all();
	} catch (Exception e) {
	    System.out.println(" --> Emailer err - Can't get study_spaces: "
		    + e.toString());
	    e.printStackTrace(System.out);
	}
	// iterate over all Study_Spaces that this server manages
	for (int i = 0; i < allSpaces.length; i++) {
	    Study_Space ss = allSpaces[i];
	    Data_Bank db = ss.db;
	    System.out.println("\nStudy_Space " + ss.study_name
		    + " CONNECTING to database: " + db.dbdata);
	    System.out.println(db.send_reminders());
	}
    }

}
