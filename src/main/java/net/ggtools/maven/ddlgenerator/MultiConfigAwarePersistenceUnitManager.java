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

import java.net.URL;

import javax.persistence.spi.PersistenceUnitInfo;

import lombok.extern.slf4j.Slf4j;

import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

/**
 * An extension to the {@link DefaultPersistenceUnitManager} that is able to
 * merge multiple <tt>persistence.xml</tt> associated to the same persistence
 * unit name.
 * <p />
 * If a module persistence unit defines managed classes explicitly, only adds
 * the specified classes. If the module persistence unit does not define any
 * managed classes, module scanning is assumed: any entity classes defined in
 * the module holding the persistence unit will be associated to the main one.
 * 
 * This class has been copied from http://bitlingo.com/tag/jpa/. I guess this is
 * the best version of this class among the ones I found on internet.
 */
@Slf4j
public class MultiConfigAwarePersistenceUnitManager extends DefaultPersistenceUnitManager {

	/** The strict. */
	private transient boolean strict = false;

	/** The persistence unit name. */
	private transient String persistenceUnitName = "default";

	/**
	 * Copies a persistence unit to another one. Takes care of copying both
	 * managed classes and urls.
	 * 
	 * @param from
	 *            the persistence unit to copy
	 * @param to
	 *            the target (copied) persistence unit
	 */
	protected void copyPersistenceUnit(MutablePersistenceUnitInfo from, MutablePersistenceUnitInfo to) {
		for (String s : from.getMappingFileNames()) {
			to.addMappingFileName(s);
		}
		for (String s : from.getManagedClassNames()) {
			to.addManagedClassName(s);
		}
		// Copy jar file urls
		for (URL url : from.getJarFileUrls()) {
			to.addJarFileUrl(url);
		}
	}

	/**
	 * Returns the main {@link MutablePersistenceUnitInfo} to use, based on the
	 * given {@link MutablePersistenceUnitInfo} and the settings of the manager.
	 * <p />
	 * In strict mode, returns the declared persistence unit with the specified
	 * name. In non strict mode and if the specified <tt>pui</tt> starts with
	 * the configured <tt>persistenceUnitName</tt>, returns the template
	 * persistence unit info used for the merging.
	 * 
	 * @param pui
	 *            the persistence unit info to handle
	 * @return the persistence unit info to use for the merging
	 */
	private MutablePersistenceUnitInfo getMainPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
		MutablePersistenceUnitInfo result = null;
		if (strict) {
			return getPersistenceUnitInfo(pui.getPersistenceUnitName());
		}
		if (pui.getPersistenceUnitName().startsWith(persistenceUnitName)) {
			// We have a match, retrieve our persistence unit name then
			result = getPersistenceUnitInfo(persistenceUnitName);
			// Sanity check
			if (result == null) {
				throw new IllegalStateException(
						"No persistence unit found with name ["
								+ persistenceUnitName
								+ "] "
								+ "so no merging is possible. It usually means that the bootstrap-persistence.xml has not been "
								+ "included in the list of persistence.xml location(s). Check your configuration as it "
								+ "should be the first in the list!");
			}
		}
		// Nothing has been found
		return result;
	}

	/**
	 * Specifies whether the specified persistence unit is the template one we
	 * use to merge.
	 * 
	 * @param pui
	 *            the persistence unit to test
	 * @return <tt>true</tt> if the persistence unit is the target, template
	 *         persistence unit
	 */
	private boolean isApplicationPersistenceUnit(MutablePersistenceUnitInfo pui) {
		return (!strict && persistenceUnitName.equals(pui.getPersistenceUnitName()));
	}

	/**
	 * Merges a persistence unit to another one. Takes care of handling both
	 * managed classes and urls. If the persistence unit has managed classes,
	 * only merge these and prevents scanning. If no managed classes are
	 * available, add the url of the module for entity scanning.
	 * 
	 * @param from
	 *            the persistence unit to handle
	 * @param to
	 *            the target (merged) persistence unit
	 */
	protected void mergePersistenceUnit(MutablePersistenceUnitInfo from, MutablePersistenceUnitInfo to) {
		if (from.getMappingFileNames().size() != 0) {
			for (String s : from.getMappingFileNames()) {
				if (log.isDebugEnabled()) {
					log.debug("Adding entity [" + s + "]");
				}
				to.addMappingFileName(s);
			}
			if (log.isDebugEnabled()) {
				log.debug("Added [" + from.getMappingFileNames().size() + "] mapping file to " + "persistence unit["
						+ to.getPersistenceUnitName() + "]");
			}
		} else if (from.getManagedClassNames().size() != 0) {
			for (String s : from.getManagedClassNames()) {
				if (log.isDebugEnabled()) {
					log.debug("Adding entity [" + s + "]");
				}
				to.addManagedClassName(s);
			}
			if (log.isDebugEnabled()) {
				log.debug("Added [" + from.getManagedClassNames().size() + "] managed classes to "
						+ "persistence unit[" + to.getPersistenceUnitName() + "]");
			}
		} else {
			to.addJarFileUrl(from.getPersistenceUnitRootUrl());
			if (log.isDebugEnabled()) {
				log.debug("Added [" + from.getPersistenceUnitRootUrl() + "] for entity scanning "
						+ "to persistence unit[" + to.getPersistenceUnitName() + "]");
			}
		}
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public PersistenceUnitInfo obtainPersistenceUnitInfo(String puName) {
		PersistenceUnitInfo persistenceUnitInfo = super.obtainPersistenceUnitInfo(puName);
		persistenceUnitInfo.getJarFileUrls().remove(persistenceUnitInfo.getPersistenceUnitRootUrl());
		return persistenceUnitInfo;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	protected void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
		super.postProcessPersistenceUnitInfo(pui);

		// This our template persistence unit that we are post-processing
		// so let's just skip.
		if (isApplicationPersistenceUnit(pui)) {
			return;
		}

		final MutablePersistenceUnitInfo mainPui = getMainPersistenceUnitInfo(pui);

		if (strict) {
			pui.addJarFileUrl(pui.getPersistenceUnitRootUrl());
		}

		if (mainPui != null) {
			if (strict) {
				if (log.isDebugEnabled()) {
					log.debug("Merging existing jar file urls " + mainPui.getJarFileUrls() + " to persistence unit ["
							+ pui.getPersistenceUnitName() + "]");
				}
				copyPersistenceUnit(mainPui, pui);
				if (log.isDebugEnabled()) {
					log.debug("Persistence unit[" + pui.getPersistenceUnitName() + "] has now "
							+ "the following jar file urls " + pui.getJarFileUrls() + "");
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Merging information from [" + pui.getPersistenceUnitName() + "] " + "to ["
							+ mainPui.getPersistenceUnitName() + "]");
				}
				mergePersistenceUnit(pui, mainPui);
				if (log.isDebugEnabled()) {
					log.debug("Persistence unit[" + mainPui.getPersistenceUnitName() + "] has now "
							+ "the following jar file urls " + mainPui.getJarFileUrls());
				}
			}
		} else if (!pui.getPersistenceUnitName().equals(persistenceUnitName)) {
			log.debug("Adding persistence unit [" + pui.getPersistenceUnitName() + "] as is (no merging)");
		}
	}

	/**
	 * Sets the name of the persistence unit that should be used. If no such
	 * persistence unit exists, an exception will be thrown, preventing the
	 * factory to be created.
	 * <p />
	 * When the <tt>strict</tt> mode is disabled, this name is used to find all
	 * matching persistence units based on a prefix. Say for instance that the
	 * <tt>persistenceUnitName</tt> to use is <tt>pu</tt>, the following
	 * applies: pu-base will be merged pufoo will be merged base-pu will
	 * <b>not</b> be merged Make sure to configure your entity manager factory
	 * to use this name as the persistence unit
	 * 
	 * @param persistenceUnitName
	 *            the name of the persistence unit to use
	 */
	public void setPersistenceUnitName(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
	}

	/**
	 * Specifies if the manager should process the persistence units in strict
	 * mode. When enabled, only the persistence unit that have the exact same
	 * names as an existing one are merged. When disabled (the default), if the
	 * name of the persistence unit matches the prefix, it is merged with the
	 * persistence unit defined by the prefix.
	 * 
	 * @param strict
	 *            if merging occurs on an exact match or on the prefix only
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

}
