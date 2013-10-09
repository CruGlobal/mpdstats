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
	}
}
