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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.ggtools.maven.ddlgenerator.DDLGenerator;
import net.ggtools.maven.ddlgenerator.SpringConfiguration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static net.ggtools.maven.ddlgenerator.SpringConfiguration.ENV_PREFIX;

/**
 * Goal which touches a timestamp file. 
 * 
 * @goal touch
 * @phase process-sources
 */
@SuppressWarnings("unused")
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public class DDLGeneratorMojo extends AbstractMojo {

	/**
	 * Destination file
	 * 
	 * @parameter
	 * @required
	 */
	private File ddlFile;

	/**
	 * Default schema
	 * 
	 * @parameter
	 */
	private String defaultSchema;

	/**
	 * Database dialect.
	 * 
	 * @parameter
	 * @required
	 */
	private String dialect;

	/**
	 * Naming strategy.
	 * 
	 * @parameter
	 */
	private String namingStrategy;

	/**
	 * Name of the persistence unit used for the DDL generation.
	 * 
	 * @parameter
	 * @required
	 */
	private String persistenceUnitName;

	/**
	 * Locations of the persistence.xml files.
	 * 
	 * @parameter
	 */
	private String[] persistenceXmlLocations;

	/**
	 * Whether or not use the new generator mapping in hibernate
	 * 
	 * @parameter default-value=false
	 */
	private Boolean useNewGenerator;

	AnnotationConfigApplicationContext createApplicationContext() {
		final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		final ConfigurableEnvironment environment = applicationContext.getEnvironment();
		final MutablePropertySources propertySources = environment.getPropertySources();
		final MapPropertySource propertySource = createPropertySource();
		propertySources.addFirst(propertySource);
		applicationContext.register(SpringConfiguration.class);
		applicationContext.refresh();
		return applicationContext;
	}

	MapPropertySource createPropertySource() {
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(ENV_PREFIX + ".ddlFile", ddlFile);
		properties.put(ENV_PREFIX + ".defaultSchema", defaultSchema);
		properties.put(ENV_PREFIX + ".dialect", dialect);
		properties.put(ENV_PREFIX + ".namingStrategy", namingStrategy);
		properties.put(ENV_PREFIX + ".persistenceUnitName", persistenceUnitName);
		properties.put(ENV_PREFIX + ".useNewGenerator", useNewGenerator);
		properties.put(ENV_PREFIX + ".persistenceXmlLocations", persistenceXmlLocations);
        properties.put(ENV_PREFIX + ".log", getLog());
		return new MapPropertySource("mojoPropertySource", properties);
	}

	@Override
	public void execute() throws MojoExecutionException {
		AnnotationConfigApplicationContext applicationContext = null;

        try {
            applicationContext = createApplicationContext();
            final DDLGenerator generator = applicationContext.getBean(DDLGenerator.class);
            getLog().info("Creating schema to " + ddlFile);
            generator.createSchema();
        } finally {
            if (applicationContext != null) {
                applicationContext.destroy();
            }
        }
    }

}
