package dev.danvega;


import dev.danvega.service.DepartmentService;
import dev.danvega.service.EmployeeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class JsontodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(JsontodbApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(EmployeeService employeeService, DepartmentService departmentService){
		return args -> {
			// read JSON and load json
			employeeService.employeeSave();
			departmentService.departmentSave();
		};
	}
}




