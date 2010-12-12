package org.ccci.mpdStats.view.process;

import static org.jboss.seam.ScopeType.STATELESS;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;


@Name("mpdSystemMonitorDbConnection")
@Scope(STATELESS)
@AutoCreate
public class SystemMonitorDbConnection {

	@In EntityManager psEntityManager;
    
	public boolean testPeoplesoftDatabase(){
		List<?> list = null;
		try{
			Query q = psEntityManager.createNamedQuery("NewStaffTrainingSessionStartDate.getAllNewStaffTrainingSessions");
			list = q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list != null;
	}

}
