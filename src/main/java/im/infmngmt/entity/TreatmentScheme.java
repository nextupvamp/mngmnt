package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "treatment_scheme")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentScheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private Diagnosis diagnosis;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "treatment_type")
    private String treatmentType;

    @Column(name = "supervision_type")
    private String supervisionType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "expected_result", length = 2000)
    private String expectedResult;

    @Column(length = 2000)
    private String contraindications;

    @Column(length = 2000)
    private String exclusions;

    @Column(name = "side_effects", length = 2000)
    private String sideEffects;

    @Column(name = "age_group")
    private String ageGroup;

    @Column(name = "control_scheme", length = 2000)
    private String controlScheme;
}