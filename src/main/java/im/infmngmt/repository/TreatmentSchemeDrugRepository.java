package im.infmngmt.repository;

import im.infmngmt.entity.TreatmentSchemeDrug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreatmentSchemeDrugRepository extends JpaRepository<TreatmentSchemeDrug, Long> {
    List<TreatmentSchemeDrug> findBySchemeId(Long schemeId);
}