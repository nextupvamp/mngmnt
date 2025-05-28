package im.infmngmt.service;

import im.infmngmt.entity.Drug;
import im.infmngmt.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrugService {
    private final DrugRepository drugRepository;

    public List<Drug> getAllDrugs() {
        return drugRepository.findAll();
    }

    public List<Drug> searchDrugs(String query) {
        return drugRepository.findByMnnContainingOrActiveSubstanceContainingOrManufacturerContaining(
                query, query, query);
    }

    public List<Drug> getDrugAnalogs(Long drugId) {
        // Реализация поиска аналогов
        return drugRepository.findAnalogsByDrugId(drugId);
    }

    public Drug saveDrug(Drug drug) {
        return drugRepository.save(drug);
    }

    public void deleteDrug(Long id) {
        drugRepository.deleteById(id);
    }
}