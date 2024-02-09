package com.entropy.gradems.config;

import com.entropy.gradems.handler.StuHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class RouterConfig {

    private final StuHandler stuHandler;

    public RouterConfig(StuHandler stuHandler) {
        this.stuHandler = stuHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes() {
        return RouterFunctions
                .route(RequestPredicates.POST("/login"), stuHandler::login)
                .andRoute(RequestPredicates.POST("/register"), stuHandler::register);
    }

    @Bean
    public RouterFunction<ServerResponse> departmentRoutes() {
        return RouterFunctions
                .route(RequestPredicates.GET("/getAllDeparts"), stuHandler::getAllDeparts)
                .andRoute(RequestPredicates.POST("/insertDepart"), stuHandler::insertDepart)
                .andRoute(RequestPredicates.DELETE("/deleteDepart/{dId}"), stuHandler::deleteDepart)
                .andRoute(RequestPredicates.PUT("/updateDepart/{dId}"), stuHandler::updateDepart)
                .andRoute(RequestPredicates.GET("/getDId"), stuHandler::getDId);
    }

    @Bean
    public RouterFunction<ServerResponse> studentRoutes() {
        return RouterFunctions
                .route(RequestPredicates.GET("/getAllStu"), stuHandler::getAllStu)
                .andRoute(RequestPredicates.GET("/getAllStuCou"), stuHandler::getAllStuCou)
                .andRoute(RequestPredicates.GET("/getAllStuDepart"), stuHandler::getAllStuDepart)
                .andRoute(RequestPredicates.POST("/insertStu"), stuHandler::insertStu)
                .andRoute(RequestPredicates.DELETE("/deleteStu/{sId}"), stuHandler::deleteStu)
                .andRoute(RequestPredicates.PUT("/updateStu/{sId}"), stuHandler::updateStu)
                .andRoute(RequestPredicates.GET("/getAllStuInfo"), stuHandler::getAllStuInfo);
    }

    @Bean
    public RouterFunction<ServerResponse> courseRoutes() {
        return RouterFunctions
                .route(RequestPredicates.GET("/getAllCourses"), stuHandler::getAllCourses)
                .andRoute(RequestPredicates.POST("/insertCourse"), stuHandler::insertCourse)
                .andRoute(RequestPredicates.DELETE("/deleteCourse/{cId}"), stuHandler::deleteCourse)
                .andRoute(RequestPredicates.PUT("/updateCourse/{cId}"), stuHandler::updateCourse);
    }

    @Bean
    public RouterFunction<ServerResponse> reportRoutes() {
        return RouterFunctions
                .route(RequestPredicates.GET("/getAllReport"), stuHandler::getAllReport)
                .andRoute(RequestPredicates.POST("/insertReport"), stuHandler::insertReport)
                .andRoute(RequestPredicates.DELETE("/deleteReport/{sId}/{cId}"), stuHandler::deleteReport)
                .andRoute(RequestPredicates.PUT("/updateReport/{sId}/{cId}"), stuHandler::updateReport)
                .andRoute(RequestPredicates.GET("/getReportBySId/{sId}"), stuHandler::getReportBySId)
                .andRoute(RequestPredicates.DELETE("/deleteReportBySId/{sId}"), stuHandler::deleteReportBySId);

    }
}
