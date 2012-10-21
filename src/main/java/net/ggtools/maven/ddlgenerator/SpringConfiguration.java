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

import org.apache.maven.plugin.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * User: Christophe Labouisse Date: 10/18/12 Time: 18:12
 */
@Configuration
public class SpringConfiguration {

	@Autowired
	Environment env;

	@Bean
	DDLGenerator ddlGenerator() {
		final Log log = env.getProperty("log", Log.class);
		final DDLGenerator generator = new DDLGenerator(log);
		// TODO set properties
		return generator;
	}
}
