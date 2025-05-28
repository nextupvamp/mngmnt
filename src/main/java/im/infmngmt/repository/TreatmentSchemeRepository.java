package im.infmngmt.repository;

import im.infmngmt.entity.TreatmentScheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreatmentSchemeRepository extends JpaRepository<TreatmentScheme, Long> {
    List<TreatmentScheme> findByDiagnosisId(Long diagnosisId);
}