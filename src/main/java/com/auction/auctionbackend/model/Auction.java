package com.auction.auctionbackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "auctions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Auction {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "starting_price")
    private Double startingPrice;

    @Column(name = "buyout_price")
    private Double buyoutPrice;

    @Column(name = "current_price")
    private Double currentPrice;

    private String location;

    private String country;

    @Formula("(SELECT COUNT(*) FROM bids b WHERE b.auction_id = id)")
    private Long bidCount;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "auction_categories",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;
}
