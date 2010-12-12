package org.ccci.mpdStats.dao;

import java.util.List;

import javax.persistence.NoResultException;

import org.ccci.mpdStats.model.NewStaffTrainingSession;
import org.ccci.mpdStats.model.ReportPeriod;
import org.joda.time.LocalDate;

public interface NewStaffTrainingSessionDAO
{

    /**
     * Returns the first {@link ReportPeriod} for the given {@link NewStaffTrainingSession}.  If a start date is not recorded for the given
     * {@link NewStaffTrainingSession}, then returns null.
     * @param newStaffTrainingSession
     * @return
     */
    ReportPeriod getFirstReportPeriod(NewStaffTrainingSession newStaffTrainingSession);

    /**
     * Returns the most recent {@link NewStaffTrainingSession} whose first {@link ReportPeriod} either contains the given date or occurs before the given date.
     * 
     * @param limitDate
     * @throws NoResultException if there is such configured {@link NewStaffTrainingSession}
     * @return
     */
    NewStaffTrainingSession getMostRecentNewStaffTrainingSession(LocalDate limitDate);

    /**
     * Returns an ordered list of all known {@link NewStaffTrainingSession}s 
     * @return
     */
    List<NewStaffTrainingSession> getAvailableNewStaffTrainingSessions();
    
}
