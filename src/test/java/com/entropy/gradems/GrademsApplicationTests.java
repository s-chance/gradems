package com.entropy.gradems;

import com.entropy.gradems.dto.ReportDTO;
import com.entropy.gradems.mapper.StuMapper;
import com.entropy.gradems.po.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest
class GrademsApplicationTests {

    @Autowired
    private StuMapper studentMapper;

    @Test
    void contextLoads() throws ParseException {
        List<ReportDTO> allReport = studentMapper.getAllReport();
        for (ReportDTO reportDTO : allReport) {
            System.out.println(reportDTO);
        }


//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        studentMapper.deleteGraduate(format.parse("2016-09-01"), 120);
    }

}
