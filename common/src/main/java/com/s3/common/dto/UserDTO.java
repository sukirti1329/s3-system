package com.s3.common.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private String userId;
    private String userName;
    private String email;
    private String role;
}
