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
    private static final LocalDate REFERENCE_REPORT_PERIOD_START = new LocalDate(2008, 11, 9);

    @ToStringProperty
    private final LocalDate reportPeriodEnd;
    
    private ReportPeriod(LocalDate reportPeriodEnd)
    {
        this.reportPeriodEnd = reportPeriodEnd; 
    }
    
    public ReportPeriod previousReportPeriod()
    {
        return newReportPeriodEndingOn(reportPeriodEnd.minusWeeks(1));
    }
    
    public ReportPeriod nextReportPeriod()
    {
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
        int daysBetween = Days.daysBetween(REFERENCE_REPORT_PERIOD_START, localDate).getDays();
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

    private static final long serialVersionUID = 1L;
}
