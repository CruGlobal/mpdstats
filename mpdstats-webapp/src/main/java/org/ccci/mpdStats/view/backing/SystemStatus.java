package org.ccci.mpdStats.view.backing;

import static org.jboss.seam.ScopeType.STATELESS;
import org.ccci.mpdStats.view.process.SystemMonitorDbConnection;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("mpdSystemStatus")
@Scope(STATELESS)
@AutoCreate
public class SystemStatus {
	@In SystemMonitorDbConnection mpdSystemMonitorDbConnection;
	
	private boolean psSuccess;
	
	public String summaryDbCheck(){
		psDbCheck();
		return (psSuccess) ? "SUCCESS" : "FAILURE";
	}
	
	public String psDbCheck(){
		psSuccess = mpdSystemMonitorDbConnection.testPeoplesoftDatabase();
		return psSuccess? "SUCCESS" : "FAILURE";
	}

}
