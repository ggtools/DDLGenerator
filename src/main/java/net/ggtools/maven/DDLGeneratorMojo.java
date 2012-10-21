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

package net.ggtools.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.ggtools.maven.ddlgenerator.DDLGenerator;
import net.ggtools.maven.ddlgenerator.SpringConfiguration;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

/**
 * Goal which touches a timestamp file. 
 * 
 * @goal touch
 * @phase process-sources
 */
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
	 * Whether or not use the new generator mapping in hibernate
	 * 
	 * @parameter default-value=false
	 */
	private Boolean useNewGenerator;

	private MapPropertySource createPropertySource() {
		final Map<String, Object> properties = new HashMap<String, Object>();
		// TODO create properties from parameters
		return new MapPropertySource("mojoPropertySource", properties);
	}

	@Override
	public void execute() throws MojoExecutionException {
		final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		final ConfigurableEnvironment environment = applicationContext.getEnvironment();
		final MutablePropertySources propertySources = environment.getPropertySources();
		final MapPropertySource propertySource = createPropertySource();
		propertySources.addFirst(propertySource);
		applicationContext.register(SpringConfiguration.class);
		applicationContext.refresh();
		final DDLGenerator generator = applicationContext.getBean(DDLGenerator.class);
	}

}
