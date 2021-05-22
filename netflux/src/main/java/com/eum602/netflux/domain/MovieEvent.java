package com.eum602.netflux.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor //all attributes to the constructor
public class MovieEvent {
    private String movieId;
    private Date movieData;
}
