package com.example.EmployeeService.service;

import com.amazonaws.services.shield.model.EmergencyContact;
import com.example.EmployeeService.Exception.DocumentNotFoundException;
import com.example.EmployeeService.Exception.EmployeeNotFoundException;
import com.example.EmployeeService.domain.*;
import com.example.EmployeeService.dto.application.EmployeeInfoDTO;
import com.example.EmployeeService.dto.profile.NameDTO;
import com.example.EmployeeService.dto.profile.PersonalContactDTO;
import com.example.EmployeeService.dto.profile.ProfileDTO;
import com.example.EmployeeService.dto.profile.address.AddressDTO;
import com.example.EmployeeService.dto.profile.emergencyContact.EmergencyContactDTO;
import com.example.EmployeeService.dto.profile.employment.VisaDTO;
import com.example.EmployeeService.repository.EmployeeRepo;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EmployeeServiceTest {
    @Mock
    private EmployeeRepo repository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    public void testAddNewEmployee() {
        // Create an Employee object to add
        Employee employee = new Employee();

        // Call the addNewEmployee method on the employeeService
        employeeService.addNewEmployee(employee);

        // Verify that the repository's save method was called once with the same Employee object
        verify(repository, times(1)).save(employee);
    }

    @Test
    public void testGetPersonalDocumentsForEmployee() {
        String employeeId = "12345";
        List<PersonalDocument> personalDocuments = new ArrayList<>();
        PersonalDocument document1 = new PersonalDocument();
        PersonalDocument document2 = new PersonalDocument();
        personalDocuments.add(document1);
        personalDocuments.add(document2);

        // Mock the behavior of the repository method
        when(repository.findPersonalDocumentsByEmployeeId(employeeId)).thenReturn(personalDocuments);

        // Call the service method and verify the result
        List<PersonalDocument> result = employeeService.getPersonalDocumentsForEmployee(employeeId);
        assertEquals(2, result.size());
        assertTrue(result.contains(document1));
        assertTrue(result.contains(document2));

        // Verify that the repository method was called once
        verify(repository, times(1)).findPersonalDocumentsByEmployeeId(employeeId);
    }

    @Test
    public void testFindEmployeeByUserId() {
        int userID = 123;
        Employee employee = new Employee();
        employee.setUserID(userID);

        when(repository.findEmployeeByUserID(userID)).thenReturn(employee);

        Employee result = employeeService.findEmployeeByUserID(userID);

        assertEquals(employee, result);
    }

    @Test
    public void testFindEmployeeByUserIdNotFound() {
        int userID = 123;

        when(repository.findEmployeeByUserID(userID)).thenReturn(null);

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.findEmployeeByUserID(userID));
    }

    @Test
    public void testFindResidentsByHouseID() {
        int houseID = 123;
        List<Employee> expectedResidents = Arrays.asList(
                new Employee(),
                new Employee());

        // mock the repository to return the expected list of residents
        when(repository.findResidentsByHouseID(houseID)).thenReturn(expectedResidents);

        // call the method being tested
        List<Employee> actualResidents = employeeService.findResidentsByHouseID(houseID);

        // assert that the actual list of residents matches the expected list
        assertEquals(expectedResidents, actualResidents);

        // verify that the repository method was called exactly once
        verify(repository, times(1)).findResidentsByHouseID(houseID);
    }

    @Test
    public void testFindEmployeeInfoByEmployeeIds() {
        List<String> employeeIds = Arrays.asList("1", "2", "3");
        EmployeeInfoDTO employeeInfoDTO1 = new EmployeeInfoDTO();
        EmployeeInfoDTO employeeInfoDTO2 = new EmployeeInfoDTO();
        EmployeeInfoDTO employeeInfoDTO3 = new EmployeeInfoDTO();
        List<EmployeeInfoDTO> expected = Arrays.asList(employeeInfoDTO1, employeeInfoDTO2, employeeInfoDTO3);

        // Mock the repository and set the expected return values
        EmployeeRepo repository = mock(EmployeeRepo.class);
        when(repository.findEmployeeInfoByEmployeeId("1")).thenReturn(employeeInfoDTO1);
        when(repository.findEmployeeInfoByEmployeeId("2")).thenReturn(employeeInfoDTO2);
        when(repository.findEmployeeInfoByEmployeeId("3")).thenReturn(employeeInfoDTO3);

        // Create an instance of EmployeeService using the mocked repository
        EmployeeService employeeService = new EmployeeService(repository);

        // Call the method being tested
        List<EmployeeInfoDTO> actual = employeeService.findEmployeeInfoByEmployeeIds(employeeIds);

        // Verify the results
        assertEquals(expected, actual);
        verify(repository, times(1)).findEmployeeInfoByEmployeeId("1");
        verify(repository, times(1)).findEmployeeInfoByEmployeeId("2");
        verify(repository, times(1)).findEmployeeInfoByEmployeeId("3");
    }

    @Test
    public void testFindEmployeeByEmployeeId() {
        // Arrange
        String employeeId = "123456";
        Employee employee = new Employee();
        when(repository.findEmployeeByEmployeeID(employeeId)).thenReturn(null);
        // Call the method that you expect to throw an exception
        assertThrows(EmployeeNotFoundException.class, () ->
            employeeService.findEmployeeByEmployeeId(employeeId)
        );

        when(repository.findEmployeeByEmployeeID(employeeId)).thenReturn(employee);
        // Act
        Employee result = employeeService.findEmployeeByEmployeeId(employeeId);
        // Assert
        assertNotNull(result);
    }

    @Test
    public void testGetEmployeeByUsername() {
        // create an Employee object with matching name
        Employee employee = new Employee();
        employee.setFirstName("John");
        //employee.setMiddleName("Robert");
        employee.setLastName("Doe");

        // mock the repository and define behavior for findByFirstNameAndMiddleNameAndLastName method
        EmployeeRepo repository = Mockito.mock(EmployeeRepo.class);
        Mockito.when(repository.findByFirstNameAndLastName("John", "Doe"))
                .thenReturn(employee);

        // create an instance of the EmployeeService with the mocked repository
        EmployeeService employeeService = new EmployeeService(repository);

        // call the getEmployeeByUsername method with the matching name
        Employee result = employeeService.getEmployeeByUsername("John", "Doe");

        // assert that the returned Employee object is the same as the one we created
        Assertions.assertEquals(employee, result);
    }

    @Test
    public void testGetEmployeeByEmail() {
        // Create a mock repository that returns a known employee
        EmployeeRepo mockRepo = mock(EmployeeRepo.class);
        Employee expectedEmployee = new Employee();
        when(mockRepo.findByEmail("john.doe@example.com")).thenReturn(expectedEmployee);

        // Create a new EmployeeService that uses the mock repository
        EmployeeService service = new EmployeeService(mockRepo);

        // Call the method with the known email and verify that it returns the expected employee
        Employee actualEmployee = service.getEmployeeByEmail("john.doe@example.com");
        assertEquals(expectedEmployee, actualEmployee);
    }

    @Test
    public void testUpdateProfileName_Invalid(){
        int userID = 123;
        ProfileDTO profileDTO = new ProfileDTO();
        when(repository.findEmployeeByUserID(userID)).thenReturn(null);
        // Call the method that you expect to throw an exception
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateProfileName(userID, profileDTO));
    }

    @Test
    public void testUpdateProfileName(){
        // Arrange
        int userId = 1;
        ProfileDTO profileDTO = new ProfileDTO();
        NameDTO nameDTO = new NameDTO();
        nameDTO.setFirstName("John");
        nameDTO.setLastName("Doe");
        nameDTO.setMiddleName("M");
        nameDTO.setPreferedName("Johnny");
        nameDTO.setEmail("john.doe@example.com");
        nameDTO.setSsn("123-45-6789");
        nameDTO.setDob("1990-01-01");
        nameDTO.setGender("Male");
        profileDTO.setName(nameDTO);
        Employee employee = new Employee();

        PersonalContactDTO personalContactDTO = new PersonalContactDTO();
        personalContactDTO.setCellPhone("555-1234");
        personalContactDTO.setAlternatePhone("555-5678");
        profileDTO.setContactInfo(personalContactDTO);

        List<AddressDTO> addressDTOList = new ArrayList<>();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressLine1("123 Main St");
        addressDTO.setCity("Anytown");
        addressDTO.setState("CA");
        addressDTO.setZipcode("12345");
        addressDTOList.add(addressDTO);
        profileDTO.setAddresses(addressDTOList);

        List<Address> addresses = new ArrayList<>();
        Address address = new Address();
        address.setAddressLine1("456 Elm St");
        address.setCity("Somewhere");
        address.setState("NY");
        address.setZipcode("67890");
        addresses.add(address);
        employee.setAddresses(addresses);

        List<VisaDTO> visaDTOList = new ArrayList<>();
        VisaDTO visaDTO1 = new VisaDTO();
        visaDTO1.setVisaType("L1");
        visaDTO1.setStartDate("2022-01-01");
        visaDTO1.setEndDate("2025-12-31");
        visaDTOList.add(visaDTO1);
        profileDTO.setEmployment(visaDTOList);

        List<VisaStatus> visaStatuses = new ArrayList<>();
        VisaStatus visaStatus1 = new VisaStatus();
        visaStatus1.setVisaType("H1B");
        visaStatus1.setStartDate("2022-01-01");
        visaStatus1.setEndDate("2024-12-31");
        visaStatuses.add(visaStatus1);
        employee.setVisaStatuses(visaStatuses);

        List<EmergencyContactDTO> emergencyContactDTOList = new ArrayList<>();
        EmergencyContactDTO emergencyContactDTO = new EmergencyContactDTO();
        emergencyContactDTO.setFirstName("Jane");
        emergencyContactDTO.setLastName("Smith");
        emergencyContactDTOList.add(emergencyContactDTO);
        profileDTO.setEmergencyContact(emergencyContactDTOList);

        List<Contact> contacts = new ArrayList<>();
        Contact contact = new Contact();
        contact.setFirstName("test");
        contact.setLastName("last");
        contacts.add(contact);
        employee.setContacts(contacts);

        when(repository.findEmployeeByUserID(userId)).thenReturn(employee);

        // Act
        employeeService.updateProfileName(userId, profileDTO);
        verify(repository).save(employee);
    }

    @Test
    public void testReviewOPTDocument_EmployeeNotFound(){
        int userID = 123;
        String title = "OPT Document";
        String status = "Approved";
        String comment = "Looks good!";
        when(repository.findEmployeeByUserID(userID)).thenReturn(null);
        // Call the method that you expect to throw an exception
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.reviewOPTDocument(userID, title, status, comment));
    }

    @Test
    public void testReviewOPTDocument(){
        int userID = 123;
        String title = "OPT Document";
        String status = "Approved";
        String comment = "Looks good!";

        Employee employee = new Employee();
        employee.setUserID(userID);
        PersonalDocument doc = new PersonalDocument();
        doc.setTitle(title);
        doc.setDocumentStatus("Pending");
        doc.setComment("");
        employee.setPersonalDocuments(Arrays.asList(doc));

        when(repository.findEmployeeByUserID(userID)).thenReturn(employee);

        List<PersonalDocument> documents = new ArrayList<>();
        PersonalDocument reviewedDoc = employeeService.reviewOPTDocument(userID, title, status, comment);
        documents.add(reviewedDoc);

        verify(repository).save(employee);
//
//        assertEquals(status, reviewedDoc.getDocumentStatus());
//        assertEquals(comment, reviewedDoc.getComment());
    }

    @Test
    public void testReviewOPTDocumentDocumentNotFoundException() {
        int userID = 123;
        String title = "I-9 Form";
        String status = "Approved";
        String comment = "Looks good!";

        Employee employee = new Employee();
        employee.setUserID(userID);
        when(repository.findEmployeeByUserID(userID)).thenReturn(employee);


        PersonalDocument doc = new PersonalDocument();
        doc.setTitle("OPT Document");
        doc.setDocumentStatus("Pending");
        doc.setComment("");

        assertThrows(DocumentNotFoundException.class, () -> employeeService.reviewOPTDocument(userID, title, status, comment));
    }

    
}
