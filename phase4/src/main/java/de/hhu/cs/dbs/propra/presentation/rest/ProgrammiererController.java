package de.hhu.cs.dbs.propra.presentation.rest;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("/programmierer")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProgrammiererController {

    @GET
    public Response getProgrammierer(){
        return Response.status(Response.Status.MOVED_PERMANENTLY).header("Location", "/entwickler")
                .build();
    }
}
