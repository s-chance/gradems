package com.entropy.gradems.service;

import com.entropy.gradems.dto.ReportDTO;
import com.entropy.gradems.dto.StuCouDTO;
import com.entropy.gradems.dto.StuDepartDTO;
import com.entropy.gradems.dto.UserRoleDTO;
import com.entropy.gradems.po.Course;
import com.entropy.gradems.po.Department;
import com.entropy.gradems.po.Report;
import com.entropy.gradems.po.Student;
import com.entropy.gradems.vo.ReportVO;
import com.entropy.gradems.vo.StuInfoVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StuService {

    Mono<String> login(String username, String rawPassword);

    Mono<String> register(UserRoleDTO userRoleDTO, String rawPassword);

    Flux<Department> getAllDeparts();

    Flux<Student> getAllStu();

    Flux<Course> getAllCourses();

    Flux<StuCouDTO> getAllStuCou();

    Mono<Long> insertDepart(String dId, String dName);

    Mono<Long> deleteDepart(String dId);

    Mono<Long> updateDepart(String dId, String dName);

    Flux<StuDepartDTO> getAllStuDepart();

    Mono<Long> insertStu(Student student);

    Mono<Long> deleteStu(String sId);

    Mono<String> getDId(String dName);

    Mono<Long> updateStu(Student student);

    Mono<Long> insertCourse(Course course);

    Mono<Long> deleteCourse(String cId);

    Mono<Long> updateCourse(Course course);

    Flux<ReportDTO> getAllReport();

    Mono<Long> insertReport(Report report);

    Mono<Long> deleteReport(String sId, String cId);

    Mono<Long> updateReport(Report report);

    Flux<ReportVO> getReportBySId(String sId);

    Flux<StuInfoVO> getAllStuInfo();

    Mono<Long> deleteReportBySId(String sId);

//    Mono<Void> deleteGraduate(Date date, Integer sHour);

}
