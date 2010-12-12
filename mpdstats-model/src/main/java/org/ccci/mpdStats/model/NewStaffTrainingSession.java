package org.ccci.mpdStats.model;

import java.io.Serializable;

import org.ccci.util.ValueObject;
import org.ccci.util.time.Year;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

/**
 * Represents a New Staff Training session.  There are four sessions per year, corresponding to the appropriate season.
 * Represented by a {@link Season} and a {@link Year}
 * 
 * 
 * @author Matt Drees
 *
 */
public class NewStaffTrainingSession extends ValueObject implements Serializable, Comparable<NewStaffTrainingSession>
{
    
    public enum Season
    {
        /** Spring */
        SP,
        /** Summer */
        SU,
        /** Fall */
        FA,
        /** Winter */
        WI;
        
        //This needs to be a static, non-calculated string, because it needs to be referenceable in annotations
        //(in particular, @Pattern(regex = Season.REGEX) )
        public static final String REGEX = "SP|SU|FA|WI";
        static
        {
            assert REGEX.equals(Joiner.on("|").join(values()));
        }
    }

    private static final String YEAR_REGEX = "[0-9]{2}";
    public static final String REGEX = "(" + Season.REGEX + ")" + YEAR_REGEX;
    
    private final Season season;
    private final Year year;
    
    /**
     * @param season
     * @param year must be between 2000 and 2100, since we represent the date only as a two digit string. 
     */
    public NewStaffTrainingSession(Season season, Year year)
    {
        Preconditions.checkNotNull(season, "season is null");
        Preconditions.checkNotNull(year, "year is null");
        Preconditions.checkArgument(!year.isBefore(Year.newYear(2000)) && year.isBefore(Year.newYear(2100)), "Invalid year: %s", year);
        this.season = season;
        this.year = year;
    }

    /**
     * Returns the concatenation of the season and the two-digit year
     * e.g. "SP09"
     */
    @Override
    public String toString()
    {
        return season.toString() + getYearAsString();
    }
    
    /**
     * Parses, e.g. "SP09"
     * @param string
     * @return
     */
    public static NewStaffTrainingSession valueOf(String string)
    {
        Preconditions.checkNotNull(string, "string is null");
        Preconditions.checkArgument(string.matches(REGEX), "string is not a valid new staff training session identifier: %s", string);
        return valueOf(string.substring(0, 2), string.substring(2));
    }

    public static NewStaffTrainingSession valueOf(Season season, String year)
    {
        Preconditions.checkNotNull(season, "season is null");
        Preconditions.checkNotNull(year, "year is null");
        Preconditions.checkArgument(year.matches(YEAR_REGEX), "year is not a valid new staff training session year: %s", year);
        return new NewStaffTrainingSession(season, Year.valueOf("20" + year));
    }

    public static NewStaffTrainingSession valueOf(String season, String year)
    {
        Preconditions.checkNotNull(season, "season is null");
        Preconditions.checkNotNull(year, "year is null");
        Preconditions.checkArgument(season.matches(Season.REGEX), "season is not a valid new staff training session season: %s", season);
        return valueOf(Season.valueOf(season), year);
    }
    
    
    @Override
    protected Object[] getComponents()
    {
        return new Object[]{season, year};
    }
    
    public int compareTo(NewStaffTrainingSession other)
    {
        int yearComparison = this.year.compareTo(other.year);
        if (yearComparison != 0) return yearComparison;
        return this.season.compareTo(other.season);
    }
    
    public Season getSeason()
    {
        return season;
    }

    public Year getYear()
    {
        return year;
    }
    
    /**
     * returns the last two digits of the year
     */
    public String getYearAsString()
    {
        return year.toString().substring(2);
    }

    private static final long serialVersionUID = 1L;

}
