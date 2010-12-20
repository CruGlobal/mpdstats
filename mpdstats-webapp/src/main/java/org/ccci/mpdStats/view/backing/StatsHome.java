package org.ccci.mpdStats.view.backing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.ccci.debug.RecordExceptions;
import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.entity.MpdGoals;
import org.ccci.mpdStats.entity.StatsReport;
import org.ccci.mpdStats.model.NewStaffTrainingSession;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.mpdStats.model.NewStaffTrainingSession.Season;
import org.ccci.mpdStats.session.AccountService;
import org.ccci.mpdStats.session.ReportProcess;
import org.ccci.util.Math;
import org.ccci.util.time.Year;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.joda.time.LocalDate;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Name("statsHome")
@RecordExceptions
public class StatsHome
{
    private String sessionYear;
    private String sessionSeason;
    private LocalDate reportEndDate;
    private int numberOfNtSurveySessions = 20;
    
    @In ReportProcess reportProcess;

    @In AccountService accountService;
    
    @In FacesMessages facesMessages;
    
    @Create
    public void create() throws ParseException
    {
        reportProcess.init();
        initTranslatedFields();
    }
    
    private void initTranslatedFields()
    {
        reportEndDate = getReport().getReportPeriod().getEndDate();
        sessionSeason = getReport().getNstSession().getSeason().toString();
        sessionYear = getReport().getNstSession().getYearAsString();
    }

    // ==========================================
    //            action methods
    // ==========================================

    @Transactional
    public String save()
    {
        if (saveInternal())
        {
            facesMessages.add("Report Saved");
            return "saved";
        }
        return null;
    }
    
    @Transactional
    public String saveAndContinue()
    {
        if (saveInternal())
        {
            return "saved";
        }
        return null;
    }
    
    @Transactional
    public void updateGoals()
    {
        saveInternal();
    }

    /**
     * Performs the following:
     * <ol>
     *  <li>updates the nst session</li>
     *  <li>updates the mpd goals</li>
     *  <li>updates the report end date (only if this is a new report)</li>
     * </ol>
     * If all are successful, the report is saved. Otherwise, 
     * appropriate user messages are added.
     * 
     * returns true if a save was performed
     */
    private boolean saveInternal()
    {
        Preconditions.checkState(!getReport().isSubmitted(), "report is already submitted!");
        reportProcess.updateGoals();
        boolean save = attemptUpdateNstSession();
        if (isNew())
        {
            save = attemptUpdateReportEndDate() && save;
        }
        
        if (save)
        {
            reportProcess.save();
            return true;
        }
        return false;
    }

    private boolean attemptUpdateReportEndDate()
    {
        ReportPeriod reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportEndDate);
        if (!getReport().getReportPeriod().equals(reportPeriod))
        {
            boolean successful = reportProcess.updateEndDate(reportPeriod);
            if (!successful)
            {
                return false;
            }
        }
        return true;
    }

    private boolean attemptUpdateNstSession()
    {
        NewStaffTrainingSession newStaffTrainingSession = NewStaffTrainingSession.valueOf(sessionSeason, sessionYear);
        if (!getReport().getNstSession().equals(newStaffTrainingSession))
        {
            boolean successful = reportProcess.updateNstSession(newStaffTrainingSession);
            if (!successful)
            {
                return false;
            }
        }
        return true;
    }
    
    public void validateReportEndDate(FacesContext context, UIComponent toValidate,
                                      Object value) throws ValidatorException
    {
        Preconditions.checkNotNull(value, "value is null");
        Preconditions.checkArgument(value instanceof LocalDate, 
            "value (%s) is of type %s, not of type %s", 
            value, value.getClass(), LocalDate.class);
        LocalDate reportPeriodEnd = (LocalDate) value;

        LocalDate thisSaturday = ReportPeriod.getReportPeriodEndForDayInPeriod(new LocalDate());
        
        
        if (!ReportPeriod.isAValidReportPeriodEndDate(reportPeriodEnd))
        {
            throw new ValidatorException(new FacesMessage(
               String.format("%s is not a Saturday; please choose a date that is", reportPeriodEnd)));
        }
        if ( reportPeriodEnd.isAfter(thisSaturday) )
        {
            throw new ValidatorException(new FacesMessage(
                String.format("%s is after this Saturday. Please choose this Saturday %s or a previous Saturday.", reportPeriodEnd, thisSaturday)));
        }
        
    }
    
    public void validateLastPrayerLetterDate(FacesContext context, UIComponent toValidate,
    										 Object value) throws ValidatorException
    {
        Preconditions.checkArgument(value instanceof LocalDate, 
                "value (%s) is of type %s, not of type %s", 
                value, value.getClass(), LocalDate.class);
        
        LocalDate lastPrayerLetterDate = (LocalDate) value;
        LocalDate today = new LocalDate();
        
    	if (today.isBefore(lastPrayerLetterDate))
        {
            throw new ValidatorException(new FacesMessage(
                String.format("%s is not a past day or today.", lastPrayerLetterDate)));
        }
    	
    }
    

    public void validateHoursOtherJob(FacesContext context, UIComponent toValidate,
                                      Object value) throws ValidatorException
    {
        Preconditions.checkNotNull(value, "value is null");
        Preconditions.checkArgument(value instanceof Double, 
            "value (%s) is of type %s, not of type %s", 
            value, value.getClass(), Double.class);
        Double hoursOtherJob = (Double) value;
        if (!reportProcess.isValidHoursAtOtherJob(hoursOtherJob))
        {
            throw new ValidatorException(new FacesMessage(
                String.format("%s is too large; please choose a smaller value", hoursOtherJob)));
        }
    }
    
    
    // ==========================================
    //            Other
    // ==========================================

    
    public double getNtSurveySessionFraction()
    {
        return Math.safeDivide(getReport().getNtSurveySession(), numberOfNtSurveySessions);
    }
    
    public double getCurrentBalance()
    {
        return accountService.getBalance(reportProcess.getReportingEmployeeId());
    }
    
    public boolean isNew()
    {
        return reportProcess.isNew();
    }
    
    public double getHoursTotalActual()
    {
        return reportProcess.calculateTotalMpdHours();
    }
    
    public boolean isWifesCoachCalledVisible()
    {
        return reportProcess.isMarriedCoupleDoingMPDTogether();
    }
    
    public StatsReport getReport()
    {
        return reportProcess.getReport();
    }
    
    public MpdGoals getMpdGoals()
    {
        return reportProcess.getMpdGoals();
    }
    
    public List<SelectItem> getNstSessionSeasons()
    {
        List<SelectItem> nstSeason = new ArrayList<SelectItem>();
        List<Season> seasons = Arrays.asList(Season.values());
        for (Season season : seasons)
        {
            String displayedValue = season.toString();
            nstSeason.add(new SelectItem(displayedValue, displayedValue));
        }
        return nstSeason;
    }

    public List<SelectItem> getNstSessionYears()
    {
        List<SelectItem> nstYear = Lists.newArrayList();
        for (Year year : reportProcess.getPossibleNstYears())
        {
            String displayedValue = year.toString().substring(2, 4);
            nstYear.add(new SelectItem(displayedValue, displayedValue));
        }
        return nstYear;
    }
    
    public EmployeeId getReportingEmployeeId()
    {
        return reportProcess.getEmployeeUnit().getPrimary().getEmployeeId();
    }
    
    public String getReportingName()
    {
        return reportProcess.getEmployeeUnit().getName();
    }
    
    public String getReportingDepartmentName()
    {
        return reportProcess.getEmployeeUnit().getPrimary().getDepartmentName();
    }
    
    // ==========================================
    //            Straight getters/setters
    // ==========================================

    
    public LocalDate getReportEndDate()
    {
        return reportEndDate;
    }

    public void setReportEndDate(LocalDate reportEndDate)
    {
        this.reportEndDate = reportEndDate;
    }

    public void setNstSessionSeason(String season)
    {
        sessionSeason = season;
    }

    public void setNstSessionYear(String year)
    {
        sessionYear = year;
    }
    
    public String getNstSessionSeason()
    {
        return sessionSeason;
    }

    public String getNstSessionYear()
    {
        return sessionYear;
    }


}
