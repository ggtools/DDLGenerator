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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.logging.Log;
import org.hibernate.cfg.Configuration;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.persistence.spi.PersistenceUnitInfo;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
@Setter
public class DDLGenerator {

    // Getter for test purpose only.
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, Object> configProperties = new HashMap<String, Object>();

    private File ddlFile;
    private String defaultSchema;
    private String dialect;
    private String namingStrategy;
    private String persistenceUnitName;
    private Boolean useNewGenerator;

    @Autowired
    @Setter(AccessLevel.PACKAGE) // For test
    private Log log;

    @Autowired
    private DefaultPersistenceUnitManager puManager;


    private void createDirectoriesIfNeeded() {
        ddlFile.getParentFile().mkdirs();
    }

    public void createSchema() {
        log.info("Exporting DDL file to " + ddlFile);
        createDirectoriesIfNeeded();
        puManager.preparePersistenceUnitInfos();
        final PersistenceUnitInfo puInfo = puManager.obtainPersistenceUnitInfo(persistenceUnitName);
        final Ejb3Configuration ejb3Config = new Ejb3Configuration();
        ejb3Config.configure(puInfo, configProperties);
        final Field field = ReflectionUtils.findField(Ejb3Configuration.class, "cfg");
        ReflectionUtils.makeAccessible(field);
        final ServiceRegistry registry = new ServiceRegistryBuilder().applySettings(configProperties)
                .buildServiceRegistry();
        final Configuration configuration = (Configuration) ReflectionUtils.getField(field, ejb3Config);
        final SchemaExport export = new SchemaExport(registry, configuration);
        export.setDelimiter(";"); // TODO introduce parameter
        export.setOutputFile(ddlFile.getAbsolutePath());
        export.execute(true, false, false, true);
    }

    @PostConstruct
    public void init() throws BeanInitializationException {
        // Check mandatory parameters.
        if (dialect == null) {
            log.error("Parameter 'dialect' is mandatory");
            throw new BeanInitializationException("Parameter 'dialect' is mandatory");
        }

        if (ddlFile == null) {
            log.error("Parameter 'ddlFile' is mandatory");
            throw new BeanInitializationException("Parameter 'ddlFile' is mandatory");
        }

        // Mandatory parameters
        configProperties.put("hibernate.dialect", dialect);

        // Optional parameters
        putIfNotNull("hibernate.default_schema", defaultSchema);
        putIfNotNull("hibernate.id.new_generator_mappings", useNewGenerator);
        putIfNotNull("hibernate.ejb.naming_strategy", namingStrategy);

        // Fixed values
        configProperties.put("hibernate.hbm2ddl.auto", "create");
        configProperties.put("hibernate.use_sql_comments", false);
        configProperties.put("hibernate.format_sql", false);
        configProperties.put("hibernate.show_sq", false);

        log.info("DDLGenerator configuration properties: " + configProperties);
    }

    private void putIfNotNull(final String key, final Object value) {
        if (value != null) {
            configProperties.put(key, value);
        }
    }
}
