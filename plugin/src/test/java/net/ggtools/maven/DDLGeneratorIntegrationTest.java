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

import net.ggtools.maven.ddlgenerator.DDLGenerator;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * User: Christophe Labouisse
 * Date: 10/24/12
 * Time: 15:36
 */
public class DDLGeneratorIntegrationTest {

    private DDLGeneratorMojo mojo = new DDLGeneratorMojo();

    private static final String SCHEMA = "defaultSchema";

    private static final String[] PERSISTENCE_XML_LOCATIONS = new String[]{"classpath:/META-INF/MyPU.xml"};

    private static final String MY_PU = "My PU";

    private File ddlFile;

    private final Log log = new SystemStreamLog();

    @BeforeMethod
    public void setUp() throws Exception {
        ddlFile = File.createTempFile("DDLGeneratorIT", ".sql");
        ddlFile.deleteOnExit();
        mojo.setDdlFile(ddlFile);
        mojo.setDialect(Oracle10gDialect.class.getName());
        mojo.setDefaultSchema(SCHEMA);
        mojo.setNamingStrategy(ImprovedNamingStrategy.class.getName());
        mojo.setPersistenceUnitName(MY_PU);
        mojo.setPersistenceXmlLocations(PERSISTENCE_XML_LOCATIONS);
        mojo.setUseNewGenerator(true);
        mojo.setLog(log);
    }

    @Test
    public void createContext() throws Exception {
        AnnotationConfigApplicationContext context = mojo.createApplicationContext();
        DDLGenerator generator = context.getBean(DDLGenerator.class);
    }

    @Test
    public void executeOracle() throws Exception {
        mojo.execute();
        assertTrue(ddlFile.exists());
    }

    @Test
    public void executePostgreSQL() throws Exception {
        mojo.setDialect(PostgreSQL82Dialect.class.getName());
        mojo.execute();
        assertTrue(ddlFile.exists());
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (ddlFile != null) {
            ddlFile.delete();
        }
    }
}
