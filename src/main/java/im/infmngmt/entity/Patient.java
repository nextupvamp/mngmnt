package im.infmngmt.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String passport;
    private String snils;
    private String address;

    @Column(name = "registration_address")
    private String registrationAddress;

    @Column(name = "social_group")
    private String socialGroup;

    private String workplace;
    private String position;

    @Column(length = 1000)
    private String contacts;

    @Column(length = 1000)
    private String notes;
}