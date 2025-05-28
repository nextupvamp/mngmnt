package im.infmngmt.service;

import im.infmngmt.entity.*;
import im.infmngmt.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentSchemeService {
    private final TreatmentSchemeRepository schemeRepository;
    private final TreatmentSchemeDrugRepository schemeDrugRepository;
    private final ControlPointRepository controlPointRepository;

    public List<TreatmentScheme> getAllSchemes() {
        return schemeRepository.findAll();
    }

    public TreatmentScheme saveScheme(TreatmentScheme scheme) {
        return schemeRepository.save(scheme);
    }

    public void deleteScheme(Long id) {
        schemeRepository.deleteById(id);
    }

    public List<TreatmentSchemeDrug> getDrugsInScheme(Long schemeId) {
        return schemeDrugRepository.findBySchemeId(schemeId);
    }

    @Transactional
    public void addDrugToScheme(TreatmentSchemeDrug schemeDrug) {
        schemeDrugRepository.save(schemeDrug);
    }

    @Transactional
    public void addControlPoint(ControlPoint controlPoint) {
        controlPointRepository.save(controlPoint);
    }

    public List<TreatmentScheme> getSchemesByDiagnosisId(Long diagnosisId) {
        return schemeRepository.findByDiagnosisId(diagnosisId);
    }

    public List<ControlPoint> getControlPointsBySchemeId(Long schemeId) {
        return controlPointRepository.findBySchemeId(schemeId);
    }
}