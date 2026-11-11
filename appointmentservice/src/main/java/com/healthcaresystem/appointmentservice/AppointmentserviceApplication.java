package com.healthcaresystem.appointmentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/* Appointment service => manages appointments between doctors and patients*/

@SpringBootApplication
@EnableFeignClients  // scans all interfaces that declare that they are feign clients using @FeignClient annotation
/* Above annotation is only needed if this service needs to call other service' rest apis within service class */
public class AppointmentserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppointmentserviceApplication.class, args);
	}

}
