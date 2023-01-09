package com.gerasimchuk.transactionisolationlevels.service;

import com.gerasimchuk.transactionisolationlevels.domain.Post;
import com.gerasimchuk.transactionisolationlevels.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirtyReadExampleService {

    private final PostRepository postRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
    @SneakyThrows
    public void modifyDataAndWaitAndRollback() {
        log.info("{}: modifying data", currentThreadName());
        var allEntries = postRepository.findAll();
        log.info("{}: All entries fetched", currentThreadName());
        for(Post post: allEntries){
            post.setContent(post.getContent() + " :modified !!");
        }
        log.info("All entries modified");
        postRepository.saveAllAndFlush(allEntries); //!! if we use saveAll (without flushing) then no dirty reads
        log.info("All entries saved, sleeping");
        Thread.sleep(5000);
        log.info("Waking up and rollback");
        throw new RuntimeException(); // for rollback
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
    @SneakyThrows
    public void waitAndReadDirtyData() {
        log.info("{}: sleeping before read", currentThreadName());
        Thread.sleep(1000);
        log.info("{} : waking up and reading entries", currentThreadName());
        var allPosts = postRepository.findAll();
        log.info("{}: all entries: {}", Thread.currentThread().getName(), allPosts);
    }

    @Transactional(readOnly = true)
    public void readConsistentData(){
        log.info("Reading consistent data (expecting lack ou modifications here)");
        var allEntries = postRepository.findAll();
        log.info("{}", allEntries);
    }

    private String currentThreadName(){
        return Thread.currentThread().getName();
    }
}
