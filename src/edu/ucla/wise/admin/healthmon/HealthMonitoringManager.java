/**
 * 
 */
package edu.ucla.wise.admin.healthmon;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.log4j.Logger;

import edu.ucla.wise.commons.AdminInfo;
import edu.ucla.wise.commons.WISE_Application;
import edu.ucla.wise.commons.WiseConstants;

/**
 * @author ssakdeo
 * 
 */
public class HealthMonitoringManager implements Runnable {

    Logger log = Logger.getLogger(HealthMonitoringManager.class);
    AdminInfo adminInfo;
    private static HealthMonitoringManager hMon = null;
    private Connection dbConnection;

    private HealthMonitoringManager(AdminInfo adminInfo) {
	this.adminInfo = adminInfo;
    }

    /**
     * This function will start monitoring for database and smtp if it has
     * already been not started. If the monitoring has already started then this
     * function will just return;
     * 
     * @param survey
     */
    public static synchronized void monitor(AdminInfo adminInfo) {
	if (hMon == null) {
	    hMon = new HealthMonitoringManager(adminInfo);
	    Thread t = new Thread(hMon);
	    t.start();
	}
    }

    @Override
    public void run() {

	while (true) {
	    checkDbHealth();
	    checkSmtpHealth();
	    try {
		Thread.sleep(WiseConstants.dbSmtpCheckInterval);
	    } catch (InterruptedException e) {
		// ignore
	    }
	}
    }

    private void checkSmtpHealth() {

	HealthStatus hStatus = HealthStatus.getInstance();
	Session session = WISE_Application.get_mailSession(null);
	if (session == null) {
	    log.error("Could not get session variable! Please retry!");
	    hStatus.updateSmtp(false, Calendar.getInstance().getTime());
	    return;
	}
	Transport tr = null;
	try {
	    tr = session.getTransport("smtp");
	} catch (NoSuchProviderException e) {
	    log.error(e);
	    hStatus.updateSmtp(false, Calendar.getInstance().getTime());
	    return;
	}
	if (tr == null) {
	    log.error("Could not get transport object");
	    hStatus.updateSmtp(false, Calendar.getInstance().getTime());
	    return;
	}
	String MailHost = null;
	String user = null;
	String pass = null;

	pass = WISE_Application.sharedProps.getString("wise.email.password");
	user = WISE_Application.sharedProps.getString("wise.email.username");//
	MailHost = WISE_Application.sharedProps.getString("email.host");
	try {
	    tr.connect(MailHost, user, pass);
	} catch (MessagingException e) {
	    log.error("Could not connect!");
	    hStatus.updateSmtp(false, Calendar.getInstance().getTime());
	    return;
	}
	try {
	    tr.close();
	} catch (MessagingException e) {
	    // ignore
	    log.info(
		    "Transport connected successully however closing failed but thats fine",
		    e);
	}
	hStatus.updateSmtp(true, Calendar.getInstance().getTime());
    }

    private void checkDbHealth() {
	HealthStatus hStatus = HealthStatus.getInstance();
	Connection dbConnection = null;
	try {
	    dbConnection = adminInfo.getDBConnection();
	} catch (SQLException e) {
	    log.error(e);
	    hStatus.updateDb(false, Calendar.getInstance().getTime());
	    return;
	} finally {
	    if (dbConnection != null) {
		try {
		    dbConnection.close();
		} catch (SQLException e) {
		}
	    }
	}
	hStatus.updateDb(true, Calendar.getInstance().getTime());
    }
}
