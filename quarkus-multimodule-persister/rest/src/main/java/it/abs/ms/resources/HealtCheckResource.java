package it.abs.ms.resources;

import it.abs.ms.common.service.CommonService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/healthcheck")
public class HealtCheckResource extends CommonService {

    @GET
    public Response healthcheck() {
        LOG.info("Managing the healthcheck request returning always a OK response");
        return Response.ok().build();
    }
}
