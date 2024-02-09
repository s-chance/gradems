package com.entropy.gradems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDTO implements Serializable {
    private String username;
    private List<String> roles;
}
