/**
 * 
 */
package edu.ucla.wise.client.healthmon;

import java.util.HashSet;
import java.util.Set;

import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.WiseConstants;

/**
 * This class is a single thread per survey application which runs and reports
 * the health of the survey server in the database. The goal is to make the
 * admin application to read "survey_health" in database and display the health
 * of survey application.
 * 
 * @author ssakdeo
 * 
 */
public class SurveyHealth implements Runnable {

    public Study_Space studySpace;
    public static Set<String> monitorStudies = new HashSet<String>();

    private SurveyHealth(Study_Space study) {
	this.studySpace = study;
    }

    /**
     * This function will start monitoring for "this" survey if it has already
     * been not started. If the monitoring has already started then this
     * function will just return;
     * 
     * @param survey
     */
    public static synchronized void monitor(Study_Space study) {
	if (!monitorStudies.contains(study.study_name)) {
	    monitorStudies.add(study.study_name);
	    Thread t = new Thread(new SurveyHealth(study));
	    t.start();
	}
    }

    @Override
    public synchronized void run() {
	while (true) {
	    studySpace.db.updateSurveyHealthStatus(studySpace.study_name);
	    try {
		Thread.sleep(WiseConstants.surveyUpdateInterval);
	    } catch (InterruptedException e) {
	    }
	}
    }
}