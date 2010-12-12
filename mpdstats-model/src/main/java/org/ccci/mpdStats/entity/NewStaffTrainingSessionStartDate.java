package org.ccci.mpdStats.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.ccci.annotations.JpaConstructor;
import org.ccci.mpdStats.model.NewStaffTrainingSession;
import org.ccci.mpdStats.model.NewStaffTrainingSession.Season;
import org.ccci.util.ValueObject;
import org.ccci.util.strings.ToStringBuilder;
import org.ccci.util.strings.ToStringProperty;
import org.ccci.util.time.TimeUtil;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.joda.time.LocalDate;

import com.google.common.base.Preconditions;

@Entity
@Table(name = "PS_MPD_STAT_NST")
@NamedQueries({
    @NamedQuery(name = "NewStaffTrainingSessionStartDate.getAllNewStaffTrainingSessions",
                query = "select nstStartDate.key from NewStaffTrainingSessionStartDate nstStartDate"),
    @NamedQuery(name = "NewStaffTrainingSessionStartDate.getMostRecentNewStaffTrainingSession",
                query = "select nstStartDate.key from NewStaffTrainingSessionStartDate nstStartDate" +
                		" where nstStartDate.startDate = (" +
                        "  select max(mostRecent.startDate) from NewStaffTrainingSessionStartDate mostRecent " +
                        "  where mostRecent.startDate <= :limitDate)")})

public class NewStaffTrainingSessionStartDate implements Serializable
{

    public static class Key extends ValueObject implements Serializable
    {
        @ToStringProperty
        @Column(name = "NST_SEASON")
        @NotNull
        @Enumerated(EnumType.STRING)
        private Season season;
        
        @ToStringProperty
        @Column(name = "NST_YEAR")
        @Length(max = 2)
        @Pattern(regex = "[0-9]{2}")
        @NotNull
        private String year;

        public Key(NewStaffTrainingSession newStaffTrainingSession)
        {
            Preconditions.checkNotNull(newStaffTrainingSession, "newStaffTrainingSession is null");
            this.season = newStaffTrainingSession.getSeason();
            this.year = newStaffTrainingSession.getYearAsString();
        }

        private Key()
        {
        }

        @Override
        protected Object[] getComponents()
        {
            return new Object[]{season, year};
        }
        
        @Override
        public String toString()
        {
            return new ToStringBuilder(this).toString();
        }
        
        public NewStaffTrainingSession getNewStaffTrainingSession()
        {
            return NewStaffTrainingSession.valueOf(season, year);
        }

        private static final long serialVersionUID = 1L;
    }
    

    @EmbeddedId
    private Key key;

    @Column(name = "START_DATE")
    @NotNull
    private Date startDate;
    
    public NewStaffTrainingSessionStartDate(NewStaffTrainingSession newStaffTrainingSession)
    {
        this.key = new Key(newStaffTrainingSession);
    }
    
    @JpaConstructor
    protected NewStaffTrainingSessionStartDate()
    {
        this.key = new Key();
    }

    public Key getKey()
    {
        return key;
    }

    public LocalDate getStartDate()
    {
        return TimeUtil.sqlDateToLocalDate(startDate);
    }

    public void setStartDate(LocalDate startDate)
    {
        this.startDate = TimeUtil.localDateToSqlDate(startDate);
    }

    private static final long serialVersionUID = 1L;
}
