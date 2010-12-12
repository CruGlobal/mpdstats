package org.ccci.mpdStats.session;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.ccci.dao.EmployeeDAO;
import org.ccci.debug.RecordExceptions;
import org.ccci.model.EmployeeId;
import org.ccci.model.EmployeeUnit;
import org.ccci.mpdStats.dao.NewStaffTrainingSessionDAO;
import org.ccci.mpdStats.dao.ReportDAO;
import org.ccci.mpdStats.entity.MonthlyCommitment;
import org.ccci.mpdStats.entity.MpdGoals;
import org.ccci.mpdStats.entity.SpecialCommitment;
import org.ccci.mpdStats.entity.StatsReport;
import org.ccci.mpdStats.model.NewStaffTrainingSession;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.util.time.TimeUtil;
import org.ccci.util.time.Year;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Name("reportProcess")
@Scope(CONVERSATION)
@AutoCreate
@RecordExceptions
public class ReportProcess implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Logger Log log;
    
    
    @In EntityManager psEntityManager;
    
    @In EmployeeId loggedInEmployeeId;

    @In ReportDAO reportDAO;
    
    @In NewStaffTrainingSessionDAO newStaffTrainingSessionDAO;
    
    @In EmployeeDAO employeeDAO;
    
    @In AccountService accountService;
    
    @In LocalDate currentLocalDate;
    
    //FUTURE: use a view-layer independent message system
    @In FacesMessages facesMessages;

    private StatsReport report;
    
    private MpdGoals mpdGoals;

    /**
     * Will be an EmployeeCouple if the logged-in employee has a spouse.
     * For now, we assume that if they're married, then they're both raising support together.  This is
     * almost always the case for new staff.  They will submit one report for the both of them.
     */
    private EmployeeUnit employeeUnit;
    
    public void init()
    {
        initEmployees();
        initReport();
        initMpdGoals();
    }

    private void initEmployees()
    {
        employeeUnit = employeeDAO.findEmployeeUnit(loggedInEmployeeId);
    }

    private void initMpdGoals()
    {
        mpdGoals = getMpdGoals(((int) Math.floor(report.getHoursOtherJob())));
    }

    private void initReport()
    {
        EmployeeId reportingEmployeeId = getReportingEmployeeId();
        if (reportDAO.isReportInProgress(reportingEmployeeId))
        {
            report = reportDAO.getInProgressReport(reportingEmployeeId);
        }
        else
        {
            List<ReportPeriod> completedReportPeriods = reportDAO.findCompletedReportPeriods(reportingEmployeeId);
            ReportPeriod reportPeriod;
            if (completedReportPeriods.isEmpty())
            {
                NewStaffTrainingSession mostRecentNewStaffTrainingSession = newStaffTrainingSessionDAO.getMostRecentNewStaffTrainingSession(currentLocalDate);
                reportPeriod = getFirstReportPeriodOfSeason(mostRecentNewStaffTrainingSession);
            }
            else
            {
                reportPeriod = getSuggestedReportPeriod(completedReportPeriods);
            }
            report = createNewReport(reportPeriod);
        }
    }

    public EmployeeId getReportingEmployeeId()
    {
        return employeeUnit.getPrimary().getEmployeeId();
    }

    private ReportPeriod getFirstReportPeriodOfSeason(NewStaffTrainingSession newStaffTrainingSession)
    {
        return newStaffTrainingSessionDAO.getFirstReportPeriod(newStaffTrainingSession);
    }

    /**
     * Gets the report period directly after the latest completed report 
     * @param completedReportPeriods must be non-empty, and sorted ascending
     * @return
     */
    ReportPeriod getSuggestedReportPeriod(List<ReportPeriod> completedReportPeriods)
    {
        ReportPeriod mostRecent = completedReportPeriods.get(completedReportPeriods.size() - 1);
        return mostRecent.nextReportPeriod();
    }


    /**
     * Creates a new report and initializes some fields.
     * If there are any existing reports, use the most recent to pre-populate certain fields in the 
     * new report.  
     * Pre-populate some based on the employee's info.
     * @param reportPeriod 
     * @return
     */
    private StatsReport createNewReport(ReportPeriod reportPeriod)
    {

        StatsReport old = reportDAO.getPreviousReport(getReportingEmployeeId(), reportPeriod);
        StatsReport report;
        if (old == null)
        {
            report = createFirstReport(reportPeriod);
        }
        else
        {
            report = createReportUsingPreviousReport(reportPeriod, old);
        }
        return report;
    }

    private StatsReport createReportUsingPreviousReport(ReportPeriod reportPeriod, StatsReport old)
    {
        StatsReport report = new StatsReport(getReportingEmployeeId(), reportPeriod);
        report.setEmployee(employeeUnit.getPrimary());
        report.setSubmitted(false);
        report.setMpdWeekNumber(calculateMpdWeekNumber(reportPeriod, old.getNstSession()));
        
        report.setTelephone(old.getTelephone());
        report.setNstSession(old.getNstSession());
        report.setContactsOnHand(old.getContactsOnHand());
        report.setLastPrayerLetterDate(old.getLastPrayerLetterDate());
        report.setReferralsOld(old.getReferralsOld());
        report.setSupportGoal(old.getSupportGoal());
        report.setSpecialGoal(old.getSpecialGoal());
        report.setHoursOtherJob(old.getHoursOtherJob());
        report.setTestDate(old.getTestDate());
        report.setNtSurveySession(old.getNtSurveySession());
        
        report.getPrayerRequest().setComments(old.getPrayerRequest().getComments());
        
        report.setHoursOtherDesc1(old.getHoursOtherDesc1());
        report.setHoursOtherDesc2(old.getHoursOtherDesc2());
        
        for (MonthlyCommitment oldMonthlyCommitment : old.getMonthlyCommitments())
        {
            report.getMonthlyCommitments().add(MonthlyCommitment.createUsingPrevious(oldMonthlyCommitment));
        }
        
        for (SpecialCommitment oldSpecialCommitment : old.getSpecialCommitments())
        {
            report.getSpecialCommitments().add(SpecialCommitment.createUsingPrevious(oldSpecialCommitment));
        }
        
        return report;
    }

    private StatsReport createFirstReport(ReportPeriod reportPeriod)
    {
        StatsReport report = new StatsReport(getReportingEmployeeId(), reportPeriod);
        report.setEmployee(employeeUnit.getPrimary());
        report.setSubmitted(false);
        report.setTelephone(employeeUnit.getPrimary().getPhone());
        report.setMpdWeekNumber(1);
        report.setNstSession(newStaffTrainingSessionDAO.getMostRecentNewStaffTrainingSession(reportPeriod.getEndDate()));
        return report;
    }

    /**
     * Returns true if this report has not yet been saved to the database
     */
    public boolean isNew()
    {
        return !psEntityManager.contains(report);
    }
    
    /**
     * Given the new amount of hours-per-week worked at another job, update
     * the {@link MpdGoals} for this staffmember
     */
    public void updateGoals()
    {
        mpdGoals = getMpdGoals(report.getHoursOtherJob());
    }

    public boolean isValidHoursAtOtherJob(double hoursAtOtherJob)
    {
        try
        {
            getMpdGoals(hoursAtOtherJob);
            return true;
        }
        catch(NoResultException e)
        {
            return false;
        }
    }
    
    private MpdGoals getMpdGoals(double hoursAtOtherJob)
    {
        return (MpdGoals) psEntityManager
            .createNamedQuery("MpdGoals.getExactGoalsByHoursWorked")
            .setParameter("hours", (int) Math.floor(hoursAtOtherJob))
            .setParameter("reportDate", TimeUtil.localDateToSqlDate(report.getReportPeriod().getEndDate()))
            .getSingleResult();
    }

    /**
     * Save all entered data to the database
     */
    public void save()
    {
    	log.info("saving report for week #0", report.getMpdWeekNumber());
        report = reportDAO.saveReport(report);
    }
    
    /**
     * Sets the status to submitted, fills in the mpd goals for this report as well as staff balance,
     * and saves to the database
     * @return true if submission is successful; false otherwise.  Appropriate faces messages will be added.
     */
    public boolean submitReport()
    {
        if (!validate())
        {
        	log.info("submission failed for week #0", report.getMpdWeekNumber());
            return false;
        }
        
        setAllGoalFields();
        setAccountBalance();
        setTotalMpdHoursWorked();
        updateCumulativeTotals(getPreviousReport(), report);
        report.setSubmitted(true);
        reportDAO.saveReport(report);
        updateCumulativeTotalsInFutureReports();
        facesMessages.add("Your MPD report for week #0 was submitted successfully", report.getMpdWeekNumber());
        log.info("submission successful for week #0", report.getMpdWeekNumber());
        return true;
    }

    //TODO: the field labels are copy/pasted from statsHome.xhtml and provision.xhtml; this should be fixed
    private boolean validate()
    {
        boolean success = true;
        if (report.getHowAreYou() == null)
        {
            facesMessages.add("Please answer the 'How are you?' question on the previous page");
            success = false;
        }
        if (report.getBillsOk() == null)
        {
            facesMessages.add("Please answer the 'Do you have enough money coming in to handle your bills?' question");
            success = false;
        }
        return success;
    }
    
    private void setAllGoalFields()
    {
        report.setAppointmentsSetGoal(mpdGoals.getAppointmentsSetGoal());
        report.setDialsGoal(mpdGoals.getDialsGoal());
        report.setIndividualAppointmentGoal(mpdGoals.getIndividualAppointmentsGoal());
        report.setNewPartnersGoal(mpdGoals.getNewPartnersGoal());
        report.setReferralsGoal(mpdGoals.getReferralsGainedGoal());
        report.setTalkedToGoal(mpdGoals.getTalkedToGoal());
        report.setHoursTotalGoal(mpdGoals.getHoursTotalGoal());
        report.setNewMonthlyGoal(mpdGoals.getNewMonthlyGoal());
    }

    private void setAccountBalance()
    {
        report.setBalance(accountService.getBalance(getReportingEmployeeId()));
    }

    private void setTotalMpdHoursWorked()
    {
        report.setHoursTotalActual(calculateTotalMpdHours());
    }
    
    private void updateCumulativeTotalsInFutureReports()
    {
        List<StatsReport> futureReports = reportDAO.getFutureReports(getReportingEmployeeId(), report.getReportPeriod()); 
        StatsReport precedingReport = report;
        for (StatsReport futureReport : futureReports)
        {
            updateCumulativeTotals(precedingReport, futureReport);
            reportDAO.saveReport(futureReport);
            precedingReport = futureReport;
        }
    }

    /**
     * Requires that {@code report} has had its newMonthlyGoal property set
     * @param precedingReport
     * @param report
     */
    private void updateCumulativeTotals(StatsReport precedingReport, StatsReport report)
    {
        report.setSupportRaised(calculateMonthlySupportRaisedToDate(precedingReport, report));
        report.setSpecialRaised(calculateSpecialSupportRaisedToDate(precedingReport, report));
        report.setSupportGoalToDate(calculateMonthlySupportRaisedToDateGoal(precedingReport, report, report.getNewMonthlyGoal()));
    }

    /**
     * change the current report's {@link ReportPeriod} to the given {@code reportPeriod}, returning false if this
     * date already has a report. If false is returned, a user message will be added to the current FacesContext 
     * 
     * @param reportPeriod
     */
    public boolean updateEndDate(ReportPeriod reportPeriod)
    {
        Preconditions.checkState(isNew());
        Preconditions.checkArgument(reportPeriod != null, "reportPeriod is null");
        Preconditions.checkArgument(!reportPeriod.equals(report.getReportPeriod()), "reportPeriod is not different");
        if (reportDAO.doesReportExist(getReportingEmployeeId(), reportPeriod))
        {
            facesMessages.add("there is already a report for that period");
            return false;
        }
        report.updateReportPeriod(reportPeriod);
        updateMpdWeekNumber();
        return true;
    }

    private void updateMpdWeekNumber()
    {
        report.setMpdWeekNumber(calculateMpdWeekNumber(report.getReportPeriod(), report.getNstSession()));
    }

    private int calculateMpdWeekNumber(ReportPeriod reportPeriod, NewStaffTrainingSession nstSession)
    {
        ReportPeriod firstReportPeriod = getFirstReportPeriodOfSeason(nstSession);
        int weeksPassedSinceFirstReportPeriod =
                Weeks.weeksBetween(firstReportPeriod.getEndDate(), reportPeriod.getEndDate()).getWeeks();
        return weeksPassedSinceFirstReportPeriod + 1;
    }


    /**
     * change the current report's NST session, returning false if that NST session has not been configured by an admin.
     * If false is returned, a user message will be added to the current FacesContext
     * @param newStaffTrainingSession
     * @return
     */
    public boolean updateNstSession(NewStaffTrainingSession newStaffTrainingSession)
    {
        if (!newStaffTrainingSessionDAO.getAvailableNewStaffTrainingSessions().contains(newStaffTrainingSession))
        {
            facesMessages.add("There is not a New Staff Training Session configured for that year and season (#0)", newStaffTrainingSession);
            return false;
        }
        else
        {
            report.setNstSession(newStaffTrainingSession);
            updateMpdWeekNumber();
            return true;
        }
    }
    
    public void removeMonthlyCommitment(MonthlyCommitment monthlyCommitment)
    {
        psEntityManager.remove(monthlyCommitment);
    }

    public void removeSpecialCommitment(SpecialCommitment specialCommitment)
    {
        psEntityManager.remove(specialCommitment);
    }

    public double calculateTotalMpdHours()
    {
        return 
            report.getHoursOnPhone() +
            report.getHoursOnEContacts() +
            report.getHoursOnAppointments() +
            report.getHoursCorrespond() +
            report.getHoursDecisions() +
            report.getHoursReferralAppointments() +
            report.getHoursPrayerLetter() +
            report.getHoursOther1() +
            report.getHoursOther2();
    }

    double calculateMonthlySupportRaisedToDate(StatsReport previousReport, StatsReport thisReport)
    {
        double previousMonthlySupportRaised = previousReport == null ? 0 : previousReport.getSupportRaised();
        return previousMonthlySupportRaised + thisReport.getNewMonthlyActual() - thisReport.getLostMonthly();
    }

    /**
     * Calculate the amount of monthly support the staff member would have if he/she had raised exactly the new-monthly-support goal 
     * for each week since the start of MPD
     * @param previousReport 
     * @param thisReport 
     * @param newMonthlyGoal the new monthly support goal for {@code thisReport}
     * @return
     */
    double calculateMonthlySupportRaisedToDateGoal(StatsReport previousReport, StatsReport thisReport, double newMonthlyGoal)
    {
        double calculatedTarget;
        if (previousReport == null)
        {
            ReportPeriod firstReportPeriodOfSeason = getFirstReportPeriodOfSeason(thisReport.getNstSession());
            int weeksSinceFirstReportPeriod = Weeks.weeksBetween(firstReportPeriodOfSeason.getEndDate(), 
                thisReport.getReportPeriod().getEndDate()).getWeeks();
            calculatedTarget = (weeksSinceFirstReportPeriod + 1) * newMonthlyGoal;
        }
        else
        {
            double previousSupportGoalToDate = previousReport.getSupportGoalToDate();
            int weeksSincePreviousReport = Weeks.weeksBetween(previousReport.getReportPeriod().getEndDate(), 
                thisReport.getReportPeriod().getEndDate()).getWeeks();
            double newMonthlySupportSincePreviousReport = previousReport.getNewMonthlyGoal() * (weeksSincePreviousReport - 1);
            calculatedTarget = previousSupportGoalToDate + newMonthlySupportSincePreviousReport  + newMonthlyGoal;
        }
        //if calculatedTarget is out of bounds, return the boundary instead
        return Math.max(0, Math.min(thisReport.getSupportGoal(), calculatedTarget));
    }

    double calculateSpecialSupportRaisedToDate(StatsReport previousReport, StatsReport thisReport)
    {
        double previousSpecialSupportRaised = previousReport == null ? 0 : previousReport.getSpecialRaised();
        return previousSpecialSupportRaised + thisReport.getNewSpecialActual();
    }

    private StatsReport getPreviousReport()
    {
        return reportDAO.getPreviousReport(getReportingEmployeeId(), report.getReportPeriod());
    }
    
    public boolean isMarriedCoupleDoingMPDTogether()
    {
        return employeeUnit.isCouple();
    }
    
    /**
     * returns the allowable years a user may pick from in choosing which NST season he/she attended
     */
    public List<Year> getPossibleNstYears()
    {
        List<Year> possibleYears = Lists.newArrayList();
        List<NewStaffTrainingSession> availableNewStaffTrainingSessions = newStaffTrainingSessionDAO.getAvailableNewStaffTrainingSessions();
        for (NewStaffTrainingSession session : availableNewStaffTrainingSessions)
        {
            Year availableYear = session.getYear();
            if (!possibleYears.contains(availableYear))
            {
                possibleYears.add(availableYear);
            }
        }
        return possibleYears;
    }

    public double calculateMonthlySupportRaisedToDateGoal()
    {
        return calculateMonthlySupportRaisedToDateGoal(getPreviousReport(), report, mpdGoals.getNewMonthlyGoal());
    }

    public double calculateMonthlySupportRaisedToDate()
    {
        return calculateMonthlySupportRaisedToDate(getPreviousReport(), report);
    }

    public double calculateSpecialSupportRaisedToDate()
    {
        return calculateSpecialSupportRaisedToDate(getPreviousReport(), report);
    }

    public StatsReport getReport()
    {
        return report;
    }

    public MpdGoals getMpdGoals()
    {
        return mpdGoals;
    }

    public EmployeeUnit getEmployeeUnit()
    {
        return employeeUnit;
    }

}
