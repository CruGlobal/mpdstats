package org.ccci.mpdStats.dao.internal;

import java.util.ArrayList;
import java.util.List;

import org.ccci.dao.EmployeeNotFoundException;
import org.ccci.dao.mock.EmptyEmployeeDAO;
import org.ccci.model.Employee;
import org.ccci.model.EmployeeId;
import org.ccci.model.EmployeePojo;
import org.ccci.mpdStats.entity.Comment;
import org.ccci.mpdStats.entity.CommentKey;
import org.ccci.mpdStats.entity.Improvement;
import org.ccci.mpdStats.entity.ImprovementKey;
import org.ccci.mpdStats.entity.MonthlyCommitment;
import org.ccci.mpdStats.entity.MonthlyCommitmentKey;
import org.ccci.mpdStats.entity.PrayerRequest;
import org.ccci.mpdStats.entity.PrayerRequestKey;
import org.ccci.mpdStats.entity.SpecialCommitment;
import org.ccci.mpdStats.entity.SpecialCommitmentKey;
import org.ccci.mpdStats.entity.StatsReport;
import org.ccci.mpdStats.entity.StatsReportKey;
import org.ccci.mpdStats.model.NewStaffTrainingSession;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.mpdStats.model.NewStaffTrainingSession.Season;
import org.ccci.mpdStats.testutils.MpdStatsPersistenceTest;
import org.ccci.util.time.Year;
import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ReportDAOImplTest extends MpdStatsPersistenceTest
{

    EmployeeId testId = EmployeeId.valueOf("123456778");
    ReportPeriod testPeriod = ReportPeriod.newReportPeriodEndingOn(new LocalDate(2008, 11, 22));

    ReportDAOImpl reportDAO;

    @BeforeMethod
    public void setupReportDAO()
    {
        reportDAO = new ReportDAOImpl();
        reportDAO.psEntityManager = entityManager;
        reportDAO.employeeDAO = new EmptyEmployeeDAO()
        {
            @Override
            public Employee find(EmployeeId employeeId) throws EmployeeNotFoundException
            {
                assert employeeId.equals(testId);
                EmployeePojo employee = new EmployeePojo(testId);
                employee.setMarriedStatus("M");
                return employee;
            }
        };
    }

    @Test
    public void testSave()
    {
        StatsReport report = createReport(testPeriod);
        report.setSubmitted(false);

        MonthlyCommitment commitment = MonthlyCommitment.createBlankEntry();
        commitment.setAccountName("Joe Smith");
        commitment.setGiftAmount(45.00);
        report.getMonthlyCommitments().add(commitment);

        SpecialCommitment specialCommitment = SpecialCommitment.createBlankEntry();
        specialCommitment.setAccountName("Judy Jones");
        specialCommitment.setGiftAmount(1000.00);
        report.getSpecialCommitments().add(specialCommitment);

        report.setAppointmentsSetActual(4);
        report.setAppointmentsSetGoal(3);

        report.getImprovement().setImprovement("not speak so quickly");

        reportDAO.saveReport(report);
        
        entityManager.clear();

        StatsReport savedReport = entityManager.find(StatsReport.class, new StatsReportKey(testId, testPeriod));
        assert savedReport != null;
        assert savedReport.getAppointmentsSetActual() == 4;
        assert savedReport.getAppointmentsSetGoal() == 3;
        assert entityManager.find(Improvement.class, new ImprovementKey(testId, testPeriod)) != null;
        assert entityManager.find(Comment.class, new CommentKey(testId, testPeriod)) != null;
        assert entityManager.find(PrayerRequest.class, new PrayerRequestKey(testId, testPeriod)) != null;
        assert entityManager.find(Improvement.class, new ImprovementKey(testId, testPeriod)) != null;
        SpecialCommitment savedComment =
                entityManager.find(SpecialCommitment.class, new SpecialCommitmentKey(testId, testPeriod, 1));
        assert savedComment != null;
        assert savedComment.getAccountName().equals("Judy Jones");
        assert savedComment.getGiftAmount() == 1000.00;
        assert entityManager.find(MonthlyCommitment.class, new MonthlyCommitmentKey(testId, testPeriod, 1)) != null;
    }

    @Test
    public void testGetCompletedReportPeriods()
    {
        StatsReport report1 = createReport(testPeriod);
        report1.setSubmitted(true);

        StatsReport report2 = createReport(testPeriod.nextReportPeriod());
        report2.setSubmitted(true);

        StatsReport report3 = createReport(testPeriod.nextReportPeriod().nextReportPeriod().nextReportPeriod());
        report3.setSubmitted(true);

        StatsReport report4 =
                createReport(testPeriod.nextReportPeriod().nextReportPeriod().nextReportPeriod().nextReportPeriod());
        report4.setSubmitted(false);

        entityManager.persist(report1);
        entityManager.persist(report2);
        entityManager.persist(report3);
        entityManager.persist(report4);

        List<ReportPeriod> foundReportPeriods = reportDAO.findCompletedReportPeriods(testId);

        ArrayList<ReportPeriod> expectedReportPeriods =
                Lists.newArrayList(testPeriod, testPeriod.nextReportPeriod(),
                    testPeriod.nextReportPeriod().nextReportPeriod().nextReportPeriod());
        Assert.assertEquals(foundReportPeriods, expectedReportPeriods);
    }

    @Test
    public void testGetPreviousReport()
    {
        StatsReport report1 = createReport(testPeriod.previousReportPeriod());
        report1.setSubmitted(true);

        StatsReport report2 = createReport(testPeriod.previousReportPeriod().previousReportPeriod());
        report2.setSubmitted(true);


        entityManager.persist(report1);
        entityManager.persist(report2);

        StatsReport foundReport = reportDAO.getPreviousReport(testId, testPeriod);

        Assert.assertEquals(foundReport, report1);
    }

    @Test
    public void testGetPreviousReport_gap()
    {
        StatsReport report = createReport(testPeriod.previousReportPeriod().previousReportPeriod());
        report.setSubmitted(true);


        entityManager.persist(report);

        StatsReport foundReport = reportDAO.getPreviousReport(testId, testPeriod);

        Assert.assertEquals(foundReport, report);
    }

    @Test
    public void testGetPreviousReport_relationships()
    {
        StatsReport report = createReport(testPeriod.previousReportPeriod());
        report.setSubmitted(true);

        SpecialCommitment specialCommitment = new SpecialCommitment(testId, report.getReportPeriod(), 1);
        specialCommitment.setAccountName("Judy Jones");
        specialCommitment.setGiftAmount(1000.00);

        Improvement improvement = new Improvement(testId, report.getReportPeriod());
        improvement.setImprovement("not speak so quickly");

        entityManager.persist(report);
        entityManager.persist(specialCommitment);
        entityManager.persist(improvement);

        StatsReport foundReport = reportDAO.getPreviousReport(testId, testPeriod);

        Assert.assertEquals(foundReport, report);
        Assert.assertEquals(Iterables.getOnlyElement(foundReport.getSpecialCommitments()), specialCommitment);
        Assert.assertEquals(foundReport.getImprovement(), improvement);
    }

    @Test
    public void testIsReportInProgress_yes()
    {
        StatsReport report = createReport(testPeriod);
        report.setSubmitted(false);

        entityManager.persist(report);

        Assert.assertTrue(reportDAO.isReportInProgress(testId));
    }

    @Test
    public void testIsReportInProgress_no()
    {
        StatsReport report = createReport(testPeriod);
        report.setSubmitted(true);

        entityManager.persist(report);

        Assert.assertFalse(reportDAO.isReportInProgress(testId));

    }

    @Test
    public void testGetInProgressReport()
    {
        StatsReport report = createReport(testPeriod);
        report.setSubmitted(false);

        entityManager.persist(report);

        StatsReport actualReport = reportDAO.getInProgressReport(testId);
        Assert.assertEquals(actualReport, report);

    }

    @Test
    public void testGetInProgressReport_relationships()
    {
        StatsReport report = createReport(testPeriod);
        report.setSubmitted(false);

        SpecialCommitment specialCommitment = new SpecialCommitment(testId, report.getReportPeriod(), 1);
        specialCommitment.setAccountName("Judy Jones");
        specialCommitment.setGiftAmount(1000.00);

        Improvement improvement = new Improvement(testId, report.getReportPeriod());
        improvement.setImprovement("not speak so quickly");

        entityManager.persist(report);
        entityManager.persist(specialCommitment);
        entityManager.persist(improvement);

        StatsReport foundReport = reportDAO.getInProgressReport(testId);

        Assert.assertEquals(foundReport, report);
        Assert.assertEquals(Iterables.getOnlyElement(foundReport.getSpecialCommitments()), specialCommitment);
        Assert.assertEquals(foundReport.getImprovement(), improvement);
    }

    @Test
    public void testDoesReportExist_exists()
    {
        StatsReport report = createReport(testPeriod);
        report.setSubmitted(true);

        entityManager.persist(report);

        Assert.assertTrue(reportDAO.doesReportExist(testId, testPeriod));
    }

    @Test
    public void testDoesReportExist_doesntExist()
    {
        Assert.assertFalse(reportDAO.doesReportExist(testId, testPeriod));
    }

    @Test
    public void testGetReport_exists()
    {
        StatsReport report = createReport(testPeriod);
        report.setSubmitted(true);

        entityManager.persist(report);

        StatsReport actualReport = reportDAO.getReport(testId, testPeriod);
        Assert.assertEquals(actualReport, report);
    }
    
    @Test
    public void testGetFutureReports()
    {
        StatsReport report1 = createReport(testPeriod);
        report1.setSubmitted(true);

        StatsReport report2 = createReport(testPeriod.nextReportPeriod());
        report2.setSubmitted(true);

        entityManager.persist(report1);
        entityManager.persist(report2);

        List<StatsReport> foundReports = reportDAO.getFutureReports(testId, testPeriod);
        Assert.assertNotNull(foundReports);
        Assert.assertEquals(Iterables.getOnlyElement(foundReports), report2);
    }
    
    @Test
    public void testGetFutureReports_relationships()
    {

        StatsReport report = createReport(testPeriod.nextReportPeriod());
        report.setSubmitted(true);

        SpecialCommitment specialCommitment = new SpecialCommitment(testId, report.getReportPeriod(), 1);
        specialCommitment.setAccountName("Judy Jones");
        specialCommitment.setGiftAmount(1000.00);

        Improvement improvement = new Improvement(testId, report.getReportPeriod());
        improvement.setImprovement("not speak so quickly");

        entityManager.persist(report);
        entityManager.persist(specialCommitment);
        entityManager.persist(improvement);

        List<StatsReport> foundReports = reportDAO.getFutureReports(testId, testPeriod);

        Assert.assertNotNull(foundReports);
        StatsReport foundReport = Iterables.getOnlyElement(foundReports);
        Assert.assertEquals(foundReport, report);
        Assert.assertEquals(Iterables.getOnlyElement(foundReport.getSpecialCommitments()), specialCommitment);
        Assert.assertEquals(foundReport.getImprovement(), improvement);
    }
    
    private StatsReport createReport(ReportPeriod reportPeriod)
    {
        StatsReport report = new StatsReport(testId, reportPeriod);
        report.setNstSession(new NewStaffTrainingSession(Season.FA, Year.newYear(2008)));
        return report;
    }
}
