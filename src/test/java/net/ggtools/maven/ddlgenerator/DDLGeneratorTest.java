/*
 * This file is part of ddlGenerator. Copyright ©2012 Christophe Labouisse
 *
 * ddlGenerator is free software: you can redistribute it and/or modifyit under the terms of the GNU General Public License as published bythe Free Software Foundation, either version 3 of the License, or(at your option) any later version.
 *
 * ddlGenerator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ddlGenerator.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.ggtools.maven.ddlgenerator;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.hibernate.dialect.Oracle10gDialect;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanInitializationException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * User: Christophe Labouisse Date: 10/19/12 Time: 07:53
 */
public class DDLGeneratorTest {
	private DDLGenerator generator;

	@Mock
	private Log log;

	private final Map<String, Object> referenceConfigProperties = new HashMap<String, Object>();

	private final String dialect = Oracle10gDialect.class.getCanonicalName();

	@Test
	public void initIsOk() throws Exception {
		generator.setDdlFile(new File("test.sql"));
		generator.setDialect(dialect);
		generator.init();
		final Map<String, Object> configProperties = generator.getConfigProperties();
		assertEquals(configProperties, referenceConfigProperties);
	}

	@Test(expectedExceptions = BeanInitializationException.class)
	public void initMissingDDLFile() throws Exception {
		generator.setDialect(dialect);
		generator.init();
	}

	@Test(expectedExceptions = BeanInitializationException.class)
	public void initMissingDialect() throws Exception {
		generator.setDdlFile(new File("test.sql"));
		generator.init();
	}

	@BeforeMethod
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		generator = new DDLGenerator(log);
		referenceConfigProperties.put("hibernate.hbm2ddl.auto", "create");
		referenceConfigProperties.put("hibernate.use_sql_comments", false);
		referenceConfigProperties.put("hibernate.format_sql", false);
		referenceConfigProperties.put("hibernate.show_sq", false);
		referenceConfigProperties.put("hibernate.dialect", dialect);
	}

	@AfterMethod
	public void tearDown() throws Exception {

	}
}
