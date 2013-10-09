package org.ccci.mpdStats.model;

import java.io.Serializable;

import org.ccci.util.Math;
import org.ccci.util.ValueObject;
import org.ccci.util.strings.ToStringBuilder;
import org.ccci.util.strings.ToStringProperty;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import com.google.common.base.Preconditions;

public class ReportPeriod extends ValueObject implements Serializable, Comparable<ReportPeriod>
{
    /**
     *  November 9 2008, a Sunday, was the beginning of a report period. 
     *  Report period boundaries are calculated from this.
     */
    private static final LocalDate REFERENCE_REPORT_PERIOD_START_OLD = new LocalDate(2008, 11, 9);
    
    /**
     * October 7, 2013, a Monday, was the beginning of a report period.
     * We will now accommodate Sunday report period begin dates from before, 
     * and Monday report period begin dates going forward.
     */
    //TODO: Make sure this is the Monday of the week it goes live
    private static final LocalDate REFERENCE_REPORT_PERIOD_START_NEW = new LocalDate(2013, 10, 7);

    @ToStringProperty
    private final LocalDate reportPeriodEnd;
    
    private ReportPeriod(LocalDate reportPeriodEnd)
    {
        this.reportPeriodEnd = reportPeriodEnd; 
    }
    
    public ReportPeriod previousReportPeriod()
    {
        /*
         * In order for a smooth transition between Saturday end dates and 
         * Sunday end dates, we need some "magic" for the week that the 
         * transition actually occurs.
         * 
         * Check to see if the current report will end on the first available 
         * Sunday of this new reporting structure.  If so, the previous report 
         * would have an end date of the previous Saturday rather than the 
         * previous Sunday, so we must subtract a day.
         */
        
        //First Sunday after the new report period start date
        LocalDate transitionDate = REFERENCE_REPORT_PERIOD_START_NEW.plusDays(6);
        if(transitionDate.isEqual(reportPeriodEnd))
        {
            return newReportPeriodEndingOn(reportPeriodEnd.minusWeeks(1).minusDays(1));
        }
        return newReportPeriodEndingOn(reportPeriodEnd.minusWeeks(1));
    }
    
    public ReportPeriod nextReportPeriod()
    {
        /*
         * In order for a smooth transition between Saturday end dates and 
         * Sunday end dates, we need some "magic" for the week that the 
         * transition actually occurs.
         * 
         * Check to see if the previous report ended on the Saturday before 
         * the new reporting structure began.  If so, the next report will 
         * end on a Sunday rather than a Saturday, so we must add a day.
         */
        
        //Saturday before the new report period start date
        LocalDate transitionDate = REFERENCE_REPORT_PERIOD_START_NEW.minusDays(2);
        if(transitionDate.isEqual(reportPeriodEnd))
        {
            //For the transition, add 1 day to go from Saturday to Sunday
            return newReportPeriodEndingOn(reportPeriodEnd.plusWeeks(1).plusDays(1));
        }
        return newReportPeriodEndingOn(reportPeriodEnd.plusWeeks(1));
    }
    
    public LocalDate getEndDate()
    {
        return reportPeriodEnd;
    }
    
    public LocalDate getStartDate()
    {
        return reportPeriodEnd.minusWeeks(1).plusDays(1);
    }
    
    public boolean contains(LocalDate localDate)
    {
        Preconditions.checkNotNull(localDate, "localDate is null");
        return !localDate.isBefore(getStartDate()) && !localDate.isAfter(getEndDate());
    }
    
    /**
     * Constructs the report period that contains the given date
     * @param date
     * @return
     */
    public static ReportPeriod newReportPeriodContaining(LocalDate date)
    {
        Preconditions.checkNotNull(date, "date is null");
        return new ReportPeriod(getReportPeriodEndForDayInPeriod(date));
    }
    
    /**
     * Effectively the same as {@link #newReportPeriodContaining(LocalDate)}, but
     * validates that <tt>ReportPeriodEnd</tt> is a valid report period end date.
     * @param reportPeriodEnd
     * @return
     */
    public static ReportPeriod newReportPeriodEndingOn(LocalDate reportPeriodEnd)
    {
        Preconditions.checkNotNull(reportPeriodEnd, "date is null");
        Preconditions.checkArgument(isAValidReportPeriodEndDate(reportPeriodEnd),
            "ReportPeriodEnd (%s) is not a valid report period end, should be %s", reportPeriodEnd, getReportPeriodEndForDayInPeriod(reportPeriodEnd));
        return new ReportPeriod(reportPeriodEnd);
    }

    public static LocalDate getReportPeriodEndForDayInPeriod(LocalDate localDate)
    {
        int daysBetween;
        
        /*
         * Set up a break between Sunday start dates and Monday start dates.  
         * It is the same logic for both (making sure the date passed in is 
         * the same day of the week), but one is based on a Monday and the 
         * other is based on a Sunday.  This will allow all prior reports to 
         * continue loading properly, while also supporting the proper report 
         * start dates going forward.
         */
        if(localDate.isEqual(REFERENCE_REPORT_PERIOD_START_NEW.minusDays(1)))
        {
            return localDate.plusWeeks(1);
        }
        else if(localDate.isBefore(REFERENCE_REPORT_PERIOD_START_NEW))
        {
            daysBetween = Days.daysBetween(REFERENCE_REPORT_PERIOD_START_OLD, localDate).getDays();
        }
        else
        {
            daysBetween = Days.daysBetween(REFERENCE_REPORT_PERIOD_START_NEW, localDate).getDays();
        }
        
        int dayInReportPeriod = Math.mod(daysBetween, 7);
        return localDate.plusDays(6 - dayInReportPeriod);
    }

    @Override
    protected Object[] getComponents()
    {
        return new Object[]{reportPeriodEnd};
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).toString();
    }

    public int compareTo(ReportPeriod other)
    {
        return this.reportPeriodEnd.compareTo(other.reportPeriodEnd);
    }

    public boolean isBefore(ReportPeriod other)
    {
        return compareTo(other) < 0;
    }
    
    public boolean isAfter(ReportPeriod other)
    {
        return compareTo(other) > 0;
    }
    
    public static boolean isAValidReportPeriodEndDate(LocalDate reportPeriodEnd)
    {
        return reportPeriodEnd.equals(getReportPeriodEndForDayInPeriod(reportPeriodEnd));
    }
    
    public static LocalDate getNewReportPeriodStartDate()
    {
        return REFERENCE_REPORT_PERIOD_START_NEW;
    }

    private static final long serialVersionUID = 1L;
}
