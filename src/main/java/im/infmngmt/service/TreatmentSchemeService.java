package im.infmngmt.service;

import im.infmngmt.entity.TreatmentScheme;
import im.infmngmt.repository.TreatmentSchemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentSchemeService {
    private final TreatmentSchemeRepository schemeRepo;

    public List<TreatmentScheme> getAllSchemes() {
        return schemeRepo.findAll();
    }
}