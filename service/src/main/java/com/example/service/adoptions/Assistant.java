package com.example.service.adoptions;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RegisterReflectionForBinding(DogAdoptionSuggestion.class)
class Assistant {

    @Bean
    ApplicationRunner demo(ChatClient cc) {
        return args -> {
            var content = cc
                    .prompt()
                    .user("do you have any neurotic dogs?")
                    .call()
                    .entity(DogAdoptionSuggestion.class);
            System.out.println("content [" + content + "]");
        };
    }

    @Bean
    ChatClient client(
            @Value("${ivs:false}") boolean initializeVs,
            VectorStore vs,
            DogRepository dr,
            ChatClient.Builder builder) {

        if (initializeVs) {
            System.out.println("initializing the vector store...");
            dr.findAll().forEach(dog -> {
                var dogument = new Document("id: %s, name: %s, description: %s".formatted(
                        dog.id(), dog.name(), dog.description()));
                vs.add(List.of(dogument));
            });
        }
        return builder
                .defaultAdvisors(new QuestionAnswerAdvisor(vs))
                .defaultSystem("""
                        You are an AI powered assistant to help people adopt a dog from the adoption 
                        agency named Pooch Palace with locations in Antwerp, Seoul, Tokyo, Singapore, Paris, 
                        Mumbai, New Delhi, Barcelona, San Francisco, and London. Information about the dogs available 
                        will be presented below. If there is no information, then return a polite response suggesting we 
                        don't have any dogs available.
                        
                        
                        """)
                .build();
    }
}

record DogAdoptionSuggestion(int id, String name, String description) {
}