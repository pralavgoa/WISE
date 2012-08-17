/**
 * 
 */
package edu.ucla.wise.admin.healthmon;

import java.util.Date;

import edu.ucla.wise.commons.Data_Bank;
import edu.ucla.wise.commons.WiseConstants;
import edu.ucla.wise.commons.WiseConstants.SURVEY_STATUS;

/**
 * @author ssakdeo
 * 
 */
public class HealthStatus {

	private static HealthStatus healthStatus = null;

	private HealthStatus() {
	}

	public static synchronized HealthStatus getInstance() {
		if (healthStatus == null) {
			healthStatus = new HealthStatus();
		}
		return healthStatus;
	}

	public boolean dbIsAlive;
	public Date dbLastUpdatedTime;
	public boolean smtpIsAlive;
	public Date smtpLastUpdatedTime;

	public boolean isDbIsAlive() {
		return dbIsAlive;
	}

	public void setDbIsAlive(boolean dbIsAlive) {
		this.dbIsAlive = dbIsAlive;
	}

	public Date getDbLastUpdatedTime() {
		return dbLastUpdatedTime;
	}

	public void setDbLastUpdatedTime(Date dbLastUpdatedTime) {
		this.dbLastUpdatedTime = dbLastUpdatedTime;
	}

	public boolean isSmtpIsAlive() {
		return smtpIsAlive;
	}

	public SURVEY_STATUS isSurveyAlive(String studyName, Data_Bank db) {

		long lastUpdateTime = db.lastSurveyHealthUpdateTime(studyName), currentTimeMillis = System
				.currentTimeMillis();
		// if the difference is more than 10min
		if (lastUpdateTime == 0)
			return SURVEY_STATUS.NOT_AVAIL;
		return (currentTimeMillis - lastUpdateTime) < WiseConstants.surveyCheckInterval ? SURVEY_STATUS.OK
				: SURVEY_STATUS.FAIL;
	}

	public void setSmtpIsAlive(boolean smtpIsAlive) {
		this.smtpIsAlive = smtpIsAlive;
	}

	public Date getSmtpLastUpdatedTime() {
		return smtpLastUpdatedTime;
	}

	public void setSmtpLastUpdatedTime(Date smtpLastUpdatedTime) {
		this.smtpLastUpdatedTime = smtpLastUpdatedTime;
	}

	public void updateSmtp(boolean health, Date updateTime) {
		this.smtpIsAlive = health;
		this.smtpLastUpdatedTime = updateTime;
	}

	public void updateDb(boolean health, Date updateTime) {
		this.dbIsAlive = health;
		this.dbLastUpdatedTime = updateTime;
	}

}
