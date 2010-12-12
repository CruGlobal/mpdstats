package org.ccci.mpdStats.view.backing;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import org.ccci.mpdStats.entity.MonthlyCommitment;
import org.ccci.mpdStats.entity.MpdGoals;
import org.ccci.mpdStats.entity.SpecialCommitment;
import org.ccci.mpdStats.entity.StatsReport;
import org.ccci.mpdStats.session.ReportProcess;
import org.ccci.util.Math;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Name("provision")
public class Provision
{
    private List<MonthlyCommitment> monthlyCommitmentsList;
    private List<SpecialCommitment> specialCommitmentsList;

    private MonthlyCommitment selectedMonthlyCommitment;
    private SpecialCommitment selectedSpecialCommitment;
    
    @In ReportProcess reportProcess;

    @In FacesMessages facesMessages;
    
    
    @Create
    public void create() throws ParseException
    {
        reportProcess.init();
        
        monthlyCommitmentsList = Lists.newArrayList(reportProcess.getReport().getMonthlyCommitments());
        monthlyCommitmentsList.add(MonthlyCommitment.createBlankEntry());
        
        specialCommitmentsList = Lists.newArrayList(reportProcess.getReport().getSpecialCommitments());
        specialCommitmentsList.add(SpecialCommitment.createBlankEntry());

    }
    
    // ==========================================
    //            Actions
    // ==========================================

    @Transactional
    public String submit()
    {
        addAnyNewCommitments();
        if (reportProcess.submitReport())
        {
            return "submitted";
        }
        else
        {
            return null;
        }
    }
    

    @Transactional
    public String save()
    {
        saveInternal();
        facesMessages.add("Report Saved");
        return "saved";
    }

    private void saveInternal()
    {
        addAnyNewCommitments();
        reportProcess.save();
    }

    private void addAnyNewCommitments()
    {
        for (MonthlyCommitment monthlyCommitment : monthlyCommitmentsList)
        {
            if (!monthlyCommitment.hasKey() && !isBlank(monthlyCommitment))
            {
                getReport().getMonthlyCommitments().add(monthlyCommitment);
            }
        }
        for (SpecialCommitment specialCommitment : specialCommitmentsList)
        {
            if (!specialCommitment.hasKey() && !isBlank(specialCommitment))
            {
                getReport().getSpecialCommitments().add(specialCommitment);
            }
        }
    }
    
    private static boolean isBlank(SpecialCommitment specialCommitment)
    {
        if (specialCommitment == null)
        {
            return false;
        }
        return specialCommitment.getAccountName().equals(SpecialCommitment.BLANK_ACCOUNT_NAME_VALUE);
    }

    private static boolean isBlank(MonthlyCommitment monthlyCommitment)
    {
        if (monthlyCommitment == null)
        {
            return false;
        }
        return monthlyCommitment.getAccountName().equals(MonthlyCommitment.BLANK_ACCOUNT_NAME_VALUE);
    }

    @Transactional
    public void removeMonthlyCommitment()
    {
        Preconditions.checkNotNull(selectedMonthlyCommitment, "selectedMonthlyCommitment is null");
        reportProcess.removeMonthlyCommitment(selectedMonthlyCommitment);
        monthlyCommitmentsList.remove(selectedMonthlyCommitment);
    }
    
    @Transactional
    public void removeSpecialCommitment()
    {
        Preconditions.checkNotNull(selectedSpecialCommitment, "selectedSpecialCommitment is null");
        reportProcess.removeSpecialCommitment(selectedSpecialCommitment);
        specialCommitmentsList.remove(selectedSpecialCommitment);
    }

    @Transactional
    public void addMonthlyCommitmentInput()
    {
        saveInternal();
        monthlyCommitmentsList.add(MonthlyCommitment.createBlankEntry());
    }
    
    @Transactional
    public void addSpecialCommitmentInput()
    {
        saveInternal();
        specialCommitmentsList.add(SpecialCommitment.createBlankEntry());
    }

    // ==========================================
    //            getters with logic
    // ==========================================

    
    public double getSpecialNeedsToDateFractionOfGoal()
    {
        return Math.safeDivide(getSpecialRaisedToDate(), getReport().getSpecialGoal());
    }

    public double getSpecialNeedsNeeded()
    {
        return getReport().getSpecialGoal() - getSpecialRaisedToDate();
    }

    public double getMonthlySupportNeeded()
    {
        return getReport().getSupportGoal() - getSupportRaisedToDate();
    }

    public double getMonthlySupportRaisedFractionOfGoal()
    {
        return Math.safeDivide(getSupportRaisedToDate(), getReport().getSupportGoal());
    }

    public boolean isMonthlyCommitmentAccountNameFieldRequired(MonthlyCommitment monthlyCommitment)
    {
        return !isBlank(monthlyCommitment);
    }
    
    public boolean isMonthlyCommitmentRemoveButtonRendered(MonthlyCommitment monthlyCommitment)
    {
        return !isBlank(monthlyCommitment);
    }

    public boolean isSpecialCommitmentAccountNameFieldRequired(SpecialCommitment specialCommitment)
    {
        return !isBlank(specialCommitment);
    }
    
    public boolean isSpecialCommitmentRemoveButtonRendered(SpecialCommitment specialCommitment)
    {
        return !isBlank(specialCommitment);
    }
    
    public double getSupportGoalToDate()
    {
        return reportProcess.calculateMonthlySupportRaisedToDateGoal();
    }
    
    public double getSupportRaisedToDate()
    {
        return reportProcess.calculateMonthlySupportRaisedToDate();
    }

    public double getSpecialRaisedToDate()
    {
        return reportProcess.calculateSpecialSupportRaisedToDate();
    }
    
    public StatsReport getReport()
    {
        return reportProcess.getReport();
    }
    
    public MpdGoals getMpdGoals()
    {
        return reportProcess.getMpdGoals();
    }
    
    // ==========================================
    //            straight getters/setters
    // ==========================================

    public List<MonthlyCommitment> getMonthlyCommitmentsList()
    {
        return Collections.unmodifiableList(monthlyCommitmentsList);
    }

    public List<SpecialCommitment> getSpecialCommitmentsList()
    {
        return Collections.unmodifiableList(specialCommitmentsList);
    }

    public MonthlyCommitment getSelectedMonthlyCommitment()
    {
        return selectedMonthlyCommitment;
    }

    public void setSelectedMonthlyCommitment(MonthlyCommitment selectedMonthlyCommitment)
    {
        this.selectedMonthlyCommitment = selectedMonthlyCommitment;
    }

    public SpecialCommitment getSelectedSpecialCommitment()
    {
        return selectedSpecialCommitment;
    }

    public void setSelectedSpecialCommitment(SpecialCommitment selectedSpecialCommitment)
    {
        this.selectedSpecialCommitment = selectedSpecialCommitment;
    }
    
}
