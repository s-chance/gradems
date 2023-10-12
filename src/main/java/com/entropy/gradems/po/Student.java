package com.entropy.gradems.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private String sId;
    private String sName;
    private String dId;
    private Date startDate;
    private Integer sHour;
}
