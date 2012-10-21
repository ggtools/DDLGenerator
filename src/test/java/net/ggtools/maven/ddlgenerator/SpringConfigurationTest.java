package net.ggtools.maven.ddlgenerator;

import static net.ggtools.maven.ddlgenerator.SpringConfiguration.ENV_PREFIX;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

import java.io.File;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.maven.plugin.logging.Log;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SpringConfigurationTest extends AbstractTestWithConfig {
	private static final String[] PERSISTENCE_XML_LOCATIONS = new String[] { "ga", "bu", "zo" };

	private static final String MY_PU = "myPU";

	@Mock
	private Environment env;

	@InjectMocks
	private final SpringConfiguration conf = new SpringConfiguration();

	@Mock
	private Log log;

	private final File ddlFile = new File("test.sql");

	@BeforeMethod
	public void beforeMethod() {
		referenceConfigProperties.put("hibernate.id.new_generator_mappings", true);
		referenceConfigProperties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");

		when(env.getProperty(eq(ENV_PREFIX + ".ddlFile"), eq(File.class))).thenReturn(ddlFile);
		when(env.getProperty(eq(ENV_PREFIX + ".persistenceUnitName"), eq(String.class))).thenReturn(MY_PU);

		when(env.getProperty(eq(ENV_PREFIX + ".defaultSchema"), eq(String.class))).thenReturn(
				(String) referenceConfigProperties.get("hibernate.default_schema"));
		when(env.getProperty(eq(ENV_PREFIX + ".dialect"), eq(String.class))).thenReturn(
				(String) referenceConfigProperties.get("hibernate.dialect"));
		when(env.getProperty(eq(ENV_PREFIX + ".namingStrategy"), eq(String.class))).thenReturn(
				(String) referenceConfigProperties.get("hibernate.ejb.naming_strategy"));
		when(env.getProperty(eq(ENV_PREFIX + ".useNewGenerator"), eq(Boolean.class))).thenReturn(
				(Boolean) referenceConfigProperties.get("hibernate.id.new_generator_mappings"));

		when(env.getProperty(eq(ENV_PREFIX + ".log"), eq(Log.class))).thenReturn(log);
	}

	@Test
	public void dataSource() throws Exception {
		DataSource dataSource = conf.dataSource();
		assertNotNull(dataSource);
	}

	@Test
	public void ddlGenerator() {
		DDLGenerator generator = conf.ddlGenerator();
		generator.init(); // Put the properties into the configuration map.
		Map<String, Object> configProperties = generator.getConfigProperties();
		assertEquals(configProperties, referenceConfigProperties);
		assertSame(ReflectionTestUtils.getField(generator, "log"), log);
		assertSame(ReflectionTestUtils.getField(generator, "ddlFile"), ddlFile);
		assertSame(ReflectionTestUtils.getField(generator, "persistenceUnitName"), MY_PU);

	}

	@Test
	public void persistenceManagerUnitNoPersistenceLocations() throws Exception {
		DefaultPersistenceUnitManager persistenceUnitManager = conf.persistenceUnitManager();
		assertNotNull(persistenceUnitManager);
	}

	@Test
	public void persistenceManagerUnitWithPersistenceLocations() throws Exception {
		when(env.getProperty("persistenceXmlLocations", String[].class)).thenReturn(PERSISTENCE_XML_LOCATIONS);
		DefaultPersistenceUnitManager persistenceUnitManager = conf.persistenceUnitManager();
		assertNotNull(persistenceUnitManager);
	}
}
