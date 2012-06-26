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
 * This class handles the search of a compound within the chebi database. There
 * are two ways to search, either in the names only or an extended search taking
 * also synonyms into account.
 *
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class SearchChebi {

    /**
     * Search the chebi database for molecule having the given string in their
     * name. 
     * The data returned contains the chebi identifier, the name and synonyms of
     * the molecule in chebi.
     *
     * @param compound_name a string, name of the molecule to search in chebi.
     * @return a dictionary containing all the molecule found for having the
     * input string in their name. The data structure returned is like: {string:
     * {'name': string, 'syn': [String]}}, where the keys are the chebi
     * identifier and the values are dictionaries containing the name of the
     * molecules and a list of its synonym.
     */
    public static HashMap<String, HashMap<
            String, ArrayList<String>>> SearchChebiSimple(String compound_name) {
        QueryRdf query = new QueryRdf();
        return query.getExactChebiFromSearch(compound_name);
    }
    
    /**
     * Search the chebi database for molecule having the given string in their
     * name or in their synonyms.
     * The data returned contains the chebi identifier, the name and synonyms of
     * the molecule in chebi.
     *
     * @param compound_name a string, name of the molecule to search in chebi.
     * @return a dictionary containing all the molecule found for having the
     * input string in their name or in their synonyms. The data structure
     * returned is like: {string: {'name': string, 'syn': [String]}}, where the
     * keys are the chebi identifier and the values are dictionaries containing
     * the name of the molecules and a list of its synonym.
     */
    public static HashMap<String, HashMap<
            String, ArrayList<String>>> SearchChebiExtended(String compound_name) {
        QueryRdf query = new QueryRdf();
        return query.getExtendedChebiFromSearch(compound_name);
    }
}
