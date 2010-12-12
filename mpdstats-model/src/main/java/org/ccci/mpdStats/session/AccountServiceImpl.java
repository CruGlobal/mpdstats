package org.ccci.mpdStats.session;

import static org.jboss.seam.ScopeType.STATELESS;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.ccci.model.EmployeeId;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.joda.time.LocalDate;

import com.google.common.base.Preconditions;

@Name("accountService")
@Scope(STATELESS)
@AutoCreate
public class AccountServiceImpl implements AccountService
{

    @In EntityManager psEntityManager;
    
    public double getBalance(EmployeeId employeeId)
    {
        Preconditions.checkNotNull(employeeId, "employeeId is null");
        EmployeeId primaryEmployeeId = employeeId.getPrimaryId();
        Double previousBalance;
        Date mostRecentStaffBalanceDate;
        try
        {
            Object[] results =  (Object[]) psEntityManager.createNamedQuery("StaffBalance.getBalance")
                    .setParameter("employeeId", primaryEmployeeId)
                    .getSingleResult();
            previousBalance = (Double) results[0];
            mostRecentStaffBalanceDate = (Date) results[1];
        }
        catch (NoResultException e)
        {
            previousBalance = 0d;
            mostRecentStaffBalanceDate = aRelativelyOldDate();
        }
        
        return previousBalance + calculateRecentAdditions(primaryEmployeeId, mostRecentStaffBalanceDate);
    }

    private Date aRelativelyOldDate() // Jan 1 1960
    {
        return new Date( new LocalDate(1960, 1, 1).toDateTimeAtStartOfDay().getMillis());
    }

    private Double calculateRecentAdditions(EmployeeId employeeId, Date boundaryDate)
    {
        Double debit;
        try
        {
            debit = (Double) psEntityManager.createNamedQuery("StaffTransaction.calculateRecentAdditions")
                    .setParameter("employeeId", employeeId)
                    .setParameter("boundaryDate", boundaryDate)
                    .getSingleResult();
        }
        catch (NoResultException e)
        {
            debit = 0d;
        }
        if (debit == null)
        {
            debit = 0d;
        }
        return debit;
    }

}
