/**
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST Integration.
 *
 * FenixEdu IST Integration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST Integration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST Integration.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.integration.service.services.manager;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.bennu.core.groups.Group;

import pt.ist.fenixedu.integration.domain.UnitFile;
import pt.ist.fenixframework.Atomic;

public class CreateUnitFile {

    @Atomic
    public static void run(byte[] content, String originalFilename, String displayName, String description, String tags,
            Group permittedGroup, Unit unit, Person person) throws FenixServiceException {
        new UnitFile(unit, person, description, tags, originalFilename, displayName, content,
                !isPublic(permittedGroup) ? permittedGroup.or(person.getUser().groupOf()) : permittedGroup);
    }

    private static boolean isPublic(Group permittedGroup) {
        if (permittedGroup == null) {
            return true;
        }

        if (permittedGroup.isMember(null)) {
            return true;
        }

        return false;
    }
}