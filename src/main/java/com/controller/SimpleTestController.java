package com.controller;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/test")
public class SimpleTestController {
    
    @GET
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHelloWorld() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, World!");
        return Response.ok(response).build();
    }
    
    @POST
    @Path("/echo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response echoMessage(Map<String, String> inputMessage) {
        if (inputMessage == null || !inputMessage.containsKey("message")) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Invalid input"))
                .build();
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("received", inputMessage.get("message"));
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() {
        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", System.currentTimeMillis());
        
        return Response.ok(healthStatus).build();
    }
}