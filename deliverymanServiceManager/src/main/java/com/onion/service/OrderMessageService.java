package com.onion.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author onion_Knight
 */
public interface OrderMessageService {
    void handleMessage() throws IOException, TimeoutException, InterruptedException;
}
