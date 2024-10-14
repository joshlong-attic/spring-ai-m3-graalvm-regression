package com.example.service.vet;

import com.example.service.adoptions.DogAdoptionEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
class Dogtor {

    @ApplicationModuleListener
    void checkup(DogAdoptionEvent dogId) {
        System.out.println("checking up on [" + dogId.dogId() + "]");
    }
}
