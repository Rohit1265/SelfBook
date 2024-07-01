package dev.danvega;


import dev.danvega.service.JsonReaderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class JsontodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(JsontodbApplication.class, args);
	}

/*	@Bean
	CommandLineRunner runner(JsonReaderService jsonReaderService){
		return args -> {
			// read JSON and load json
			jsonReaderService.createTableAndInsertData("DEP");
		};*/

}




