package im.infmngmt.repository;

import im.infmngmt.entity.TreatmentScheme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentSchemeRepository extends JpaRepository<TreatmentScheme, Long> {
}