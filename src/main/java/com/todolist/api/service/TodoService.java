package com.todolist.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todolist.api.model.Message;
import com.todolist.api.model.Task;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class TodoService {
    private final String FILENAME = "task.txt";
    private final String TMPFILE = "tmp.txt";
    List<Task> taskList = null;

    public Object allTask() {
        try {
            return convertToObjectList(readFromFile());
        } catch (Exception e) {
            return getMessage(e);
        }
    }

    public Object findOne(String id) {
        try {
            taskList = new ArrayList<Task>();
            taskList.addAll(convertToObjectList(readFromFile()));
            return getObjectById(taskList, id);
        } catch (Exception e) {
            return getMessage(e);
        }
    }


    @SuppressWarnings("unchecked")
    private void createTask(Task task) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            task.setId(generateTaskId());
            task.setStatus("Pending");
            JSONObject obj = convertModelToJsonObj(task);

            File file = new File(FILENAME);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(obj.toJSONString());
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Object updateTask(Task task) {
        try {
            String newContent = convertModelToJsonObj(task).toJSONString();
            updateData(newContent, task);
            return task;
        } catch (Exception e) {
            return getMessage(e);
        } finally {
        }
    }

    public Object updateStatus(Task task) {
        try {
            Task tmpTask = (Task) findOne(task.getId());
            tmpTask.setStatus(task.getStatus());
            String newContent = convertModelToJsonObj(tmpTask).toJSONString();
            updateData(newContent, tmpTask);
            return tmpTask;
        } catch (Exception e) {
            return getMessage(e);
        } finally {
        }
    }

    public void updateData(String newContent, Task task) throws Exception {
        File original = new File(FILENAME);
        if (!original.exists()) {
            original.createNewFile();
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(original));
        File tmp = new File(TMPFILE);
        PrintWriter printWriter = new PrintWriter(new FileWriter(tmp));
        String line = null;
        String content = fileToHash().get(task.getId());
        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals(content)) {
                line = newContent;
            }
            printWriter.println(line);
            printWriter.flush();
        }
        printWriter.close();
        bufferedReader.close();

        if (!original.delete()) {
            System.out.println("Could not delete file");
        }

        if (!tmp.renameTo(original)) {
            System.out.println("Could not rename file");
        }
    }

    public Object deleteTask(String id) {
        Task task = null;
        try {
            File original = new File(FILENAME);
            if (!original.exists()) {
                original.createNewFile();
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(original));
            File tmp = new File(TMPFILE);
            PrintWriter printWriter = new PrintWriter(new FileWriter(tmp));
            String line = null;
            String content = fileToHash().get(id);
            task = (Task) findOne(id);
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.equals(content)) {
                    printWriter.println(line);
                }
                printWriter.flush();
            }
            printWriter.close();
            bufferedReader.close();

            if (!original.delete()) {
                System.out.println("Could not delete file");
            }

            if (!tmp.renameTo(original)) {
                System.out.println("Could not rename file");
            }
        } catch (Exception e) {
            return getMessage(e);
        } finally {
        }
        return task;
    }

    private Object getObjectById(List<Task> taskList, String id) {
        Task task = null;
        try {
            task = new Task();
            Optional<Task> matchingObj = taskList.stream()
                    .filter(t -> t.getId().equals(id))
                    .findAny();
            task = matchingObj.get();
        } catch (Exception e) {
            return getMessage(e);
        }
        return task;
    }

    private List<String> readFromFile() throws Exception {
        List<String> lines = null;
        try {
            File file = new File(FILENAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            taskList = new ArrayList<Task>();
            FileReader reader = new FileReader(FILENAME);
            BufferedReader bufferReader = new BufferedReader(reader);
            lines = new ArrayList<String>();
            String line = null;
            while ((line = bufferReader.readLine()) != null) {
                lines.add(line);
            }
            bufferReader.close();

        } catch (Exception e) {
            throw e;
        }
        return lines;
    }

    private List<Task> convertToObjectList(List<String> lines) throws Exception {
        taskList = new ArrayList<Task>();
        for (String l : lines) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(l);
            ObjectMapper objectMapper = new ObjectMapper();
            Task task = objectMapper.readValue(l, Task.class);
            taskList.add(task);
        }
        return taskList;
    }

    private HashMap<String, String> fileToHash() throws Exception {
        HashMap<String, String> map = null;
        List<String> lines = null;
        try {
            FileReader reader = new FileReader(FILENAME);
            BufferedReader bufferReader = new BufferedReader(reader);
            lines = new ArrayList<String>();
            String line = null;
            map = new HashMap<>();
            while ((line = bufferReader.readLine()) != null) {
                lines.add(line);
            }
            bufferReader.close();
            for (String l : lines) {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(l);
                ObjectMapper objectMapper = new ObjectMapper();
                Task task = objectMapper.readValue(l, Task.class);
                map.put(task.getId(), l);
            }
        } catch (Exception e) {
            throw e;
        }
        return map;
    }

    public Object addTask(Task task) {
        try {
            createTask(task);
            return task;
        } catch (Exception e) {
            return getMessage(e);
        } finally {
        }
    }

    private JSONObject convertModelToJsonObj(Task task) {
        JSONObject obj = new JSONObject();
        obj.put("id", task.getId());
        obj.put("subject", task.getSubject());
        obj.put("detail", task.getDetail());
        obj.put("status", task.getStatus());
        return obj;
    }

    private Message getMessage(Exception e) {
        Message message = new Message("ER",e.getMessage());
        return message;
    }


    private String generateTaskId() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String key = "";
        try {
            Random random = new Random();
            int randomLen = 6;
            for (int i = 0; i < randomLen; i++) {
                char c = alphabet.charAt(random.nextInt(26));
                key += c;
            }
        } finally {
        }
        return key;
    }

    public Message clearTask(){
        File file = new File(FILENAME);
        if(file.delete()){
           return new Message("OK","Task list is empty.");
        }else{
            return new Message("ER","Operation is failed.");
        }

    }

}
