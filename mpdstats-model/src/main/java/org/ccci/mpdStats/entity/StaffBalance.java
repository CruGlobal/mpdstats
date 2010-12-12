package org.ccci.mpdStats.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.ccci.model.EmployeeId;
import org.ccci.util.strings.ToStringBuilder;
import org.ccci.util.strings.ToStringProperty;

@Entity
@Table(name = "PS_STAFF_BALANCES")
@NamedQuery(name ="StaffBalance.getBalance" ,
    query = "select staffBalance.balance, staffBalance.effectiveDate from StaffBalance staffBalance "+ 
            "where staffBalance.key.staffAccountType = 'PRIME' " +
            "and staffBalance.key.employeeId = :employeeId " +
            "and staffBalance.effectiveDate = (" +
    		" select max(effectiveDate) from StaffBalance otherStaffBalance " +
    		" where otherStaffBalance.key.staffAccountType = 'PRIME' " +
    		" and otherStaffBalance.key.employeeId = :employeeId)") 
   
public class StaffBalance implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String REPORT_DATE_FORMAT = "MM-dd-yyyy";

    @ToStringProperty
    @EmbeddedId
    private final StaffBalanceKey key = new StaffBalanceKey();

    @Column(name = "EFFDT", nullable = false)
    public Date effectiveDate;

    @Column(name = "BALANCE", nullable = false)
    public double balance;

    public String getStaffAccountType()
    {
        return key.getStaffAccountType();
    }

    public EmployeeId getEmployeeId()
    {
        return key.getEmployeeId();
    }

    public Date getEffectiveDate()
    {
        return effectiveDate;
    }

    public void setEffDate(Date effectiveDate)
    {
        this.effectiveDate = effectiveDate;
    }

    public double getBalance()
    {
        return balance;
    }

    public void setBalance(double balance)
    {
        this.balance = balance;
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).toString();
    }
}
