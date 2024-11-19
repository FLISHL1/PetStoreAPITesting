package ru.flish1.testtaskpetshop.entity;


import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {
    private String username;
    private String password;
}
