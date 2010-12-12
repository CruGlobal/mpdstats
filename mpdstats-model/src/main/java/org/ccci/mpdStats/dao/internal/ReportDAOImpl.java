package org.ccci.mpdStats.dao.internal;

import static org.jboss.seam.ScopeType.STATELESS;

import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.ccci.dao.EmployeeDAO;
import org.ccci.debug.RecordExceptions;
import org.ccci.model.Employee;
import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.dao.ReportDAO;
import org.ccci.mpdStats.entity.Comment;
import org.ccci.mpdStats.entity.CommentKey;
import org.ccci.mpdStats.entity.Improvement;
import org.ccci.mpdStats.entity.ImprovementKey;
import org.ccci.mpdStats.entity.MonthlyCommitment;
import org.ccci.mpdStats.entity.PrayerRequest;
import org.ccci.mpdStats.entity.PrayerRequestKey;
import org.ccci.mpdStats.entity.SpecialCommitment;
import org.ccci.mpdStats.entity.StatsReport;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.util.Generics;
import org.ccci.util.time.TimeUtil;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * A stateless dao for stats reports.
 * Dependent upon EmployeeDAO and a peoplesoft entityManager.
 * 
 * Does some work in linking up relationships on top of what JPA does; as a result, it
 * may do some database work that isn't needed by the client.  But for consistency's sake,
 * all methods return fully usable {@link StatsReport}s, because the db traffic is negligible for
 * a small app like this.
 * 
 * @author Matt Drees
 */
@Name("reportDAO")
@Scope(STATELESS)
@AutoCreate
@RecordExceptions
public class ReportDAOImpl implements ReportDAO
{

    @In
    EntityManager psEntityManager;

    @In
    EmployeeDAO employeeDAO;
    
    public StatsReport getInProgressReport(EmployeeId employeeId)
    {
        StatsReport report = getInProgresReportInternal(employeeId);
        initialize(report);
        return report;
    }

    private void initialize(StatsReport report)
    {
        ReportPeriod reportPeriod = report.getReportPeriod();
        EmployeeId employeeId = report.getEmployeeId();
        
        Employee employee = employeeDAO.find(employeeId);
        Preconditions.checkState(employee != null, "no employee with id %s", employeeId);
        report.setEmployee(employee);

        Comment comment = psEntityManager.find(Comment.class, new CommentKey(employeeId, reportPeriod));
        if (comment == null)
        {
            comment = new Comment(employeeId, reportPeriod);
        }
        report.setComment(comment);

        Improvement improvement =
                psEntityManager.find(Improvement.class, new ImprovementKey(employeeId, reportPeriod));
        if (improvement == null)
        {
            improvement = new Improvement(employeeId, reportPeriod);
        }
        report.setImprovement(improvement);
        
        PrayerRequest prayerRequest =
                psEntityManager.find(PrayerRequest.class, new PrayerRequestKey(employeeId, reportPeriod));
        if (prayerRequest == null)
        {
            prayerRequest = new PrayerRequest(employeeId, reportPeriod);
        }
        report.setPrayerRequest(prayerRequest);
        
        List<MonthlyCommitment> monthlyCommitments = Generics.checkList(MonthlyCommitment.class, 
            psEntityManager.createNamedQuery("MonthlyCommitment.findByEmployeeIdAndReportDate")
                .setParameter("employeeId", employeeId)
                .setParameter("reportDate", TimeUtil.localDateToSqlDate(reportPeriod.getEndDate()))
                .getResultList());
        
        report.setMonthlyCommitments(monthlyCommitments);
        
        List<SpecialCommitment> specialCommitments = Generics.checkList(SpecialCommitment.class,
            psEntityManager.createNamedQuery("SpecialCommitment.findByEmployeeIdAndReportDate")
                .setParameter("employeeId", employeeId)
                .setParameter("reportDate", TimeUtil.localDateToSqlDate(reportPeriod.getEndDate()))
                .getResultList());
        report.setSpecialCommitments(specialCommitments);
    }

    private StatsReport getInProgresReportInternal(EmployeeId employeeId)
    {
        return (StatsReport) psEntityManager.createNamedQuery("StatsReport.getInProgressReport")
            .setParameter("employeeId", employeeId)
            .getSingleResult();
    }

    public boolean isReportInProgress(EmployeeId employeeId)
    {
        try
        {
            getInProgresReportInternal(employeeId);
        }
        catch (NoResultException e)
        {
            return false;
        }
        return true;
    }

    public StatsReport saveReport(StatsReport report)
    {
        StatsReport managedReport = psEntityManager.merge(report);
        
        Comment comment = psEntityManager.merge(report.getComment());
        managedReport.setComment(comment);
        
        Improvement improvement = psEntityManager.merge(report.getImprovement());
        managedReport.setImprovement(improvement);
        
        PrayerRequest prayerRequest = psEntityManager.merge(report.getPrayerRequest());
        managedReport.setPrayerRequest(prayerRequest);
        
        List<MonthlyCommitment> managedMonthlyCommitments = Lists.newArrayList();
        for (MonthlyCommitment monthlyCommitment : report.getMonthlyCommitments())
        {
            if (!monthlyCommitment.getAccountName().equals(MonthlyCommitment.BLANK_ACCOUNT_NAME_VALUE))
            {
                managedMonthlyCommitments.add(psEntityManager.merge(monthlyCommitment));
            }
        }
        managedReport.setMonthlyCommitments(managedMonthlyCommitments);
        
        
        List<SpecialCommitment> managedSpecialCommitments = Lists.newArrayList();
        for (SpecialCommitment specialCommitment : report.getSpecialCommitments())
        {
            if (!specialCommitment.getAccountName().equals(SpecialCommitment.BLANK_ACCOUNT_NAME_VALUE))
            {
                managedSpecialCommitments.add(psEntityManager.merge(specialCommitment));
            }
        }
        managedReport.setSpecialCommitments(managedSpecialCommitments);
        
        psEntityManager.flush();
        return managedReport;
    }

    public List<ReportPeriod> findCompletedReportPeriods(EmployeeId employeeId)
    {
        List<Date> reportedDates = Generics.checkList(Date.class,
            psEntityManager.createNamedQuery("StatsReport.findCompletedReportedDates")
                .setParameter("employeeId", employeeId)
                .getResultList());
        List<ReportPeriod> completedReportPeriods = Lists.newArrayList();
        for (Date date : reportedDates)
        {
            completedReportPeriods.add(ReportPeriod.newReportPeriodEndingOn(TimeUtil.sqlDateToLocalDate(date)));
        }
        return completedReportPeriods;
    }

    /*
     * TODO: this maybe should be cached; sometimes called multiple times per request 
     */
    public StatsReport getPreviousReport(EmployeeId employeeId, ReportPeriod reportPeriod)
    {
        Preconditions.checkNotNull(employeeId, "employeeId is null");
        Preconditions.checkNotNull(reportPeriod, "reportPeriod is null");
        try
        {
            StatsReport report = (StatsReport) psEntityManager.createNamedQuery("StatsReport.getPreviousReport")
                .setParameter("employeeId", employeeId)
                .setParameter("reportDate", TimeUtil.localDateToSqlDate(reportPeriod.previousReportPeriod().getEndDate()))
                .getSingleResult();
            initialize(report);
            return report;
        }
        catch (NoResultException e) 
        {
            return null;
        }
    }

    public boolean doesReportExist(EmployeeId employeeId, ReportPeriod reportPeriod)
    {
        Preconditions.checkNotNull(employeeId, "employeeId is null");
        Preconditions.checkNotNull(reportPeriod, "reportPeriod is null");
        long count = (Long) psEntityManager.createNamedQuery("StatsReport.countReports")
            .setParameter("employeeId", employeeId)
            .setParameter("reportDate", TimeUtil.localDateToSqlDate(reportPeriod.getEndDate()))
            .getSingleResult();
        return count > 0;
    }

    public StatsReport getReport(EmployeeId employeeId, ReportPeriod reportPeriod)
    {
        Preconditions.checkNotNull(employeeId, "employeeId is null");
        Preconditions.checkNotNull(reportPeriod, "reportPeriod is null");
        StatsReport report = (StatsReport) psEntityManager.createNamedQuery("StatsReport.getReport")
            .setParameter("employeeId", employeeId)
            .setParameter("reportDate", TimeUtil.localDateToSqlDate(reportPeriod.getEndDate()))
            .getSingleResult();
        initialize(report);
        return report;
    }

    public List<StatsReport> getFutureReports(EmployeeId employeeId, ReportPeriod reportPeriod)
    {
        Preconditions.checkNotNull(employeeId, "employeeId is null");
        Preconditions.checkNotNull(reportPeriod, "reportPeriod is null");
        List<StatsReport> reports = Generics.checkList(StatsReport.class, psEntityManager.createNamedQuery("StatsReport.getFutureReports")
            .setParameter("employeeId", employeeId)
            .setParameter("reportDate", TimeUtil.localDateToSqlDate(reportPeriod.getEndDate()))
            .getResultList());
        //exhibits O(n) selects behavior, but since this is only used for retroactive report submission (which is rare) and people will
        //rarely submit reports far in the past, this is fine.
        for (StatsReport report : reports)
        {
            initialize(report);
        }
        return reports;
    }

}
