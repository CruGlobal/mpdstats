package org.ccci.mpdStats.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.ccci.annotations.RequiredOnlyByPeoplesoft;
import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.util.ValueObject;
import org.ccci.util.time.TimeUtil;

@Embeddable
public class ImprovementKey extends ValueObject implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Embedded
    @AttributeOverride(name = "employeeId", column =
        @Column(name = "EMPLID", nullable = false, updatable = false, length = 11))
    private EmployeeId employeeId;

    @Column(name = "report_date", nullable = false)
    private Date reportDate;

    @RequiredOnlyByPeoplesoft
    @Column(name = "effSeq", nullable = false)
    private int effectiveSequence = 0;

    protected ImprovementKey()
    {
    }

    public ImprovementKey(EmployeeId employeeId, ReportPeriod reportPeriod)
    {
        this.employeeId = employeeId;
        this.reportDate = TimeUtil.localDateToSqlDate(reportPeriod.getEndDate());
    }

    @Override
    protected Object[] getComponents()
    {
        return new Object[] { employeeId, reportDate, effectiveSequence };
    }

    public EmployeeId getEmployeeId()
    {
        return employeeId;
    }

    public Date getReportDate()
    {
        return reportDate;
    }

}
