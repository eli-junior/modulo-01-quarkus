package com.eli;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/unipds")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class UniPDSResource {
    private int i = 0;

    @GET
    public int getI() {
        return i;
    }
    @POST
    public void postI() {
        this.i++;
    }
    @PUT
    public void putI(int value) {
        this.i = value;
    }
    @DELETE
    public void deleteI() {
        this.i = 0;
    }
}
