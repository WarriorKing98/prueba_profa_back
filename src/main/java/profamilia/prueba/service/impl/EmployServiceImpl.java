package profamilia.prueba.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import profamilia.prueba.dto.EmployRequestDTO;
import profamilia.prueba.dto.EmployResponseDTO;
import profamilia.prueba.exception.ConflictException;
import profamilia.prueba.exception.ResourceNotFoundException;
import profamilia.prueba.mapper.EmployMapper;
import profamilia.prueba.model.EmployEntity;
import profamilia.prueba.repository.EmployRepository;
import profamilia.prueba.service.EmployService;

@Service
@RequiredArgsConstructor
public class EmployServiceImpl implements EmployService {

    private final EmployRepository employRepository;
    private final EmployMapper employMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmployResponseDTO> getAllEmployees() {
        return employRepository.findAll().stream()
                .map(employMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EmployResponseDTO createEmployee(EmployRequestDTO dto) {
        EmployEntity saved = employRepository.save(employMapper.toEntity(dto));
        return employMapper.toDto(saved);
    }

    @Override
    @Transactional
    public EmployResponseDTO updateEmployeeStatus(Long id, Boolean present) {
        EmployEntity employee = employRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getPresent().equals(present)) {
            String status = present ? "active (present)" : "inactive (absent)";
            throw new ConflictException("Employee is already " + status);
        }

        employRepository.updateStatusById(id, present);
        employee.setPresent(present);
        return employMapper.toDto(employee);
    }
}
