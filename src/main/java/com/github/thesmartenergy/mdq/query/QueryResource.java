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
package com.github.thesmartenergy.mdq.query;

import com.github.thesmartenergy.mdq.entities.Ontology;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.io.IOUtils;

/**
 * @author maxime.lefrancois
 */
@Path("")
public class QueryResource {

    static final Logger LOG = Logger.getLogger(QueryResource.class.getSimpleName());
    static final String BASE = "http://w3id.org/multidimensional-quantity/query/";
    
    @Inject
    Ontology ontology;

    @GET
    @Produces("application/sparql-generate")
    @Path("{id}")
    public Response getQuery(@PathParam("id") String id) {
        try {
            InputStream in = ontology.getFileManager().open(BASE + id);
            return Response.ok(IOUtils.toString(in), "application/sparql-generate").build();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return Response.status(Status.NOT_FOUND).build();
        }
    }

}
