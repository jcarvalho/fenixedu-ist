/**
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST QUC.
 *
 * FenixEdu IST QUC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST QUC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST QUC.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.quc.domain;

import org.fenixedu.academic.domain.person.RoleType;
import org.fenixedu.bennu.core.groups.Group;

public class ExecutionCourseAuditFile extends ExecutionCourseAuditFile_Base {

    public ExecutionCourseAuditFile(ExecutionCourseAudit executionCourseAudit, String filename, byte[] file) {
        super();
        setExecutionCourseAudit(executionCourseAudit);
        super.init(filename, filename, file, getPermissionGroup());
    }

    private Group getPermissionGroup() {
        Group teacherGroup = getExecutionCourseAudit().getTeacherAuditor().getPerson().getUser().groupOf();
        Group studentGroup = getExecutionCourseAudit().getStudentAuditor().getPerson().getUser().groupOf();
        Group pedagogicalCouncil = RoleType.PEDAGOGICAL_COUNCIL.actualGroup();
        return teacherGroup.or(studentGroup).or(pedagogicalCouncil);
    }

    @Override
    public void delete() {
        setExecutionCourseAudit(null);
        super.delete();
    }

}
