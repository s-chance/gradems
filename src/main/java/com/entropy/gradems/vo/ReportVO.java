package com.entropy.gradems.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportVO {
    private String sId;
    private String cName;
    private Integer grade;
}
