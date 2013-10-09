package org.ccci.mpdStats.model;

import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReportPeriodTest
{
    @DataProvider
    public Object[][] ValidPeriodEndForDayInPeriodProvider() {
        return new Object[][] {
                {new LocalDate(2013, 10, 4), new LocalDate(2013, 10, 5)},
                {new LocalDate(2013, 10, 5), new LocalDate(2013, 10, 5)},
                {new LocalDate(2013, 10, 6), new LocalDate(2013, 10, 13)},
                {new LocalDate(2013, 10, 7), new LocalDate(2013, 10, 13)},
                {new LocalDate(2013, 10, 8), new LocalDate(2013, 10, 13)},
                {new LocalDate(2013, 10, 12), new LocalDate(2013, 10, 13)},
                {new LocalDate(2013, 10, 13), new LocalDate(2013, 10, 13)},
                {new LocalDate(2013, 10, 14), new LocalDate(2013, 10, 20)},
                {new LocalDate(2013, 8, 5), new LocalDate(2013, 8, 10)},
                {new LocalDate(2013, 8, 10), new LocalDate(2013, 8, 10)}
        };
    }
    
    @DataProvider
    public Object[][] ValidPreviousReportPeriodProvider() {
        return new Object[][] {
                {new LocalDate(2013, 8, 10), new LocalDate(2013, 8, 3)},
                {new LocalDate(2013, 10, 5), new LocalDate(2013, 9, 28)},
                {new LocalDate(2013, 10, 13), new LocalDate(2013, 10, 5)},
                {new LocalDate(2013, 10, 20), new LocalDate(2013, 10, 13)}
        };
    }
    
    @DataProvider
    public Object[][] ValidNextReportPeriodProvider() {
        return new Object[][] {
                {new LocalDate(2013, 8, 10), new LocalDate(2013, 8, 17)},
                {new LocalDate(2013, 9, 28), new LocalDate(2013, 10, 5)},
                {new LocalDate(2013, 10, 5), new LocalDate(2013, 10, 13)},
                {new LocalDate(2013, 10, 13), new LocalDate(2013, 10, 20)}
        };
    }
    
    @Test(dataProvider = "ValidPeriodEndForDayInPeriodProvider")
    public void testGetReportPeriodEndForDayInPeriod(LocalDate localDate, LocalDate expectedReportPeriodEnd)
    {
        LocalDate reportPeriodEnd = ReportPeriod.getReportPeriodEndForDayInPeriod(localDate);
        Assert.assertEquals(reportPeriodEnd, expectedReportPeriodEnd);
    }
    
    @Test(dataProvider = "ValidPreviousReportPeriodProvider")
    public void testPreviousReportPeriod(LocalDate reportPeriodEnd, LocalDate expectedPreviousReportEndDate)
    {
        ReportPeriod reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportPeriodEnd);
        Assert.assertEquals(reportPeriod.previousReportPeriod().getEndDate(), expectedPreviousReportEndDate);
    }
    
    @Test(dataProvider = "ValidNextReportPeriodProvider")
    public void testNextReportPeriod(LocalDate reportPeriodEnd, LocalDate expectedNextReportEndDate)
    {
        ReportPeriod reportPeriod = ReportPeriod.newReportPeriodEndingOn(reportPeriodEnd);
        ReportPeriod nextPeriod = reportPeriod.nextReportPeriod();
        Assert.assertEquals(nextPeriod.getEndDate(), expectedNextReportEndDate);
    }
}
