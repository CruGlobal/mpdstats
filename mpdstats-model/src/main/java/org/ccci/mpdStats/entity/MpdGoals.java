package org.ccci.mpdStats.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "PS_MPD_STAT_GOALS")
@NamedQuery(
    name = "MpdGoals.getExactGoalsByHoursWorked",
    query = "select goals from MpdGoals goals " + 
        "where goals.key.maxHours >= :hours " +
        "and goals.minHoursRequired <= :hours " +
        "and goals.key.effectiveDate = (" +
        " select max(mostRecent.key.effectiveDate) from MpdGoals mostRecent" +
        " where mostRecent.key.effectiveDate <= :reportDate" +
        " and mostRecent.key.maxHours >= :hours" +
        " and mostRecent.minHoursRequired <= :hours)")
public class MpdGoals implements Serializable
{

    @EmbeddedId
    private final MpdGoalsKey key = new MpdGoalsKey();

    /*
     * getters and setters
     */
    @Column(name = "APPTS_SET_G")
    public int appointmentsSetGoal;
    
    @Column(name = "DIALS_G")
    public int dialsGoal;
    
    @Column(name = "HOURS_TOTAL_G")
    public double hoursTotalGoal;
    
    @Column(name = "INDIV_APPT_G")
    public int individualAppointmentsGoal;
    
    @Column(name = "MIN_HOURS_REQD")
    public int minHoursRequired;
    
    @Column(name = "NEW_MONTHLY_g")
    public double newMonthlyGoal;
    
    @Column(name = "NEW_PARTNERS_g")
    public int newPartnersGoal;
    
    @Column(name = "REFERRALS_GAINED")
    public int referralsGainedGoal;
    
    @Column(name = "TALKED_TO_G")
    public int talkedToGoal;

    public Date getEffectiveDate()
    {
        return key.getEffectiveDate();
    }

    public int getMaxHours()
    {
        return key.getMaxHours();
    }

    public int getAppointmentsSetGoal()
    {
        return appointmentsSetGoal;
    }

    public void setAppointmentsSetGoal(int appointmentsSetGoal)
    {
        this.appointmentsSetGoal = appointmentsSetGoal;
    }

    public int getDialsGoal()
    {
        return dialsGoal;
    }

    public void setDialsGoal(int dialsGoal)
    {
        this.dialsGoal = dialsGoal;
    }

    public double getHoursTotalGoal()
    {
        return hoursTotalGoal;
    }

    public void setHoursTotalGoal(double hoursTotalGoal)
    {
        this.hoursTotalGoal = hoursTotalGoal;
    }

    public int getIndividualAppointmentsGoal()
    {
        return individualAppointmentsGoal;
    }

    public void setIndivApptGoal(int indivApptGoal)
    {
        this.individualAppointmentsGoal = indivApptGoal;
    }

    public int getMinHoursRequired()
    {
        return minHoursRequired;
    }

    public void setMinHoursRequired(int minHoursRequired)
    {
        this.minHoursRequired = minHoursRequired;
    }

    public double getNewMonthlyGoal()
    {
        return newMonthlyGoal;
    }

    public void setNewMonthlyGoal(double newMonthlyGoal)
    {
        this.newMonthlyGoal = newMonthlyGoal;
    }

    public int getNewPartnersGoal()
    {
        return newPartnersGoal;
    }

    public void setNewPartnersGoal(int newPartnersGoal)
    {
        this.newPartnersGoal = newPartnersGoal;
    }

    public int getReferralsGainedGoal()
    {
        return referralsGainedGoal;
    }

    public void setReferralsGainedGoal(int referralsGained)
    {
        this.referralsGainedGoal = referralsGained;
    }

    public int getTalkedToGoal()
    {
        return talkedToGoal;
    }

    public void setTalkedToGoal(int talkedToGoal)
    {
        this.talkedToGoal = talkedToGoal;
    }

    private static final long serialVersionUID = 1L;
}
