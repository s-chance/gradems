package com.entropy.gradems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StuDepartDTO {
    private String sId;
    private String sName;
    private String dName;
    private Date startDate;
}
