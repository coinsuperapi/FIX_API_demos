package com.demo.config;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import quickfix.ConfigError;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SLF4JLogFactory;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketInitiator;

import java.util.concurrent.Executors;

/**
 * @author zhangbin
 * @Description:
 * @date create at 2018/12/5 8:40 PM
 */
@Configuration
public class FixClientConfig {


    public LogFactory slf4jLogFactory(SessionSettings serverSessionSettings) {
        return new SLF4JLogFactory(serverSessionSettings);
    }

    @Bean
    public Initiator clientInitiator(quickfix.Application clientApplication, MessageStoreFactory clientMessageStoreFactory,
                                     SessionSettings clientSessionSettings, MessageFactory clientMessageFactory) throws ConfigError {
        ThreadedSocketInitiator initiator = new ThreadedSocketInitiator(clientApplication, clientMessageStoreFactory, clientSessionSettings,
                slf4jLogFactory(clientSessionSettings), clientMessageFactory);
        DefaultIoFilterChainBuilder ioFilterChainBuilder = new DefaultIoFilterChainBuilder();
        ioFilterChainBuilder.addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        initiator.setIoFilterChainBuilder(ioFilterChainBuilder);
        return initiator;
    }
}
