DDL Generator
=============

A Maven plugin to create DDL file from a JPA+Hibernate project.

Usage
-----

Just add the following configuration in `pom.xml`:

	<plugin>
		<groupId>net.ggtools.maven</groupId>
		<artifactId>ddlgenerator-maven-plugin</artifactId>
		<version>0.1</version>
		<executions>
			<execution>
				<id>make-raw-sql</id>
				<goals>
					<goal>generate</goal>
				</goals>
				<phase>prepare-package</phase>
				<configuration>
					<ddlFile>${project.build.directory}/sql/ddl.sql</ddlFile>
					<defaultSchema>MY_SCHEMA</defaultSchema>
					<dialect>org.hibernate.dialect.Oracle10gDialect</dialect>
					<namingStrategy>your.naming.Strategy</namingStrategy>
					<persistenceUnitName>MYPU</persistenceUnitName>
					<persistenceXmlLocations>
						<param>classpath*:/META-INF/persistence.xml</param>
					</persistenceXmlLocations>
					<useNewGenerator>true</useNewGenerator>
				</configuration>
			</execution>
		</executions>
	</plugin>

Until more documentation is available you should have a look to the `samples` directory.
