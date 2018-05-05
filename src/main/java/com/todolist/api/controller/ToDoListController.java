package com.todolist.api.controller;

import com.todolist.api.model.Message;
import com.todolist.api.model.Task;
import com.todolist.api.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ToDoListController {

    @Autowired
    private TodoService todoService;

    @RequestMapping("/tasks")
    public Object list() {
        return todoService.allTask();
    }

    @PostMapping("/tasks")
    public Object addTask(@RequestBody Task task) {
        return todoService.addTask(task);
    }

    @GetMapping("/tasks/{id}")
    public Object addTask(@PathVariable String id) {
        return todoService.findOne(id);
    }

    @PutMapping("/tasks")
    public Object updateTask(@RequestBody Task task) {
        return todoService.updateTask(task);
    }

    @DeleteMapping("/tasks/{id}")
    public Object deleteTask(@PathVariable String id) {
        return todoService.deleteTask(id);
    }

    @PostMapping("/tasks/update-status")
    public Object updateStatus(@RequestBody Task task) {
        return todoService.updateStatus(task);
    }

    @GetMapping("/tasks/clear-task")
    public Message clearTask(){
        return todoService.clearTask();
    }

}
