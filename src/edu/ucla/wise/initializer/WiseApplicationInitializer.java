package edu.ucla.wise.initializer;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.ucla.wise.commons.AdminInfo;
import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.WISE_Application;
import edu.ucla.wise.emailscheduler.EmailScheduler;

public class WiseApplicationInitializer implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
	// TODO Auto-generated method stub

	EmailScheduler.destroyScheduler();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
	try {
	WISE_Application.log_info("Wise Application initializing");

	StudySpaceParametersProvider.initialize();

	    String contextPath = servletContextEvent.getServletContext()
		.getContextPath();

	    String adminInitializeError = AdminInfo.check_init(contextPath);

	    if (adminInitializeError != null) {
		WISE_Application.log_info("Admin app not initialized");
		throw new IllegalStateException("Admin app not initialized");
	    }

	    String surveyInitializeError = Surveyor_Application
		    .check_init(contextPath);

	    if (surveyInitializeError != null) {
		WISE_Application.log_info("Survey app not initialized");
		throw new IllegalStateException("Survey app not initialized");
	    }

	WISE_Application.log_info("Wise Application initialized");

	WISE_Application.log_info("Staring Email Scheduler");
	EmailScheduler.startEmailSendingThreads();
	WISE_Application.log_info("Email Scheduler is alive");
	} catch (IOException e) {
	    WISE_Application.log_error("IO Exception while initializing", e);
	} catch (IllegalStateException e) {
	    WISE_Application
		    .log_error(
			    "The admin or the survey app was not initialized, WISE application cannot start",
			    e);
	}

    }

}
