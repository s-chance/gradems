package com.entropy.gradems.controller;

import com.entropy.gradems.dto.CustomResponse;
import com.entropy.gradems.dto.ReportDTO;
import com.entropy.gradems.dto.StuCouDTO;
import com.entropy.gradems.dto.StuDepartDTO;
import com.entropy.gradems.po.Course;
import com.entropy.gradems.po.Department;
import com.entropy.gradems.po.Report;
import com.entropy.gradems.po.Student;
import com.entropy.gradems.service.StuService;
import com.entropy.gradems.util.HttpStatus;
import com.entropy.gradems.vo.ReportVO;
import com.entropy.gradems.vo.StuInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.entropy.gradems.dto.CustomResponse.getIntegerCustomResponse;

@RestController
public class StuController {

    @Autowired
    private StuService stuService;

    @GetMapping("/getAllDeparts")
    List<Department> getAllDeparts() {
        return stuService.getAllDeparts();
    }

    @GetMapping("/getAllStu")
    List<Student> getAllStu() {
        return stuService.getAllStu();
    }

    @GetMapping("/getAllCourses")
    List<Course> getAllCourses() {
        return stuService.getAllCourses();
    }

    @GetMapping("/getAllStuCou")
    List<StuCouDTO> getAllStuCou() {
        return stuService.getAllStuCou();
    }


    @PostMapping("/insertDepart")
    CustomResponse<Integer> insertDepart(@RequestBody Department depart) {
        int result = stuService.insertDepart(depart.getDId(), depart.getDName());
        return getIntegerCustomResponse(result);
    }

    @DeleteMapping("/deleteDepart/{dId}")
    CustomResponse<Integer> deleteDepart(@PathVariable String dId) {
        int result = stuService.deleteDepart(dId);
        return getIntegerCustomResponse(result);
    }

    @PatchMapping("/updateDepart/{dId}")
    CustomResponse<Integer> updateDepart(@PathVariable String dId, @RequestBody Department depart) {
        int result = stuService.updateDepart(dId, depart.getDName());
        return getIntegerCustomResponse(result);
    }

    @GetMapping("/getAllStuDepart")
    List<StuDepartDTO> getAllStuDepart() {
        return stuService.getAllStuDepart();
    }

    @PostMapping("/insertStu")
    CustomResponse<Integer> insertStu(@RequestBody Student student) {
        int result = stuService.insertStu(student);
        return getIntegerCustomResponse(result);
    }

    @DeleteMapping("/deleteStu/{sId}")
    CustomResponse<Integer> deleteStu(@PathVariable String sId) {
        int result = stuService.deleteStu(sId);
        return getIntegerCustomResponse(result);
    }

    @GetMapping("/getDId")
    String getDId(@RequestBody Map<String, String> data) {
        return stuService.getDId(data.get("dname"));
    }

    @PutMapping("/updateStu/{sId}")
    CustomResponse<Integer> updateStu(@PathVariable String sId, @RequestBody Student student) {
        int result = stuService.updateStu(student);
        return getIntegerCustomResponse(result);
    }

    @PostMapping("/insertCourse")
    CustomResponse<Integer> insertCourse(@RequestBody Course course) {
        int result = stuService.insertCourse(course);
        return getIntegerCustomResponse(result);
    }

    @DeleteMapping("/deleteCourse/{cId}")
    CustomResponse<Integer> deleteCourse(@PathVariable String cId) {
        int result = stuService.deleteCourse(cId);
        return getIntegerCustomResponse(result);
    }

    @PutMapping("/updateCourse/{cId}")
    CustomResponse<Integer> updateCourse(@PathVariable String cId, @RequestBody Course course) {
        int result = stuService.updateCourse(course);
        return getIntegerCustomResponse(result);
    }

    @GetMapping("/getAllReport")
    List<ReportDTO> getAllReport() {
        return stuService.getAllReport();
    }

    @PostMapping("/insertReport")
    CustomResponse<Integer> insertReport(@RequestBody Report report) {
        int result = stuService.insertReport(report);
        return getIntegerCustomResponse(result);
    }

    @DeleteMapping("/deleteReport/{sId}/{cId}")
    CustomResponse<Integer> deleteReport(@PathVariable String sId, @PathVariable String cId) {
        int result = stuService.deleteReport(sId, cId);
        return getIntegerCustomResponse(result);
    }

    @PutMapping("/updateReport/{sId}/{cId}")
    CustomResponse<Integer> updateReport(@PathVariable String sId, @PathVariable String cId, @RequestBody Report report) {
        int result = stuService.updateReport(report);
        return getIntegerCustomResponse(result);
    }

    @GetMapping("/getReportBySId/{sId}")
    List<ReportVO> getReportBySId(@PathVariable String sId) {
        return stuService.getReportBySId(sId);
    }

    @GetMapping("/getAllStuInfo")
    List<StuInfoVO> getAllStuInfo() {
        return stuService.getAllStuInfo();
    }

    @DeleteMapping("/deleteReportBySId/{sId}")
    CustomResponse<Integer> deleteReportBySId(@PathVariable String sId) {
        int result = stuService.deleteReportBySId(sId);
        return getIntegerCustomResponse(result);
    }


}


