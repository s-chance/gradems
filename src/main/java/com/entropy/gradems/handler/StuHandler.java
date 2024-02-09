package com.entropy.gradems.handler;

import com.entropy.gradems.dto.*;
import com.entropy.gradems.jwt.JwtUtil;
import com.entropy.gradems.po.*;
import com.entropy.gradems.service.StuService;
import com.entropy.gradems.vo.ReportVO;
import com.entropy.gradems.vo.StuInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class StuHandler {

    private final JwtUtil jwtUtil;
    private final StuService stuService;

    @Autowired
    public StuHandler(JwtUtil jwtUtil, StuService stuService) {
        this.jwtUtil = jwtUtil;
        this.stuService = stuService;
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(login ->
                        stuService.login(login.getUsername(), login.getPassword())
                )
                .flatMap(token ->
                        ServerResponse.ok()
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // 将生成的Token放入响应头
                                .bodyValue(new ApiResponse<>(200, token))
                )
                .switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }

    public Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(RegisterRequest.class)
                .flatMap(register -> {
                    String username = register.getUsername();
                    String password = register.getPassword();
                    List<String> roles = register.getRoles();
                    UserRoleDTO userRoleDTO = new UserRoleDTO(username, roles);
                    return stuService.register(userRoleDTO, password)
                            .flatMap(token -> {
                                        if ("User already exists".equals(token)) {
                                            return ServerResponse.status(HttpStatus.CONFLICT)
                                                    .bodyValue(new ApiResponse<>(409, token));
                                        }
                                        return ServerResponse.ok()
                                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // 将生成的Token放入响应头
                                                .bodyValue(new ApiResponse<>(200, token));
                                    }
                            );
                })
                .switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }

    public Mono<ServerResponse> getAllDeparts(ServerRequest request) {
        Flux<Department> departments = stuService.getAllDeparts();
        Mono<ApiResponse<List<Department>>> response = departments.collectList().map(data -> new ApiResponse<>(200, data));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(response, ApiResponse.class);
    }

    public Mono<ServerResponse> getAllStu(ServerRequest request) {
        Flux<Student> students = stuService.getAllStu();
        Mono<ApiResponse<List<Student>>> response = students.collectList().map(data -> new ApiResponse<>(200, data));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(response, ApiResponse.class);
    }

    public Mono<ServerResponse> getAllCourses(ServerRequest request) {
        Flux<Course> courses = stuService.getAllCourses();
        Mono<ApiResponse<List<Course>>> response = courses.collectList().map(data -> new ApiResponse<>(200, data));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(response, ApiResponse.class);
    }

    public Mono<ServerResponse> getAllStuCou(ServerRequest request) {
        Flux<StuCouDTO> stuCou = stuService.getAllStuCou();
        Mono<ApiResponse<List<StuCouDTO>>> response = stuCou.collectList().map(data -> new ApiResponse<>(200, data));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(response, ApiResponse.class);
    }

    public Mono<ServerResponse> insertDepart(ServerRequest request) {
        Mono<Department> departmentMono = request.bodyToMono(Department.class);
        return departmentMono.flatMap(depart -> stuService.insertDepart(depart.getDId(), depart.getDName()))
                .flatMap(depart -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, depart)));
    }

    public Mono<ServerResponse> deleteDepart(ServerRequest request) {
        String dId = request.pathVariable("dId");
        return stuService.deleteDepart(dId)
                .flatMap(depart -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, depart)));
    }

    public Mono<ServerResponse> updateDepart(ServerRequest request) {
        String dId = request.pathVariable("dId");
        Mono<Department> departmentMono = request.bodyToMono(Department.class);
        return departmentMono.flatMap(depart -> stuService.updateDepart(dId, depart.getDName()))
                .flatMap(depart -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, depart)));
    }

    public Mono<ServerResponse> getAllStuDepart(ServerRequest request) {
        Flux<StuDepartDTO> allStuDepart = stuService.getAllStuDepart();
        Mono<ApiResponse<List<StuDepartDTO>>> response = allStuDepart.collectList().map(data -> new ApiResponse<>(200, data));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(response, ApiResponse.class);
    }

    public Mono<ServerResponse> insertStu(ServerRequest request) {
        Mono<Student> studentMono = request.bodyToMono(Student.class);
        return studentMono.flatMap(stuService::insertStu)
                .flatMap(stu -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, stu)));
    }

    public Mono<ServerResponse> deleteStu(ServerRequest request) {
        String sId = request.pathVariable("sId");
        return stuService.deleteStu(sId)
                .flatMap(stu -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, stu)));
    }

    public Mono<ServerResponse> getDId(ServerRequest request) {
        String dname = request.queryParam("dname").orElse("");
        return stuService.getDId(dname)
                .flatMap(dId -> ServerResponse.ok().bodyValue(dId));
    }

    public Mono<ServerResponse> updateStu(ServerRequest request) {
        String sId = request.pathVariable("sId");
        Mono<Student> studentMono = request.bodyToMono(Student.class);
        return studentMono.flatMap(stuService::updateStu)
                .flatMap(stu -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, stu)));
    }

    public Mono<ServerResponse> insertCourse(ServerRequest request) {
        Mono<Course> courseMono = request.bodyToMono(Course.class);
        return courseMono.flatMap(stuService::insertCourse)
                .flatMap(cou -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, cou)));
    }

    public Mono<ServerResponse> deleteCourse(ServerRequest request) {
        String cId = request.pathVariable("cId");
        return stuService.deleteCourse(cId)
                .flatMap(cou -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, cou)));
    }

    public Mono<ServerResponse> updateCourse(ServerRequest request) {
        String cId = request.pathVariable("cId");
        Mono<Course> courseMono = request.bodyToMono(Course.class);
        return courseMono.flatMap(stuService::updateCourse)
                .flatMap(cou -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, cou)));
    }

    public Mono<ServerResponse> getAllReport(ServerRequest request) {
        Flux<ReportDTO> allReport = stuService.getAllReport();
        Mono<ApiResponse<List<ReportDTO>>> response = allReport.collectList().map(data -> new ApiResponse<>(200, data));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(response, ApiResponse.class);
    }

    public Mono<ServerResponse> insertReport(ServerRequest request) {
        Mono<Report> reportMono = request.bodyToMono(Report.class);
        return reportMono.flatMap(stuService::insertReport)
                .flatMap(report -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, report)));
    }

    public Mono<ServerResponse> deleteReport(ServerRequest request) {
        String sId = request.pathVariable("sId");
        String cId = request.pathVariable("cId");
        return stuService.deleteReport(sId, cId)
                .flatMap(report -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, report)));
    }

    public Mono<ServerResponse> updateReport(ServerRequest request) {
        String sId = request.pathVariable("sId");
        String cId = request.pathVariable("cId");
        Mono<Report> reportMono = request.bodyToMono(Report.class);
        return reportMono.flatMap(stuService::updateReport)
                .flatMap(report -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, report)));
    }

    public Mono<ServerResponse> getReportBySId(ServerRequest request) {
        String sId = request.pathVariable("sId");
        Flux<ReportVO> reportBySId = stuService.getReportBySId(sId);
        Mono<ApiResponse<List<ReportVO>>> response = reportBySId.collectList().map(data -> new ApiResponse<>(200, data));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(response, ApiResponse.class);
    }

    public Mono<ServerResponse> getAllStuInfo(ServerRequest request) {
        Flux<StuInfoVO> allStuInfo = stuService.getAllStuInfo();
        Mono<ApiResponse<List<StuInfoVO>>> response = allStuInfo.collectList().map(data -> new ApiResponse<>(200, data));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(response, ApiResponse.class);
    }

    public Mono<ServerResponse> deleteReportBySId(ServerRequest request) {
        String sId = request.pathVariable("sId");
        return stuService.deleteReportBySId(sId)
                .flatMap(report -> ServerResponse.ok().bodyValue(new ApiResponse<>(200, report)));
    }
}
