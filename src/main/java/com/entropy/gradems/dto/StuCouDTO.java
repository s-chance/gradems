package com.entropy.gradems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StuCouDTO implements Serializable {
    private String sId;
    private String sName;
    private String cId;
    private String cName;
}
