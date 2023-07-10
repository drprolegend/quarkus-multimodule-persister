package it.abs.ms.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import it.abs.ms.common.model.PayloadBase;
import it.abs.ms.common.persister.Persister;
import it.abs.ms.common.utils.ObjectMapperBuilder;
import it.abs.ms.utils.BaseEndpoint;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/persist")
public class PersisterResource extends BaseEndpoint {

    private static final ObjectMapper om = ObjectMapperBuilder.getInstance();

    @Inject
    Persister persister;

    @POST
    @Path("/{servicename}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> persist(
            @HeaderParam("request_id") String request_id,
            @HeaderParam("session_id") String session_id,
            @HeaderParam("operation_id") String operation_id,
            @PathParam("servicename") String servicename,
            PayloadBase payload) {
        try {
            return persister.persist(request_id, session_id, operation_id, servicename, payload)
                    .onItem()
                    .transform(responseData -> {
                        LOG.info("END bulk");
                        return Response.ok().entity(responseData).build();
                    })
                    .onFailure()
                    .recoverWithItem(error -> buildErrorResponse(error, 500, operation_id));

        } catch (NullPointerException e) {
            return Uni.createFrom().item(buildErrorResponse(e, 400, operation_id));
        } catch (BadRequestException e) {
            return Uni.createFrom().item(buildErrorResponse(e, 400, operation_id));
        } catch (Exception e) {
            return Uni.createFrom().item(buildErrorResponse(e, 500, operation_id));

        }
    }


}
