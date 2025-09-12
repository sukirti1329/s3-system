package com.s3.common.logging;


import lombok.Setter;
import org.slf4j.MDC;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Setter
public class LoggingUtil {

    private static final String REQUEST_ID = "request-id";
    private static final String TRACE_ID = "trace-id";
    private static final String USER_ID = "user-id";
    private static final String SERVICE_NAME = "service-name";


    private static String serviceName = "unknown-service"; // default until set


    private LoggingUtil() {
    }

    public static void initContext(String incomingTraceId){
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(TRACE_ID, incomingTraceId != null ? incomingTraceId : UUID.randomUUID().toString());
        MDC.put(SERVICE_NAME, serviceName );
    }

    //Optional
    public static void setUserName(String userName){
        MDC.put(userName, userName);
    }

    public static void clear(){
        MDC.clear();
    }

    /** Standard SLF4J logger getter. */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
