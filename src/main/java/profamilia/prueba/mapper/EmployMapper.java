package profamilia.prueba.mapper;

import org.springframework.stereotype.Component;
import profamilia.prueba.dto.EmployRequestDTO;
import profamilia.prueba.dto.EmployResponseDTO;
import profamilia.prueba.model.EmployEntity;

@Component
public class EmployMapper {

    public EmployEntity toEntity(EmployRequestDTO dto) {
        EmployEntity entity = new EmployEntity();
        entity.setFullName(dto.getFullName());
        entity.setPosition(dto.getPosition());
        entity.setPresent(false);
        return entity;
    }

    public EmployResponseDTO toDto(EmployEntity entity) {
        return EmployResponseDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .position(entity.getPosition())
                .present(entity.getPresent())
                .build();
    }
}
