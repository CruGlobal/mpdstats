package org.ccci.mpdStats.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.ccci.annotations.JpaConstructor;
import org.ccci.model.EmployeeId;


/**
 * Note: I don't think this table actually has a primary key. So, we do our best.
 * @author Matt Drees
 */
@Entity
@Table(name = "PS_STAFF_TRNSACTNS")
@NamedQuery(name = "StaffTransaction.calculateRecentAdditions",
   query = "select sum(transaction.transactionAmount) from StaffTransaction transaction " +
           "where transaction.key.employeeId = :employeeId  " +
           "and transaction.key.staffAccountType = 'PRIME' " +
           "and transaction.key.sourceCodeType in ('G', 'X') " +
           "and transaction.key.transactionDate > :boundaryDate")

public class StaffTransaction implements Serializable
{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private final StaffTransactionKey key;

    @Column(name = "TRANS_AMOUNT", nullable = false)
    private double transactionAmount;

    @Column(name = "POSTED_FLAG", nullable = false, length = 1)
    private String postedFlag;
    
    @Column(name = "DESCR", nullable = false, length = 30)
    private String description;
    
    @Column(name = "SOURCE_DATE", nullable = false)
    private Date sourceDate;
    
    @Column(name = "CLEARED_STATUS", nullable = false, length = 1)
    private String clearedStatus;

    @Column(name = "ADVANCE_CHK_NUM", nullable = false)
    private double advanceCheckNumber;
    
    @Column(name = "AUDIT_OPRID", nullable = false, length = 30)
    private String auditOperatorId;

    @Column(name = "TO_CLEAR_BY_DATE", nullable = false)
    private Date toClearByDate;
    
    @JpaConstructor
    protected StaffTransaction()
    {
        key = new StaffTransactionKey();
    }
    
    public StaffTransaction(StaffTransactionKey key)
    {
        this.key = key;
    }

    public String getStaffAccountType()
    {
        return key.getStaffAccountType();
    }

    public EmployeeId getEmployeeId()
    {
        return key.getEmployeeId();
    }

    public Date getTransactionDate()
    {
        return key.getTransactionDate();
    }

    public String getSourceSystem()
    {
        return key.getSourceSystem();
    }

    public String getSourceKey()
    {
        return key.getSourceKey();
    }

    public String getSourceCode()
    {
        return key.getSourceCode();
    }

    public void setPostedFlag(String postedFlag)
    {
        this.postedFlag = postedFlag;
    }

    public double getTransactionAmount()
    {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount)
    {
        this.transactionAmount = transactionAmount;
    }

    public String getPostedFlag()
    {
        return postedFlag;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getSourceDate()
    {
        return sourceDate;
    }

    public void setSourceDate(Date sourceDate)
    {
        this.sourceDate = sourceDate;
    }

    public String getClearedStatus()
    {
        return clearedStatus;
    }

    public void setClearedStatus(String clearedStatus)
    {
        this.clearedStatus = clearedStatus;
    }

    public double getAdvanceCheckNumber()
    {
        return advanceCheckNumber;
    }

    public void setAdvanceCheckNumber(double advanceCheckNumber)
    {
        this.advanceCheckNumber = advanceCheckNumber;
    }

    public String getAuditOperatorId()
    {
        return auditOperatorId;
    }

    public void setAuditOperatorId(String auditOperatorId)
    {
        this.auditOperatorId = auditOperatorId;
    }

    public Date getToClearByDate()
    {
        return toClearByDate;
    }

    public void setToClearByDate(Date toClearByDate)
    {
        this.toClearByDate = toClearByDate;
    }
}
