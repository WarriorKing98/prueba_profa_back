package profamilia.prueba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import profamilia.prueba.model.EmployEntity;

public interface EmployRepository extends JpaRepository<EmployEntity, Long> {
    
    @Modifying
    @Query("UPDATE EmployEntity e SET e.present = :present WHERE e.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("present") Boolean present);
}
