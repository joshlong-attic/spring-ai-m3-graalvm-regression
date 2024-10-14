package com.example.service.adoptions;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@ResponseBody
class DogAdoptionController {

    private final DogRepository repository;

    private final ApplicationEventPublisher publisher;

    DogAdoptionController(DogRepository repository, ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @PostMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable int dogId,
               @RequestBody Map<String, String> request) {
        this.repository.findById(dogId).ifPresent(dog -> {
            var newDog = this.repository.save(new Dog(
                    dog.id(), dog.name(), dog.description(), request.get("name")));
            System.out.println("adopted [" + newDog + "]");
            this.publisher.publishEvent(new DogAdoptionEvent(newDog.id()));
        });

    }
}


interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record Dog(@Id int id, String name, String description, String owner) {
}