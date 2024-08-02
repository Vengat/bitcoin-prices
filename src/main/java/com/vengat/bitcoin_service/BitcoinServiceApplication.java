package com.vengat.bitcoin_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class BitcoinServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(BitcoinServiceApplication.class);
	public static void main(String[] args) {
		logger.info("Starting Bitcoin Service Application");
		SpringApplication.run(BitcoinServiceApplication.class, args);
	}

}
