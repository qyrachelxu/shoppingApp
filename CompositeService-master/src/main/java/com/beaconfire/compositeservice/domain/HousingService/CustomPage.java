package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomPage<T> {
    private int number;         // current page number
    private int size;           // page size
    private int totalElements;  // total number of elements
    private int totalPages;     // total number of pages
    private List<T> content;    // list of elements
}
