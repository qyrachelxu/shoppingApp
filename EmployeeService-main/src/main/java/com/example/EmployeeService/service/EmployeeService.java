package com.example.EmployeeService.service;

import com.example.EmployeeService.Exception.DocumentNotFoundException;
import com.example.EmployeeService.Exception.EmployeeNotFoundException;
import com.example.EmployeeService.Exception.RoommateNotFoundException;
import com.example.EmployeeService.domain.Employee;
import com.example.EmployeeService.domain.PersonalDocument;
import com.example.EmployeeService.domain.VisaStatus;
import com.example.EmployeeService.dto.application.ApplicationDTO;
import com.example.EmployeeService.dto.application.EmployeeInfoDTO;
import com.example.EmployeeService.dto.houseComposite.RoommateDTO;
import com.example.EmployeeService.dto.profile.NameDTO;
import com.example.EmployeeService.dto.profile.PersonalContactDTO;
import com.example.EmployeeService.dto.profile.emergencyContact.EmergencyContactDTO;
import com.example.EmployeeService.dto.profile.address.AddressDTO;
import com.example.EmployeeService.dto.profile.ProfileDTO;
import com.example.EmployeeService.dto.profile.document.DocumentDTO;
import com.example.EmployeeService.dto.profile.employment.EmployeeSummaryDTO;
import com.example.EmployeeService.dto.profile.employment.EmployeeVisaDTO;
import com.example.EmployeeService.dto.profile.employment.VisaDTO;
import com.example.EmployeeService.repository.EmployeeRepo;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepo repository;

    public EmployeeService(EmployeeRepo repository) {
        this.repository = repository;
    }

    public void addNewEmployee(Employee employee) {
        repository.save(employee);
    }

    public List<PersonalDocument> getPersonalDocumentsForEmployee(String employeeId) {
        return repository.findPersonalDocumentsByEmployeeId(employeeId);
    }

    public ProfileDTO findProfileByUserID(int userID) {
        if(repository.findEmployeeByUserID(userID) == null) throw new EmployeeNotFoundException("Employee Not Found. Please provide correct ID");

        List<AddressDTO> addressDTOList = new ArrayList<>();
        repository.findAddressProfileByUserID(userID).getAddresses().forEach(a -> {
            addressDTOList.add(AddressDTO.builder()
                            .addressLine1(a.getAddressLine1())
                            .addressLine2(a.getAddressLine2())
                            .city(a.getCity())
                            .state(a.getState())
                            .zipcode(a.getZipcode())
                            .build());
        });

        List<DocumentDTO> documentDTOList = new ArrayList<>();
        repository.findDocumentProfileByUserID(userID).getPersonalDocuments().forEach(d -> {
            documentDTOList.add(DocumentDTO
                            .builder()
                            .personalDocumentId(d.getPersonalDocumentId())
                            .path(d.getPath())
                            .title(d.getTitle())
                    .build());
        });

        List<EmergencyContactDTO> emergencyContactDTOList = new ArrayList<>();
        repository.findEmergencyContactProfileByUserID(userID).getContacts().forEach(c -> {
            emergencyContactDTOList.add(EmergencyContactDTO.builder()
                            .alternatePhone(c.getAlternatePhone())
                            .cellPhone(c.getCellPhone())
                            .email(c.getEmail())
                            .firstName(c.getFirstName())
                            .lastName(c.getLastName())
                            .middleName(c.getMiddleName())
                            .relationship(c.getRelationship())
                    .build());
        });


        List<VisaDTO> visaDTOList = new ArrayList<>();
        repository.findVisaProfileByUserID(userID).getVisaStatuses().forEach(v -> {
            visaDTOList.add(VisaDTO.builder()
                            .endDate(v.getEndDate())
                            .startDate(v.getStartDate())
                            .visaType(v.getVisaType())
                    .build());
        });


        return ProfileDTO.builder()
                .name(repository.findNameProfileByUserID(userID))
                .addresses(addressDTOList)
                .contactInfo(repository.findContactInfoProfileByUserID(userID))
                .employment(visaDTOList)
                .documents(documentDTOList)
                .emergencyContact(emergencyContactDTOList)
                .build();
    }

    public ApplicationDTO findApplicationByUserID(int userID) {
        if(repository.findEmployeeByUserID(userID) == null) throw new EmployeeNotFoundException("Employee Not Found. Please provide correct ID");

        System.out.println("service return ApplicationDTO");
        List<DocumentDTO> documentDTOList = new ArrayList<>();
        repository.findDocumentProfileByUserID(userID).getPersonalDocuments().forEach(d -> {
            documentDTOList.add(DocumentDTO
                    .builder()
                    .personalDocumentId(d.getPersonalDocumentId())
                    .path(d.getPath())
                    .title(d.getTitle())
                    .build());
        });

        return ApplicationDTO.builder()
                .form(repository.findApplicationFormByUserID(userID))
                .documents(documentDTOList)
                .build();
    }

    public Employee findEmployeeByUserIDOrEmail(int userID, String email) {
        if(repository.findEmployeeByUserID(userID) == null && repository.findEmployeeByEmail(email) == null) return null;
        else if(repository.findEmployeeByEmail(email) != null) return repository.findEmployeeByEmail(email);
        return repository.findEmployeeByUserID(userID);
    }

    public Employee findEmployeeByUserID(int userID) {
        if(repository.findEmployeeByUserID(userID) == null) throw new EmployeeNotFoundException("Employee Not Found. Please provide correct userID");

        return repository.findEmployeeByUserID(userID);
    }

    public void updateProfileName(int userId, ProfileDTO profileDTO) {
        Employee employee = repository.findEmployeeByUserID(userId);
        if(employee == null) throw new EmployeeNotFoundException("Employee Not Found. Please provide correct userID");

        //update name section
        NameDTO nameDTO = profileDTO.getName();
        employee.setFirstName(nameDTO.getFirstName());
        employee.setLastName(nameDTO.getLastName());
        employee.setMiddleName(nameDTO.getMiddleName());
        employee.setPreferedName(nameDTO.getPreferedName());
        employee.setEmail(nameDTO.getEmail());
        employee.setSsn(nameDTO.getSsn());
        employee.setDob(nameDTO.getDob());
        employee.setGender(nameDTO.getGender());

        //update personal contact
        PersonalContactDTO personalContactDTO = profileDTO.getContactInfo();
        employee.setCellPhone(personalContactDTO.getCellPhone());
        employee.setAlternatePhone(personalContactDTO.getAlternatePhone());

        //update address
        List<AddressDTO> addressDTOList = profileDTO.getAddresses();
        for(int i = 0; i < addressDTOList.size(); i++) {
            AddressDTO tmpDTO = addressDTOList.get(i);
            employee.getAddresses().get(i).setAddressLine1(tmpDTO.getAddressLine1());
            employee.getAddresses().get(i).setAddressLine2(tmpDTO.getAddressLine2());
            employee.getAddresses().get(i).setCity(tmpDTO.getCity());
            employee.getAddresses().get(i).setState(tmpDTO.getState());
            employee.getAddresses().get(i).setZipcode(tmpDTO.getZipcode());
        }

        //update visa
        List<VisaDTO> visaDTOList = profileDTO.getEmployment();
        for(int i = 0; i < visaDTOList.size(); i++) {
            VisaDTO visaDTO = visaDTOList.get(i);
            employee.getVisaStatuses().get(i).setVisaType(visaDTO.getVisaType());
            employee.getVisaStatuses().get(i).setStartDate(visaDTO.getStartDate());
            employee.getVisaStatuses().get(i).setEndDate(visaDTO.getEndDate());
            employee.getVisaStatuses().get(i).setLastModificationDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }

        //update emergency contact
        List<EmergencyContactDTO> emergencyContactDTOList = profileDTO.getEmergencyContact();
        for(int i = 0; i < emergencyContactDTOList.size(); i++) {
            EmergencyContactDTO emergencyContactDTO = profileDTO.getEmergencyContact().get(i);
            employee.getContacts().get(i).setFirstName(emergencyContactDTO.getFirstName());
            employee.getContacts().get(i).setLastName(emergencyContactDTO.getLastName());
            employee.getContacts().get(i).setMiddleName(emergencyContactDTO.getMiddleName());
            employee.getContacts().get(i).setRelationship(emergencyContactDTO.getRelationship());
            employee.getContacts().get(i).setEmail(emergencyContactDTO.getEmail());
            employee.getContacts().get(i).setCellPhone(emergencyContactDTO.getCellPhone());
            employee.getContacts().get(i).setAlternatePhone(emergencyContactDTO.getAlternatePhone());
        }


        repository.save(employee);
    }

    public List<RoommateDTO> findRoommatesByEmployeeId(String employeeId) {
        Employee employee = repository.findEmployeeByEmployeeID(employeeId);
        if(repository.findEmployeeByEmployeeID(employeeId) == null) throw new EmployeeNotFoundException("Employee Not Found. Please provide correct id");

        int houseId = employee.getHouseID();

        //find All Roommate
        List<RoommateDTO> roommates = repository.findRoommateByHouseIDAndEmployeeIdNot(houseId, employeeId);
        return roommates;
    }

    public List<Employee> findResidentsByHouseID(int houseID) {
        return repository.findResidentsByHouseID(houseID);
    }

    public List<PersonalDocument> getPersonalDocuments(Integer userID) {
        if(repository.findEmployeeByUserID(userID) == null) throw new EmployeeNotFoundException("Employee Not Found. Please provide correct userID");

        Employee employee = repository.findByUserID(userID);
        List<PersonalDocument> personalDocuments = new ArrayList<>();
        personalDocuments.addAll(employee.getPersonalDocuments());

        return personalDocuments;
    }

    public List<EmployeeInfoDTO> findEmployeeInfoByEmployeeIds(List<String> employeeIds) {
        List<EmployeeInfoDTO> employees = new ArrayList<>();
        employeeIds.forEach(id -> employees.add(repository.findEmployeeInfoByEmployeeId(id)));
        return employees;
    }

    public Employee findEmployeeByEmployeeId(String employeeId) {
        if(repository.findEmployeeByEmployeeID(employeeId) == null) throw new EmployeeNotFoundException("Employee Not Found. Please provide correct userID");

        return repository.findEmployeeByEmployeeID(employeeId);
    }

    public List<EmployeeInfoDTO> findEmployeeInfoByUserIDs(List<Integer> userIDs) {
        List<EmployeeInfoDTO> employees = new ArrayList<>();
        userIDs.forEach(id -> {
            EmployeeInfoDTO employee = repository.findEmployeeInfoByUserID(id);
            if(employee == null) throw new EmployeeNotFoundException("Employee Not Found. Please provide correct ID");
            employees.add(repository.findEmployeeInfoByUserID(id));
        });
        return employees;
    }

    public List<EmployeeSummaryDTO> getAllEmployeeSummaries(int page) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("lastName"));

        List<Employee> employees = repository.findAll(pageable).getContent();
        List<EmployeeSummaryDTO> employeeSummaries = new ArrayList<>();

        for (Employee employee : employees) {
            EmployeeSummaryDTO employeeSummary = new EmployeeSummaryDTO();

            employeeSummary.setFullName(employee.getFirstName() + " " + employee.getMiddleName() + " " + employee.getLastName());
            employeeSummary.setSsn(employee.getSsn());
            employeeSummary.setCellPhone(employee.getCellPhone());
            employeeSummary.setEmail(employee.getEmail());

            List<VisaStatus> visaStatuses = employee.getVisaStatuses();
            if (!visaStatuses.isEmpty()) {
                VisaStatus latestVisaStatus = visaStatuses.get(visaStatuses.size() - 1);
                employeeSummary.setWorkAythorizationType(latestVisaStatus.getVisaType());
            }

            employeeSummaries.add(employeeSummary);
        }
        return employeeSummaries;
    }

    public List<EmployeeVisaDTO> getEmployeeByVisaStatus(int page) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Employee> employeePage = repository.findAll(pageable);

        List<EmployeeVisaDTO> employeeWithVisaStatusList = new ArrayList<>();
        for (Employee employee : employeePage.getContent()) {
            EmployeeVisaDTO employeeWithVisaStatus = new EmployeeVisaDTO();
            employeeWithVisaStatus.setFullName(employee.getFirstName() + " " + employee.getMiddleName() + " " + employee.getLastName());
            List<VisaStatus> visaStatuses = employee.getVisaStatuses();
            if (!visaStatuses.isEmpty()) {
                VisaStatus latestVisaStatus = visaStatuses.get(visaStatuses.size() - 1);
                LocalDate endDateParsed = null;
                if (latestVisaStatus.getEndDate() != null && !latestVisaStatus.getEndDate().isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    endDateParsed = LocalDate.parse(latestVisaStatus.getEndDate(), formatter);
                }
                if (endDateParsed == null || endDateParsed.isBefore(LocalDate.now())) {
                    employeeWithVisaStatus.setWorkAuthorizationType("No valid visa");
                    employeeWithVisaStatus.setDaysLeft(0);
                    employeeWithVisaStatus.setExpirationDate("2099-01-01");
                } else {
                    employeeWithVisaStatus.setWorkAuthorizationType(latestVisaStatus.getVisaType());
                    int daysLeft = (int) ChronoUnit.DAYS.between(LocalDate.now(), endDateParsed);
                    employeeWithVisaStatus.setDaysLeft(daysLeft);
                    employeeWithVisaStatus.setExpirationDate(latestVisaStatus.getEndDate());
                }
            } else {
                employeeWithVisaStatus.setWorkAuthorizationType("Citizen or Greencard holder or no valid visa");
                employeeWithVisaStatus.setDaysLeft(999999);
                employeeWithVisaStatus.setExpirationDate("2099-01-01");
            }
            employeeWithVisaStatusList.add(employeeWithVisaStatus);
        }
        return employeeWithVisaStatusList;
    }

    public Employee getEmployeeByUsername(String firstName, String lastName) {
        return repository.findByFirstNameAndLastName(firstName, lastName);
    }

    public Employee getEmployeeByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Employee editEmployee(Employee employee) {
        Employee emp = repository.findEmployeeByUserID(employee.getUserID());
        if(emp == null) throw new EmployeeNotFoundException("Employee not found. Please assign with correct userID");

        ObjectId id = emp.get_id();
        String email = emp.getEmail();
        emp = employee;
        emp.set_id(id);
        emp.setEmail(email);

        return repository.save(emp);
    }

    public PersonalDocument reviewOPTDocument(int userID, String title, String status, String comment) {
        if(repository.findEmployeeByUserID(userID) == null) throw new EmployeeNotFoundException("Employee not found. Please assign with correct userID");

        Employee employee = findEmployeeByUserID(userID);
        List<PersonalDocument> documents = employee.getPersonalDocuments();
        for(PersonalDocument doc: documents) {
            if(doc.getTitle().equals(title)) {
                if(!doc.getDocumentStatus().equals("Pending")) throw new DocumentNotFoundException("Update fail. Document has been approved.");
                doc.setDocumentStatus(status);
                doc.setComment(comment);
                repository.save(employee);
                return doc;
            }
        }
        throw new DocumentNotFoundException("Document not found. Please validate document's title.");
    }

    public String getObjectIdByUserId(int userID) {
        Employee employee = repository.findByUserID((userID));
        return employee.get_id().toString();
    }
}
