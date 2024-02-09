package com.entropy.gradems.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Serializable {
    @Id
    private String sId;
    private String sName;
    private String dId;
    private LocalDate startDate;
    private Integer sHour;
}
