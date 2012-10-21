package net.ggtools.maven;

import static net.ggtools.maven.ddlgenerator.SpringConfiguration.ENV_PREFIX;
import static org.testng.Assert.assertEquals;

import java.io.File;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.dialect.Oracle10gDialect;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.MapPropertySource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DDLGeneratorMojoTest {

	private static final String SCHEMA = "defaultSchema";

	private static final String[] PERSISTENCE_XML_LOCATIONS = new String[] { "ga", "bu", "zo" };

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
