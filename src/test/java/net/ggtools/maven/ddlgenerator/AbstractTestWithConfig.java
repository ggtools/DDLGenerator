package net.ggtools.maven.ddlgenerator;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.dialect.Oracle10gDialect;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;

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
