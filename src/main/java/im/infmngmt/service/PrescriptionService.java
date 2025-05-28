package im.infmngmt.service;

import im.infmngmt.entity.Prescription;
import im.infmngmt.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepo;

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepo.findAll();
    }

    public void cancelPrescription(Long prescriptionId) {
        prescriptionRepo.deleteById(prescriptionId);
    }
}