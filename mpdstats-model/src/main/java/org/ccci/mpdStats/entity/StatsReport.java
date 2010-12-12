package org.ccci.mpdStats.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.ccci.model.Employee;
import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.model.NewStaffTrainingSession;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.util.EffectiveSequencedRelationshipManagingList;
import org.ccci.util.strings.ToStringBuilder;
import org.ccci.util.strings.ToStringProperty;
import org.ccci.util.time.TimeUtil;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;

@Entity
@Table(name = "PS_MPD_STATS")
@NamedQueries({
    @NamedQuery(
        name = "StatsReport.findCompletedReportedDates", 
        query = "select report.key.reportDate " +
        		"from StatsReport report where report.key.employeeId = :employeeId " +
        		"and report.submitted = 'Y' " +
        		"order by report.key.reportDate"),
	@NamedQuery(
	    name = "StatsReport.getInProgressReport", 
	    query = "select report " +
	            "from StatsReport report where report.key.employeeId = :employeeId " +
	            "and report.submitted = 'N'"),
    @NamedQuery(
        name = "StatsReport.getReport", 
        query = "select report " +
                "from StatsReport report where report.key.employeeId = :employeeId " +
                "and report.key.reportDate = :reportDate"),
	/*
	 * I wanted to implement the following query more like 
	 * "select exists (select key from StatsReport where ...)", but couldn't figure out how
	 * to get this working
	 */
    @NamedQuery(
        name = "StatsReport.countReports", 
        query = "select count(report.key.employeeId) from StatsReport report " +
        		" where report.key.employeeId = :employeeId " +
                " and report.key.reportDate = :reportDate"),
    @NamedQuery(
        name = "StatsReport.getPreviousReport", 
        query = "select report " +
                "from StatsReport report where report.key.employeeId = :employeeId " +
                "and report.submitted = 'Y' " + //TODO: I think this breaks if the previous report is not submitted
                "and report.key.reportDate = (" +
                " select max(mostRecent.key.reportDate) from StatsReport mostRecent " +
                " where mostRecent.key.reportDate <= :reportDate " +
                " and mostRecent.key.employeeId = :employeeId)"),
    @NamedQuery(
        name = "StatsReport.getFutureReports", 
        query = "select report " +
                "from StatsReport report where report.key.employeeId = :employeeId " +
                "and report.submitted = 'Y' " +
                "and report.key.reportDate > :reportDate")})
public class StatsReport implements Serializable
{

    private static final String NULL_TOKEN_SHORT = " ";
    
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private StatsReportKey key;

    @Column(name = "BALANCE")
    private double balance = 0d;

    @Column(name = "PHONE")
    @Length(max = 24)
    private String telephone;

    /** the week number for this report. Counts up starting at 1. */
    @Column(name = "MPD_WEEKS", nullable = false)
    private int mpdWeekNumber;

    @Column(name = "LAST_PL_DATE")
    private Date lastPrayerLetterDate;

    /** 
     * e.g. "FA08", "WI09" 
     * Brian Pettitt and I decided it would have been better if this had been separated into two fields, but
     * it's not worth changing now.
     */
    @Column(name = "NST_SESSION", nullable = false)
    @Length(max = 4)
    @Pattern(regex = NewStaffTrainingSession.REGEX)
    @NotNull
    private String nstSession;

    @Column(name = "DIALS_A", nullable = false)
    private int dialsActual;
    
    @Column(name = "E_DIALS_A", nullable = false)
    private int dialsEContactsActual;

    @Column(name = "DIALS_G", nullable = false)
    private int dialsGoal;

    @Column(name = "TALKED_TO_A", nullable = false)
    private int talkedToActual;
    
    @Column(name = "E_TALKED_TO_A", nullable = false)
    private int talkedToEContactsActual;

    @Column(name = "TALKED_TO_G", nullable = false)
    private int talkedToGoal;

    @Column(name = "APPTS_SET_A", nullable = false)
    private int appointmentsSetActual;
    
    @Column(name = "E_APPTS_SET_A", nullable = false)
    private int appointmentsSetEContactsActual;

    @Column(name = "APPTS_SET_G", nullable = false)
    private int appointmentsSetGoal;

    @Column(name = "HOURS_ON_PHONE", nullable = false)
    private double hoursOnPhone;
    
    @Column(name = "HOURS_ON_E", nullable = false)
    private double hoursOnEContacts;

    @Column(name = "INDIV_APPT_A", nullable = false)
    private int individualAppointmentActual;

    @Column(name = "INDIV_APPT_G", nullable = false)
    private int individualAppointmentGoal;

    @Column(name = "GROUP_MEETINGS", nullable = false)
    private int groupMeetings;

    @Column(name = "GROUP_MEET_ATTEND", nullable = false)
    private int groupMeetAttend;

    @Column(name = "HOURS_ON_APPTS", nullable = false)
    private double hoursOnAppointments;

    @Column(name = "PRE_CALL_LETTERS", nullable = false)
    private int preCallLetters;

    @Column(name = "SUPPORT_LETTERS", nullable = false)
    private int supportLetters;

    @Column(name = "TY_REMIND_LTR", nullable = false)
    private int tyRemindLetter;

    @Column(name = "HOURS_CORRESPOND", nullable = false)
    private double hoursCorrespond;

    @Column(name = "HOURS_DECISIONS", nullable = false)
    private double hoursDecisions;

    @Column(name = "HOURS_APPT_REF", nullable = false)
    private double hoursReferralAppointments;

    @Column(name = "HOURS_PL", nullable = false)
    private double hoursPrayerLetter;

    @Column(name = "HOURS_OTHER1", nullable = false)
    private double hoursOther1;

    @Column(name = "HOURS_OTHER_DESC1")
    @NotNull
    @Length(max = 55)
    private String hoursOtherDesc1 = NULL_TOKEN_SHORT;

    @Column(name = "HOURS_OTHER2", nullable = false)
    private double hoursOther2;

    @Column(name = "HOURS_OTHER_DESC2")
    @NotNull
    @Length(max = 55)
    private String hoursOtherDesc2 = NULL_TOKEN_SHORT;

    @Column(name = "HOURS_OTHER_STTL", nullable = false)
    private double hoursOtherSttl;

    @Column(name = "HOURS_TOTAL_A", nullable = false)
    private double hoursTotalActual;

    @Column(name = "HOURS_TOTAL_G", nullable = false)
    private double hoursTotalGoal;

    @Column(name = "NT_SURVEY_SESS", nullable = false)
    private int ntSurveySession;

    @Column(name = "HOURS_NT_SURVEY", nullable = false)
    private double hoursNtSurvey;

    @Column(name = "HOURS_ESCOM", nullable = false)
    private double hoursEveryStudentDotCom;

    @Column(name = "HOURS_NON_MPD", nullable = false)
    private double hoursNonMpd;

    @Column(name = "HOURS_OTHER_JOB", nullable = false)
    private double hoursOtherJob;

    @Column(name = "COACH_CALLED", nullable = false, length = 1)
    private String coachCalled = NULL_TOKEN_SHORT;

    @Column(name = "WIFES_COACH_CALLED", nullable = false, length = 1)
    private String wifesCoachCalled = NULL_TOKEN_SHORT;

    @Column(name = "TEAM_CALLED", nullable = false, length = 1)
    private String teamCalled = NULL_TOKEN_SHORT;

    @Column(name = "HOW_ARE_YOU", nullable = false, length = 1)
    private String howAreYou = NULL_TOKEN_SHORT;

    @Column(name = "SUPPORT_GOAL", nullable = false)
    private double supportGoal;

    @Column(name = "NEW_MONTHLY_A", nullable = false)
    private double newMonthlyActual;

    @Column(name = "LOST_MONTHLY", nullable = false)
    private double lostMonthly;
    
    @Column(name = "NEW_MONTHLY_G", nullable = false)
    private double newMonthlyGoal;

    /** cumulative total of monthly support raised to date */
    @Column(name = "SUPPORT_RAISED", nullable = false)
    private double supportRaised;

    /** where the staff person would be at if he/she were exactly meeting weekly goals */
    @Column(name = "SUPPORT_GOAL_TD", nullable = false)
    private double supportGoalToDate;

    @Column(name = "NEW_PARTNERS_A", nullable = false)
    private int newPartnersActual;

    @Column(name = "LOST_PARTNERS", nullable = false)
    private int lostPartners;
    
    @Column(name = "NEW_PARTNERS_G", nullable = false)
    private int newPartnersGoal;

    @Column(name = "SPECIAL_GOAL", nullable = false)
    private double specialGoal;

    /** needs raised this report period*/
    @Column(name = "NEW_SPECIAL_A", nullable = false)
    private double newSpecialActual;

    /** cumulative total of special needs raised to date */
    @Column(name = "SPECIAL_RAISED", nullable = false)
    private double specialRaised;

    @Column(name = "CONTACTS_ON_HAND", nullable = false)
    private int contactsOnHand;

    @Column(name = "REFERRALS_GAINED", nullable = false)
    private int referralsGained;

    @Column(name = "REFERRALS_GOAL", nullable = false)
    private int referralsGoal;

    @Column(name = "REFERRALS_OLD", nullable = false)
    private int referralsOld;

    @Column(name = "BILLS_OK", nullable = false, length = 1)
    private String billsOk = NULL_TOKEN_SHORT;

    @Column(name = "TEST_DT")
    private Date testDate;
    
    
    
    /**
     * "Y" or "N"
     */
    @Column(name = "SUBMITTED", length = 1)
    @NotNull
    private String submitted;

    
    /* The following @Transient fields are relationships not managed by JPA.
     * I didn't have time to model this yet, though I hope to someday.
     */
    
    @Transient
    private Employee employee;
    
    @Transient
    private Comment comment;
    
    @Transient
    private Improvement improvement;
    
    @Transient
    private PrayerRequest prayerRequest;
    
    @Transient
    private List<MonthlyCommitment> monthlyCommitments = Lists.newArrayList();

    @Transient
    private List<SpecialCommitment> specialCommitments = Lists.newArrayList();
    
    
    
    protected StatsReport()
    {
        key = new StatsReportKey();
    }

    public StatsReport(EmployeeId employeeId, ReportPeriod reportPeriod)
    {
        key = new StatsReportKey(employeeId, reportPeriod);
        comment = new Comment(employeeId, reportPeriod);
        improvement = new Improvement(employeeId, reportPeriod);
        prayerRequest = new PrayerRequest(employeeId, reportPeriod);
    }

    @ToStringProperty
    public ReportPeriod getReportPeriod()
    {
        return key.getReportPeriod();
    }

    @ToStringProperty
    public EmployeeId getEmployeeId()
    {
        return key.getEmployeeId();
    }
    
    public void updateReportPeriod(ReportPeriod newReportPeriod)
    {
        key = new StatsReportKey(key.getEmployeeId(), newReportPeriod);
        if (comment != null)
        {
            comment.updateReportPeriod(newReportPeriod);
        }
        if (improvement != null)
        {
            improvement.updateReportPeriod(newReportPeriod);
        }
        if (prayerRequest != null)
        {
            prayerRequest.updateReportPeriod(newReportPeriod);
        }
        
        for (MonthlyCommitment monthlyCommitment : monthlyCommitments)
        {
            monthlyCommitment.updateReportPeriod(newReportPeriod);
        }
        for (SpecialCommitment specialCommitment : specialCommitments)
        {
            specialCommitment.updateReportPeriod(newReportPeriod);
        }
    }

    
    /*
     * Logic-y getters/setters
     */

    /**
     * Only DAO is allowed to call this
     * @param monthlyCommitments
     */
    public void setMonthlyCommitments(List<MonthlyCommitment> monthlyCommitments)
    {
        this.monthlyCommitments = monthlyCommitments;
    }
    
    /**
     * Only DAO is allowed to call this
     * @param specialCommitments
     */
    public void setSpecialCommitments(List<SpecialCommitment> specialCommitments)
    {
        this.specialCommitments = specialCommitments;
    }
    
    public List<MonthlyCommitment> getMonthlyCommitments()
    {
        return new MonthlyCommitmentList(monthlyCommitments, this);
    }
    
    public List<SpecialCommitment> getSpecialCommitments()
    {
        return new SpecialCommitmentList(specialCommitments, this);
    }

    
    public boolean isSubmitted()
    {
        return nonNullableShortStringToBoolean(submitted);
    }

    public void setSubmitted(boolean submitted)
    {
        this.submitted = booleanToNonNullableShortString(submitted);
    }

    public Boolean getCoachCalled()
    {
        return nonNullableShortStringToBoolean(coachCalled);
    }

    public void setCoachCalled(Boolean coachCalled)
    {
        this.coachCalled = booleanToNonNullableShortString(coachCalled);
    }

    public Boolean getWifesCoachCalled()
    {
        return nonNullableShortStringToBoolean(wifesCoachCalled);
    }

    public void setWifesCoachCalled(Boolean wifesCoachCalled)
    {
        this.wifesCoachCalled = booleanToNonNullableShortString(wifesCoachCalled);
    }
    
    public Boolean getTeamCalled()
    {
        return nonNullableShortStringToBoolean(teamCalled);
    }

    public void setTeamCalled(Boolean teamCalled)
    {
        this.teamCalled = booleanToNonNullableShortString(teamCalled);
    }

    public String getHowAreYou()
    {
        return nonNullableStringToNullableString(howAreYou);
    }

    public void setHowAreYou(String howAreYou)
    {
        this.howAreYou = nullableStringToNonNullableString(howAreYou);
    }

    public String getBillsOk()
    {
        return nonNullableStringToNullableString(billsOk);
    }

    public void setBillsOk(String billsOk)
    {
        this.billsOk = nullableStringToNonNullableString(billsOk);
    }

    public String getHoursOtherDesc1()
    {
        return nonNullableStringToNullableString(hoursOtherDesc1);
    }
    
    public void setHoursOtherDesc1(String hoursOtherDesc1)
    {
        this.hoursOtherDesc1 = nullableStringToNonNullableString(hoursOtherDesc1);
    }

    public String getHoursOtherDesc2()
    {
        return nonNullableStringToNullableString(hoursOtherDesc2);
    }

    public void setHoursOtherDesc2(String hoursOtherDesc2)
    {
        this.hoursOtherDesc2 = nullableStringToNonNullableString(hoursOtherDesc2);
    }
    
    public LocalDate getTestDate()
    {
        return TimeUtil.sqlDateToLocalDate(testDate);
    }

    public void setTestDate(LocalDate examDate)
    {
        this.testDate = TimeUtil.localDateToSqlDate(examDate);
    }
    
    
    /*
     * Conversion Logic
     * 
     * Peoplesoft doesn't allow null strings, so we use a single space (" "). We would use empty strings,
     * but Oracle doesn't distinguish between empty strings and NULLs. However, we want to avoid single spaces in
     * java code as much as possible, so here we convert single spaces to nulls and vice versa.
     * 
     * We also convert Y/N into Booleans
     * 
     * TODO: investigate persistence-impl level conversion, e.g. http://www.hibernate.org/169.html
     */
    
    private Boolean nonNullableShortStringToBoolean(String string)
    {
        return string.equals(NULL_TOKEN_SHORT) ? null : "Y".equals(string);
    }

    private String booleanToNonNullableShortString(Boolean booleanValue)
    {
        return booleanValue == null ? NULL_TOKEN_SHORT : booleanValue ? "Y" : "N";
    }
    
    private String nullableStringToNonNullableString(String string)
    {
        return string == null || string.isEmpty() ? NULL_TOKEN_SHORT : string;
    }
    
    private String nonNullableStringToNullableString(String string)
    {
        return string.equals(NULL_TOKEN_SHORT) ? null : string;
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).toString();
    }
    

    
    /*
     * straight getters and setters
     */

    public Employee getEmployee()
    {
        return employee;
    }

    public void setEmployee(Employee employee)
    {
        this.employee = employee;
    }

    public Comment getComment()
    {
        return comment;
    }

    public void setComment(Comment comment)
    {
        this.comment = comment;
    }

    public Improvement getImprovement()
    {
        return improvement;
    }

    public void setImprovement(Improvement improvement)
    {
        this.improvement = improvement;
    }

    public PrayerRequest getPrayerRequest()
    {
        return prayerRequest;
    }

    public void setPrayerRequest(PrayerRequest prayer)
    {
        this.prayerRequest = prayer;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }

    public NewStaffTrainingSession getNstSession()
    {
        return NewStaffTrainingSession.valueOf(nstSession);
    }

    public void setNstSession(NewStaffTrainingSession nstSession)
    {
        this.nstSession = nstSession.toString();
    }
    
    public int getMpdWeekNumber()
    {
        return mpdWeekNumber;
    }

    public void setMpdWeekNumber(int mpdWeeks)
    {
        this.mpdWeekNumber = mpdWeeks;
    }

    public int getDialsActual()
    {
        return dialsActual;
    }

    public void setDialsActual(int dialsA)
    {
        this.dialsActual = dialsA;
    }
    
    public int getDialsEContactsActual()
    {
        return dialsEContactsActual;
    }

    public void setDialsEContactsActual(int dialsEContactsActual)
    {
        this.dialsEContactsActual = dialsEContactsActual;
    }

    public int getDialsGoal()
    {
        return dialsGoal;
    }

    public void setDialsGoal(int dialsG)
    {
        this.dialsGoal = dialsG;
    }

    public int getTalkedToActual()
    {
        return talkedToActual;
    }

    public void setTalkedToActual(int talkedToA)
    {
        this.talkedToActual = talkedToA;
    }

    public int getTalkedToEContactsActual()
    {
        return talkedToEContactsActual;
    }

    public void setTalkedToEContactsActual(int talkedToEContactsActual)
    {
        this.talkedToEContactsActual = talkedToEContactsActual;
    }

    public int getTalkedToGoal()
    {
        return talkedToGoal;
    }

    public void setTalkedToGoal(int talkedToG)
    {
        this.talkedToGoal = talkedToG;
    }

    public int getAppointmentsSetActual()
    {
        return appointmentsSetActual;
    }

    public void setAppointmentsSetActual(int apptsSetA)
    {
        this.appointmentsSetActual = apptsSetA;
    }

    public int getAppointmentsSetEContactsActual()
    {
        return appointmentsSetEContactsActual;
    }

    public void setAppointmentsSetEContactsActual(int appointmentsSetEContactsActual)
    {
        this.appointmentsSetEContactsActual = appointmentsSetEContactsActual;
    }

    public int getAppointmentsSetGoal()
    {
        return appointmentsSetGoal;
    }

    public void setAppointmentsSetGoal(int apptsSetG)
    {
        this.appointmentsSetGoal = apptsSetG;
    }

    public double getHoursOnPhone()
    {
        return hoursOnPhone;
    }

    public void setHoursOnPhone(double hoursOnPhone)
    {
        this.hoursOnPhone = hoursOnPhone;
    }

    public double getHoursOnEContacts()
    {
        return hoursOnEContacts;
    }

    public void setHoursOnEContacts(double hoursOnEContacts)
    {
        this.hoursOnEContacts = hoursOnEContacts;
    }

    public int getIndividualAppointmentActual()
    {
        return individualAppointmentActual;
    }

    public void setIndividualAppointmentActual(int indivApptA)
    {
        this.individualAppointmentActual = indivApptA;
    }

    public int getIndividualAppointmentGoal()
    {
        return individualAppointmentGoal;
    }

    public void setIndividualAppointmentGoal(int indivApptG)
    {
        this.individualAppointmentGoal = indivApptG;
    }

    public int getGroupMeetings()
    {
        return groupMeetings;
    }

    public void setGroupMeetings(int groupMeetings)
    {
        this.groupMeetings = groupMeetings;
    }

    public int getGroupMeetAttend()
    {
        return groupMeetAttend;
    }

    public void setGroupMeetAttend(int groupMeetAttend)
    {
        this.groupMeetAttend = groupMeetAttend;
    }

    public double getHoursOnAppointments()
    {
        return hoursOnAppointments;
    }

    public void setHoursOnAppointments(double hoursOnAppts)
    {
        this.hoursOnAppointments = hoursOnAppts;
    }

    public int getPreCallLetters()
    {
        return preCallLetters;
    }

    public void setPreCallLetters(int preCallLetters)
    {
        this.preCallLetters = preCallLetters;
    }

    public int getSupportLetters()
    {
        return supportLetters;
    }

    public void setSupportLetters(int supportLetters)
    {
        this.supportLetters = supportLetters;
    }

    public int getTyRemindLetter()
    {
        return tyRemindLetter;
    }

    public void setTyRemindLetter(int tyRemindLtr)
    {
        this.tyRemindLetter = tyRemindLtr;
    }

    public double getHoursCorrespond()
    {
        return hoursCorrespond;
    }

    public void setHoursCorrespond(double hoursCorrespond)
    {
        this.hoursCorrespond = hoursCorrespond;
    }

    public double getHoursDecisions()
    {
        return hoursDecisions;
    }

    public void setHoursDecisions(double hoursDecisions)
    {
        this.hoursDecisions = hoursDecisions;
    }

    public double getHoursReferralAppointments()
    {
        return hoursReferralAppointments;
    }

    public void setHoursReferralAppointments(double hoursApptRef)
    {
        this.hoursReferralAppointments = hoursApptRef;
    }

    public double getHoursPrayerLetter()
    {
        return hoursPrayerLetter;
    }

    public void setHoursPrayerLetter(double hoursPl)
    {
        this.hoursPrayerLetter = hoursPl;
    }

    public double getHoursOther1()
    {
        return hoursOther1;
    }

    public void setHoursOther1(double hoursOther1)
    {
        this.hoursOther1 = hoursOther1;
    }

    public double getHoursOther2()
    {
        return hoursOther2;
    }

    public void setHoursOther2(double hoursOther2)
    {
        this.hoursOther2 = hoursOther2;
    }

    public double getHoursOtherSttl()
    {
        return hoursOtherSttl;
    }

    public void setHoursOtherSttl(double hoursOtherSttl)
    {
        this.hoursOtherSttl = hoursOtherSttl;
    }

    public double getHoursTotalActual()
    {
        return hoursTotalActual;
    }

    public void setHoursTotalActual(double hoursTotalA)
    {
        this.hoursTotalActual = hoursTotalA;
    }

    public double getHoursTotalGoal()
    {
        return hoursTotalGoal;
    }

    public void setHoursTotalGoal(double hoursTotalG)
    {
        this.hoursTotalGoal = hoursTotalG;
    }

    public int getNtSurveySession()
    {
        return ntSurveySession;
    }

    public void setNtSurveySession(int ntSurveySess)
    {
        this.ntSurveySession = ntSurveySess;
    }

    public double getHoursNtSurvey()
    {
        return hoursNtSurvey;
    }

    public void setHoursNtSurvey(double hoursNtSurvey)
    {
        this.hoursNtSurvey = hoursNtSurvey;
    }

    public double getHoursEveryStudentDotCom()
    {
        return hoursEveryStudentDotCom;
    }

    public void setHoursEveryStudentDotCom(double hoursEscom)
    {
        this.hoursEveryStudentDotCom = hoursEscom;
    }

    public double getHoursNonMpd()
    {
        return hoursNonMpd;
    }

    public void setHoursNonMpd(double hoursNonMpd)
    {
        this.hoursNonMpd = hoursNonMpd;
    }

    public double getHoursOtherJob()
    {
        return hoursOtherJob;
    }

    public void setHoursOtherJob(double hoursOtherJob)
    {
        this.hoursOtherJob = hoursOtherJob;
    }

    public double getSupportGoal()
    {
        return supportGoal;
    }

    public void setSupportGoal(double supportGoal)
    {
        this.supportGoal = supportGoal;
    }

    public double getNewMonthlyActual()
    {
        return newMonthlyActual;
    }

    public void setNewMonthlyActual(double newMonthlyA)
    {
        this.newMonthlyActual = newMonthlyA;
    }

    public double getNewMonthlyGoal()
    {
        return newMonthlyGoal;
    }

    public void setNewMonthlyGoal(double newMonthlyG)
    {
        this.newMonthlyGoal = newMonthlyG;
    }

    public double getSupportRaised()
    {
        return supportRaised;
    }

    public void setSupportRaised(double supportRaised)
    {
        this.supportRaised = supportRaised;
    }

    public double getSupportGoalToDate()
    {
        return supportGoalToDate;
    }

    public void setSupportGoalToDate(double supportGoalTd)
    {
        this.supportGoalToDate = supportGoalTd;
    }

    public int getNewPartnersActual()
    {
        return newPartnersActual;
    }

    public void setNewPartnersActual(int newPartnersA)
    {
        this.newPartnersActual = newPartnersA;
    }

    public int getNewPartnersGoal()
    {
        return newPartnersGoal;
    }

    public void setNewPartnersGoal(int newPartnersG)
    {
        this.newPartnersGoal = newPartnersG;
    }

    public double getSpecialGoal()
    {
        return specialGoal;
    }

    public void setSpecialGoal(double specialGoal)
    {
        this.specialGoal = specialGoal;
    }

    public double getNewSpecialActual()
    {
        return newSpecialActual;
    }

    public void setNewSpecialActual(double newSpecialA)
    {
        this.newSpecialActual = newSpecialA;
    }

    public double getSpecialRaised()
    {
        return specialRaised;
    }

    public void setSpecialRaised(double specialRaised)
    {
        this.specialRaised = specialRaised;
    }

    public int getContactsOnHand()
    {
        return contactsOnHand;
    }

    public void setContactsOnHand(int contactsOnHand)
    {
        this.contactsOnHand = contactsOnHand;
    }

    public int getReferralsGained()
    {
        return referralsGained;
    }

    public void setReferralsGained(int referralsGained)
    {
        this.referralsGained = referralsGained;
    }

    public int getReferralsGoal()
    {
        return referralsGoal;
    }

    public void setReferralsGoal(int referralsGoal)
    {
        this.referralsGoal = referralsGoal;
    }

    public int getReferralsOld()
    {
        return referralsOld;
    }

    public void setReferralsOld(int referralsOld)
    {
        this.referralsOld = referralsOld;
    }

    public LocalDate getLastPrayerLetterDate()
    {
        return TimeUtil.sqlDateToLocalDate(lastPrayerLetterDate);
    }

    public void setLastPrayerLetterDate(LocalDate lastPrayerLetterDate)
    {
        this.lastPrayerLetterDate = TimeUtil.localDateToSqlDate(lastPrayerLetterDate);
    }

    public double getBalance()
    {
        return balance;
    }

    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    
    public double getLostMonthly()
    {
        return lostMonthly;
    }

    public void setLostMonthly(double lostMonthly)
    {
        this.lostMonthly = lostMonthly;
    }

    public int getLostPartners()
    {
        return lostPartners;
    }

    public void setLostPartners(int lostPartners)
    {
        this.lostPartners = lostPartners;
    }


    static class MonthlyCommitmentList extends EffectiveSequencedRelationshipManagingList<MonthlyCommitment, StatsReport>
    {
        private static final long serialVersionUID = 1L;
        
        public MonthlyCommitmentList(List<MonthlyCommitment> delegate, StatsReport parent)
        {
            super(delegate, "report", parent);
        }
    }
    
    static class SpecialCommitmentList extends EffectiveSequencedRelationshipManagingList<SpecialCommitment, StatsReport>
    {
        private static final long serialVersionUID = 1L;
        
        public SpecialCommitmentList(List<SpecialCommitment> delegate, StatsReport parent)
        {
            super(delegate, "report", parent);
        }
    }

}
