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
            String res = "No HTML documentation for resource &lt;" + requestedUri + "&gt; for now. Use HTTP Request Header Field Accept: text/turtle , or application/rdf+xml instead.";
            return Response.ok(res, "text/html").build();
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
        return Response.ok(ontology.getDocuments().get(requestedUri).asTurtle(), "text/turtle").build();
    }
        
//    @GET
//    @Produces("application/rdf+xml")
//    @Path("{id}")
//    public Response getAsXML(@PathParam("id") String id) {
//        if(TurtleDocument.baseResources.contains(id)) {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            TurtleDocument.baseModel.write(out);
//            InputStream in = new ByteArrayInputStream(out.toByteArray());
//            return Response.ok(in, "application/rdf+xml").build();
//        }
//        return Response.status(Response.Status.NOT_FOUND).build();
//    }
        

}
