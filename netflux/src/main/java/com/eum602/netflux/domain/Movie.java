package com.eum602.netflux.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document //-> tells mongo it is going to be a mongo document
@Data //@Data is a convenient shortcut annotation that bundles the features of @ToString, @EqualsAndHashCode, @Getter / @Setter and @RequiredArgsConstructor together: In other words, @Data generates all the boilerplate that is normally associated with simple POJOs (Plain Old Java Objects) and beans: getters for all fields, setters for all non-final fields, and appropriate toString, equals and hashCode implementations that involve the fields of the class,
@NoArgsConstructor //@NoArgsConstructor will generate a constructor with no parameters
@RequiredArgsConstructor //Generates a constructor with required arguments. Required arguments are uninitialized final fields and fields with constraints such as @NonNull
@Builder //using the Builder pattern without writing boilerplate code
public class Movie {
    private String id;

    @NonNull
    private String title;
}
