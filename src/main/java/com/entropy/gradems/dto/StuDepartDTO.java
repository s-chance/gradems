package com.entropy.gradems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StuDepartDTO implements Serializable {
    private String sId;
    private String sName;
    private String dName;
    private LocalDate startDate;
}
