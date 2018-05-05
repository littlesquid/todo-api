package com.todolist.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodolistApplication {

	public static void main(String[] args) {
		System.out.println("Start SpringApplication");
		SpringApplication.run(TodolistApplication.class, args);
	}
}
