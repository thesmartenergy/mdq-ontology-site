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
package com.github.thesmartenergy.mdq.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author maxime.lefrancois
 */
@Path("")
public class OntologyResourceResource {

    static final Logger LOG = Logger.getLogger(OntologyResourceResource.class.getSimpleName());

    @GET
    @Path("{id}")
    public Response getResource(@PathParam("id") String id) {
        try {
            return Response.seeOther(new URI("../page/" + id)).build();
        } catch (URISyntaxException ex) {
            Logger.getLogger(OntologyResourceResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
}
