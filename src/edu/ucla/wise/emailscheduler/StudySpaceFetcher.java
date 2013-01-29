package edu.ucla.wise.emailscheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ucla.wise.commons.AdminInfo;
import edu.ucla.wise.commons.Study_Space;

public class StudySpaceFetcher {

    static Logger LOG = Logger.getLogger(StudySpaceFetcher.class);

    public static List<Study_Space> getStudySpaces(String appName) {

	ArrayList<Study_Space> startConfigList = new ArrayList<Study_Space>();

	// start the email sending procedure
	java.util.Date today = new java.util.Date();

	LOG.info("Launching Email Manager on " + today.toString()
		+ " for studies assigned to " + appName + " on this server.");

	try {
	    AdminInfo.check_init(appName);
	} catch (IOException e1) {
	    LOG.error(e1);
	}

	Study_Space[] allSpaces = new Study_Space[0];

	try {
	    allSpaces = Study_Space.get_all();

	    for (Study_Space studySpace : allSpaces) {

		startConfigList.add(studySpace);
	    }

	} catch (Exception e) {
	    LOG.info(" --> Emailer err - Can't get study_spaces: "
		    + e.toString());
	    e.printStackTrace(System.out);
	}

	return startConfigList;
    }
}
