package org.ccci.mpdStats.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.ccci.model.EmployeeId;
import org.ccci.util.ValueObject;

/**
 * See comments on {@link StaffTransaction}
 * 
 * @author Matt Drees
 */
@Embeddable
public class StaffTransactionKey extends ValueObject implements Serializable
{
    
    /** N.B. only primary employee ids are stored in this table */
    @Embedded
    @AttributeOverride(name = "employeeId", column =
        @Column(name = "EMPLID", nullable = false, updatable = false, length = 11))
    private EmployeeId employeeId;

    @Column(name = "STF_ACCT_TYPE", nullable = false)
    private String staffAccountType;

    @Column(name = "TRANS_DATE", nullable = false)
    private Date transactionDate;

    /**
     * GL, SAS, AP, PRP, PRR, CN, DN, PRS, SGL
     */
    @Column(name = "SOURCE_SYSTEM", nullable = false)
    private String sourceSystem;

    @Column(name = "SOURCE_KEY", nullable = false)
    private String sourceKey;

    @Column(name = "SOURCE_CODE", nullable = false)
    private String sourceCode;

    /** G, E, X, T, or D */
    @Column(name = "SOURCE_CODE_TYPE", nullable = false)
    private String sourceCodeType;
    
    public StaffTransactionKey()
    {
    }

    public StaffTransactionKey(String staffAccountType, EmployeeId emplId, Date transactionDate, String sourceSystem,
                               String sourceKey, String sourceCode, String sourceCodeType)
    {
        this.staffAccountType = staffAccountType;
        this.employeeId = emplId;
        this.transactionDate = transactionDate;
        this.sourceSystem = sourceSystem;
        this.sourceKey = sourceKey;
        this.sourceCode = sourceCode;
        this.sourceCodeType = sourceCodeType;
    }

    @Override
    protected Object[] getComponents()
    {
        return new Object[]{employeeId, staffAccountType, transactionDate, sourceSystem, sourceKey, sourceCode, sourceCodeType};
    }

    public String getStaffAccountType()
    {
        return staffAccountType;
    }

    public EmployeeId getEmployeeId()
    {
        return employeeId;
    }

    public Date getTransactionDate()
    {
        return transactionDate;
    }

    public String getSourceSystem()
    {
        return sourceSystem;
    }

    public String getSourceKey()
    {
        return sourceKey;
    }

    public String getSourceCode()
    {
        return sourceCode;
    }
    
    public String getSourceCodeType()
    {
        return sourceCodeType;
    }

    private static final long serialVersionUID = 1L;
}
