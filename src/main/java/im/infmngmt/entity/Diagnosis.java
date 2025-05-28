package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "diagnosis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "icd10_code")
    private String icd10Code;

    private String description;

    @Column(name = "diagnosis_date")
    private LocalDate diagnosisDate;

    @Column(length = 2000)
    private String anamnesis;

    @Column(name = "patient_condition")
    private String patientCondition;
}