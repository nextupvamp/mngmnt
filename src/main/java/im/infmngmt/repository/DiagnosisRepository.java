package im.infmngmt.repository;

import im.infmngmt.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    List<Diagnosis> findByPatientId(Long patientId);
}