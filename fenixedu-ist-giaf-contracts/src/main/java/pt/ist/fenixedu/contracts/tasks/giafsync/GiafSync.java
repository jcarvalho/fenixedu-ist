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
package pt.ist.fenixedu.contracts.tasks.giafsync;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Task(englishTitle = "Giaf Sync", readOnly = true)
public class GiafSync extends CronTask {
    private static final Logger logger = LoggerFactory.getLogger(GiafSync.class);

    public static interface Modification {
        public void execute();
    }

    public static abstract class ImportProcessor {
        public void process(GiafMetadata metadata, PrintWriter log, Logger logger) throws Exception {
            internalProcess(processChanges(metadata, log, logger));
        }

        @Atomic(mode = TxMode.WRITE)
        private void internalProcess(List<Modification> modifications) {
            for (Modification modification : modifications) {
                modification.execute();
            }
        }

        public abstract List<Modification> processChanges(GiafMetadata metadata, PrintWriter log, Logger logger) throws Exception;
    }

    public static interface MetadataProcessor {
        public void processChanges(GiafMetadata metadata, PrintWriter log, Logger logger) throws Exception;
    }

    @Override
    public void runTask() throws Exception {
        GiafMetadata metadata = new GiafMetadata();
        updateMetadata(metadata);

        StringWriter writer = new StringWriter();
        PrintWriter logWriter = new PrintWriter(writer);
        new ImportPersonProfessionalData().process(metadata, logWriter, logger);
        new ImportPersonContractSituationsFromGiaf().process(metadata, logWriter, logger);
        new ImportPersonProfessionalCategoriesFromGiaf().process(metadata, logWriter, logger);
        new ImportPersonProfessionalContractsFromGiaf().process(metadata, logWriter, logger);
        new ImportPersonProfessionalRegimesFromGiaf().process(metadata, logWriter, logger);
        new ImportPersonProfessionalRelationsFromGiaf().process(metadata, logWriter, logger);
        new ImportPersonFunctionsAccumulationsFromGiaf().process(metadata, logWriter, logger);
        new ImportPersonSabbaticalsFromGiaf().process(metadata, logWriter, logger);
        new ImportPersonServiceExemptionsFromGiaf().process(metadata, logWriter, logger);
        new ImportPersonGrantOwnerEquivalentFromGiaf().process(metadata, logWriter, logger);
        new ImportPersonAbsencesFromGiaf().process(metadata, logWriter, logger);
        new ImportEmployeeUnitsFromGiaf().process(metadata, logWriter, logger);
        taskLog(writer.toString());
    }

    @Atomic(mode = TxMode.WRITE)
    private void updateMetadata(GiafMetadata metadata) throws Exception {
        StringWriter writer = new StringWriter();
        PrintWriter logWriter = new PrintWriter(writer);
        new ImportTypesFromGiaf().processChanges(metadata, logWriter, logger);
        new ImportContractSituationsFromGiaf().processChanges(metadata, logWriter, logger);
        new ImportProfessionalCategoryFromGiaf().processChanges(metadata, logWriter, logger);
        new ImportProfessionalRegimesFromGiaf().processChanges(metadata, logWriter, logger);
        new ImportProfessionalRelationsFromGiaf().processChanges(metadata, logWriter, logger);
        taskLog(writer.toString());
    }
}
