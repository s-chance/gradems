package com.entropy.gradems.repository;

import com.entropy.gradems.dto.ReportDTO;
import com.entropy.gradems.dto.StuCouDTO;
import com.entropy.gradems.dto.StuDepartDTO;
import com.entropy.gradems.dto.UserRoleDTO;
import com.entropy.gradems.po.*;
import com.entropy.gradems.vo.ReportVO;
import com.entropy.gradems.vo.StuInfoVO;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.LocalDate;
import java.util.ArrayList;


@Repository
public class StuRepoImpl implements StuRepo {

    private final DatabaseClient databaseClient;

    public StuRepoImpl(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }


    @Override
    public Mono<User> findByUsername(String username) {
        return databaseClient.sql("select * from users where username = :username")
                .bind("username", username)
                .map((row, rowMetadata) -> new User(
                        row.get("id", Integer.class), // 这里的id是自增的，所以不需要传入
                        row.get("username", String.class),
                        row.get("password", String.class)))
                .one();
    }

    @Override
    public Mono<UserRoleDTO> getUserRole(String username) {
        return databaseClient.sql("select username, name " +
                        "from users u " +
                        "join user_role ur on u.id = ur.user_id " +
                        "join roles r on ur.role_id = r.id " +
                        "where username = :username")
                .bind("username", username)
                .map((row, rowMetadata) -> Tuples.of(
                        row.get("username", String.class),
                        row.get("name", String.class)))
                .all()
                .collectMultimap(tuple -> tuple.getT1(), tuple -> tuple.getT2())
                .flatMap(map -> {
                    // 从map中取出username和对应的roles列表
                    return Mono.justOrEmpty(map.entrySet().stream()
                            .map(entry -> new UserRoleDTO(entry.getKey(), new ArrayList<>(entry.getValue())))
                            .findFirst());
                });
    }

    @Override
    public Mono<Integer> insertUser(User user) {
        // 返回插入的id，这里的密码应该是加密后的
        return databaseClient.sql("insert into users (username, password) values (:username, :password) returning id")
                .bind("username", user.getUsername())
                .bind("password", user.getPassword())
                .map((row, rowMetadata) -> row.get("id", Integer.class))
                .first();
    }

    @Override
    public Mono<Long> insertUserRole(Integer userId, UserRoleDTO userRoleDTO) {
        // 根据角色名查询对应的id，然后插入到user_role表中
        return databaseClient.sql("select id from roles where name in (:names)")
                .bind("names", userRoleDTO.getRoles())
                .fetch()
                .all()
                .flatMap(row ->
                    databaseClient.sql("insert into user_role (user_id, role_id) values (:user_id, :role_id)")
                            .bind("user_id", userId)
                            .bind("role_id", (Integer) row.get("id"))
                            .fetch()
                            .rowsUpdated()
                ).reduce(0L, Long::sum);
    }


    @Override
    public Flux<Department> getAllDeparts() {
        return databaseClient.sql("select * from department")
                .map((row, rowMetadata) -> new Department(
                        row.get("d_id", String.class),
                        row.get("d_name", String.class)))
                .all();
    }

    @Override
    public Flux<Student> getAllStu() {
        return databaseClient.sql("select * from student")
                .map((row, rowMetadata) -> new Student(
                        row.get("s_id", String.class),
                        row.get("s_name", String.class),
                        row.get("d_id", String.class),
                        row.get("start_date", LocalDate.class),
                        row.get("s_hour", Integer.class)))
                .all();
    }

    @Override
    public Flux<Course> getAllCourses() {
        return databaseClient.sql("select * from course")
                .map((row, rowMetadata) -> new Course(
                        row.get("c_id", String.class),
                        row.get("c_name", String.class),
                        row.get("c_hour", Integer.class)))
                .all();
    }

    @Override
    public Flux<StuCouDTO> getAllStuCou() {
        return databaseClient.sql("select s_id, s_name, c_id, c_name from student,course")
                .map((row, rowMetadata) -> new StuCouDTO(row.get("s_id", String.class),
                        row.get("s_name", String.class),
                        row.get("c_id", String.class),
                        row.get("c_name", String.class)))
                .all();
    }

    @Override
    public Mono<Long> insertDepart(String dId, String dName) {
        return databaseClient.sql("insert into department values (:dId,:dName)")
                .bind("dId", dId)
                .bind("dName", dName)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<Long> deleteDepart(String dId) {
        return databaseClient.sql("delete from department where d_id = :dId")
                .bind("dId", dId)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<Long> updateDepart(String dId, String dName) {
        return databaseClient.sql("update department set d_name = :dName where d_id = :dId")
                .bind("dId", dId)
                .bind("dName", dName)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Flux<StuDepartDTO> getAllStuDepart() {
        return databaseClient.sql("select s_id, s_name, d_name, start_date from student s,department d where s.d_id=d.d_id")
                .map((row, rowMetadata) -> new StuDepartDTO(row.get("s_id", String.class),
                        row.get("s_name", String.class),
                        row.get("d_name", String.class),
                        row.get("start_date", LocalDate.class)))
                .all();
    }

    @Override
    public Mono<Long> insertStu(Student student) {
        return databaseClient.sql("insert into student values (:sId,:sName,:dId,:startDate,:sHour)")
                .bind("sId", student.getSId())
                .bind("sName", student.getSName())
                .bind("dId", student.getDId())
                .bind("startDate", student.getStartDate())
                .bind("sHour", student.getSHour())
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<Long> deleteStu(String sId) {
        return databaseClient.sql("delete from student where s_id = :sId")
                .bind("sId", sId)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<String> getDId(String dName) {
        return databaseClient.sql("select d_id from department where d_name = :dName")
                .bind("dName", dName)
                .map((row, rowMetadata) -> row.get("d_id", String.class))
                .one();
    }

    @Override
    public Mono<Long> updateStu(Student student) {
        return databaseClient.sql("update student set s_name = :sName, d_id = :dId, start_date = :startDate where s_id = :sId")
                .bind("sId", student.getSId())
                .bind("sName", student.getSName())
                .bind("dId", student.getDId())
                .bind("startDate", student.getStartDate())
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<Long> insertCourse(Course course) {
//        Flux<Course> course1 = Flux.just(new Course("t1", "ss", 3), new Course("t2", "ss", 3));
//        Mono<Long> count = course1.flatMap(courses ->
//                databaseClient.sql("INSERT INTO course VALUES (:cId,:cName,:cHour)")
//                        .bind("cId", courses.getCId())
//                        .bind("cName", courses.getCName())
//                        .bind("cHour", courses.getCHour())
//                        .fetch()
//                        .rowsUpdated()
//        ).count();
//        return count;
        return databaseClient.sql("insert into course values (:cId,:cName,:cHour)")
                .bind("cId", course.getCId())
                .bind("cName", course.getCName())
                .bind("cHour", course.getCHour())
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<Long> deleteCourse(String cId) {
//        Flux<String> idsToDelete = Flux.just("t1", "t2"); // 假设这是要删除的ID列表
//        Mono<Long> longMono = idsToDelete.collectList().flatMap(ids ->
//                databaseClient.sql("DELETE FROM course WHERE c_id IN (:ids)")
//                        .bind("ids", ids)
//                        .fetch()
//                        .rowsUpdated()
//        );
//        return longMono;
        return databaseClient.sql("delete from course where c_id = :cId")
                .bind("cId", cId)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<Long> updateCourse(Course course) {
//        Flux<Course> course1 = Flux.just(new Course("t1", "jmsjasj", 3), new Course("t2", "sassajknc", 3));
//        Mono<Long> count = course1.flatMap(courses ->
//                databaseClient.sql("UPDATE course SET c_name = :cName WHERE c_id IN (:cId)")
//                        .bind("cId", courses.getCId())
//                        .bind("cName", courses.getCName())
//                        .fetch()
//                        .rowsUpdated()
//        ).reduce(0L, Long::sum);
//        return count;
        return databaseClient.sql("update course set c_name = :cName, c_hour = :cHour where c_id = :cId")
                .bind("cId", course.getCId())
                .bind("cName", course.getCName())
                .bind("cHour", course.getCHour())
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Flux<ReportDTO> getAllReport() {
        return databaseClient.sql("select r.s_id, s_name, r.c_id, c_name, grade from student s, course c, report r where s.s_id = r.s_id and c.c_id = r.c_id")
                .map((row, rowMetadata) -> new ReportDTO(row.get("s_id", String.class),
                        row.get("s_name", String.class),
                        row.get("c_id", String.class),
                        row.get("c_name", String.class),
                        row.get("grade", Integer.class)))
                .all();
    }

    @Override
    public Mono<Long> insertReport(Report report) {
        return databaseClient.sql("insert into report values (:s_id,:c_id,:grade)")
                .bind("s_id", report.getSId())
                .bind("c_id", report.getCId())
                .bind("grade", report.getGrade())
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<Long> deleteReport(String sId, String cId) {
        return databaseClient.sql("delete from report where s_id=:s_id and c_id=:c_id")
                .bind("s_id", sId)
                .bind("c_id", cId)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<Long> updateReport(Report report) {
        return databaseClient.sql("update report set grade = :grade where s_id = :s_id and c_id = :c_id")
                .bind("s_id", report.getSId())
                .bind("c_id", report.getCId())
                .bind("grade", report.getGrade())
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Flux<ReportVO> getReportBySId(String sId) {
        return databaseClient.sql("select r.s_id, c_name, r.grade from report r,course c where r.c_id = c.c_id and r.s_id = :s_id")
                .bind("s_id", sId)
                .map((row, rowMetadata) -> new ReportVO(row.get("s_id", String.class),
                        row.get("c_name", String.class),
                        row.get("grade", Integer.class)))
                .all();
    }

    @Override
    public Flux<StuInfoVO> getAllStuInfo() {
        return databaseClient.sql("select s_id, s_name, d_name, start_date, s_hour from student s, department d where s.d_id = d.d_id")
                .map((row, rowMetadata) -> new StuInfoVO(row.get("s_id", String.class),
                        row.get("s_name", String.class),
                        row.get("d_name", String.class),
                        row.get("start_date", LocalDate.class),
                        row.get("s_hour", Integer.class)))
                .all();
    }

    @Override
    public Mono<Long> deleteReportBySId(String sId) {
        return databaseClient.sql("delete from report where s_id = :s_id")
                .bind("s_id", sId)
                .fetch()
                .rowsUpdated();
    }
}
