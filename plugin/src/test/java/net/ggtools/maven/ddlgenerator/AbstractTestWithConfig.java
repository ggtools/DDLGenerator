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

import org.hibernate.dialect.Oracle10gDialect;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTestWithConfig {

    protected final Map<String, Object> referenceConfigProperties = new HashMap<String, Object>();

    protected final String dialect = Oracle10gDialect.class.getCanonicalName();

    @BeforeClass
    public final void baseSetUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        referenceConfigProperties.put("hibernate.hbm2ddl.auto", "create");
        referenceConfigProperties.put("hibernate.use_sql_comments", false);
        referenceConfigProperties.put("hibernate.format_sql", false);
        referenceConfigProperties.put("hibernate.show_sq", false);
        referenceConfigProperties.put("hibernate.dialect", dialect);
    }

}
