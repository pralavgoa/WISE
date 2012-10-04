package edu.ucla.wise.commons;

import org.w3c.dom.Node;

/**
 * This class contains a typical reminder message_sequence and its properties
 */

public class Reminder extends Message {
    /** Instance Variables */
    public int trigger_days, max_count;

    // the constructor for message_sequence
    public Reminder(Node n) {
	super(n);
	try {
	    // System.out.println("reminder id=" + id);
	    trigger_days = Integer.parseInt(n.getAttributes()
		    .getNamedItem("Trigger_Days").getNodeValue());
	    max_count = Integer.parseInt(n.getAttributes()
		    .getNamedItem("Max_Count").getNodeValue());
	    // System.out.println("max count=" + max_count);
	} catch (Exception e) {
	    WISE_Application.log_error("WISE EMAIL - REMINDER: ID = " + id
		    + "; Subject = " + subject + " --> " + e.toString(), null);
	    return;
	}
    }

}
