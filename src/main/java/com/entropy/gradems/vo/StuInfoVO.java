package com.entropy.gradems.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StuInfoVO implements Serializable {
    private String sId;
    private String sName;
    private String dName;
    private LocalDate startDate;
    private Integer sHour;
}
