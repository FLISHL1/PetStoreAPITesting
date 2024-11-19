package ru.flish1.testtaskpetshop.entity;


import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private Integer code;
    private String type;
    private String message;
}
