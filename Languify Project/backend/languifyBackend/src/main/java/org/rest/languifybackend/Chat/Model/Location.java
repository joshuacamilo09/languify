package org.rest.languifybackend.Chat.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Location")
public class Location
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long location_id;

    @Column
    private String country;

    @Column
    private String city;

    @OneToOne
    private Chat chat;
}
