package org.ccci.mpdStats.deployments;

import java.util.Set;

import org.ccci.deployment.InvalidEnvironmentException;
import org.ccci.deployment.MissingParameterException;
import org.ccci.deployment.Options;
import org.ccci.deployment.spi.Application;
import org.ccci.windows.smb.ActiveDirectoryCredential;
import org.testng.v6.Sets;

public class MpdStatsApplication implements Application
{

    @Override
    public MpdStatsDeploymentConfiguration buildDeploymentConfiguration(Options options)
    {

        MpdStatsEnvironment environment;
        try
        {
            environment = MpdStatsEnvironment.valueOf(options.environment.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            Set<String> possibilities = Sets.newHashSet();
            for (MpdStatsEnvironment possibility : MpdStatsEnvironment.values())
            {
                possibilities.add(possibility.toString().toLowerCase());
            }
            throw new InvalidEnvironmentException(options.environment, possibilities);
        }
        
        require(options.username, "username");
        require(options.password, "password");
        
        if (options.domain == null)
            options.domain = "NET";

        ActiveDirectoryCredential credential = new ActiveDirectoryCredential(
            options.domain, 
            options.password, 
            options.username);
        
        if (options.version == null)
        {
            options.version = getJarVersion();
        }
        
        return new MpdStatsDeploymentConfiguration(
            credential,
            environment, 
            options.version);
    }

    private String getJarVersion()
    {
        return getClass().getPackage().getImplementationVersion();
    }

    private void require(Object parameter, String parameterName)
    {
        if (parameter == null)
        {
            throw new MissingParameterException(parameterName);
        }
    }
    
    @Override
    public String getName()
    {
        return "MPD Stats";
    }

    @Override
    public boolean isDefault()
    {
        return true;
    }

}
