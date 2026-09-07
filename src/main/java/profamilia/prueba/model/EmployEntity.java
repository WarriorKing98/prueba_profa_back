package profamilia.prueba.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "empleados")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String fullName;

    @Column(name = "puesto", nullable = false, length = 100)
    private String position;

    @Column(name = "presente", nullable = false)
    private Boolean present = false;

}
