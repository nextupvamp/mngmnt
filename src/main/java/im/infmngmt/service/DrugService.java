package im.infmngmt.service;

import im.infmngmt.entity.Drug;
import im.infmngmt.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrugService {
    private final DrugRepository repository;

    public List<Drug> getAllDrugs() {
        return repository.findAll();
    }

    public void saveDrug(Drug drug) {
        repository.save(drug);
    }
}