/*
 * Copyright 2016 ITEA 12004 SEAS Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.thesmartenergy.mdq.page;

import com.github.thesmartenergy.mdq.entities.Ontology;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author maxime.lefrancois
 */
@Path("")
public class OntologyPageResource {

    private static final Logger LOG = Logger.getLogger(OntologyPageResource.class.getSimpleName());

    @Inject
    HttpServletRequest request;
    
    @Inject
    Ontology ontology;
    
    @GET
    @Produces("text/html")
    @Path("{id}")
    public Response getAsHtml(@PathParam("id") String id) {
        String requestedUri = ontology.getBase() + "resource/" + id;
        if(ontology.getDocuments().containsKey(requestedUri)) {
            try {
                return Response.seeOther(new URI("http://vowl.visualdataweb.org/webvowl/#iri=" + requestedUri)).build();
            } catch (URISyntaxException ex) {
                Logger.getLogger(OntologyPageResource.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    @GET
    @Produces("text/turtle")
    @Path("{id}")
    public Response getAsTurtle(@PathParam("id") String id) {
        String requestedUri = ontology.getBase() + "resource/" + id;
        System.out.println(requestedUri );
        if(!ontology.getDocuments().containsKey(requestedUri)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Response.ResponseBuilder res = Response.ok(ontology.getDocuments().get(requestedUri).asTurtle(), "text/turtle");
        res.header("Content-Disposition", "filename= mdq.ttl;");
        return res.build();
    }
        
    @GET
    @Produces("application/rdf+xml")
    @Path("{id}")
    public Response getAsXML(@PathParam("id") String id) {
        String requestedUri = ontology.getBase() + "resource/" + id;
        System.out.println(requestedUri );
        if(!ontology.getDocuments().containsKey(requestedUri)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Response.ResponseBuilder res = Response.ok(ontology.getDocuments().get(requestedUri).asXML(), "application/rdf+xml");
        res.header("Content-Disposition", "filename= mdq.rdf;");
        return res.build();
    }

}
