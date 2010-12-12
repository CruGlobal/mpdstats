package org.ccci.mpdStats.dao.internal;

import java.util.List;

import org.ccci.mpdStats.entity.NewStaffTrainingSessionStartDate;
import org.ccci.mpdStats.model.NewStaffTrainingSession;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.mpdStats.model.NewStaffTrainingSession.Season;
import org.ccci.mpdStats.testutils.MpdStatsPersistenceTest;
import org.ccci.util.time.Year;
import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class NewStaffTrainingSessionDAOImplTest extends MpdStatsPersistenceTest
{

    private NewStaffTrainingSessionDAOImpl newStaffTrainingSessionDAO;
    
    @BeforeMethod
    public void setupDAO()
    {
        newStaffTrainingSessionDAO = new NewStaffTrainingSessionDAOImpl();
        newStaffTrainingSessionDAO.psEntityManager = entityManager;
    }
    
    @Test
    public void testGetFirstReportPeriod()
    {
        NewStaffTrainingSession newStaffTrainingSession = new NewStaffTrainingSession(Season.SP, Year.newYear(2009));
        NewStaffTrainingSessionStartDate nstStartDate = new NewStaffTrainingSessionStartDate(newStaffTrainingSession);
        LocalDate startDate = new LocalDate(2009, 4, 4);
        nstStartDate.setStartDate(startDate);
        entityManager.persist(nstStartDate);
        entityManager.flush();
        entityManager.clear();
        
        ReportPeriod firstReportPeriod = newStaffTrainingSessionDAO.getFirstReportPeriod(newStaffTrainingSession);
        Assert.assertEquals(firstReportPeriod, ReportPeriod.newReportPeriodEndingOn(startDate));
    }
    
    @Test
    public void testGetFirstReportPeriod_notThere()
    {
        NewStaffTrainingSession newStaffTrainingSession = new NewStaffTrainingSession(Season.SP, Year.newYear(2009));
        ReportPeriod firstReportPeriod = newStaffTrainingSessionDAO.getFirstReportPeriod(newStaffTrainingSession);
        Assert.assertNull(firstReportPeriod);
    }
    
    @Test
    public void testGetMostRecentNewStaffTrainingSession()
    {
        NewStaffTrainingSession newStaffTrainingSession1 = new NewStaffTrainingSession(Season.SP, Year.newYear(2009));
        NewStaffTrainingSessionStartDate nstStartDate1 = new NewStaffTrainingSessionStartDate(newStaffTrainingSession1);
        LocalDate startDate1 = new LocalDate(2009, 4, 4);
        nstStartDate1.setStartDate(startDate1);
        
        NewStaffTrainingSession newStaffTrainingSession2 = new NewStaffTrainingSession(Season.SU, Year.newYear(2009));
        NewStaffTrainingSessionStartDate nstStartDate2 = new NewStaffTrainingSessionStartDate(newStaffTrainingSession2);
        LocalDate startDate2 = new LocalDate(2009, 6, 6);
        nstStartDate2.setStartDate(startDate2);

        entityManager.persist(nstStartDate1);
        entityManager.persist(nstStartDate2);
        entityManager.flush();
        entityManager.clear();
        
        check(newStaffTrainingSession1, startDate1.minusDays(6));
        check(newStaffTrainingSession1, startDate1);
        check(newStaffTrainingSession1, startDate1.plusWeeks(1));
        check(newStaffTrainingSession1, startDate2.minusWeeks(1));
        check(newStaffTrainingSession2, startDate2.minusDays(6));
        check(newStaffTrainingSession2, startDate2);
        check(newStaffTrainingSession2, startDate2.plusWeeks(1));
        
    }

    private void check(NewStaffTrainingSession expectedNewStaffTrainingSession, LocalDate limitDate)
    {
        Assert.assertEquals(newStaffTrainingSessionDAO.getMostRecentNewStaffTrainingSession(limitDate), expectedNewStaffTrainingSession);
    }
    
    @Test 
    public void testGetAvailableNewStaffTrainingSessions()
    {
        NewStaffTrainingSession newStaffTrainingSession1 = new NewStaffTrainingSession(Season.SP, Year.newYear(2009));
        NewStaffTrainingSessionStartDate nstStartDate1 = new NewStaffTrainingSessionStartDate(newStaffTrainingSession1);
        LocalDate startDate1 = new LocalDate(2009, 4, 4);
        nstStartDate1.setStartDate(startDate1);
        
        NewStaffTrainingSession newStaffTrainingSession2 = new NewStaffTrainingSession(Season.SU, Year.newYear(2009));
        NewStaffTrainingSessionStartDate nstStartDate2 = new NewStaffTrainingSessionStartDate(newStaffTrainingSession2);
        LocalDate startDate2 = new LocalDate(2009, 6, 6);
        nstStartDate2.setStartDate(startDate2);

        entityManager.persist(nstStartDate1);
        entityManager.persist(nstStartDate2);
        entityManager.flush();
        entityManager.clear();
        
        List<NewStaffTrainingSession> actualNSTSessions = newStaffTrainingSessionDAO.getAvailableNewStaffTrainingSessions();
        List<NewStaffTrainingSession> expectedNSTSessions = Lists.newArrayList(newStaffTrainingSession1, newStaffTrainingSession2);
        
        Assert.assertEquals(actualNSTSessions, expectedNSTSessions);
        
    }
}
