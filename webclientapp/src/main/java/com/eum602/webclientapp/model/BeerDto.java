package com.eum602.webclientapp.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto {
    @Null
    private UUID id;
    @NotBlank
    private String beerName;
    @NotBlank
    private String beerStyle;

    private String upc;
    private BigDecimal price;
    private Integer quantityOnHand;
    private OffsetDateTime createDate;
    private OffsetDateTime lastUpdateDate;
}
