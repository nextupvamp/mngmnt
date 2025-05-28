package im.infmngmt.ui;

import im.infmngmt.service.*;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class MainAppFrame extends JFrame {
    public MainAppFrame(PatientService patientService, DiagnosisService diagnosisService,
                        DrugService drugService, TreatmentSchemeService treatmentSchemeService,
                        PrescriptionService prescriptionService) {
        setTitle("Медицинская система");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Пациенты", new PatientPanel(patientService));
        tabs.addTab("Диагнозы", new DiagnosisPanel(diagnosisService, patientService, treatmentSchemeService));
        tabs.addTab("Лекарства", new DrugPanel(drugService));
        tabs.addTab("Схемы лечения", new TreatmentSchemePanel(treatmentSchemeService, diagnosisService, drugService));
        tabs.addTab("Назначения", new PrescriptionPanel(prescriptionService, patientService, drugService));

        add(tabs);
    }
}