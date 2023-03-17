package com.example.EmployeeService.repository;

import com.example.EmployeeService.domain.Employee;
import com.example.EmployeeService.domain.PersonalDocument;
import com.example.EmployeeService.dto.*;
import com.example.EmployeeService.dto.application.ApplicationFormDTO;
import com.example.EmployeeService.dto.application.EmployeeInfoDTO;
import com.example.EmployeeService.dto.houseComposite.RoommateDTO;
import com.example.EmployeeService.dto.profile.NameDTO;
import com.example.EmployeeService.dto.profile.PersonalContactDTO;
import com.example.EmployeeService.dto.profile.document.DocumentDTO;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepo extends MongoRepository<Employee, String> {
    @Query("{_id: '?0'}")
    List<PersonalDocument> findPersonalDocumentsByEmployeeId(String employeeId);

    Employee findByUserID(int userID);

    @Query("{_id: '?0'}")
    Employee findEmployeeByEmployeeID(String employeeId);

    Employee findEmployeeByUserID(int userID);

    NameDTO findNameProfileByUserID(int userID);
    AddressListDTO findAddressProfileByUserID(int userID);
    PersonalContactDTO findContactInfoProfileByUserID(int userID);
    VisaListDTO findVisaProfileByUserID(int userID);
    EmergencyContactListDTO findEmergencyContactProfileByUserID(int userID);
    DocumentListDTO findDocumentProfileByUserID(int userID);

    ApplicationFormDTO findApplicationFormByUserID(int userID);

    @Query("{ houseID : ?0, _id : { $ne: '?1' } }")
    List<RoommateDTO> findRoommateByHouseIDAndEmployeeIdNot(int houseID, String employeeId);

    @Query("{ houseID : ?0 }")
    List<Employee> findResidentsByHouseID(int houseID);

    @Query("{_id: '?0' }")
    EmployeeInfoDTO findEmployeeInfoByEmployeeId(String employeeId);

    @Query("{userID: ?0}")
    EmployeeInfoDTO findEmployeeInfoByUserID(int id);

    Employee findByFirstNameAndMiddleNameAndLastName(String firstName, String middleName, String lastName);

    Employee findByFirstNameAndLastName(String firstName, String lastName);

    Employee findByEmail(String email);

    @Query("{email: '?0'}")
    Employee findEmployeeByEmail(String email);

}