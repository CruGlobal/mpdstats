package org.ccci.mpdStats.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.entity.Comment;
import org.ccci.mpdStats.entity.Improvement;
import org.ccci.mpdStats.entity.MonthlyCommitment;
import org.ccci.mpdStats.entity.PrayerRequest;
import org.ccci.mpdStats.entity.SpecialCommitment;
import org.ccci.mpdStats.entity.StatsReport;
import org.ccci.mpdStats.model.ReportPeriod;

public interface ReportDAO
{

    /**
     * Returns true if there is a {@link StatsReport} in the database that is not yet submitted
     * @param employeeId
     * @return
     * @throws NonUniqueResultException if more than one non-submitted {@link StatsReport} is in the database,
     * which should not happen
     */
    boolean isReportInProgress(EmployeeId employeeId);

    /**
     * Gets the StatsReport from the database that is not yet submitted.   
     * @param employeeId
     * @return a non-null {@link StatsReport}
     * @throws NoResultException if no report exists that is not submitted
     * @throws NonUniqueResultException if multiple reports exist that have not been submitted
     */
    StatsReport getInProgressReport(EmployeeId employeeId) throws NoResultException, NonUniqueResultException;

    /**
     * Merges the given {@link StatsReport} and all associated objects ( {@link Comment}, {@link Improvement}, {@link PrayerRequest}, {@link MonthlyCommitment}s, and {@link SpecialCommitment}s )
     * into the current persistence context, and then flushes.
     * @param report may be managed by the current entityManager, or not.  Should not be null.
     * @return the result of {@code entityManager.merge(report)}, associated with the managed versions
     * of {@code report}'s associated objects
     */
    StatsReport saveReport(StatsReport report);

    /**
     * Gets a sorted list of ReportPeriods for which the given employee has submitted a {@link StatsReport}
     * @param employeeId
     * @return
     */
    List<ReportPeriod> findCompletedReportPeriods(EmployeeId employeeId);

    /**
     * Gets the report for the given employee that was completed most recently whose report period is before the given report period.
     * @param employeeId should not be null
     * @param reportPeriod should not be null
     * @return the previous report, or null if none exists
     */
    StatsReport getPreviousReport(EmployeeId employeeId, ReportPeriod reportPeriod);

    /**
     * returns true if the employee with the given id has saved a report for the given {@link ReportPeriod} 
     * @param employeeId
     * @param reportPeriod
     * @return
     */
    boolean doesReportExist(EmployeeId employeeId, ReportPeriod reportPeriod);

    /**
     * returns the {@link StatsReport} for the given {@link ReportPeriod}
     * @param employeeId
     * @param reportPeriod
     * @return
     * @throws NoResultException if there is no {@link StatsReport} for the given {@link ReportPeriod} 
     */
    StatsReport getReport(EmployeeId employeeId, ReportPeriod reportPeriod);

    /**
     * Returns a sorted list of reports submitted by the given employee for periods after the given report period.
     * @param employeeId
     * @param reportPeriod
     * @return
     */
    List<StatsReport> getFutureReports(EmployeeId employeeId, ReportPeriod reportPeriod);

}
