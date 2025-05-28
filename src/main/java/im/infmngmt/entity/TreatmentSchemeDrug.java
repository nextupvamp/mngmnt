package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "treatment_scheme_drug")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentSchemeDrug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scheme_id", nullable = false)
    private TreatmentScheme scheme;

    @ManyToOne
    @JoinColumn(name = "drug_id")
    private Drug drug;

    @Column(name = "frequency_per_day", nullable = false)
    private Integer frequencyPerDay;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "administration_scheme", nullable = false, length = 1000)
    private String administrationScheme;
}