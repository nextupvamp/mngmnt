package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "patient_drug_intolerance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDrugIntolerance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "drug_mnn", nullable = false)
    private String drugMnn;

    @Column(name = "reaction_description", length = 1000)
    private String reactionDescription;
}