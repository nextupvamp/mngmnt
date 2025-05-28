package im.infmngmt.repository;

import im.infmngmt.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DrugRepository extends JpaRepository<Drug, Long> {
    List<Drug> findByMnnContainingOrActiveSubstanceContainingOrManufacturerContaining(
            String mnn, String activeSubstance, String manufacturer);

    @Query("SELECT d FROM Drug d JOIN DrugAnalog da ON d.id = da.id WHERE da.id = :drugId")
    List<Drug> findAnalogsByDrugId(@Param("drugId") Long drugId);
}