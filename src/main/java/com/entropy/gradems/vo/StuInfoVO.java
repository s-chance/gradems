package com.entropy.gradems.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StuInfoVO {
    private String sId;
    private String sName;
    private String dName;
    private Date startDate;
    private String sHour;
}
