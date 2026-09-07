package profamilia.prueba.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import profamilia.prueba.dto.EmployRequestDTO;
import profamilia.prueba.dto.EmployResponseDTO;
import profamilia.prueba.service.EmployService;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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
    public ResponseEntity<?> updateEmployeeStatus(@PathVariable Long id, @RequestBody Boolean present) {
        try {
            return ResponseEntity.ok(employService.updateEmployeeStatus(id, present));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
