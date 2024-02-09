package com.entropy.gradems.service;

import com.entropy.gradems.dto.ReportDTO;
import com.entropy.gradems.dto.StuCouDTO;
import com.entropy.gradems.dto.StuDepartDTO;
import com.entropy.gradems.dto.UserRoleDTO;
import com.entropy.gradems.jwt.JwtUtil;
import com.entropy.gradems.po.*;
import com.entropy.gradems.repository.*;
import com.entropy.gradems.util.Converter;
import com.entropy.gradems.vo.ReportVO;
import com.entropy.gradems.vo.StuInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class StuServiceImpl implements StuService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final StuRepo stuRepo;
    private final DepartMongoRepo departMongoRepo;
    private final StuMongoRepo stuMongoRepo;
    private final CouMongoRepo couMongoRepo;
    private final ReportMongoRepo reportMongoRepo;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Autowired
    public StuServiceImpl(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, StuRepo stuRepo, DepartMongoRepo departMongoRepo, StuMongoRepo stuMongodbRepo, CouMongoRepo couMongoRepo, ReportMongoRepo reportMongoRepo, ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.stuRepo = stuRepo;
        this.departMongoRepo = departMongoRepo;
        this.stuMongoRepo = stuMongodbRepo;
        this.couMongoRepo = couMongoRepo;
        this.reportMongoRepo = reportMongoRepo;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }


    @Override
    public Mono<String> login(String username, String rawPassword) {
        return stuRepo.findByUsername(username)
                .flatMap(user -> {
                    if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                        return stuRepo.getUserRole(username)
                                .flatMap(userRole -> Mono.fromSupplier(() ->
                                        jwtUtil.generateToken(username, userRole.getRoles()))
                                );
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Mono<String> register(UserRoleDTO userRoleDTO, String rawPassword) {
        return stuRepo.findByUsername(userRoleDTO.getUsername())
                .flatMap(user -> Mono.just("User already exists"))
                .switchIfEmpty(Mono.defer(() -> {
                    String password = passwordEncoder.encode(rawPassword);
                    // id为null，由数据库自动生成
                    User user = new User(null, userRoleDTO.getUsername(), password);
                    return stuRepo.insertUser(user)
                            .flatMap(userId -> stuRepo.insertUserRole(userId, userRoleDTO)
                                    .thenReturn(jwtUtil.generateToken(userRoleDTO.getUsername(), userRoleDTO.getRoles())));
                }));
    }

    @Override
    public Flux<Department> getAllDeparts() {
        String cacheKey = "allDeparts";
//        return reactiveRedisTemplate.opsForValue().get(cacheKey)
//                .flatMapMany(data -> {
//                    if (data instanceof List) {
//                        List<Department> departments = (List<Department>) data;
//                        return Flux.fromIterable(departments);
//                    }
//                    return Flux.empty();
//                }) // 将List转换为Flux
//                .switchIfEmpty(Flux.defer(() ->
//                        // 如果缓存为空，从数据库获取
//                        departMongoRepo.findAll()
//                                .collectList() // 收集所有Department为List
//                                .flatMap(departs ->
//                                        reactiveRedisTemplate.opsForValue().set(cacheKey, departs)
//                                                .thenReturn(departs)) // 保存到Redis
//                                .flatMapMany(Flux::fromIterable) // 再次转换为Flux以返回
//                ))
//                .switchIfEmpty(Flux.defer(() ->
//                        stuRepo.getAllDeparts()
//                                .collectList()
//                                .flatMap(departs ->
//                                        departMongoRepo.saveAll(departs)
//                                                .collectList()
//                                                .flatMap(departments ->
//                                                        reactiveRedisTemplate.opsForValue().set(cacheKey, departments)
//                                                                .thenReturn(departments)
//                                                )
//                                )
//                                .flatMapMany(Flux::fromIterable)
//                ));
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .cast(List.class)
                .flatMapMany(Flux::fromIterable) // 尝试从Redis获取并反序列化
                .switchIfEmpty(Flux.defer(() ->
                        departMongoRepo.findAll()
                                .collectList()
                                .flatMapMany(data -> {
                                    if (!data.isEmpty()) {
                                        // 如果MongoDB中有数据，更新Redis缓存并返回数据
                                        return reactiveRedisTemplate.opsForValue().set(cacheKey, data) // 确保此方法处理序列化
                                                .thenMany(Flux.fromIterable(data));
                                    } else {
                                        // 如果MongoDB中没有数据，从PostgreSQL获取，保存到MongoDB，更新Redis缓存
                                        return stuRepo.getAllDeparts()
                                                .collectList()
                                                .flatMapMany(departs -> Flux.fromIterable(departs)
                                                        .flatMap(departMongoRepo::save) // 假设save方法返回保存的对象
                                                        .collectList())
                                                .flatMap(departs -> reactiveRedisTemplate.opsForValue().set(cacheKey, departs)
                                                        .thenMany(Flux.fromIterable(departs)));
                                    }
                                }))
                );
    }

    @Override
    public Flux<Student> getAllStu() {
        String cacheKey = "allStu";
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .flatMapMany(data -> {
                    if (data instanceof List) {
                        List<Student> students = (List<Student>) data;
                        return  Flux.fromIterable(students);
                    }
                    return Flux.empty();
                })
                .switchIfEmpty(Flux.defer(() ->
                        stuMongoRepo.findAll()
                                .collectList()
                                .flatMap(students ->
                                        reactiveRedisTemplate.opsForValue().set(cacheKey, students)
                                                .thenReturn(students))
                                .flatMapMany(Flux::fromIterable)
                        ))
                .switchIfEmpty(Flux.defer(() ->
                        stuRepo.getAllStu()
                                .collectList()
                                .flatMap(stu ->
                                        stuMongoRepo.saveAll(stu)
                                                .collectList()
                                                .flatMap(students ->
                                                        reactiveRedisTemplate.opsForValue().set(cacheKey, students)
                                                                .thenReturn(students)
                                                )
                                )
                                .flatMapMany(Flux::fromIterable)
                ));
    }

    @Override
    public Flux<Course> getAllCourses() {
        String cacheKey = "allCou";
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .flatMapMany(data -> {
                    if (data instanceof List) {
                        List<Course> courses = (List<Course>) data;
                        return Flux.fromIterable(courses);
                    }
                    return Flux.empty();
                })
                .switchIfEmpty(Flux.defer(() ->
                        couMongoRepo.findAll()
                                .collectList()
                                .flatMap(cou ->
                                        reactiveRedisTemplate.opsForValue().set(cacheKey, cou)
                                                .thenReturn(cou))
                                .flatMapMany(Flux::fromIterable)
                ))
                .switchIfEmpty(Flux.defer(() ->
                        stuRepo.getAllCourses()
                                .collectList()
                                .flatMap(cou ->
                                        couMongoRepo.saveAll(cou)
                                                .collectList()
                                                .flatMap(courses ->
                                                        reactiveRedisTemplate.opsForValue().set(cacheKey, courses)
                                                                .thenReturn(courses)
                                                )
                                )
                                .flatMapMany(Flux::fromIterable)
                ));
    }

    @Override
    public Flux<StuCouDTO> getAllStuCou() {
        String cacheKey = "AllStuCou";
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .flatMapMany(data -> {
                    if (data instanceof List) {
                        List<StuCouDTO> stuCou = (List<StuCouDTO>) data;
                        return Flux.fromIterable(stuCou);
                    }
                    return Flux.empty();
                })
                .switchIfEmpty(Flux.defer(() ->
                        stuRepo.getAllStuCou()
                                .collectList()
                                .flatMap(stuCou ->
                                        reactiveRedisTemplate.opsForValue().set(cacheKey, stuCou)
                                                .thenReturn(stuCou)
                                )
                                .flatMapMany(Flux::fromIterable)
                ));
    }

    @Override
    public Mono<Long> insertDepart(String dId, String dName) {
        String cacheKey = "allDeparts";
        Department department = new Department(dId, dName);
        return stuRepo.insertDepart(dId, dName)
                .flatMap(res ->
                        departMongoRepo.save(department)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Mono<Long> deleteDepart(String dId) {
        String cacheKey = "allDeparts";
        return stuRepo.deleteDepart(dId)
                .flatMap(res ->
                        departMongoRepo.deleteById(dId)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Mono<Long> updateDepart(String dId, String dName) {
        String cacheKey = "allDeparts";
        Department department = new Department(dId, dName);
        return stuRepo.updateDepart(dId, dName)
                .flatMap(res ->
                        departMongoRepo.save(department)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Flux<StuDepartDTO> getAllStuDepart() {
        String cacheKey = "AllStuDepart";
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .flatMapMany(data -> {
                    if (data instanceof List) {
                        List<StuDepartDTO> stuDepart = (List<StuDepartDTO>) data;
                        return Flux.fromIterable(stuDepart);
                    }
                    return Flux.empty();
                })
                .switchIfEmpty(Flux.defer(() ->
                        stuRepo.getAllStuDepart()
                                .collectList()
                                .flatMap(stuDepart ->
                                        reactiveRedisTemplate.opsForValue().set(cacheKey, stuDepart)
                                                .thenReturn(stuDepart)
                                )
                                .flatMapMany(Flux::fromIterable)
                ));
    }

    @Override
    public Mono<Long> insertStu(Student student) {
        String cacheKey = "allStu";
        return stuRepo.insertStu(student)
                .flatMap(res ->
                        stuMongoRepo.save(student)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Mono<Long> deleteStu(String sId) {
        String cacheKey = "allStu";
        return stuRepo.deleteStu(sId)
                .flatMap(res ->
                        stuMongoRepo.deleteById(sId)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Mono<String> getDId(String dName) {
        return stuRepo.getDId(dName);
    }

    @Override
    public Mono<Long> updateStu(Student student) {
        String cacheKey = "allStu";
        return stuRepo.updateStu(student)
                .flatMap(res ->
                        stuMongoRepo.save(student)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Mono<Long> insertCourse(Course course) {
        String cacheKey = "allCou";
        return stuRepo.insertCourse(course)
                .flatMap(res ->
                        couMongoRepo.save(course)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Mono<Long> deleteCourse(String cId) {
        String cacheKey = "allCou";
        return stuRepo.deleteCourse(cId)
                .flatMap(res ->
                        couMongoRepo.deleteById(cId)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Mono<Long> updateCourse(Course course) {
        String cacheKey = "allCou";
        return stuRepo.updateCourse(course)
                .flatMap(res ->
                        couMongoRepo.save(course)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Flux<ReportDTO> getAllReport() {
        String cacheKey = "allReport";
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .flatMapMany(data -> {
                    if (data instanceof List) {
                        List<ReportDTO> reports = (List<ReportDTO>) data;
                        return Flux.fromIterable(reports);
                    }
                    return Flux.empty();
                })
                .switchIfEmpty(Flux.defer(() ->
                        stuRepo.getAllReport()
                                .collectList()
                                .flatMap(report ->
                                        reactiveRedisTemplate.opsForValue().set(cacheKey, report)
                                                .thenReturn(report)
                                )
                                .flatMapMany(Flux::fromIterable)
                ));
    }

    @Override
    public Mono<Long> insertReport(Report report) {
        String cacheKey = "allReport";
        return stuRepo.insertReport(report)
                .flatMap(res ->
                        reportMongoRepo.save(report)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Mono<Long> deleteReport(String sId, String cId) {
        String cacheKey = "allReport";
        Query query = new Query(Criteria.where("sId").is(sId).and("cId").is(cId));
        return stuRepo.deleteReport(sId, cId)
                .flatMap(res ->
                        reactiveMongoTemplate.remove(query, Report.class)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Mono<Long> updateReport(Report report) {
        String cacheKey = "allReport";
        return stuRepo.updateReport(report)
                .flatMap(res ->
                        reportMongoRepo.save(report)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

    @Override
    public Flux<ReportVO> getReportBySId(String sId) {
        String cacheKey = "allReport";
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .flatMapMany(data -> {
                    if (data instanceof List) {
                        List<ReportDTO> reports = (List<ReportDTO>) data;
                        return Flux.fromIterable(reports)
                                .filter(report -> sId.equals(report.getSId()))
                                .map(Converter::reportDTO2VO);
                    }
                    return Flux.empty();
                })
                .switchIfEmpty(Flux.defer(() ->
                        stuRepo.getReportBySId(sId)
                                .collectList()
                                .flatMap(reportVo ->
                                        reactiveRedisTemplate.opsForValue().set(cacheKey, reportVo)
                                                .thenReturn(reportVo)
                                )
                                .flatMapMany(Flux::fromIterable)
                ));
    }

    @Override
    public Flux<StuInfoVO> getAllStuInfo() {
        String cacheKey = "allStuInfo";
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .flatMapMany(data -> {
                    if (data instanceof List) {
                        List<StuInfoVO> stuInfo = (List<StuInfoVO>) data;
                        return Flux.fromIterable(stuInfo);
                    }
                    return Flux.empty();
                })
                .switchIfEmpty(Flux.defer(() ->
                        stuRepo.getAllStuInfo()
                                .collectList()
                                .flatMap(stuInfo ->
                                        reactiveRedisTemplate.opsForValue().set(cacheKey, stuInfo)
                                                .thenReturn(stuInfo))
                                .flatMapMany(Flux::fromIterable)
                ));
    }

    @Override
    public Mono<Long> deleteReportBySId(String sId) {
        String cacheKey = "allReport";
        Query query = new Query(Criteria.where("sId").is(sId));
        return stuRepo.deleteReportBySId(sId)
                .flatMap(res ->
                        reactiveMongoTemplate.remove(query, Report.class)
                                .thenReturn(res))
                .flatMap(res ->
                        reactiveRedisTemplate.opsForValue().delete(cacheKey)
                                .thenReturn(res));
    }

//    @Override
//    public Mono<Void> deleteGraduate(Date date, Integer sHour) {
//        return databaseClient.sql("call delete_graduate(:endDate, :sHour)")
//                .bind("endDate", date)
//                .bind("sHour", sHour)
//                .then();
//    }

}
