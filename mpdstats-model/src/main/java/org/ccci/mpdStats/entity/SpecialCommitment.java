package org.ccci.mpdStats.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.util.Child;
import org.ccci.util.EffectiveSequenced;
import org.ccci.util.strings.ToStringBuilder;
import org.ccci.util.strings.ToStringProperty;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "PS_MPD_SPEC_COMMT")
@NamedQuery(name = "SpecialCommitment.findByEmployeeIdAndReportDate", 
    query = "select commitment from SpecialCommitment commitment " + 
            "where commitment.key.employeeId = :employeeId " +
            "and commitment.key.reportDate = :reportDate " +
            "order by commitment.key.effectiveSequence")
public class SpecialCommitment implements Child<StatsReport>, EffectiveSequenced, Serializable
{
    private static final long serialVersionUID = 1L;

    //TODO: remove this somehow
    public static final String BLANK_ACCOUNT_NAME_VALUE = "";

    public static final String REPORT_DATE_FORMAT = "MM-dd-yyyy";
    
    @EmbeddedId
    private SpecialCommitmentKey key;

    @ToStringProperty
    @Column(name = "GIFT_AMT")
    private double giftAmount = 0d;

    @ToStringProperty
    @Column(name = "ACCOUNT_NAME")
    @Length(max = 50)
    @NotNull
    private String accountName;

    @Transient
    private StatsReport report;
    
    protected SpecialCommitment()
    {
        key = new SpecialCommitmentKey();
    }

    public SpecialCommitment(EmployeeId employeeId, ReportPeriod reportPeriod, int effectiveSequence)
    {
        key = new SpecialCommitmentKey(employeeId, reportPeriod, effectiveSequence);
    }

    /*
     * getters and setters
     */
    public EmployeeId getEmployeeId()
    {
        return key.getEmployeeId();
    }
    
    public int getEffectiveSequence()
    {
        return key.getEffectiveSequence();
    }

    public double getGiftAmount()
    {
        return giftAmount;
    }

    public void setGiftAmount(double giftAmount)
    {
        this.giftAmount = giftAmount;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public StatsReport getParent()
    {
        return report;
    }

    public void setParent(StatsReport report)
    {
        this.report = report;
    }

    public SpecialCommitmentKey getKey()
    {
        return key;
    }

    /**
     * Note: this changes the key, which is ok if this SpecialCommitment isn't managed by the current
     * persistence context.  Otherwise, this could be problematic.
     */
    public void updateEffectiveSequence(int effectiveSequence)
    {
        key = new SpecialCommitmentKey(report.getEmployeeId(), report.getReportPeriod(), effectiveSequence);
    }

    public void updateReportPeriod(ReportPeriod newReportPeriod)
    {
        key = new SpecialCommitmentKey(key.getEmployeeId(), newReportPeriod, key.getEffectiveSequence());
    }
    
    public static SpecialCommitment createBlankEntry()
    {
        SpecialCommitment specialCommitment = new SpecialCommitment();
        specialCommitment.setAccountName(BLANK_ACCOUNT_NAME_VALUE);
        return specialCommitment;
    }

    /**
     * Returns true if this {@code SpecialCommitment} has a key (valid employeeId, report period, etc)
     * @return
     */
    public boolean hasKey()
    {
        return !key.isEmpty();
    }

    public static SpecialCommitment createUsingPrevious(SpecialCommitment oldSpecialCommitment)
    {
        SpecialCommitment newSpecialCommitment = SpecialCommitment.createBlankEntry();
        newSpecialCommitment.setAccountName(oldSpecialCommitment.getAccountName());
        newSpecialCommitment.setGiftAmount(oldSpecialCommitment.getGiftAmount());
        return newSpecialCommitment;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).toString();
    }
}
