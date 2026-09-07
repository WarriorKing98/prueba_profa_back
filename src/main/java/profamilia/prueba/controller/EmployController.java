package profamilia.prueba.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import profamilia.prueba.dto.EmployRequestDTO;
import profamilia.prueba.dto.EmployResponseDTO;
import profamilia.prueba.service.EmployService;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployController {

    private final EmployService employService;

    @GetMapping
    public ResponseEntity<List<EmployResponseDTO>> getAllEmployees() {
        return ResponseEntity.ok(employService.getAllEmployees());
    }

    @PostMapping
    public ResponseEntity<EmployResponseDTO> createEmployee(@RequestBody EmployRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employService.createEmployee(dto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EmployResponseDTO> updateEmployeeStatus(@PathVariable Long id,
            @RequestBody Boolean present) {
        return ResponseEntity.ok(employService.updateEmployeeStatus(id, present));
    }
}
