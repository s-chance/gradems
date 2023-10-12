package com.entropy.gradems.mapper;

import com.entropy.gradems.dto.ReportDTO;
import com.entropy.gradems.dto.StuCouDTO;
import com.entropy.gradems.dto.StuDepartDTO;
import com.entropy.gradems.po.Course;
import com.entropy.gradems.po.Department;
import com.entropy.gradems.po.Report;
import com.entropy.gradems.po.Student;
import com.entropy.gradems.vo.ReportVO;
import com.entropy.gradems.vo.StuInfoVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StuMapper {

    @Select("select * from department")
    List<Department> getAllDeparts();

    @Select("select * from student")
    List<Student> getAllStu();

    @Select("select * from course")
    List<Course> getAllCourses();

    @Select("select s_id, s_name, c_id, c_name from student,course")
    List<StuCouDTO> getAllStuCou();


    @Insert("insert into department values (#{dId},#{dName})")
    int insertDepart(@Param("dId") String dId, @Param("dName") String dName);

    @Delete("delete from department where d_id = #{dId}")
    int deleteDepart(@Param("dId") String dId);

    @Update("update department set d_name = #{dName} where d_id = #{dId}")
    int updateDepart(@Param("dId") String dId, @Param("dName") String dName);

    @Select("select s_id, s_name, d_name, start_date from student s,department d where s.d_id=d.d_id")
    List<StuDepartDTO> getAllStuDepart();

    @Insert("insert into student values (#{sId},#{sName},#{dId},#{startDate},#{sHour})")
    int insertStu(Student student);

    @Delete("delete from student where s_id = #{sId}")
    int deleteStu(@Param("sId") String sId);

    @Select("select d_id from department where d_name = #{dName}")
    String getDId(@Param("dName") String dName);

    @Update("update student set s_name = #{sName}, d_id = #{dId}, start_date = #{startDate} where s_id = #{sId}")
    int updateStu(Student student);

    @Insert("insert into course values (#{cId},#{cName},#{cHour})")
    int insertCourse(Course course);

    @Delete("delete from course where c_id = #{cId}")
    int deleteCourse(@Param("cId") String cId);

    @Update("update course set c_name = #{cName}, c_hour = #{cHour} where c_id = #{cId}")
    int updateCourse(Course course);

    @Select("select r.s_id, s_name, r.c_id, c_name, grade from student s, course c, report r where s.s_id = r.s_id and c.c_id = r.c_id")
    List<ReportDTO> getAllReport();

    @Insert("insert into report values (#{sId},#{cId},#{grade})")
    int insertReport(Report report);

    @Delete("delete from report where s_id = #{sId} and c_id = #{cId}")
    int deleteReport(@Param("sId") String sId, @Param("cId") String cId);

    @Update("update report set grade = #{grade} where s_id = #{sId} and c_id = #{cId}")
    int updateReport(Report report);

    @Select("select r.s_id, c_name, r.grade from report r,course c where r.c_id = c.c_id and r.s_id = #{sId}")
    List<ReportVO> getReportBySId(@Param("sId") String sId);

    @Select("select s_id, s_name, d_name, start_date, s_hour from student s, department d where s.d_id = d.d_id")
    List<StuInfoVO> getAllStuInfo();

    @Delete("delete from report where s_id = #{sId}")
    int deleteReportBySId(@Param("sId") String sId);


    // 调用存储过程
    @Select("call delete_graduate(#{endDate}, #{sHour})")
    void deleteGraduate(@Param("endDate") Date endDate, @Param("sHour") Integer sHour);

}
