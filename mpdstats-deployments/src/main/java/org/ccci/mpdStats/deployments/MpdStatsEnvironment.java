package org.ccci.mpdStats.deployments;

import java.util.List;
import java.util.Set;

import org.ccci.deployment.Node;
import org.ccci.util.mail.EmailAddress;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public enum MpdStatsEnvironment
{
    TEST(
        buildA321(),
        buildProjectLead()),
    STAGING(
        buildA331A332(),
        buildProjectLead()),
    STAGE1(
        buildA331(),
        buildProjectLead()),
    STAGE2(
        buildA332(),
        buildProjectLead()),
    PRODUCTION(
        buildA341A342(),
        buildProductionSubscribers()),
    PROD1(
        buildA341(),
        buildProductionSubscribers()),
    PROD2(
        buildA342(),
        buildProductionSubscribers());

    public final String serviceName = "JBAS423SVC";
    
    public final List<Node> nodes;

    public Set<EmailAddress> deploymentSubscribers;

    public final int port = 8880;
    
    private MpdStatsEnvironment(
         List<Node> nodes, 
         Set<EmailAddress> deploymentSubscribers)
    {
        this.nodes = nodes;
        this.deploymentSubscribers = deploymentSubscribers;
    }


    private static Set<EmailAddress> buildProjectLead()
    {
        return ImmutableSet.of(EmailAddress.valueOf("matt.drees@ccci.org"));
    }

    private static Set<EmailAddress> buildProductionSubscribers()
    {
        return ImmutableSet.of(
            EmailAddress.valueOf("matt.drees@ccci.org"),
            EmailAddress.valueOf("ben.sisson@ccci.org"),
            EmailAddress.valueOf("ryan.t.carlson@ccci.org"),
            EmailAddress.valueOf("linda.ye@ccci.org"),
            EmailAddress.valueOf("steve.bratton@ccci.org"),
            EmailAddress.valueOf("luis.rodriguez@ccci.org"));
    }
    
    private static List<Node> buildA321()
    {
        return ImmutableList.of(new Node("a321", "hart-a321.net.ccci.org"));
    }
    
    private static List<Node> buildA331()
    {
        return ImmutableList.of(new Node("a331", "hart-a331.net.ccci.org"));
    }
    
    private static List<Node> buildA332()
    {
        return ImmutableList.of(new Node("a332", "hart-a332.net.ccci.org"));
    }
    
    private static List<Node> buildA331A332()
    {
        return ImmutableList.copyOf(
            Iterables.concat(buildA331(), buildA332()));
    }
    
    private static List<Node> buildA341()
    {
        return ImmutableList.of(new Node("a341", "hart-a341.net.ccci.org"));
    }
    
    private static List<Node> buildA342()
    {
        return ImmutableList.of(new Node("a342", "hart-a342.net.ccci.org"));
    }
    
    private static List<Node> buildA341A342()
    {
        return ImmutableList.copyOf(
            Iterables.concat(buildA341(), buildA342()));
    }

    public List<Node> listNodes()
    {
        return nodes;
    }

    public Set<EmailAddress> getDeploymentSubscribers()
    {
        return deploymentSubscribers;
    }
    
}