package com.springboot.demo.web.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@ComponentScan ("com.springboot.demo")
@ServletComponentScan ("com.springboot.demo")
public class DemoApplication {

	public static void main (String[] args) {
		SpringApplication.run (DemoApplication.class, args);
	}

}
