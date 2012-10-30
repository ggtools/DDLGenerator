/*
 * This file is part of ddlGenerator. Copyright ©2012 Christophe Labouisse
 *
 * ddlGenerator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ddlGenerator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ddlGenerator.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.ggtools.maven;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.dialect.Oracle10gDialect;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.MapPropertySource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static net.ggtools.maven.ddlgenerator.SpringConfiguration.ENV_PREFIX;
import static org.testng.Assert.assertEquals;

public class DDLGeneratorMojoTest {

    private static final String SCHEMA = "defaultSchema";

    private static final String[] PERSISTENCE_XML_LOCATIONS = new String[]{"ga", "bu", "zo"};

    private static final String MY_PU = "myPU";

    private final DDLGeneratorMojo mojo = new DDLGeneratorMojo();

    private final File ddlFile = new File("test.sql");

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
        mojo.setDdlFile(ddlFile);
        mojo.setDialect(Oracle10gDialect.class.getName());
        mojo.setDefaultSchema(SCHEMA);
        mojo.setNamingStrategy(ImprovedNamingStrategy.class.getName());
        mojo.setPersistenceUnitName(MY_PU);
        mojo.setPersistenceXmlLocations(PERSISTENCE_XML_LOCATIONS);
        mojo.setUseNewGenerator(true);
    }

    @Test
    public void createPropertySource() {
        MapPropertySource propertySource = mojo.createPropertySource();
        assertEquals(propertySource.getProperty(ENV_PREFIX + ".ddlFile"), ddlFile);
        assertEquals(propertySource.getProperty(ENV_PREFIX + ".defaultSchema"), SCHEMA);
        assertEquals(propertySource.getProperty(ENV_PREFIX + ".dialect"), Oracle10gDialect.class.getName());
        assertEquals(propertySource.getProperty(ENV_PREFIX + ".namingStrategy"), ImprovedNamingStrategy.class.getName());
        assertEquals(propertySource.getProperty(ENV_PREFIX + ".persistenceUnitName"), MY_PU);
        assertEquals(propertySource.getProperty(ENV_PREFIX + ".useNewGenerator"), true);
        assertEquals(propertySource.getProperty(ENV_PREFIX + ".persistenceXmlLocations"), PERSISTENCE_XML_LOCATIONS);
    }
}
