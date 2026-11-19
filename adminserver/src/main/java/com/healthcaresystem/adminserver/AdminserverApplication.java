package com.healthcaresystem.adminserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

/* Admin Server helps to monitor other services 
 * Either through Eureka server
 * Or By registering every services to admin service instead of eureka server
 * */
@SpringBootApplication
@EnableAdminServer       //enables this class as admin server - enables central monitoring dashboard
public class AdminserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminserverApplication.class, args);
	}

}
