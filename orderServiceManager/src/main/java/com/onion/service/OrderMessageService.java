package com.onion.service;

import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author onion_Knight
 */
public interface OrderMessageService {

    @Async
    void handleMessage() throws IOException, TimeoutException, InterruptedException;
}
