package im.infmngmt.service;

import im.infmngmt.entity.Diagnosis;
import im.infmngmt.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisService {
    private final DiagnosisRepository diagnosisRepository;

    public List<Diagnosis> getAllDiagnoses() {
        return diagnosisRepository.findAll();
    }

    public Diagnosis saveDiagnosis(Diagnosis diagnosis) {
        return diagnosisRepository.save(diagnosis);
    }

    public void deleteDiagnosis(Long id) {
        diagnosisRepository.deleteById(id);
    }

    public List<Diagnosis> getDiagnosesByPatientId(Long patientId) {
        return diagnosisRepository.findByPatientId(patientId);
    }
}