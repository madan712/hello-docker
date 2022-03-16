package com.javaxp.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class HelloController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/say-hello")
    public String sayHello(@RequestParam("name") Optional<String> name) {
        System.out.println("inside sayHello "+name);
        return "Hello "+name.orElse("World")+"!";
    }

    @GetMapping("/employee-list")
    public List<String> getEmployesList() {
        System.out.println("inside getEmployesList");

        List<String> empList = new ArrayList<>();

        jdbcTemplate.queryForList("select empId, empName from employee").forEach(e -> {
            empList.add(e.get("empName").toString());
        });
        return empList;
    }
}
