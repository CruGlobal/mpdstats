package org.ccci.mpdStats.session;

import java.util.List;

import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.mpdStats.session.ReportProcess;
import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * The algorithm for determining which report period used to be more complex, which justified these tests.
 * Since then, we simplified the rules, so these seem a little frivolous, but they don't hurt.
 * 
 * @author Matt Drees
 *
 */
public class ReportProcessTest
{

    @Test
    public void testGetAppropriateSuggestedReportDate_firstPeriod()
    {
        ReportProcess process = new ReportProcess();
        ReportPeriod singleReportPeriod = ReportPeriod.newReportPeriodContaining(new LocalDate(2008, 11, 7));
        List<ReportPeriod> reportPeriods = Lists.newArrayList(singleReportPeriod);
        ReportPeriod suggestedPeriod = process.getSuggestedReportPeriod(reportPeriods);
        Assert.assertEquals(suggestedPeriod, singleReportPeriod.nextReportPeriod());
    }
    
    @Test
    public void testGetAppropriateSuggestedReportDate_noGap()
    {
        ReportProcess process = new ReportProcess();
        ReportPeriod firstReportPeriod = ReportPeriod.newReportPeriodContaining(new LocalDate(2008, 11, 7));
        ReportPeriod secondReportPeriod = ReportPeriod.newReportPeriodContaining(new LocalDate(2008, 11, 14));
        List<ReportPeriod> reportPeriods = Lists.newArrayList(firstReportPeriod, secondReportPeriod);
        ReportPeriod suggestedPeriod = process.getSuggestedReportPeriod(reportPeriods);
        Assert.assertEquals(suggestedPeriod, secondReportPeriod.nextReportPeriod());
    }
    
    @Test
    public void testGetAppropriateSuggestedReportDate_gapAfterFirst()
    {
        ReportProcess process = new ReportProcess();
        ReportPeriod firstReportPeriod = ReportPeriod.newReportPeriodContaining(new LocalDate(2008, 11, 7));
        ReportPeriod secondReportPeriod = ReportPeriod.newReportPeriodContaining(new LocalDate(2008, 11, 21));
        List<ReportPeriod> reportPeriods = Lists.newArrayList(firstReportPeriod, secondReportPeriod);
        ReportPeriod suggestedPeriod = process.getSuggestedReportPeriod(reportPeriods);
        Assert.assertEquals(suggestedPeriod, secondReportPeriod.nextReportPeriod());
    }
    
}
