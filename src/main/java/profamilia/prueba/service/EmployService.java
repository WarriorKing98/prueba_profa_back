package profamilia.prueba.service;

import java.util.List;

import profamilia.prueba.dto.EmployRequestDTO;
import profamilia.prueba.dto.EmployResponseDTO;

public interface EmployService {

    List<EmployResponseDTO> getAllEmployees();

    EmployResponseDTO createEmployee(EmployRequestDTO employeeDTO);

    EmployResponseDTO updateEmployeeStatus(Long id, Boolean present);
}
