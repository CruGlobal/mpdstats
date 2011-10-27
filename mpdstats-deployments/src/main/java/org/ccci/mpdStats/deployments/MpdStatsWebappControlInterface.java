package org.ccci.mpdStats.deployments;

import org.apache.log4j.Logger;
import org.ccci.deployment.Version;
import org.ccci.deployment.spi.WebappControlInterface;
import org.ccci.deployment.util.PageMatcher;

public class MpdStatsWebappControlInterface implements WebappControlInterface
{
    
    Logger log = Logger.getLogger(MpdStatsWebappControlInterface.class);

    private final String server;
    private final int port;
    
    public MpdStatsWebappControlInterface(String server, int port)
    {
        this.server = server;
        this.port = port;
    }

    @Override
    public void disableForUpgrade()
    {
    }
    
    @Override
    public void verifyNewDeploymentActive()
    {
        String uri = "http://" + server + ":" + port +"/MpdStats/up.html";
        new PageMatcher().pingUntilPageMatches(uri, ".*System is Up.*", "up.html", 180);
    }

    @Override
    public Version getCurrentVersion()
    {
        throw new UnsupportedOperationException();
    }

}
