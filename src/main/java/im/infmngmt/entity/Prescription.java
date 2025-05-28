package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "prescription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "drug_id")
    private Drug drug;

    @ManyToOne
    @JoinColumn(name = "scheme_id")
    private TreatmentScheme scheme;

    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate = LocalDate.now();

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String dosage;

    @Column(name = "frequency_per_day", nullable = false)
    private Integer frequencyPerDay;

    @Column(name = "administration_instructions", nullable = false, length = 1000)
    private String administrationInstructions;

    @Column(name = "is_replaced")
    private Boolean isReplaced = false;

    @Column(name = "original_drug_id")
    private Long originalDrugId;

    @Column(name = "replacement_reason", length = 1000)
    private String replacementReason;
}