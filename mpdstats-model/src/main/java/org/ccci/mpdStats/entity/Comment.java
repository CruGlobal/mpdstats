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
@Table(name = "PS_MPD_COMMENTS")
public class Comment implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String REPORT_DATE_FORMAT = "MM-dd-yyyy";

    @Column(name = "COMMENTS")
    @Lob
    private String comments;

    @ToStringProperty
    @EmbeddedId
    private CommentKey key;

    @JpaConstructor
    protected Comment()
    {
        key =  new CommentKey();
    }
    
    public Comment(EmployeeId employeeId, ReportPeriod reportPeriod)
    {
        key = new CommentKey(employeeId, reportPeriod);
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

    public CommentKey getKey()
    {
        return key;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).toString();
    }

    public void updateReportPeriod(ReportPeriod newReportPeriod)
    {
        key = new CommentKey(key.getEmployeeId(), newReportPeriod);
    }

}
