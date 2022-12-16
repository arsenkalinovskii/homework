package com.example.something;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;


@Controller
@RestController
public class ApiController {
    private List<User> users = new ArrayList<>();

    Comparator<User> comparatorByName = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return o1.getUsername().compareTo(o2.getUsername());
        }
    };
    Comparator<User> comparatorByAge = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            if(o1.getAge() > o2.getAge())
                return 1;
            if(o2.getAge() < o1.getAge())
                return -1;
            return 0;
        }
    };
    Comparator<User> comparatorByNameReversed = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return -1*o1.getUsername().compareTo(o2.getUsername());
        }
    };
    Comparator<User> comparatorByAgeReversed = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            if(o1.getAge() > o2.getAge())
                return -1;
            if(o2.getAge() < o1.getAge())
                return 1;
            return 0;
        }
    };

    // curl localhost:8080/users
    // curl localhost:8080/users?age=20
    @GetMapping("users")
    public ResponseEntity<List<UserEntity>> getUsers(@RequestParam(required = false) Integer age,
                                                     @RequestParam(required = false) String sortBy,
                                                     @RequestParam(required = false) String direction,
                                                     @RequestParam(required = false) Integer pageNumber,
                                                     @RequestParam(required = false) Integer pageSize){
        if(age == null)
            age = -1;
        List<UserEntity> returnList = new ArrayList<>();
        List<User> tempList = new ArrayList<>(users);
        if(sortBy != null && direction != null) {
            switch (sortBy) {
                case "names" :
                    switch (direction) {
                        case "up":
                            tempList.sort(comparatorByName);
                            break;
                        case "down":
                            tempList.sort(comparatorByNameReversed);
                            break;
                        default:
                            return ResponseEntity.badRequest().build();
                    }
                case "age":
                    switch (direction) {
                        case "up":
                            tempList.sort(comparatorByAge);
                            break;
                        case "down":
                            tempList.sort(comparatorByAgeReversed);
                            break;
                        default:
                            return ResponseEntity.badRequest().build();
                    }
                default:
                    return ResponseEntity.badRequest().build();
            }
        }
        for(User user : tempList)
            returnList.add(new UserEntity(user.getUsername(), user.getAge()));
        if(age >= 0)
            for(int i = 0; i < returnList.size(); i++)
                if(Math.abs(returnList.get(i).getAge() - age) > 5)
                    returnList.remove(i--);
                else{}
        else return ResponseEntity.badRequest().build();
        if(pageSize != null && pageNumber != null){
            List<UserEntity> page = new ArrayList<>();
            if(pageNumber < 0 || pageSize <= 0)
                return ResponseEntity.badRequest().build();
            if((int) (pageSize*pageNumber) > returnList.size())
                return ResponseEntity.ok(page);
            int FinalIndex = (pageSize*(pageNumber+1) > returnList.size()) ?
                    returnList.size() : pageSize*(pageNumber+1);
            for(int i = pageNumber*pageSize; i < FinalIndex; i++)
                page.add(returnList.get(i));
            returnList = page;
        }
        return ResponseEntity.ok(returnList);
    }

    // curl localhost:8080/users/0
    @GetMapping("users/{id}")
    public ResponseEntity<UserEntity> getUserByID(@PathVariable("id") Integer id){
        if(id >= users.size())
            return ResponseEntity.notFound().build();
        if(users.get((int)id) == null)
            return ResponseEntity.notFound().build();
        UserEntity returnvalue = new UserEntity( users.get((int)id).getUsername(),
                                                users.get((int)id).getAge());
        return ResponseEntity.ok(returnvalue);
    }

    // curl -X POST localhost:8080/users -H "Content-Type: application/json" -d "{\"username\":\"Bob\",\"password\":\"Bob\"}"
    @PostMapping("users")
    public ResponseEntity<Void> createUser(@RequestBody DataPacket userinfo){
        for(User user : users)
            if(userinfo.getUsername().equals(user.getUsername()))
                return ResponseEntity.status(409).build();
        if(!userinfo.getUserPassword().equals(userinfo.getRepeatPassword()))
            return ResponseEntity.badRequest().build();
        users.add(new User(userinfo.getUsername(), userinfo.getUserPassword(), userinfo.getUserAge()));
        return ResponseEntity.accepted().build();
    }
    // curl -X PUT localhost:8080/users/0 -H "Content-Type: application/json" -d "{\"username\":\"Bob1\",\"password\":\"Bob1\"}"
    @PutMapping("users/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable("id") Integer id,
                                           @RequestBody DataPacket userinfo){
        if(!userinfo.getUserPassword().equals(userinfo.getRepeatPassword()))
            return ResponseEntity.badRequest().build();
        if(id >= users.size())
            return ResponseEntity.notFound().build();
        if(users.get((int)id) == null)
            return ResponseEntity.notFound().build();
        for(User user : users)
            if(user.getUsername().equals(userinfo.getUsername()))
                return ResponseEntity.status(404).build();
        users.get((int)id).setAge(userinfo.getUserAge());
        users.get((int)id).setUsername(userinfo.getUsername());
        users.get((int)id).setPassword(userinfo.getUserPassword());
        return ResponseEntity.accepted().build();
    }


    // curl -X DELETE localhost:8080/users/0
    @DeleteMapping("users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Integer id){
        if(id >= users.size())
            return ResponseEntity.notFound().build();
        if(users.get(id) == null)
            return ResponseEntity.notFound().build();
        users.remove((int)id);
        return ResponseEntity.accepted().build();
    }
}
