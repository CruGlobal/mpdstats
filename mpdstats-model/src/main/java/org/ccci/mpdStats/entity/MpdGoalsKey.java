package org.ccci.mpdStats.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.ccci.util.ValueObject;

@Embeddable
public class MpdGoalsKey extends ValueObject implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(name = "EFFDT", nullable = false)
    private Date effectiveDate;

    @Column(name = "MAX_HOURS", nullable = false)
    private int maxHours = 0;

    public MpdGoalsKey()
    {
    }

    public MpdGoalsKey(Date effectiveDate, int maxHours)
    {
        this.effectiveDate = effectiveDate;
        this.maxHours = maxHours;
    }

    @Override
    protected Object[] getComponents()
    {
        return new Object[] { effectiveDate, maxHours };
    }

    public Date getEffectiveDate()
    {
        return effectiveDate;
    }

    public int getMaxHours()
    {
        return maxHours;
    }
    
}
