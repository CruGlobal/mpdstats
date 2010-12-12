package org.ccci.mpdStats.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.ccci.annotations.JpaConstructor;
import org.ccci.model.EmployeeId;
import org.ccci.mpdStats.model.ReportPeriod;
import org.ccci.util.Child;
import org.ccci.util.EffectiveSequenced;
import org.ccci.util.strings.ToStringBuilder;
import org.ccci.util.strings.ToStringProperty;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "PS_MPD_MONTH_COMMT")
@NamedQuery( name = "MonthlyCommitment.findByEmployeeIdAndReportDate", 
    query = "select commitment from MonthlyCommitment commitment " +
    		"where commitment.key.employeeId = :employeeId " +
    		"and commitment.key.reportDate = :reportDate " +
    		"order by commitment.key.effectiveSequence")
public class MonthlyCommitment implements Serializable, Child<StatsReport>, EffectiveSequenced
{
    private static final long serialVersionUID = 1L;

    //TODO: remove this somehow
    public static final String BLANK_ACCOUNT_NAME_VALUE = "";

    public static final String REPORT_DATE_FORMAT = "MM-dd-yyyy";

    @ToStringProperty
    @Column(name = "GIFT_AMT")
    private double giftAmount = 0d;

    @ToStringProperty
    @Column(name = "ACCOUNT_NAME")
    @Length(max = 50)
    @NotNull
    private String accountName;

    @EmbeddedId
    private MonthlyCommitmentKey key;

    @Transient
    private StatsReport report;

    @JpaConstructor
    protected MonthlyCommitment()
    {
        key = new MonthlyCommitmentKey();
    }
    
    public MonthlyCommitment(EmployeeId employeeId, ReportPeriod reportPeriod, int effectiveSequence)
    {
        key = new MonthlyCommitmentKey(employeeId, reportPeriod, effectiveSequence);
    }


    /*
     * getters and setters
     */
    public EmployeeId getEmployeeId()
    {
        return key.getEmployeeId();
    }

    public int getEffSeq()
    {
        return key.getEffectiveSequence();
    }

    public ReportPeriod getReportPeriod()
    {
        return key.getReportPeriod();
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
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).toString();
    }

    public StatsReport getParent()
    {
        return report;
    }

    public void setParent(StatsReport report)
    {
        this.report = report;
    }

    public int getEffectiveSequence()
    {
        return key.getEffectiveSequence();
    }

    /**
     * Note: this changes the key, which is ok if this SpecialCommitment isn't managed by the current
     * persistence context.  Otherwise, this could be problematic.
     */
    public void updateEffectiveSequence(int effectiveSequence)
    {
        key = new MonthlyCommitmentKey(report.getEmployeeId(), report.getReportPeriod(), effectiveSequence);
    }
    
    public void updateReportPeriod(ReportPeriod newReportPeriod)
    {
        key = new MonthlyCommitmentKey(key.getEmployeeId(), newReportPeriod, key.getEffectiveSequence());
    }

    public static MonthlyCommitment createBlankEntry()
    {
        MonthlyCommitment monthlyCommitment = new MonthlyCommitment();
        monthlyCommitment.setAccountName(BLANK_ACCOUNT_NAME_VALUE);
        return monthlyCommitment;
    }

    public boolean hasKey()
    {
        return !key.isEmpty();
    }

    public static MonthlyCommitment createUsingPrevious(MonthlyCommitment oldMonthlyCommitment)
    {
        MonthlyCommitment newMonthlyCommitment = createBlankEntry();
        newMonthlyCommitment.setAccountName(oldMonthlyCommitment.getAccountName());
        newMonthlyCommitment.setGiftAmount(oldMonthlyCommitment.getGiftAmount());
        return newMonthlyCommitment;
    }

}
