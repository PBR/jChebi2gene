/*
 *  Copyright 2011 Pierre-Yves Chibon <py@chibon.fr>.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package nl.wur.plantbreeding.chebi2gene;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class handles the core function of the chebi2gene package.
 * For a given chebi identifier it will search all the proteins related to this
 * compound and then retrieve all the pathways, genes and organisms associated
 * with these proteins.
 *
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class Chebi2gene {
    
    /**
     * A dictionary containing all the proteins related with the
     * compound specified.
     * The data structure returned is like: {string: [String]}, where the keys
     * are reaction identifiers and the values are list of proteins associated
     * with the reaction.
     */
    private HashMap<String, ArrayList<String>> proteins =
            new HashMap<String, ArrayList<String>>();
    /**
     * A dictionary containing all the pathways related with the
     * proteins specified.
     * The data structure returned is like: {string: [String]}, where the keys
     * are the uniprot identifier and the values are list of pathways associated
     * with the protein.
     */
    private HashMap<String, ArrayList<String>> pathways =
            new HashMap<String, ArrayList<String>>();
    /**
     * A dictionary containing all the organism related with the
     * proteins specified.
     * The data structure returned is like: {string: [String]}, where the keys
     * are the uniprot identifier and the values are list of organisms
     * associated with the protein.
     */
    private HashMap<String, ArrayList<String>> organisms =
            new HashMap<String, ArrayList<String>>();
    /**
     * A dictionary containing all the genes related with the proteins
     * specified.
     * The data structure returned is like: {string: [{String: String}]}, where
     * the keys are the uniprot identifier and the values are list of gene
     * hash map associated with the protein.
     */
    private HashMap<String, ArrayList<HashMap<String, String>>> genes =
            new HashMap<String, ArrayList<HashMap<String, String>>>();
    
    /**
     * Constructor which for a given chebi identifier will retrieve all
     * proteins associated with this compound and then all pathways, genes and
     * organisms associated with the proteins found.
     * The information can then be retrieved using the different getters
     * available.
     * @param chebi_id 
     */
    public void Chebi2gene(String chebi_id){
        QueryRdf query = new QueryRdf();
        proteins = query.getProteinOfChebi(chebi_id);
        pathways = query.getPathwaysOfProteins(proteins);
        genes = query.getGenesOfProteins(proteins);
        organisms = query.getOrganismOfProteins(proteins);
    }

    /**
     * Returns the genes information.
     * @return A dictionary containing all the genes related with the proteins
     * specified.
     */
    public HashMap<String, ArrayList<HashMap<String, String>>> getGenes() {
        return genes;
    }

    /**
     * Returns the organisms information.
     * @return A dictionary containing all the genes related with the proteins
     * specified.
     */
    public HashMap<String, ArrayList<String>> getOrganisms() {
        return organisms;
    }

    /**
     * Returns the pathways information.
     * @return A dictionary containing all the pathways related with the
     * proteins specified.
     */
    public HashMap<String, ArrayList<String>> getPathways() {
        return pathways;
    }

    /**
     * Returns the proteins information.
     * @return A dictionary containing all the proteins related with the
     * compound specified.
     */
    public HashMap<String, ArrayList<String>> getProteins() {
        return proteins;
    }
    
    
    
}
