package org.ccci.mpdStats;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class NumberConverterTest
{

    @Test
    public void testParse() throws ParseException
    {
        DecimalFormat format = new DecimalFormat("#,##0");
        Number parsed = format.parse("1000");
        Assert.assertEquals(parsed.longValue(), 1000L);
    }
}
