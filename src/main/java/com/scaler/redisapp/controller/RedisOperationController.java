package com.scaler.redisapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaler.redisapp.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/person")
public class RedisOperationController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(RedisOperationController.class);

    private static final String PERSON_KEY_PREFIX= "per::";
    private static final String PERSON_LIST_PREFIX="per_list::";

    private static final String PERSON_HASH_PREFIX="per_hash::";

    @PostMapping("/save/key")
    public void savePerson(@RequestBody Person person){
        logger.info("Data: {}",person);
      String key=getKeyId(person.getId());
      redisTemplate.opsForValue().set(key,person);
    }

    @GetMapping("/fetch")
    public Person fetchPerson(@RequestParam("Id") Long id){
        String key=getKeyId(id);
        return (Person) redisTemplate.opsForValue().get(key);
    }

    @PostMapping("/lpush")
    public void lpushPerson(@RequestBody List<Person> personList){
        for (Person person: personList) {
            redisTemplate.opsForList().leftPush(PERSON_LIST_PREFIX,person);
        }
    }

    @PostMapping("/rpush")
    public void rpushPerson(@RequestBody List<Person> personList){
        for (Person person: personList) {
            redisTemplate.opsForList().rightPush(PERSON_LIST_PREFIX,person);
        }
    }

    @DeleteMapping("/lpop")
    public List<Person> lpopPerson(@RequestParam(value="count", required = false, defaultValue = "1") int count){
        return redisTemplate.opsForList().leftPop(PERSON_LIST_PREFIX,count)
                .stream().map(person-> (Person) person)
                .collect(Collectors.toList());
    }
    @DeleteMapping("/rpop")
    public List<Person> rpopPerson(@RequestParam(value="count", required = false, defaultValue = "1") int count){
        return redisTemplate.opsForList().rightPop(PERSON_LIST_PREFIX,count)
                .stream().map(person-> (Person) person)
                .collect(Collectors.toList());
    }

    @GetMapping("/range/person")
    public List<Person> getRangeOfPerson(@RequestParam(value="start", required = false,defaultValue = "0") int start,
                               @RequestParam(value="end", required = false,defaultValue = "-1") int end){
        return redisTemplate.opsForList().range(PERSON_LIST_PREFIX,start,end)
                .stream().map(x -> (Person)x)
                .collect(Collectors.toList());
    }

    @PostMapping("/save/hash")
    public void savePersonInHash(@RequestBody List<Person> listPerson){
        listPerson.stream().forEach(person -> {
            Map map= objectMapper.convertValue(person,Map.class);
            redisTemplate.opsForHash().putAll(getHashId(person.getId()),map);
        });
    }

    @GetMapping("/fetch/hash")
    public List<Person> getPersonInHash(@RequestParam  List<Long> ids){
        List<Person> resultPerson=new ArrayList<Person>();
        ids.stream().forEach(id->{
            Map map=redisTemplate.opsForHash().entries(getHashId(id));
            resultPerson.add(objectMapper.convertValue(map,Person.class));
        });
        return resultPerson;
    }


    private String getHashId(Long id) {
        return PERSON_HASH_PREFIX+id;
    }

    private String getKeyId(Long Id){
        return PERSON_KEY_PREFIX+Id;
    }
}
