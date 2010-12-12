package org.ccci.mpdStats.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.ccci.annotations.JpaConstructor;
import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.util.strings.ToStringBuilder;
import org.ccci.util.strings.ToStringProperty;

@Entity
@Table(name = "PS_MPD_IMPROVEMENT")
public class Improvement implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String REPORT_DATE_FORMAT = "MM-dd-yyyy";

    @Column(name = "COMMENTS")
    @Lob
    private String improvement;

    @ToStringProperty
    @EmbeddedId
    private ImprovementKey key;

    @JpaConstructor
    protected Improvement() 
    {
        key = new ImprovementKey();
    }
    
    public Improvement(EmployeeId employeeId, ReportPeriod reportPeriod)
    {
        key = new ImprovementKey(employeeId, reportPeriod);
    }

    /*
     * getters and setters
     */
    public EmployeeId getEmployeeId()
    {
        return key.getEmployeeId();
    }

    public String getImprovement()
    {
        return improvement;
    }

    public void setImprovement(String improvement)
    {
        this.improvement = improvement;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).toString();
    }

    public void updateReportPeriod(ReportPeriod newReportPeriod)
    {
        key = new ImprovementKey(key.getEmployeeId(), newReportPeriod);
    }
}
