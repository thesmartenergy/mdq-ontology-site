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
package com.github.thesmartenergy.mdq.entities;

import java.io.StringWriter;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.writer.TurtleWriter;

/**
 *
 * @author maxime.lefrancois
 */
public class Vocabulary implements Document {

    private static final Logger LOG = Logger.getLogger(Vocabulary.class.getSimpleName());

    private final Ontology ontology;
    private final Model model;

    public Vocabulary(Ontology ontology, Model model) {
        this.ontology = ontology;
        this.model = model;
    }

    @Override
    public String asTurtle() {
        StringWriter sw = new StringWriter();
        sw.append(ontology.asTurtle());
        model.write(sw, "TTL");
        return sw.toString();        
    }
    
    @Override
    public String asXML() {
        Model model = ModelFactory.createDefaultModel().read(IOUtils.toInputStream(asTurtle()), "http://ex.org/", "TTL");
        StringWriter sw = new StringWriter();
        model.write(sw);
        return sw.toString();
    }

}
