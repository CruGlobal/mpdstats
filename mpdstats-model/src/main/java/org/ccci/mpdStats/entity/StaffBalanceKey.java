package org.ccci.mpdStats.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.ccci.model.EmployeeId;
import org.ccci.util.ValueObject;

@Embeddable
public class StaffBalanceKey extends ValueObject implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Embedded
    @AttributeOverride(name = "employeeId", column =
        @Column(name = "EMPLID", nullable = false, updatable = false, length = 11))
    private EmployeeId employeeId;

    @Column(name = "STF_ACCT_TYPE", nullable = false)
    private String staffAccountType;

    protected StaffBalanceKey()
    {
    }

    public StaffBalanceKey(String staffAccountType, EmployeeId emplid)
    {
        this.staffAccountType = staffAccountType;
        this.employeeId = emplid;
    }

    @Override
    protected Object[] getComponents()
    {
        return new Object[] { employeeId, staffAccountType };
    }

    public String getStaffAccountType()
    {
        return staffAccountType;
    }

    public EmployeeId getEmployeeId()
    {
        return employeeId;
    }

}
