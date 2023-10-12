package com.entropy.gradems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    private String sId;
    private String sName;
    private String cId;
    private String cName;
    private Integer grade;
}
