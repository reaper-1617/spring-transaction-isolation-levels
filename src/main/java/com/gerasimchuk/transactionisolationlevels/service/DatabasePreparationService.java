package com.gerasimchuk.transactionisolationlevels.service;

import com.gerasimchuk.transactionisolationlevels.domain.Post;
import com.gerasimchuk.transactionisolationlevels.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabasePreparationService {

    private final PostRepository postRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void doWhenContextCreated() {
        log.info("Preparing database entries");
        List<Post> newEntries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var post = Post.builder()
                    .author("Ivanov " + UUID.randomUUID())
                    .content("Test content " + UUID.randomUUID())
                    .build();
            newEntries.add(post);
        }
        var saved = postRepository.saveAll(newEntries);
        log.info("{} entries saved", saved.size());
    }
}
