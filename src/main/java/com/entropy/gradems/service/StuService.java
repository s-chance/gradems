package com.entropy.gradems.service;

import com.entropy.gradems.dto.ReportDTO;
import com.entropy.gradems.dto.StuCouDTO;
import com.entropy.gradems.dto.StuDepartDTO;
import com.entropy.gradems.po.Course;
import com.entropy.gradems.po.Department;
import com.entropy.gradems.po.Report;
import com.entropy.gradems.po.Student;
import com.entropy.gradems.vo.ReportVO;
import com.entropy.gradems.vo.StuInfoVO;

import java.util.List;

public interface StuService {
    List<Department> getAllDeparts();

    List<Student> getAllStu();

    List<Course> getAllCourses();

    List<StuCouDTO> getAllStuCou();

    int insertDepart(String dId, String dName);

    int deleteDepart(String dId);

    int updateDepart(String dId, String dName);

    List<StuDepartDTO> getAllStuDepart();

    int insertStu(Student student);

    int deleteStu(String sId);

    String getDId(String dName);

    int updateStu(Student student);

    int insertCourse(Course course);

    int deleteCourse(String cId);

    int updateCourse(Course course);

    List<ReportDTO> getAllReport();

    int insertReport(Report report);

    int deleteReport(String sId, String cId);

    int updateReport(Report report);

    List<ReportVO> getReportBySId(String sId);

    List<StuInfoVO> getAllStuInfo();

    int deleteReportBySId(String sId);

}
