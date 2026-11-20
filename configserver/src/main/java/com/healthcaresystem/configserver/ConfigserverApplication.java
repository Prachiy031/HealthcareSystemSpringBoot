package com.healthcaresystem.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/* For central configuration, storing all application's configurations in central place
 * 1. Every configuration is kept separate apart from source code, build artifacts reducing rebuilds when value changes 
 * 2. Allows certian configuration changes to refresh without restarting or redeploying running configuration
 * 3. Supports encryption/decryption and access separation for sensitive values like credentials or API keys 
 */

@SpringBootApplication
@EnableConfigServer    //this application acts as a config server which exposes Http endpoints through which other applications can retrieve their configuration
public class ConfigserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigserverApplication.class, args);
	}

}
