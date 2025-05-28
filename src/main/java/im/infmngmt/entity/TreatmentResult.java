package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "treatment_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "scheme_id")
    private TreatmentScheme scheme;

    @Column(name = "evaluation_date", nullable = false)
    private LocalDate evaluationDate = LocalDate.now();

    @Column(nullable = false, length = 2000)
    private String results;

    @Enumerated(EnumType.STRING)
    private Effectiveness effectiveness;

    @Column(length = 2000)
    private String comments;

    public enum Effectiveness {
        EXCELLENT, GOOD, SATISFACTORY, UNSATISFACTORY
    }
}