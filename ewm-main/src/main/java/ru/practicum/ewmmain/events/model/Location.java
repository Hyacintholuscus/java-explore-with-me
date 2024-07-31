package ru.practicum.ewmmain.events.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Pattern;

@Value
@Builder
public class Location {
    @Pattern(regexp = "^-?([0-8]?\\d(\\.\\d{1,6})?|90)$",
            message = "Latitude of location should be between -90 and 90")
    Double lat;
    @Pattern(regexp = "^-?(?:1(?:[0-7]\\d(\\.\\d{1,6})?|80)|([1-9]?\\d(\\.\\d{1,6})?))$",
            message = "Longitude of location should be between -180 and 180")
    Double lon;
}
