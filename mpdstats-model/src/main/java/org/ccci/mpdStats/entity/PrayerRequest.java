package org.ccci.mpdStats.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.model.ReportPeriod;

@Entity
@Table(name="PS_MPD_PRAYERS")
public class PrayerRequest implements Serializable 
{
    private static final long serialVersionUID = 1L;

    public static final String REPORT_DATE_FORMAT = "MM-dd-yyyy";

    @Column(name="COMMENTS")
    @Lob
    private String comments;
    
    @EmbeddedId
    private PrayerRequestKey key;

    protected PrayerRequest()
    {
        key = new PrayerRequestKey();
    }
    
    public PrayerRequest(EmployeeId employeeId, ReportPeriod reportPeriod)
    {
        key = new PrayerRequestKey(employeeId, reportPeriod);
    }

    /*
     * getters and setters
     */
    public EmployeeId getEmployeeId()
    {
        return key.getEmployeeId();
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public void updateReportPeriod(ReportPeriod newReportPeriod)
    {
        key = new PrayerRequestKey(key.getEmployeeId(), newReportPeriod);
    }       
}
