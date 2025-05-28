package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "patient_chronic_disease")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientChronicDisease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "disease_name", nullable = false)
    private String diseaseName;

    private String severity;

    @Column(name = "diagnosis_date")
    private LocalDate diagnosisDate;
}