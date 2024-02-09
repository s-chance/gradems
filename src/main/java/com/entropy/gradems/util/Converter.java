package com.entropy.gradems.util;

import com.entropy.gradems.dto.ReportDTO;
import com.entropy.gradems.vo.ReportVO;

public class Converter {

        public static ReportVO reportDTO2VO(ReportDTO dto) {
            ReportVO vo = new ReportVO();
            vo.setSId(dto.getSId());
            vo.setCName(dto.getCName());
            vo.setGrade(dto.getGrade());
            return vo;
        }
}
