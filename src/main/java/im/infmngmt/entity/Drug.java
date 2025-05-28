package im.infmngmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "drug")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Drug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mnn;

    @Column(name = "active_substance", nullable = false)
    private String activeSubstance;

    @Column(name = "dosage_form", nullable = false)
    private String dosageForm;

    private String packaging;
    private String dosage;

    @Column(name = "commercial_name", nullable = false)
    private String commercialName;

    @Column(name = "min_price")
    private BigDecimal minPrice;

    @Column(name = "max_price")
    private BigDecimal maxPrice;

    @Column(name = "price_category")
    private String priceCategory;

    private String manufacturer;
    private String country;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    private Boolean available = true;

    @Column(name = "side_effects", length = 1000)
    private String sideEffects;

    @Column(length = 1000)
    private String contraindications;

    @Column(length = 1000)
    private String interactions;

    @Column(length = 1000)
    private String incompatibility;
}