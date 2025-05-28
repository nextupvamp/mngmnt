package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drug_analog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugAnalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @ManyToOne
    @JoinColumn(name = "analog_id", nullable = false)
    private Drug analog;
}