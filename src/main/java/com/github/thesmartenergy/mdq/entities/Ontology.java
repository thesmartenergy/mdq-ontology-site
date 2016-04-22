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

import com.github.thesmartenergy.sparql.generate.jena.engine.PlanFactory;
import com.github.thesmartenergy.sparql.generate.jena.engine.RootPlan;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.LocationMapper;
import org.apache.jena.util.Locator;
import org.apache.jena.util.LocatorFile;

/**
 *
 * @author maxime.lefrancois
 */
@ApplicationScoped
public class Ontology implements Document {

    private static final Logger LOG = Logger.getLogger(Ontology.class.getSimpleName());

    private final String base = "http://w3id.org/multidimensional-quantity/";
    private final String uri = base + "resource/Ontology";
    
    private final Map<String, Document> documents = new HashMap<>();
    
    private FileManager fileManager;

    private String turtle;
    
    @Inject
    ServletContext context;

    @PostConstruct
    public void initialize() {
        try {
            String dir = context.getRealPath("/WEB-INF/classes/");
            LOG.info(dir);
            LOG.info(context.getRealPath("/WEB-INF/classes"));
            LOG.info(context.getClassLoader().getResource("/").toString());
            LOG.info(context.getClassLoader().getResource("/").getPath());
            LOG.info(context.getClassLoader().getResource("/").toURI().toString());

            // initialize file manager
            fileManager = FileManager.makeGlobal();
            Locator loc = new LocatorFile(dir);
            Model conf = RDFDataMgr.loadModel(dir + "/configuration.ttl");
            LocationMapper mapper = new LocationMapper(conf);
            fileManager.addLocator(loc);
            fileManager.setLocationMapper(mapper);
            FileManager.setGlobalFileManager(fileManager);

            turtle = IOUtils.toString(fileManager.open(uri));
            Model model = ModelFactory.createDefaultModel().read(IOUtils.toInputStream(turtle), base, "TTL");
            model.listSubjects().forEachRemaining(new Consumer<Resource>() {
                @Override
                public void accept(Resource t) {
                    if (!t.isURIResource()) {
                        return;
                    }
                    documents.put(t.getURI(), Ontology.this);
                }
            });
            documents.put(uri, this);

            // parse the SPARQL-Generate query and create plan
            String queryString = IOUtils.toString(fileManager.open(base + "query/Vocabulary"));
            PlanFactory factory = new PlanFactory(fileManager);
            RootPlan plan = factory.create(queryString);

            // parse each vocabulary
            File vocabDir = new File(Ontology.class.getResource("/vocab").toURI());
            for (File vocabFile : vocabDir.listFiles()) {
                // create the initial model
                Model vocabModel = ModelFactory.createDefaultModel();

                // create the initial binding
                String variable = "msg";
                String uri = "urn:iana:mime:application/json";
                String message = IOUtils.toString(new FileInputStream(vocabFile));
                QuerySolutionMap initialBinding = new QuerySolutionMap();
                TypeMapper typeMapper = TypeMapper.getInstance();
                RDFDatatype dt = typeMapper.getSafeTypeByName(uri);
                Node arqLiteral = NodeFactory.createLiteral(message, dt);
                RDFNode jenaLiteral = vocabModel.asRDFNode(arqLiteral);
                initialBinding.add(variable, jenaLiteral);

                // execute the plan
                plan.exec(initialBinding, vocabModel);
                
//                File f = new File("C:/temp/mdq/out.ttl");
//                OutputStream out = new FileOutputStream(f);
//                vocabModel.write(out, "TTL"); 
//                out.close();
                
                
                // get the uri of the vocabulary                
                Resource vocabResource = vocabModel.listSubjectsWithProperty(
                        vocabModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                        vocabModel.createResource("http://purl.org/vocommons/voaf#Vocabulary")).nextResource();
                
                // set the vocabulary
                final Vocabulary vocabularyDocument = new Vocabulary(this, vocabModel);
                documents.put(vocabResource.getURI(), vocabularyDocument);

                // get the uri of each of the resources defined by the vocabulary
                vocabModel.listSubjectsWithProperty(
                        vocabModel.createProperty("http://www.w3.org/2000/01/rdf-schema#isDefinedBy"),
                        vocabResource).forEachRemaining(new Consumer<Resource>() {
                            @Override
                            public void accept(Resource t) {
                                if (!t.isURIResource()) {
                                    return;
                                }
                                documents.put(t.getURI(), vocabularyDocument);
                            }
                        });
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "error while initializing app ", ex);
            throw new RuntimeException("error while initializing app ", ex);
        }
    }

    public Map<String, Document> getDocuments() {
        return documents;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public String getBase() {
        return base;
    }
    
    
    @Override
    public String asTurtle() {
        return turtle;
    }

}
