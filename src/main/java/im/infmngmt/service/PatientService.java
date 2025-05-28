package im.infmngmt.service;

import im.infmngmt.entity.Patient;
import im.infmngmt.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;

    public List<Patient> getAll() {
        return repository.findAll();
    }

    public void save(Patient patient) {
        repository.save(patient);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}