package com.petgroomer.payload;

import lombok.Data;

@Data
public class JWTTokenDTO {

    private String type;

    private String token;
}
