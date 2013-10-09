package org.ccci.mpdStats.model;

import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportPeriodTest
{
    @Test
    public void testGetReportPeriodEndForDayInPeriod()
    {
        LocalDate localDate = new LocalDate(2013, 10, 4);
        LocalDate reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, new LocalDate(2013, 10, 5));
        
        localDate = new LocalDate(2013, 10, 5);
        reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, new LocalDate(2013, 10, 5));
        
        localDate = new LocalDate(2013, 10, 6);
        reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, new LocalDate(2013, 10, 13));
        
        localDate = new LocalDate(2013, 10, 7);
        reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, new LocalDate(2013, 10, 13));
        
        localDate = new LocalDate(2013, 10, 8);
        reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, new LocalDate(2013, 10, 13));
        
        localDate = new LocalDate(2013, 10, 12);
        reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, new LocalDate(2013, 10, 13));
        
        localDate = new LocalDate(2013, 10, 13);
        reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, new LocalDate(2013, 10, 13));
        
        localDate = new LocalDate(2013, 10, 14);
        reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, new LocalDate(2013, 10, 20));
        
        localDate = new LocalDate(2013, 8, 5);
        reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, new LocalDate(2013, 8, 10));
        
        localDate = new LocalDate(2013, 8, 10);
        reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, new LocalDate(2013, 8, 10));
    }
    
    @Test
    public void testPreviousReportPeriod()
    {
        LocalDate reportPeriodEnd = new LocalDate(2013, 8, 10);
        ReportPeriod reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportPeriodEnd);
        ReportPeriod previousPeriod = reportPeriod.previousReportPeriod();
        Assert.assertEquals(previousPeriod.getEndDate(), new LocalDate(2013, 8, 3));
        
        reportPeriodEnd = new LocalDate(2013, 10, 5);
        reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportPeriodEnd);
        previousPeriod = reportPeriod.previousReportPeriod();
        Assert.assertEquals(previousPeriod.getEndDate(), new LocalDate(2013, 9, 28));
        
        reportPeriodEnd = new LocalDate(2013, 10, 13);
        reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportPeriodEnd);
        previousPeriod = reportPeriod.previousReportPeriod();
        Assert.assertEquals(previousPeriod.getEndDate(), new LocalDate(2013, 10, 5));
        
        reportPeriodEnd = new LocalDate(2013, 10, 20);
        reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportPeriodEnd);
        previousPeriod = reportPeriod.previousReportPeriod();
        Assert.assertEquals(previousPeriod.getEndDate(), new LocalDate(2013, 10, 13));
    }
    
    @Test
    public void testNextReportPeriod()
    {
        LocalDate reportPeriodEnd = new LocalDate(2013, 8, 10);
        ReportPeriod reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportPeriodEnd);
        ReportPeriod nextPeriod = reportPeriod.nextReportPeriod();
        Assert.assertEquals(nextPeriod.getEndDate(), new LocalDate(2013, 8, 17));
        
        reportPeriodEnd = new LocalDate(2013, 9, 28);
        reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportPeriodEnd);
        nextPeriod = reportPeriod.nextReportPeriod();
        Assert.assertEquals(nextPeriod.getEndDate(), new LocalDate(2013, 10, 5));
        
        reportPeriodEnd = new LocalDate(2013, 10, 5);
        reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportPeriodEnd);
        nextPeriod = reportPeriod.nextReportPeriod();
        Assert.assertEquals(nextPeriod.getEndDate(), new LocalDate(2013, 10, 13));
        
        reportPeriodEnd = new LocalDate(2013, 10, 13);
        reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportPeriodEnd);
        nextPeriod = reportPeriod.nextReportPeriod();
        Assert.assertEquals(nextPeriod.getEndDate(), new LocalDate(2013, 10, 20));
    }
}
