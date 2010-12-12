package org.ccci.mpdStats.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.PostLoad;

import org.ccci.dao.IllegalDatabaseStateException;
import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.util.ValueObject;
import org.ccci.util.time.TimeUtil;
import org.joda.time.LocalDate;

@Embeddable
public class StatsReportKey extends ValueObject implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Embedded
    @AttributeOverride(name = "employeeId", column =
        @Column(name = "EMPLID", nullable = false, updatable = false, length = 11))
    private EmployeeId employeeId;

    @Column(name = "REPORT_DATE", nullable = false) //actually, peoplesoft makes this column nullable.  Lame.
    private Date reportDate;

    protected StatsReportKey()
    {
    }

    public StatsReportKey(EmployeeId employeeId, ReportPeriod reportPeriod)
    {
        this.employeeId = employeeId;
        this.reportDate = TimeUtil.localDateToSqlDate(reportPeriod.getEndDate());
    }

    @Override
    protected Object[] getComponents()
    {
        return new Object[] { employeeId, reportDate };
    }

    public EmployeeId getEmployeeId()
    {
        return employeeId;
    }

    public ReportPeriod getReportPeriod()
    {
        return ReportPeriod.newReportPeriodEndingOn(new LocalDate(reportDate));
    }

    @PostLoad
    protected void validateReportDate()
    {
        if (!ReportPeriod.isAValidReportPeriodEndDate(new LocalDate(reportDate)))
        {
            throw new IllegalDatabaseStateException("invalid report date: " + reportDate);
        }
    }
    
}
