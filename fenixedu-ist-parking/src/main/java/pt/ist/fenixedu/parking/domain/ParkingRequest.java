/**
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST Parking.
 *
 * FenixEdu IST Parking is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST Parking is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST Parking.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.parking.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.contacts.EmailAddress;
import org.fenixedu.academic.domain.contacts.MobilePhone;
import org.fenixedu.academic.domain.contacts.Phone;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.academic.service.services.commons.FactoryExecutor;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.commons.i18n.I18N;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.io.ByteStreams;

public class ParkingRequest extends ParkingRequest_Base {
    public ParkingRequest(ParkingRequestFactoryCreator creator) {
        super();
        setRootDomainObject(Bennu.getInstance());
        setParkingRequestState(ParkingRequestState.PENDING);
        setDriverLicenseDeliveryType(creator.getDriverLicenseDeliveryType());
        setParkingParty(creator.getParkingParty());
        setCreationDate(new DateTime());

        Phone defaultPhone = ((Person) creator.getParkingParty().getParty()).getDefaultPhone();
        if (defaultPhone != null) {
            setPhone(defaultPhone.getNumber());
        }
        MobilePhone defaultMobilePhone = ((Person) creator.getParkingParty().getParty()).getDefaultMobilePhone();
        if (defaultMobilePhone != null) {
            setMobile(defaultMobilePhone.getNumber());
        }
        EmailAddress defaultEmailAddress = ((Person) creator.getParkingParty().getParty()).getDefaultEmailAddress();
        if (defaultEmailAddress != null) {
            setEmail(defaultEmailAddress.getValue());
        }

        setRequestedAs(creator.getRequestAs() != null ? creator.getRequestAs() : creator.getParkingParty().getSubmitAsRoles()
                .iterator().next());
        boolean limitlessAccessCard = creator.isLimitlessAccessCard();
        PartyClassification classification = PartyClassification.getPartyClassification(creator.getParkingParty().getParty());
        if (limitlessAccessCard == false
                && (classification.equals(PartyClassification.TEACHER) || classification.equals(PartyClassification.EMPLOYEE))) {
            limitlessAccessCard = true;
        }
        setLimitlessAccessCard(limitlessAccessCard);
        setParkingRequestType(ParkingRequestType.FIRST_TIME);
    }

    public ParkingRequest(ParkingRequest oldParkingRequest, Boolean limitlessAccessCard) {
        super();
        setRootDomainObject(Bennu.getInstance());
        setParkingRequestState(ParkingRequestState.PENDING);
        setDriverLicenseDeliveryType(oldParkingRequest.getDriverLicenseDeliveryType());
        setParkingParty(oldParkingRequest.getParkingParty());
        setCreationDate(new DateTime());
        Phone defaultPhone = ((Person) oldParkingRequest.getParkingParty().getParty()).getDefaultPhone();
        if (defaultPhone != null) {
            setPhone(defaultPhone.getNumber());
        }
        MobilePhone defaultMobilePhone = ((Person) oldParkingRequest.getParkingParty().getParty()).getDefaultMobilePhone();
        if (defaultMobilePhone != null) {
            setMobile(defaultMobilePhone.getNumber());
        }
        EmailAddress defaultEmailAddress = ((Person) oldParkingRequest.getParkingParty().getParty()).getDefaultEmailAddress();
        if (defaultEmailAddress != null) {
            setEmail(defaultEmailAddress.getValue());
        }
        String requestedAs = oldParkingRequest.getRequestedAs();
        if (requestedAs == null) {
            requestedAs = oldParkingRequest.getParkingParty().getRoleToRequestUnlimitedCard();
        }
        setRequestedAs(requestedAs);
        setLimitlessAccessCard(limitlessAccessCard);
        setParkingRequestType(ParkingRequestType.RENEW);
    }

    public ParkingRequestFactoryEditor getParkingRequestFactoryEditor() {
        return new ParkingRequestFactoryEditor(this);
    }

    public static abstract class ParkingRequestFactory implements Serializable, FactoryExecutor {
        private ParkingParty parkingParty;

        private String firstVechicleID;

        private String secondVechicleID;

        private String firstCarPlateNumber;

        private String firstCarMake;

        private String secondCarPlateNumber;

        private String secondCarMake;

        private String driverLicenseFileName;

        private transient InputStream driverLicenseInputStream;

        private byte[] driverLicenseByteArray;

        private String firstCarPropertyRegistryFileName;

        private transient InputStream firstCarPropertyRegistryInputStream;

        private byte[] firstCarPropertyRegistryByteArray;

        private String firstCarOwnerIdFileName;

        private transient InputStream firstCarOwnerIdInputStream;

        private byte[] firstCarOwnerIdByteArray;

        private String firstDeclarationAuthorizationFileName;

        private transient InputStream firstDeclarationAuthorizationInputStream;

        private byte[] firstDeclarationAuthorizationByteArray;

        private String firstInsuranceFileName;

        private transient InputStream firstInsuranceInputStream;

        private byte[] firstInsuranceByteArray;

        private String secondCarPropertyRegistryFileName;

        private transient InputStream secondCarPropertyRegistryInputStream;

        private byte[] secondCarPropertyRegistryByteArray;

        private String secondCarOwnerIdFileName;

        private transient InputStream secondCarOwnerIdInputStream;

        private byte[] secondCarOwnerIdByteArray;

        private String secondDeclarationAuthorizationFileName;

        private transient InputStream secondDeclarationAuthorizationInputStream;

        private byte[] secondDeclarationAuthorizationByteArray;

        private String secondInsuranceFileName;

        private transient InputStream secondInsuranceInputStream;

        private byte[] secondInsuranceByteArray;

        private long driverLicenseFileSize;

        private long firstCarPropertyRegistryFileSize;

        private long firstInsuranceFileSize;

        private long firstCarOwnerIdFileSize;

        private long firstDeclarationAuthorizationFileSize;

        private long secondCarPropertyRegistryFileSize;

        private long secondInsuranceFileSize;

        private long secondCarOwnerIdFileSize;

        private long secondDeclarationAuthorizationFileSize;

        DocumentDeliveryType driverLicenseDeliveryType;

        DocumentDeliveryType firstCarPropertyRegistryDeliveryType;

        DocumentDeliveryType firstCarInsuranceDeliveryType;

        DocumentDeliveryType firstCarOwnerIdDeliveryType;

        DocumentDeliveryType firstCarDeclarationDeliveryType;

        DocumentDeliveryType secondCarPropertyRegistryDeliveryType;

        DocumentDeliveryType secondCarInsuranceDeliveryType;

        DocumentDeliveryType secondCarOwnerIdDeliveryType;

        DocumentDeliveryType secondCarDeclarationDeliveryType;

        String requestAs;

        boolean limitlessAccessCard;

        public ParkingRequestFactory(ParkingParty parkingParty) {
            super();
            setParkingParty(parkingParty);
        }

        public void saveInputStreams() throws IOException {
            driverLicenseByteArray = getByteArray(driverLicenseInputStream, driverLicenseByteArray);
            firstCarPropertyRegistryByteArray =
                    getByteArray(firstCarPropertyRegistryInputStream, firstCarPropertyRegistryByteArray);
            firstCarOwnerIdByteArray = getByteArray(firstCarOwnerIdInputStream, firstCarOwnerIdByteArray);
            firstDeclarationAuthorizationByteArray =
                    getByteArray(firstDeclarationAuthorizationInputStream, firstDeclarationAuthorizationByteArray);
            firstInsuranceByteArray = getByteArray(firstInsuranceInputStream, firstInsuranceByteArray);
            secondCarPropertyRegistryByteArray =
                    getByteArray(secondCarPropertyRegistryInputStream, secondCarPropertyRegistryByteArray);
            secondCarOwnerIdByteArray = getByteArray(secondCarOwnerIdInputStream, secondCarOwnerIdByteArray);
            secondDeclarationAuthorizationByteArray =
                    getByteArray(secondDeclarationAuthorizationInputStream, secondDeclarationAuthorizationByteArray);
            secondInsuranceByteArray = getByteArray(secondInsuranceInputStream, secondInsuranceByteArray);
        }

        private byte[] getByteArray(final InputStream inputStream, byte[] b) throws IOException {
            if (inputStream == null) {
                return b;
            }
            return ByteStreams.toByteArray(inputStream);
        }

        public String getDriverLicenseFileName() {
            return driverLicenseFileName;
        }

        public void setDriverLicenseFileName(String driverLicenseFileName) {
            this.driverLicenseFileName = driverLicenseFileName;
        }

        public InputStream getDriverLicenseInputStream() {
            return driverLicenseByteArray == null ? null : new ByteArrayInputStream(driverLicenseByteArray);
        }

        public void setDriverLicenseInputStream(InputStream driverLicenseInputStream) {
            this.driverLicenseInputStream = driverLicenseInputStream;
        }

        public String getFirstCarMake() {
            return firstCarMake;
        }

        public void setFirstCarMake(String firstCarMake) {
            this.firstCarMake = firstCarMake;
        }

        public String getFirstCarOwnerIdFileName() {
            return firstCarOwnerIdFileName;
        }

        public void setFirstCarOwnerIdFileName(String firstCarOwnerIdFileName) {
            this.firstCarOwnerIdFileName = firstCarOwnerIdFileName;
        }

        public InputStream getFirstCarOwnerIdInputStream() {
            return firstCarOwnerIdByteArray == null ? null : new ByteArrayInputStream(firstCarOwnerIdByteArray);
        }

        public void setFirstCarOwnerIdInputStream(InputStream firstCarOwnerIdInputStream) {
            this.firstCarOwnerIdInputStream = firstCarOwnerIdInputStream;
        }

        public String getFirstCarPlateNumber() {
            return firstCarPlateNumber;
        }

        public void setFirstCarPlateNumber(String firstCarPlateNumber) {
            this.firstCarPlateNumber = firstCarPlateNumber;
        }

        public String getFirstCarPropertyRegistryFileName() {
            return firstCarPropertyRegistryFileName;
        }

        public void setFirstCarPropertyRegistryFileName(String firstCarPropertyRegistryFileName) {
            this.firstCarPropertyRegistryFileName = firstCarPropertyRegistryFileName;
        }

        public InputStream getFirstCarPropertyRegistryInputStream() {
            return firstCarPropertyRegistryByteArray == null ? null : new ByteArrayInputStream(firstCarPropertyRegistryByteArray);
        }

        public void setFirstCarPropertyRegistryInputStream(InputStream firstCarPropertyRegistryInputStream) {
            this.firstCarPropertyRegistryInputStream = firstCarPropertyRegistryInputStream;
        }

        public ParkingParty getParkingParty() {
            return parkingParty;
        }

        public void setParkingParty(ParkingParty parkingParty) {
            if (parkingParty != null) {
                this.parkingParty = parkingParty;
            }
        }

        public String getSecondCarMake() {
            return secondCarMake;
        }

        public void setSecondCarMake(String secondCarMake) {
            this.secondCarMake = secondCarMake;
        }

        public String getSecondCarOwnerIdFileName() {
            return secondCarOwnerIdFileName;
        }

        public void setSecondCarOwnerIdFileName(String secondCarOwnerIdFileName) {
            this.secondCarOwnerIdFileName = secondCarOwnerIdFileName;
        }

        public InputStream getSecondCarOwnerIdInputStream() {
            return secondCarOwnerIdByteArray == null ? null : new ByteArrayInputStream(secondCarOwnerIdByteArray);
        }

        public void setSecondCarOwnerIdInputStream(InputStream secondCarOwnerIdInputStream) {
            this.secondCarOwnerIdInputStream = secondCarOwnerIdInputStream;
        }

        public String getSecondCarPlateNumber() {
            return secondCarPlateNumber;
        }

        public void setSecondCarPlateNumber(String secondCarPlateNumber) {
            this.secondCarPlateNumber = secondCarPlateNumber;
        }

        public String getSecondCarPropertyRegistryFileName() {
            return secondCarPropertyRegistryFileName;
        }

        public void setSecondCarPropertyRegistryFileName(String secondCarPropertyRegistryFileName) {
            this.secondCarPropertyRegistryFileName = secondCarPropertyRegistryFileName;
        }

        public InputStream getSecondCarPropertyRegistryInputStream() {
            return secondCarPropertyRegistryByteArray == null ? null : new ByteArrayInputStream(
                    secondCarPropertyRegistryByteArray);
        }

        public void setSecondCarPropertyRegistryInputStream(InputStream secondCarPropertyRegistryInputStream) {
            this.secondCarPropertyRegistryInputStream = secondCarPropertyRegistryInputStream;
        }

        public String getFirstDeclarationAuthorizationFileName() {
            return firstDeclarationAuthorizationFileName;
        }

        public void setFirstDeclarationAuthorizationFileName(String firstDeclarationAuthorizationFileName) {
            this.firstDeclarationAuthorizationFileName = firstDeclarationAuthorizationFileName;
        }

        public InputStream getFirstDeclarationAuthorizationInputStream() {
            return firstDeclarationAuthorizationByteArray == null ? null : new ByteArrayInputStream(
                    firstDeclarationAuthorizationByteArray);
        }

        public void setFirstDeclarationAuthorizationInputStream(InputStream firstDeclarationAuthorizationInputStream) {
            this.firstDeclarationAuthorizationInputStream = firstDeclarationAuthorizationInputStream;
        }

        public String getSecondDeclarationAuthorizationFileName() {
            return secondDeclarationAuthorizationFileName;
        }

        public void setSecondDeclarationAuthorizationFileName(String secondDeclarationAuthorizationFileName) {
            this.secondDeclarationAuthorizationFileName = secondDeclarationAuthorizationFileName;
        }

        public InputStream getSecondDeclarationAuthorizationInputStream() {
            return secondDeclarationAuthorizationByteArray == null ? null : new ByteArrayInputStream(
                    secondDeclarationAuthorizationByteArray);
        }

        public void setSecondDeclarationAuthorizationInputStream(InputStream secondDeclarationAuthorizationInputStream) {
            this.secondDeclarationAuthorizationInputStream = secondDeclarationAuthorizationInputStream;
        }

        public String getFirstInsuranceFileName() {
            return firstInsuranceFileName;
        }

        public void setFirstInsuranceFileName(String firstInsuranceFileName) {
            this.firstInsuranceFileName = firstInsuranceFileName;
        }

        public InputStream getFirstInsuranceInputStream() {
            return firstInsuranceByteArray == null ? null : new ByteArrayInputStream(firstInsuranceByteArray);
        }

        public void setFirstInsuranceInputStream(InputStream firstInsuranceInputStream) {
            this.firstInsuranceInputStream = firstInsuranceInputStream;
        }

        public String getSecondInsuranceFileName() {
            return secondInsuranceFileName;
        }

        public void setSecondInsuranceFileName(String secondInsuranceFileName) {
            this.secondInsuranceFileName = secondInsuranceFileName;
        }

        public InputStream getSecondInsuranceInputStream() {
            return secondInsuranceByteArray == null ? null : new ByteArrayInputStream(secondInsuranceByteArray);
        }

        public void setSecondInsuranceInputStream(InputStream secondInsuranceInputStream) {
            this.secondInsuranceInputStream = secondInsuranceInputStream;
        }

        protected void writeFirstVehicleDocuments(Vehicle vehicle) throws IOException {
            writeVehicleFile(vehicle, getFirstCarOwnerIdFileName(), NewParkingDocumentType.VEHICLE_OWNER_ID,
                    getFirstCarOwnerIdInputStream(), getFirstCarOwnerIdDeliveryType());
            writeVehicleFile(vehicle, getFirstCarPropertyRegistryFileName(), NewParkingDocumentType.VEHICLE_PROPERTY_REGISTER,
                    getFirstCarPropertyRegistryInputStream(), getFirstCarPropertyRegistryDeliveryType());
            writeVehicleFile(vehicle, getFirstDeclarationAuthorizationFileName(),
                    NewParkingDocumentType.DECLARATION_OF_AUTHORIZATION, getFirstDeclarationAuthorizationInputStream(),
                    getFirstCarDeclarationDeliveryType());
            writeVehicleFile(vehicle, getFirstInsuranceFileName(), NewParkingDocumentType.VEHICLE_INSURANCE,
                    getFirstInsuranceInputStream(), getFirstCarInsuranceDeliveryType());
        }

        protected void writeSecondVehicleDocuments(Vehicle vehicle) throws IOException {
            writeVehicleFile(vehicle, getSecondCarOwnerIdFileName(), NewParkingDocumentType.VEHICLE_OWNER_ID,
                    getSecondCarOwnerIdInputStream(), getSecondCarOwnerIdDeliveryType());
            writeVehicleFile(vehicle, getSecondCarPropertyRegistryFileName(), NewParkingDocumentType.VEHICLE_PROPERTY_REGISTER,
                    getSecondCarPropertyRegistryInputStream(), getSecondCarPropertyRegistryDeliveryType());
            writeVehicleFile(vehicle, getSecondDeclarationAuthorizationFileName(),
                    NewParkingDocumentType.DECLARATION_OF_AUTHORIZATION, getSecondDeclarationAuthorizationInputStream(),
                    getSecondCarDeclarationDeliveryType());
            writeVehicleFile(vehicle, getSecondInsuranceFileName(), NewParkingDocumentType.VEHICLE_INSURANCE,
                    getSecondInsuranceInputStream(), getSecondCarInsuranceDeliveryType());
        }

        private void writeVehicleFile(Vehicle vehicle, String filename, NewParkingDocumentType parkingDocumentType,
                InputStream inputStream, DocumentDeliveryType documentDeliveryType) throws IOException {
            NewParkingDocument parkingDocument = vehicle.getParkingDocument(parkingDocumentType);
            if (parkingDocument != null
                    && (inputStream != null || documentDeliveryType != DocumentDeliveryType.ELECTRONIC_DELIVERY)) {
                parkingDocument.delete();
            }
            if (documentDeliveryType == DocumentDeliveryType.ELECTRONIC_DELIVERY && inputStream != null) {
                final Group group = getGroup(vehicle.getParkingRequest().getParkingParty().getParty());
                final ParkingFile parkingFile = new ParkingFile(filename, filename, ByteStreams.toByteArray(inputStream), group);
                new NewParkingDocument(parkingDocumentType, parkingFile, vehicle);
            }
        }

        protected void writeDriverLicenseFile(ParkingRequest parkingRequest) throws IOException {
            NewParkingDocument parkingDocument = parkingRequest.getDriverLicenseDocument();
            DocumentDeliveryType documentDeliveryType = getDriverLicenseDeliveryType();
            String filename = getDriverLicenseFileName();

            if (parkingDocument != null
                    && (getDriverLicenseInputStream() != null || documentDeliveryType != DocumentDeliveryType.ELECTRONIC_DELIVERY)) {
                parkingDocument.delete();
            }
            if (documentDeliveryType == DocumentDeliveryType.ELECTRONIC_DELIVERY && getDriverLicenseInputStream() != null) {
                final Group group = getGroup(getParkingParty().getParty());
                final ParkingFile parkingFile =
                        new ParkingFile(filename, filename, ByteStreams.toByteArray(getDriverLicenseInputStream()), group);
                new NewParkingDocument(NewParkingDocumentType.DRIVER_LICENSE, parkingFile, parkingRequest);
            }
        }

        private Group getGroup(Party party) {
            final Group personGroup = ((Person) party).getUser().groupOf();
            final Group roleGroup = DynamicGroup.get("parkingManager");
            return personGroup.or(roleGroup);
        }

        public long getDriverLicenseFileSize() {
            return driverLicenseFileSize;
        }

        public void setDriverLicenseFileSize(long driverLicenseFileSize) {
            this.driverLicenseFileSize = driverLicenseFileSize;
        }

        public long getFirstCarOwnerIdFileSize() {
            return firstCarOwnerIdFileSize;
        }

        public void setFirstCarOwnerIdFileSize(long firstCarOwnerIdFileSize) {
            this.firstCarOwnerIdFileSize = firstCarOwnerIdFileSize;
        }

        public long getFirstCarPropertyRegistryFileSize() {
            return firstCarPropertyRegistryFileSize;
        }

        public void setFirstCarPropertyRegistryFileSize(long firstCarPropertyRegistryFileSize) {
            this.firstCarPropertyRegistryFileSize = firstCarPropertyRegistryFileSize;
        }

        public long getFirstDeclarationAuthorizationFileSize() {
            return firstDeclarationAuthorizationFileSize;
        }

        public void setFirstDeclarationAuthorizationFileSize(long firstDeclarationAuthorizationFileSize) {
            this.firstDeclarationAuthorizationFileSize = firstDeclarationAuthorizationFileSize;
        }

        public long getFirstInsuranceFileSize() {
            return firstInsuranceFileSize;
        }

        public void setFirstInsuranceFileSize(long firstInsuranceFileSize) {
            this.firstInsuranceFileSize = firstInsuranceFileSize;
        }

        public long getSecondCarOwnerIdFileSize() {
            return secondCarOwnerIdFileSize;
        }

        public void setSecondCarOwnerIdFileSize(long secondCarOwnerIdFileSize) {
            this.secondCarOwnerIdFileSize = secondCarOwnerIdFileSize;
        }

        public long getSecondCarPropertyRegistryFileSize() {
            return secondCarPropertyRegistryFileSize;
        }

        public void setSecondCarPropertyRegistryFileSize(long secondCarPropertyRegistryFileSize) {
            this.secondCarPropertyRegistryFileSize = secondCarPropertyRegistryFileSize;
        }

        public long getSecondDeclarationAuthorizationFileSize() {
            return secondDeclarationAuthorizationFileSize;
        }

        public void setSecondDeclarationAuthorizationFileSize(long secondDeclarationAuthorizationFileSize) {
            this.secondDeclarationAuthorizationFileSize = secondDeclarationAuthorizationFileSize;
        }

        public long getSecondInsuranceFileSize() {
            return secondInsuranceFileSize;
        }

        public void setSecondInsuranceFileSize(long secondInsuranceFileSize) {
            this.secondInsuranceFileSize = secondInsuranceFileSize;
        }

        public String getRequestAs() {
            return requestAs;
        }

        public void setRequestAs(String requestAs) {
            this.requestAs = requestAs;
        }

        public boolean isLimitlessAccessCard() {
            return limitlessAccessCard;
        }

        public void setLimitlessAccessCard(boolean limitlessAccessCard) {
            this.limitlessAccessCard = limitlessAccessCard;
        }

        public String getFirstVechicleID() {
            return firstVechicleID;
        }

        public void setFirstVechicleID(String firstVechicleID) {
            this.firstVechicleID = firstVechicleID;
        }

        public String getSecondVechicleID() {
            return secondVechicleID;
        }

        public void setSecondVechicleID(String secondVechicleID) {
            this.secondVechicleID = secondVechicleID;
        }

        public DocumentDeliveryType getFirstCarDeclarationDeliveryType() {
            return firstCarDeclarationDeliveryType;
        }

        public void setFirstCarDeclarationDeliveryType(DocumentDeliveryType firstCarDeclarationDeliveryType) {
            this.firstCarDeclarationDeliveryType = firstCarDeclarationDeliveryType;
        }

        public DocumentDeliveryType getFirstCarInsuranceDeliveryType() {
            return firstCarInsuranceDeliveryType;
        }

        public void setFirstCarInsuranceDeliveryType(DocumentDeliveryType firstCarInsuranceDeliveryType) {
            this.firstCarInsuranceDeliveryType = firstCarInsuranceDeliveryType;
        }

        public DocumentDeliveryType getFirstCarOwnerIdDeliveryType() {
            return firstCarOwnerIdDeliveryType;
        }

        public void setFirstCarOwnerIdDeliveryType(DocumentDeliveryType firstCarOwnerIdDeliveryType) {
            this.firstCarOwnerIdDeliveryType = firstCarOwnerIdDeliveryType;
        }

        public DocumentDeliveryType getFirstCarPropertyRegistryDeliveryType() {
            return firstCarPropertyRegistryDeliveryType;
        }

        public void setFirstCarPropertyRegistryDeliveryType(DocumentDeliveryType firstCarPropertyRegistryDeliveryType) {
            this.firstCarPropertyRegistryDeliveryType = firstCarPropertyRegistryDeliveryType;
        }

        public DocumentDeliveryType getSecondCarDeclarationDeliveryType() {
            return secondCarDeclarationDeliveryType;
        }

        public void setSecondCarDeclarationDeliveryType(DocumentDeliveryType secondCarDeclarationDeliveryType) {
            this.secondCarDeclarationDeliveryType = secondCarDeclarationDeliveryType;
        }

        public DocumentDeliveryType getSecondCarInsuranceDeliveryType() {
            return secondCarInsuranceDeliveryType;
        }

        public void setSecondCarInsuranceDeliveryType(DocumentDeliveryType secondCarInsuranceDeliveryType) {
            this.secondCarInsuranceDeliveryType = secondCarInsuranceDeliveryType;
        }

        public DocumentDeliveryType getSecondCarOwnerIdDeliveryType() {
            return secondCarOwnerIdDeliveryType;
        }

        public void setSecondCarOwnerIdDeliveryType(DocumentDeliveryType secondCarOwnerIdDeliveryType) {
            this.secondCarOwnerIdDeliveryType = secondCarOwnerIdDeliveryType;
        }

        public DocumentDeliveryType getSecondCarPropertyRegistryDeliveryType() {
            return secondCarPropertyRegistryDeliveryType;
        }

        public void setSecondCarPropertyRegistryDeliveryType(DocumentDeliveryType secondCarPropertyRegistryDeliveryType) {
            this.secondCarPropertyRegistryDeliveryType = secondCarPropertyRegistryDeliveryType;
        }

        public DocumentDeliveryType getDriverLicenseDeliveryType() {
            return driverLicenseDeliveryType;
        }

        public void setDriverLicenseDeliveryType(DocumentDeliveryType driverLicenseDeliveryType) {
            this.driverLicenseDeliveryType = driverLicenseDeliveryType;
        }
    }

    public static class ParkingRequestFactoryCreator extends ParkingRequestFactory {

        private static final Logger logger = LoggerFactory.getLogger(ParkingRequest.ParkingRequestFactoryCreator.class);

        public ParkingRequestFactoryCreator(ParkingParty parkingParty) {
            super(parkingParty);
            if ((getParkingParty().getParkingRequestsSet().isEmpty())
                    && parkingParty.getParty().getParkingPartyHistoriesSet().size() != 0) {
                Vehicle firstVehicle = parkingParty.getFirstVehicle();
                if (firstVehicle != null) {
                    setFirstCarPlateNumber(firstVehicle.getPlateNumber());
                    setFirstCarMake(firstVehicle.getVehicleMake());
                    setFirstCarPropertyRegistryDeliveryType(firstVehicle.getPropertyRegistryDeliveryType());
                    setFirstCarInsuranceDeliveryType(firstVehicle.getInsuranceDeliveryType());
                    setFirstCarOwnerIdDeliveryType(firstVehicle.getOwnerIdDeliveryType());
                    setFirstCarDeclarationDeliveryType(firstVehicle.getAuthorizationDeclarationDeliveryType());
                }
                Vehicle secondVehicle = parkingParty.getSecondVehicle();
                if (secondVehicle != null) {
                    setSecondCarPlateNumber(secondVehicle.getPlateNumber());
                    setSecondCarMake(secondVehicle.getVehicleMake());
                    setSecondCarPropertyRegistryDeliveryType(secondVehicle.getPropertyRegistryDeliveryType());
                    setSecondCarInsuranceDeliveryType(secondVehicle.getInsuranceDeliveryType());
                    setSecondCarOwnerIdDeliveryType(secondVehicle.getOwnerIdDeliveryType());
                    setSecondCarDeclarationDeliveryType(secondVehicle.getAuthorizationDeclarationDeliveryType());
                }
                setRequestAs(parkingParty.getRequestedAs());
            }
        }

        @Override
        public ParkingRequest execute() {
            if (!getParkingParty().hasFirstTimeRequest()) {
                try {
                    ParkingRequest parkingRequest = new ParkingRequest(this);
                    writeDriverLicenseFile(parkingRequest);

                    Vehicle firstVehicle = new Vehicle();
                    firstVehicle.setParkingRequest(parkingRequest);
                    firstVehicle.setPlateNumber(getFirstCarPlateNumber());
                    firstVehicle.setVehicleMake(getFirstCarMake());
                    firstVehicle.setPropertyRegistryDeliveryType(getFirstCarPropertyRegistryDeliveryType());
                    firstVehicle.setInsuranceDeliveryType(getFirstCarInsuranceDeliveryType());
                    firstVehicle.setOwnerIdDeliveryType(getFirstCarOwnerIdDeliveryType());
                    firstVehicle.setAuthorizationDeclarationDeliveryType(getFirstCarDeclarationDeliveryType());
                    writeFirstVehicleDocuments(firstVehicle);

                    if (getSecondCarMake() != null) {
                        Vehicle secondVehicle = new Vehicle();
                        secondVehicle.setParkingRequest(parkingRequest);
                        secondVehicle.setPlateNumber(getSecondCarPlateNumber());
                        secondVehicle.setVehicleMake(getSecondCarMake());
                        secondVehicle.setPropertyRegistryDeliveryType(getSecondCarPropertyRegistryDeliveryType());
                        secondVehicle.setInsuranceDeliveryType(getSecondCarInsuranceDeliveryType());
                        secondVehicle.setOwnerIdDeliveryType(getSecondCarOwnerIdDeliveryType());
                        secondVehicle.setAuthorizationDeclarationDeliveryType(getSecondCarDeclarationDeliveryType());
                        writeSecondVehicleDocuments(secondVehicle);
                    }

                    setRequestAs(parkingRequest.getRequestedAs());
                    setLimitlessAccessCard(parkingRequest.getLimitlessAccessCard());

                    return parkingRequest;
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            return null;
        }
    }

    public static class ParkingRequestFactoryEditor extends ParkingRequestFactory {

        private static final Logger logger = LoggerFactory.getLogger(ParkingRequest.ParkingRequestFactoryEditor.class);

        private ParkingRequest parkingRequest;

        public ParkingRequestFactoryEditor(ParkingParty parkingParty) {
            super(parkingParty);
        }

        public ParkingRequestFactoryEditor(ParkingRequest parkingRequest) {
            super(parkingRequest.getParkingParty());
            setParkingRequest(parkingRequest);

            setDriverLicenseFileName(parkingRequest.getDriverLicenseFileName());
            setDriverLicenseDeliveryType(parkingRequest.getDriverLicenseDeliveryType());

            if (!parkingRequest.getVehiclesSet().isEmpty()) {
                Vehicle firstVehicle = parkingRequest.getVehiclesSet().iterator().next();
                setFirstVechicleID(firstVehicle.getExternalId());
                setFirstCarMake(firstVehicle.getVehicleMake());
                setFirstCarPlateNumber(firstVehicle.getPlateNumber());
                setFirstCarPropertyRegistryFileName(firstVehicle.getPropertyRegistryFileName());
                setFirstInsuranceFileName(firstVehicle.getInsuranceFileName());
                setFirstCarOwnerIdFileName(firstVehicle.getOwnerIdFileName());
                setFirstDeclarationAuthorizationFileName(firstVehicle.getAuthorizationDeclarationFileName());
                setFirstCarPropertyRegistryDeliveryType(firstVehicle.getPropertyRegistryDeliveryType());
                setFirstCarInsuranceDeliveryType(firstVehicle.getInsuranceDeliveryType());
                setFirstCarOwnerIdDeliveryType(firstVehicle.getOwnerIdDeliveryType());
                setFirstCarDeclarationDeliveryType(firstVehicle.getAuthorizationDeclarationDeliveryType());
            }

            if (parkingRequest.getVehiclesSet().size() > 1) {
                Iterator<Vehicle> vehicles = parkingRequest.getVehiclesSet().iterator();
                vehicles.next();
                Vehicle secondVehicle = vehicles.next();
                setSecondVechicleID(secondVehicle.getExternalId());
                setSecondCarMake(secondVehicle.getVehicleMake());
                setSecondCarPlateNumber(secondVehicle.getPlateNumber());
                setSecondCarPropertyRegistryFileName(secondVehicle.getPropertyRegistryFileName());
                setSecondInsuranceFileName(secondVehicle.getInsuranceFileName());
                setSecondCarOwnerIdFileName(secondVehicle.getOwnerIdFileName());
                setSecondDeclarationAuthorizationFileName(secondVehicle.getAuthorizationDeclarationFileName());
                setSecondCarPropertyRegistryDeliveryType(secondVehicle.getPropertyRegistryDeliveryType());
                setSecondCarInsuranceDeliveryType(secondVehicle.getInsuranceDeliveryType());
                setSecondCarOwnerIdDeliveryType(secondVehicle.getOwnerIdDeliveryType());
                setSecondCarDeclarationDeliveryType(secondVehicle.getAuthorizationDeclarationDeliveryType());
            }

            setRequestAs(parkingRequest.getRequestedAs());
            setLimitlessAccessCard(parkingRequest.getLimitlessAccessCard());
        }

        public ParkingRequest getParkingRequest() {
            return parkingRequest;
        }

        public void setParkingRequest(ParkingRequest parkingRequest) {
            if (parkingRequest != null) {
                this.parkingRequest = parkingRequest;
            }
        }

        @Override
        public ParkingRequest execute() {
            try {
                ParkingRequest parkingRequest = getParkingRequest();
                parkingRequest.edit(this);
                writeDriverLicenseFile(parkingRequest);
                Vehicle firstVehicle = FenixFramework.getDomainObject(getFirstVechicleID());
                if (firstVehicle != null) {
                    firstVehicle.setPlateNumber(getFirstCarPlateNumber());
                    firstVehicle.setVehicleMake(getFirstCarMake());
                    firstVehicle.setPropertyRegistryDeliveryType(getFirstCarPropertyRegistryDeliveryType());
                    firstVehicle.setInsuranceDeliveryType(getFirstCarInsuranceDeliveryType());
                    firstVehicle.setOwnerIdDeliveryType(getFirstCarOwnerIdDeliveryType());
                    firstVehicle.setAuthorizationDeclarationDeliveryType(getFirstCarDeclarationDeliveryType());
                    writeFirstVehicleDocuments(firstVehicle);
                }
                if (getSecondVechicleID() != null) {
                    Vehicle secondVehicle = FenixFramework.getDomainObject(getSecondVechicleID());

                    if (getSecondCarMake() == null) {
                        secondVehicle.delete();
                    } else {
                        secondVehicle.setPlateNumber(getSecondCarPlateNumber());
                        secondVehicle.setVehicleMake(getSecondCarMake());
                        secondVehicle.setPropertyRegistryDeliveryType(getSecondCarPropertyRegistryDeliveryType());
                        secondVehicle.setInsuranceDeliveryType(getSecondCarInsuranceDeliveryType());
                        secondVehicle.setOwnerIdDeliveryType(getSecondCarOwnerIdDeliveryType());
                        secondVehicle.setAuthorizationDeclarationDeliveryType(getSecondCarDeclarationDeliveryType());
                        writeSecondVehicleDocuments(secondVehicle);
                    }
                } else if (getSecondCarMake() != null) {
                    Vehicle secondVehicle = new Vehicle();
                    secondVehicle.setParkingRequest(parkingRequest);
                    secondVehicle.setPlateNumber(getSecondCarPlateNumber());
                    secondVehicle.setVehicleMake(getSecondCarMake());
                    secondVehicle.setPropertyRegistryDeliveryType(getSecondCarPropertyRegistryDeliveryType());
                    secondVehicle.setInsuranceDeliveryType(getSecondCarInsuranceDeliveryType());
                    secondVehicle.setOwnerIdDeliveryType(getSecondCarOwnerIdDeliveryType());
                    secondVehicle.setAuthorizationDeclarationDeliveryType(getSecondCarDeclarationDeliveryType());
                    writeSecondVehicleDocuments(secondVehicle);
                }
                return parkingRequest;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }

    public boolean hasVehicleContainingPlateNumber(String plateNumber) {
        String plateNumberLowerCase = plateNumber.toLowerCase();
        for (Vehicle vehicle : getVehiclesSet()) {
            if (vehicle.getPlateNumber().toLowerCase().contains(plateNumberLowerCase)) {
                return true;
            }
        }
        return false;
    }

    public void edit(ParkingRequestFactoryEditor parkingRequestFactoryEditor) {
        setDriverLicenseDeliveryType(parkingRequestFactoryEditor.getDriverLicenseDeliveryType());
        setRequestedAs(parkingRequestFactoryEditor.getRequestAs());
        setLimitlessAccessCard(parkingRequestFactoryEditor.isLimitlessAccessCard());
    }

    public String getDriverLicenseFileName() {
        NewParkingDocument driverLicenseDocument = getDriverLicenseDocument();
        if (driverLicenseDocument != null) {
            return driverLicenseDocument.getParkingFile().getFilename();
        }
        return null;
    }

    public String getDriverLicenseFileNameToDisplay() {
        NewParkingDocument driverLicenseDocument = getDriverLicenseDocument();
        if (driverLicenseDocument != null) {
            return driverLicenseDocument.getParkingFile().getFilename();
        } else if (getDriverLicenseDeliveryType() != null) {
            ResourceBundle bundle = ResourceBundle.getBundle("resources.ParkingResources", I18N.getLocale());
            return bundle.getString(getDriverLicenseDeliveryType().name());
        }
        return "";
    }

    public void deleteDriverLicenseDocument() {
        NewParkingDocument parkingDocument = getDriverLicenseDocument();
        if (parkingDocument != null) {
            parkingDocument.delete();
        }
    }

    public void delete() {
        if (canBeDeleted()) {
            for (; getVehiclesSet().size() != 0; getVehiclesSet().iterator().next().delete()) {
                ;
            }
            deleteDriverLicenseDocument();
            setParkingParty(null);
            setRootDomainObject(null);
            deleteDomainObject();
        }
    }

    private boolean canBeDeleted() {
        return getParkingRequestState() != ParkingRequestState.ACCEPTED;
    }

    public boolean getHasHistory() {
        return getParkingParty().getParty().getParkingPartyHistoriesSet().size() != 0;
    }
}
