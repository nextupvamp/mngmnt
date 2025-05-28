package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "control_point")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scheme_id", nullable = false)
    private TreatmentScheme scheme;

    @Column(nullable = false)
    private String name;

    @Column(name = "control_date", nullable = false)
    private LocalDate controlDate;

    @Column(nullable = false)
    private String controller;

    @Column(length = 1000)
    private String method;

    @Column(length = 1000)
    private String result;

    private String evaluation;
}