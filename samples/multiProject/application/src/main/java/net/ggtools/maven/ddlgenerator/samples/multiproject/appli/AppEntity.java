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

package net.ggtools.maven.ddlgenerator.samples.multiproject.appli;

import lombok.Data;
import net.ggtools.maven.ddlgenerator.samples.multiproject.domain.DomainEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Christophe Labouisse
 * Date: 10/25/12
 * Time: 15:30
 */
@Entity
@Data
public class AppEntity {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private String domainName;

    @ElementCollection
    private List<DomainEntity> selection = new ArrayList<DomainEntity>();
}
