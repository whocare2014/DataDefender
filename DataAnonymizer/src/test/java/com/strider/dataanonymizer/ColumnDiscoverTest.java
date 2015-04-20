/*
 * Copyright 2014, Armenak Grigoryan, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package com.strider.dataanonymizer;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.strider.dataanonymizer.database.H2DB;
import com.strider.dataanonymizer.database.metadata.MatchMetaData;

/**
 * @author Akira Matsuo
 */
public class ColumnDiscoverTest extends H2DB {
    
    @SuppressWarnings("serial")
    final Properties sampleCProps = new Properties() {{ setProperty("fname", "true" ); }};
    @SuppressWarnings("serial")
    final Properties badCProps = new Properties() {{ setProperty("la colonna non esiste", "true" ); }};

    @Test
    public void testWithColumns() throws AnonymizerException { 
        IDiscoverer discoverer = new ColumnDiscoverer();
        List<MatchMetaData> suspects = discoverer.discover(factory, sampleCProps, new HashSet<String>());
        assertEquals(1, suspects.size());
        assertEquals("ju_users.fname", suspects.get(0).toString());
        assertEquals("null.ju_users.fname(varchar)", suspects.get(0).toVerboseStr());
    }

    @Test
    public void testWithTablesColumns() throws AnonymizerException { 
        IDiscoverer discoverer = new ColumnDiscoverer();
        List<MatchMetaData> suspects = discoverer.discover(factory, sampleCProps, 
            new HashSet<String>(Arrays.asList("ju_users")));
        assertEquals(1, suspects.size());
        assertEquals("ju_users.fname", suspects.get(0).toString());
        assertEquals("null.ju_users.fname(varchar)", suspects.get(0).toVerboseStr());
    }

    @Test
    public void testWithBadTablesColumns() throws AnonymizerException { 
        IDiscoverer discoverer = new ColumnDiscoverer();
        List<MatchMetaData> suspects = discoverer.discover(factory, sampleCProps, 
            new HashSet<String>(Arrays.asList("il tavolo non esiste")));
        assertTrue(suspects.isEmpty());
    }

    @Test
    public void testWithTablesBadColumns() throws AnonymizerException { 
        IDiscoverer discoverer = new ColumnDiscoverer();
        List<MatchMetaData> suspects = discoverer.discover(factory, badCProps, 
            new HashSet<String>(Arrays.asList("ju_users")));
        assertTrue(suspects.isEmpty());
    }
}