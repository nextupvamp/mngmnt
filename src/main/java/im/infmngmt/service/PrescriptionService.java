package im.infmngmt.service;

import im.infmngmt.entity.Drug;
import im.infmngmt.entity.Prescription;
import im.infmngmt.repository.DrugRepository;
import im.infmngmt.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepo;
    private final DrugRepository drugRepository;

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepo.findAll();
    }

    public void cancelPrescription(Long prescriptionId) {
        prescriptionRepo.deleteById(prescriptionId);
    }

    public Prescription savePrescription(Prescription prescription) {
        return prescriptionRepo.save(prescription);
    }

    public void replaceDrug(Long prescriptionId, Long newDrugId, String reason) {
        Prescription prescription = prescriptionRepo.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Назначение не найдено"));
        Drug newDrug = drugRepository.findById(newDrugId)
                .orElseThrow(() -> new IllegalArgumentException("Препарат не найден"));

        prescription.setOriginalDrugId(prescription.getDrug().getId());
        prescription.setDrug(newDrug);
        prescription.setIsReplaced(true);
        prescription.setReplacementReason(reason);

        prescriptionRepo.save(prescription);
    }
}