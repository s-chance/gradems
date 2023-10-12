package com.entropy.gradems.service;

import com.entropy.gradems.dto.ReportDTO;
import com.entropy.gradems.dto.StuCouDTO;
import com.entropy.gradems.dto.StuDepartDTO;
import com.entropy.gradems.mapper.StuMapper;
import com.entropy.gradems.po.Course;
import com.entropy.gradems.po.Department;
import com.entropy.gradems.po.Report;
import com.entropy.gradems.po.Student;
import com.entropy.gradems.vo.ReportVO;
import com.entropy.gradems.vo.StuInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StuServiceImpl implements StuService {
    @Autowired
    private StuMapper stuMapper;

    @Override
    public List<Department> getAllDeparts() {
        return stuMapper.getAllDeparts();
    }

    @Override
    public List<Student> getAllStu() {
        return stuMapper.getAllStu();
    }

    @Override
    public List<Course> getAllCourses() {
        return stuMapper.getAllCourses();
    }

    @Override
    public List<StuCouDTO> getAllStuCou() {
        return stuMapper.getAllStuCou();
    }

    @Override
    public int insertDepart(String dId, String dName) {
        return stuMapper.insertDepart(dId, dName);
    }

    @Override
    public int deleteDepart(String dId) {
        return stuMapper.deleteDepart(dId);
    }

    @Override
    public int updateDepart(String dId, String dName) {
        return stuMapper.updateDepart(dId, dName);
    }

    @Override
    public List<StuDepartDTO> getAllStuDepart() {
        return stuMapper.getAllStuDepart();
    }

    @Override
    public int insertStu(Student student) {
        return stuMapper.insertStu(student);
    }

    @Override
    public int deleteStu(String sId) {
        return stuMapper.deleteStu(sId);
    }

    @Override
    public String getDId(String dName) {
        return stuMapper.getDId(dName);
    }

    @Override
    public int updateStu(Student student) {
        return stuMapper.updateStu(student);
    }

    @Override
    public int insertCourse(Course course) {
        return stuMapper.insertCourse(course);
    }

    @Override
    public int deleteCourse(String cId) {
        return stuMapper.deleteCourse(cId);
    }

    @Override
    public int updateCourse(Course course) {
        return stuMapper.updateCourse(course);
    }

    @Override
    public List<ReportDTO> getAllReport() {
        return stuMapper.getAllReport();
    }

    @Override
    public int insertReport(Report report) {
        return stuMapper.insertReport(report);
    }

    @Override
    public int deleteReport(String sId, String cId) {
        return stuMapper.deleteReport(sId, cId);
    }

    @Override
    public int updateReport(Report report) {
        return stuMapper.updateReport(report);
    }

    @Override
    public List<ReportVO> getReportBySId(String sId) {
        return stuMapper.getReportBySId(sId);
    }

    @Override
    public List<StuInfoVO> getAllStuInfo() {
        return stuMapper.getAllStuInfo();
    }

    @Override
    public int deleteReportBySId(String sId) {
        return stuMapper.deleteReportBySId(sId);
    }

}
