package com.entropy.gradems.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    private String sId;
    private String cId;
    private Integer grade;
}
