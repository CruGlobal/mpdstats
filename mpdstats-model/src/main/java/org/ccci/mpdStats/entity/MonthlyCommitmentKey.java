package org.ccci.mpdStats.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.util.ValueObject;
import org.ccci.util.time.TimeUtil;
import org.hibernate.validator.Min;

import com.google.common.base.Preconditions;

@Embeddable
public class MonthlyCommitmentKey extends ValueObject implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Embedded
    @AttributeOverride(name = "employeeId", column =
        @Column(name = "EMPLID", nullable = false, updatable = false, length = 11))
    private EmployeeId employeeId;


    @Column(name = "report_date", nullable = false)
    private Date reportDate;

    @Column(name = "effSeq", nullable = false)
    @Min(0)
    private int effectiveSequence = -1;

    public MonthlyCommitmentKey()
    {
    }

    public MonthlyCommitmentKey(EmployeeId employeeId, ReportPeriod reportPeriod, int effectiveSequence)
    {
        Preconditions.checkArgument(employeeId != null, "employeeId is null");
        Preconditions.checkArgument(reportPeriod != null, "reportPeriod is null");
        Preconditions.checkArgument(effectiveSequence >= 0, "effectiveSequence is negative");
        this.employeeId = employeeId;
        this.reportDate = TimeUtil.localDateToSqlDate(reportPeriod.getEndDate());
        this.effectiveSequence = effectiveSequence;
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

    public ReportPeriod getReportPeriod()
    {
        return ReportPeriod.newReportPeriodEndingOn(TimeUtil.sqlDateToLocalDate(reportDate));
    }

    public int getEffectiveSequence()
    {
        return effectiveSequence;
    }

    public boolean isEmpty()
    {
        return employeeId == null && reportDate == null && effectiveSequence < 0;
    }

}
