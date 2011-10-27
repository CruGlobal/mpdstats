package org.ccci.mpdStats.deployments;

import java.util.List;
import java.util.Set;

import org.ccci.deployment.BasicWebappDeployment;
import org.ccci.deployment.MavenRepositoryLocalDeploymentStorage;
import org.ccci.deployment.Node;
import org.ccci.deployment.RestartType;
import org.ccci.deployment.WebappDeployment;
import org.ccci.deployment.WebappDeployment.Packaging;
import org.ccci.deployment.spi.AppserverInterface;
import org.ccci.deployment.spi.DeploymentConfiguration;
import org.ccci.deployment.spi.DeploymentTransferInterface;
import org.ccci.deployment.spi.LoadbalancerInterface;
import org.ccci.deployment.spi.LocalDeploymentStorage;
import org.ccci.deployment.spi.WebappControlInterface;
import org.ccci.deployment.windows.SimpleWindowsServiceAppserverInterface;
import org.ccci.deployment.windows.SmbDeploymentTransferService;
import org.ccci.util.mail.EmailAddress;
import org.ccci.windows.smb.ActiveDirectoryCredential;
import org.ccci.windows.smb.SmbEndpoint;

public class MpdStatsDeploymentConfiguration implements DeploymentConfiguration
{

    private final ActiveDirectoryCredential credential;
    private final MpdStatsEnvironment environment;
    private String groupId = "org.ccci";
    private String artifactId = "mpdstats-webapp";
    private String version;
    
    public MpdStatsDeploymentConfiguration(
        ActiveDirectoryCredential credential,
        MpdStatsEnvironment environment, 
        String version)
    {
        this.credential = credential;
        this.environment = environment;
        this.version = version;
    }

    @Override
    public AppserverInterface buildAppserverInterface(Node node)
    {
        return new SimpleWindowsServiceAppserverInterface(node, environment.serviceName, credential);
    }

    @Override
    public DeploymentTransferInterface connectDeploymentTransferInterface(Node node)
    {
        String serverRoot = "W$/jboss-4.2.3.GA/server/production";
        String remoteDeploymentDirectory = serverRoot + "/deploy";
        String remoteTransferDirectory = serverRoot + "/tmp";
        String remoteBackupDirectory = serverRoot + "/data/backups";
        return new SmbDeploymentTransferService(
            createEndpoint(node), 
            remoteDeploymentDirectory, 
            remoteTransferDirectory, 
            remoteBackupDirectory);
    }

    private SmbEndpoint createEndpoint(Node node)
    {
        return new SmbEndpoint(
            credential,
            node.getHostname());
    }

    @Override
    public LocalDeploymentStorage buildLocalDeploymentStorage()
    {
        return new MavenRepositoryLocalDeploymentStorage(artifactId, groupId, version);
    }

    @Override
    public WebappControlInterface buildWebappControlInterface(Node node)
    {
        return new MpdStatsWebappControlInterface(node.getHostname(), environment.port);
    }

    @Override
    public WebappDeployment buildWebappDeployment()
    {
        BasicWebappDeployment deployment = new BasicWebappDeployment();
        deployment.setDeployedWarName("mpdstats");
        deployment.setName(artifactId + "-" + version);
        deployment.setPackaging(Packaging.ARCHIVE);
        return deployment;
    }

    @Override
    public RestartType getDefaultRestartType()
    {
        return RestartType.FULL_PROCESS_RESTART;
    }

    @Override
    public List<Node> listNodes()
    {
        return environment.listNodes();
    }

    @Override
    public boolean supportsCautiousShutdown()
    {
        return false;
    }

    @Override
    public Set<EmailAddress> listDeploymentNotificationRecipients()
    {
        return environment.getDeploymentSubscribers();
    }

    @Override
    public LoadbalancerInterface buildLoadBalancerInterface()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void closeResources()
    {
    }

    @Override
    public int getWaitTimeBetweenNodes()
    {
        return 30;
    }

}
