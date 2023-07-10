package it.abs.ms.common.service;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommonService {

    public final Logger LOG = LoggerFactory.getLogger(String.valueOf(getClass()));

    public void addLoggingValues(
            String request_id,
            String session_id,
            String operation_id
    ) {
        addLoggingValues(request_id, session_id, operation_id, null, null);

    }
    public void addLoggingValues(
            String request_id,
            String session_id,
            String operation_id,
            Object payload
    ) {
        addLoggingValues(request_id, session_id, operation_id, payload, null);

    }

    public void addLoggingValues(
            String request_id,
            String session_id,
            String operation_id,
            Object payload,
            String servicename
    ) {
        try {
            MDC.clear();
            if (request_id != null) {
                MDC.put("request_id", request_id);
            }
            if (session_id != null) {
                MDC.put("session_id", session_id);
            }
            if (operation_id != null) {
                MDC.put("operation_id", operation_id);
            }
            if (servicename != null) {
                MDC.put("servicename", servicename);
            }
            if (payload != null) {
                MDC.put("payload", payload);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during configure logging");
        }

    }
}
