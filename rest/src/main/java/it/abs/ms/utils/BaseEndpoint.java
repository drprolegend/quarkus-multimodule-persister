package it.abs.ms.utils;

import it.abs.ms.common.service.CommonService;

import javax.ws.rs.core.Response;

public class BaseEndpoint extends CommonService {

    protected Response buildErrorResponse(
            Throwable e) {
        return buildErrorResponse(e, 500);
    }

    protected Response buildErrorResponse(
            Throwable e,
            int status) {
        return buildErrorResponse(e, 500, null);
    }

    protected Response buildErrorResponse(
            Throwable e,
            int status,
            String operation_id) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.operation_id = operation_id;
        if (e != null) {
            errorResponse.message = e.getMessage();
            e.printStackTrace();
            LOG.error("Error {}", e);
        }
        return Response.status(status)
                .entity(errorResponse)
                .build();
    }
}
