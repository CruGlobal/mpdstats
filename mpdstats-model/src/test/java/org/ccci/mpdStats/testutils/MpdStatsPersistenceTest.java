package org.ccci.mpdStats.testutils;

import org.ccci.testutils.persistence.HibernatePersistenceTest;

public class MpdStatsPersistenceTest extends HibernatePersistenceTest
{

    @Override
    protected String getDefaultPersistenceUnitName()
    {
        return "MpdStats";
    }

}
