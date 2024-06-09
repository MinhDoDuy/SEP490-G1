package com.ffood.g1.dto;


import com.ffood.g1.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserDTO extends User {
    private Integer userId;
    private String userName;
    private String password;
    private String email;
    private String phone;

}
