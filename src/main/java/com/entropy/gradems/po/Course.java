package com.entropy.gradems.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private String cId;
    private String cName;
    private Integer cHour;
}
