package com.gerasimchuk.transactionisolationlevels.controller;

import com.gerasimchuk.transactionisolationlevels.service.DirtyReadExampleService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Consumer;

@RestController
@RequestMapping("/examples")
@RequiredArgsConstructor
@Slf4j
public class IsolationsSamplesController {

    private final DirtyReadExampleService dirtyReadExampleService;

    @PostMapping("/dirty")
    @SneakyThrows
    public void dirtyRead(){
        log.info("Running dirty read example ...");
        // run concurrently to see a dirty reading
        var t1 = runInNewThread(dirtyReadExampleService::modifyDataAndWaitAndRollback);
        t1.setUncaughtExceptionHandler((t, e) -> log.info("Exception for rollback (as expected)"));
        var t2 = runInNewThread(dirtyReadExampleService::waitAndReadDirtyData);
        t1.join();
        t2.join();
        dirtyReadExampleService.readConsistentData();
        log.info("Dirty read example finished");
    }


    private Thread runInNewThread(Runnable runnable){
        Thread thread = new Thread(runnable);
        thread.setName("Example thread " + UUID.randomUUID());
        thread.start();
        return thread;
    }
}
