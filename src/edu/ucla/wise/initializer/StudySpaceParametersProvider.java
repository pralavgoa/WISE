package edu.ucla.wise.initializer;

import java.util.Map;

import edu.ucla.wise.commons.WISE_Application;
import edu.ucla.wise.studyspace.parameters.StudySpaceParameters;
import edu.ucla.wise.studyspacewizard.database.DatabaseConnector;

public class StudySpaceParametersProvider {

    private static StudySpaceParametersProvider studySpaceParametersProvider;

    private final Map<String, StudySpaceParameters> studySpaceParameters;
    
    private StudySpaceParametersProvider() {

	// connect to the database and get all parameters in memory

	studySpaceParameters = DatabaseConnector.getMapOfStudySpaceParameters();

	WISE_Application.log_info("Found " + studySpaceParameters.size()
		+ " Study Spaces");
	WISE_Application.log_info("Spaces are "
		+ studySpaceParameters.toString());


    }

    public static boolean initialize() {
	
	
	if (studySpaceParametersProvider == null) {
	    studySpaceParametersProvider = new StudySpaceParametersProvider();
	} else {
	    WISE_Application
		    .log_info("studySpaceParametersProvider already initialized");
	}
	return true;
    }

    public static boolean destroy() {
	studySpaceParametersProvider = null;
	return true;
    }

    public static StudySpaceParametersProvider getInstance() {
	if (studySpaceParametersProvider == null) {
	    initialize();
	}
	return studySpaceParametersProvider;
    }

    public StudySpaceParameters getStudySpaceParameters(String studySpaceName) {
	WISE_Application
		.log_info("Requesting parameters for " + studySpaceName);
	if (studySpaceParameters.get(studySpaceName) == null) {
	    WISE_Application.log_info("Study space parameters not found");
	    WISE_Application.log_info("Current study space parameters are "
		    + getStudySpaceParametersMap().toString());
	}
	return studySpaceParameters.get(studySpaceName);
    }

    public Map<String, StudySpaceParameters> getStudySpaceParametersMap() {
	return this.studySpaceParameters;
    }

}
