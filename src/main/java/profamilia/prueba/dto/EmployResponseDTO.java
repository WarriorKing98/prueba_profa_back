package profamilia.prueba.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployResponseDTO {

    private Long id;
    private String fullName;
    private String position;
    private Boolean present;
}
