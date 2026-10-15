package com.sandaru.blue_bird_vacation_planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dining_insights")
public class DiningInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @Column(name = "price_range")
    private String priceRange;

    @Column(name = "recommended_dishes", columnDefinition = "TEXT")
    private String recommendedDishes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Destination destination;

    @ElementCollection
    @CollectionTable(
            name = "dining_images",
            joinColumns = @JoinColumn(name = "dining_insight_id")
    )
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

}