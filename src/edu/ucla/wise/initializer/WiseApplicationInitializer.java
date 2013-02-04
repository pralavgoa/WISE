package edu.ucla.wise.initializer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.ucla.wise.commons.WISE_Application;
import edu.ucla.wise.emailscheduler.EmailScheduler;

public class WiseApplicationInitializer implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
	// TODO Auto-generated method stub

	EmailScheduler.destroyScheduler();
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
	WISE_Application.log_info("Wise Application initializing");

	StudySpaceParametersProvider.initialize();

	WISE_Application.log_info("Wise Application initialized");

	WISE_Application.log_info("Staring Email Scheduler");
	EmailScheduler.startEmailSendingThreads();
	WISE_Application.log_info("Email Scheduler is alive");

    }

}
