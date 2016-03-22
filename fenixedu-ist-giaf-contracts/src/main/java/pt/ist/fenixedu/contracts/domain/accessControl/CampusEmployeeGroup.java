/**
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST GIAF Contracts.
 *
 * FenixEdu IST GIAF Contracts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST GIAF Contracts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST GIAF Contracts.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.contracts.domain.accessControl;

import java.util.stream.Stream;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accessControl.FenixGroup;
import org.fenixedu.bennu.core.annotation.GroupArgument;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.spaces.domain.Space;
import org.joda.time.DateTime;

import pt.ist.fenixedu.contracts.domain.Employee;
import pt.ist.fenixedu.contracts.domain.personnelSection.contracts.GiafProfessionalData;
import pt.ist.fenixedu.contracts.domain.personnelSection.contracts.PersonProfessionalData;
import pt.ist.fenixedu.contracts.domain.util.CategoryType;

import com.google.common.base.Objects;

@GroupOperator("campusEmployee")
public class CampusEmployeeGroup extends FenixGroup {
    private static final long serialVersionUID = 4185082898828533195L;

    @GroupArgument
    private Space campus;

    private CampusEmployeeGroup() {
        super();
    }

    private CampusEmployeeGroup(Space campus) {
        this();
        this.campus = campus;
    }

    public static CampusEmployeeGroup get(Space campus) {
        return new CampusEmployeeGroup(campus);
    }

    @Override
    public String[] getPresentationNameKeyArgs() {
        return new String[] { campus.getName() };
    }

    @Override
    public Stream<User> getMembers() {
        return getMembers(new DateTime());
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return Employee.EMPLOYEE_GROUP.getMembers().filter(
                user -> user.getPerson() != null && isMember(user.getPerson(), campus, when));
    }

    @Override
    public boolean isMember(User user) {
        return isMember(user, new DateTime());
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return user != null && isMember(user.getPerson(), campus, when);
    }

    public boolean isMember(final Person person, final Space campus, DateTime when) {
        //when is ignored, professional data doesn't seem to have proper historic
        if (person != null) {
            PersonProfessionalData personProfessionalData = person.getPersonProfessionalData();
            if (personProfessionalData != null) {
                GiafProfessionalData giafProfessionalDataByCategoryType =
                        personProfessionalData.getGiafProfessionalDataByCategoryType(CategoryType.EMPLOYEE);
                if (giafProfessionalDataByCategoryType != null && giafProfessionalDataByCategoryType.getCampus() != null
                        && giafProfessionalDataByCategoryType.getCampus().equals(campus)
                        && !giafProfessionalDataByCategoryType.getContractSituation().getEndSituation()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public PersistentGroup toPersistentGroup() {
        return PersistentCampusEmployeeGroup.getInstance(campus);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof CampusEmployeeGroup) {
            return Objects.equal(campus, ((CampusEmployeeGroup) object).campus);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(campus);
    }
}
