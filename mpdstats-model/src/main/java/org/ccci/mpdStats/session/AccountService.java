package org.ccci.mpdStats.session;

import org.ccci.model.EmployeeId;

public interface AccountService
{

    /**
     * Returns the current account balance for the staff member with the given employee id.
     * The amount is calculated from the balance currently held in the staff balances table plus
     * any recent entries in the staff transactions table that have not be included in the most current staff balance row.
     * 
     * If the given employee is not supported, will return 0.
     * TODO: use a currency class instead of a double!
     * 
     * @param employeeId
     * @return
     */
    double getBalance(EmployeeId employeeId);

}
