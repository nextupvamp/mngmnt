package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "diagnosis_test_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisTestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private Diagnosis diagnosis;

    @Column(name = "test_name", nullable = false)
    private String testName;

    @Column(name = "result_value")
    private String resultValue;

    private String unit;

    @Column(name = "reference_range", length = 1000)
    private String referenceRange;

    @Column(name = "test_date")
    private LocalDate testDate;
}