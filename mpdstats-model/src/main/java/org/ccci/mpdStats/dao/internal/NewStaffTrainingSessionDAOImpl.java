package org.ccci.mpdStats.dao.internal;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.ccci.debug.RecordExceptions;
import org.ccci.mpdStats.dao.NewStaffTrainingSessionDAO;
import org.ccci.mpdStats.entity.NewStaffTrainingSessionStartDate;
import org.ccci.mpdStats.entity.NewStaffTrainingSessionStartDate.Key;
import org.ccci.mpdStats.model.NewStaffTrainingSession;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.util.Generics;
import org.ccci.util.time.TimeUtil;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;

@Name("newStaffTrainingSessionDAO")
@Scope(ScopeType.STATELESS)
@AutoCreate
@RecordExceptions
public class NewStaffTrainingSessionDAOImpl implements NewStaffTrainingSessionDAO
{

    @In EntityManager psEntityManager;
    
    public ReportPeriod getFirstReportPeriod(NewStaffTrainingSession newStaffTrainingSession)
    {
        NewStaffTrainingSessionStartDate newStaffTrainingSessionStartDate = psEntityManager.find(NewStaffTrainingSessionStartDate.class, new NewStaffTrainingSessionStartDate.Key(newStaffTrainingSession));
        if (newStaffTrainingSessionStartDate == null)
        {
            return null;
        }
        return ReportPeriod.newReportPeriodContaining(newStaffTrainingSessionStartDate.getStartDate());
    }

    public NewStaffTrainingSession getMostRecentNewStaffTrainingSession(LocalDate limitDate)
    {
        ReportPeriod limitReportPeriod = ReportPeriod.newReportPeriodContaining(limitDate);
        Key key = (Key) psEntityManager.createNamedQuery("NewStaffTrainingSessionStartDate.getMostRecentNewStaffTrainingSession")
            .setParameter("limitDate", TimeUtil.localDateToSqlDate(limitReportPeriod.getEndDate()))
            .getSingleResult();
        return key.getNewStaffTrainingSession();
    }

    public List<NewStaffTrainingSession> getAvailableNewStaffTrainingSessions()
    {
        List<Key> keys = Generics.checkList(Key.class, psEntityManager.createNamedQuery("NewStaffTrainingSessionStartDate.getAllNewStaffTrainingSessions")
            .getResultList());
        List<NewStaffTrainingSession> availableNewStaffTrainingSessions = Lists.newArrayList();
        for (Key key : keys)
        {
            availableNewStaffTrainingSessions.add(key.getNewStaffTrainingSession());
        }
        Collections.sort(availableNewStaffTrainingSessions);
        return availableNewStaffTrainingSessions;
    }

}
