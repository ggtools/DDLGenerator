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

package net.ggtools.maven.ddlgenerator;

import java.io.File;

import javax.sql.DataSource;

import org.apache.maven.plugin.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;

/**
 * User: Christophe Labouisse Date: 10/18/12 Time: 18:12
 */
@Configuration
public class SpringConfiguration {

	public final static String ENV_PREFIX = "jpahbn2ddl";

	@Autowired
	Environment env;

	@Bean
	public DataSource dataSource() {
		return new SimpleDriverDataSource();
	}

	@Bean
	public DDLGenerator ddlGenerator() {
		final Log log = env.getProperty(ENV_PREFIX + ".log", Log.class);
		final DDLGenerator generator = new DDLGenerator(log);
		generator.setDdlFile(env.getProperty(ENV_PREFIX + ".ddlFile", File.class));
		generator.setDefaultSchema(env.getProperty(ENV_PREFIX + ".defaultSchema", String.class));
		generator.setDialect(env.getProperty(ENV_PREFIX + ".dialect", String.class));
		generator.setNamingStrategy(env.getProperty(ENV_PREFIX + ".namingStrategy", String.class));
		generator.setPersistenceUnitName(env.getProperty(ENV_PREFIX + ".persistenceUnitName", String.class));
		generator.setUseNewGenerator(env.getProperty(ENV_PREFIX + ".useNewGenerator", Boolean.class));
		return generator;
	}

	@Bean
	public DefaultPersistenceUnitManager persistenceUnitManager() {
		MultiConfigAwarePersistenceUnitManager puManager = new MultiConfigAwarePersistenceUnitManager();
		puManager.setDefaultDataSource(dataSource());
		puManager.setPersistenceUnitName(env.getProperty(ENV_PREFIX + ".persistenceUnitName", String.class));
		String[] xmlLocations = env.getProperty(ENV_PREFIX + ".persistenceXmlLocations", String[].class);
		if (xmlLocations != null) {
			puManager.setPersistenceXmlLocations(xmlLocations);
		}
		return puManager;
	}
}
