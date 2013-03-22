/*
 * Copyright 2013 Wageningen UR Plant breeding.
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
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * This class handles the query of the endpoints (either locally (in a given
 * model) or remotely (on a given endpoint)).
 *
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class QueryRdf extends QueryRdfEngine {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(
            QueryRdf.class.getName());
    /**
     * Graph containing the ITAG information.
     */
    private String itag = "FROM <http://itag2.pbr.wur.nl/> \n";
    /**
     * Graph containing UNIPROT.
     */
    private final String uniprot = "FROM <http://uniprot.pbr.wur.nl/> \n";
    /**
     * Graph containing Chebi from EBI.
     */
    private final String chebi = "FROM <http://chebi.pbr.wur.nl/>";
    /**
     * Graph containing rhea from EBI.
     */
    private final String rhea = "FROM <http://rhea.pbr.wur.nl/> \n";

    /**
     * Default constructor.
     */
    public QueryRdf() {
    }

    /**
     * Constructor setting the URI used.
     *
     * @param uri to set
     */
    public QueryRdf(final String uri) {
        this.URI = uri;
    }

    private String arrayListToString(ArrayList<String> arrayList) {
        String string = "";
        for (int cnt = 0; cnt < arrayList.size(); cnt++) {
            string = string
                    + "<http://purl.uniprot.org/uniprot/"
                    + arrayList.get(cnt) + ">";
            if (cnt + 1 == arrayList.size()) {
                string = string + "\n";
            } else {
                string = string + ", \n";
            }
        }
        return string;
    }

    /**
     * Search the chebi database for molecule having the given string in their
     * name. The data returned contains the chebi identifier, the name and
     * synonyms of the molecule in chebi.
     *
     * @param name a string, name of the molecule to search in chebi.
     * @return a dictionary containing all the molecule found for having the
     * input string in their name. The data structure returned is like: {string:
     * {'name': string, 'syn': [String]}}, where the keys are the chebi
     * identifier and the values are dictionaries containing the name of the
     * molecules and a list of its synonym.
     */
    public final HashMap<String, HashMap<String, ArrayList<String>>> getExactChebiFromSearch(final String name) {
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
                + "    PREFIX obo:<http://purl.obolibrary.org/obo#> \n"
                + "    SELECT DISTINCT ?id ?name ?syn \n"
                + chebi
                + "    WHERE { \n"
                + "      { \n"
                + "        ?id rdfs:label ?name . \n"
                + "        ?id obo:Synonym ?syn . \n"
                + "        FILTER ( \n"
                + "            regex(?name, \"" + name + "\", \"i\") \n"
                + "        ) \n"
                + "      } \n"
                + "    } ORDER BY ?id ";
//        System.out.println(querystring);
        String[] keys = {"id", "name", "syn"};
        matrix = this.remoteSelectQuery(querystring, matrix, keys);

        HashMap<String, HashMap<String, ArrayList<String>>> output =
                new HashMap<String, HashMap<String, ArrayList<String>>>();
        for (ArrayList<String> rows : matrix) {
            String[] tmp1 = rows.get(0).split("/");
            String[] tmp2 = tmp1[tmp1.length - 1].split("_");
            String chebi_id = tmp2[tmp2.length - 1];
            if (output.containsKey(chebi_id)) {
                HashMap<String, ArrayList<String>> tmp = output.get(chebi_id);
                ArrayList<String> syns = tmp.get("syn");
                syns.add(rows.get(2));
                tmp.put("syn", syns);
                output.put(chebi_id, tmp);
            } else {
                HashMap<String, ArrayList<String>> tmp =
                        new HashMap<String, ArrayList<String>>();
                ArrayList<String> names = new ArrayList<String>();
                names.add(rows.get(1));
                ArrayList<String> syns = new ArrayList<String>();
                syns.add(rows.get(2));
                tmp.put("name", names);
                tmp.put("syn", syns);
                output.put(chebi_id, tmp);
            }
        }
        return output;
    }

    /**
     * Search the chebi database for molecule having the given string in their
     * name or in their synonyms. The data returned contains the chebi
     * identifier, the name and synonyms of the molecule in chebi.
     *
     * @param name a string, name of the molecule to search in chebi.
     * @return a dictionary containing all the molecule found for having the
     * input string in their name or in their synonyms. The data structure
     * returned is like: {string: {'name': string, 'syn': [String]}}, where the
     * keys are the chebi identifier and the values are dictionaries containing
     * the name of the molecules and a list of its synonym.
     */
    public final HashMap<String, HashMap<String, ArrayList<String>>> getExtendedChebiFromSearch(final String name) {
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
                + "    PREFIX obo:<http://purl.obolibrary.org/obo#> \n"
                + "    SELECT DISTINCT ?id ?name ?syn \n"
                + chebi
                + "    WHERE { \n"
                + "      { \n"
                + "        ?id rdfs:label ?name . \n"
                + "        ?id obo:Synonym ?syn . \n"
                + "        FILTER ( \n"
                + "            regex(?name, \"" + name + "\", \"i\") \n"
                + "                  || regex(?syn, \"" + name + "\", \"i\")\n"
                + "        ) \n"
                + "      } \n"
                + "    } ORDER BY ?id ";
//        System.out.println(querystring);
        String[] keys = {"id", "name", "syn"};
        matrix = this.remoteSelectQuery(querystring, matrix, keys);

        HashMap<String, HashMap<String, ArrayList<String>>> output =
                new HashMap<String, HashMap<String, ArrayList<String>>>();
        for (ArrayList<String> rows : matrix) {
            String[] tmp1 = rows.get(0).split("/");
            String[] tmp2 = tmp1[tmp1.length - 1].split("_");
            String chebi_id = tmp2[tmp2.length - 1];
            if (output.containsKey(chebi_id)) {
                HashMap<String, ArrayList<String>> tmp = output.get(chebi_id);
                ArrayList<String> syns = tmp.get("syn");
                syns.add(rows.get(2));
                tmp.put("syn", syns);
                output.put(chebi_id, tmp);
            } else {
                HashMap<String, ArrayList<String>> tmp =
                        new HashMap<String, ArrayList<String>>();
                ArrayList<String> names = new ArrayList<String>();
                names.add(rows.get(1));
                ArrayList<String> syns = new ArrayList<String>();
                syns.add(rows.get(2));
                tmp.put("name", names);
                tmp.put("syn", syns);
                output.put(chebi_id, tmp);
            }
        }
        return output;
    }

    /**
     * Returns the genes associated with proteins.
     *
     * @param data a dictionary where the keys are reactions identifier and the
     * values lists of proteins identifier.
     * @return a dictionary containing all the genes related with the proteins
     * specified. The data structure returned is like: {string: [{String:
     * String}]}, where the keys are the uniprot identifier and the values are
     * list of gene identifier associated with the protein.
     */
    public final HashMap<String, ArrayList<
            HashMap<String, String>>> getGenesOfProteins(
            HashMap<String, ArrayList<String>> data) {
        HashMap<String, ArrayList<HashMap<String, String>>> output =
                new HashMap<String, ArrayList<HashMap<String, String>>>();
        for (Entry<String, ArrayList<String>> entry : data.entrySet()) {
            ArrayList<String> arrayList = entry.getValue();
            String proteins = arrayListToString(arrayList);

            ArrayList<ArrayList<String>> matrix =
                    new ArrayList<ArrayList<String>>();
            String querystring =
                    "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                    + "        PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                    + "        SELECT DISTINCT ?prot ?name ?sca ?start ?stop ?desc \n"
                    + itag
                    + "        WHERE{ \n"
                    + "            ?gene gene:Protein ?prot . \n"
                    + "                FILTER ( \n"
                    + "                ?prot IN ( \n"
                    + proteins
                    + "                ) \n"
                    + "            ) \n"
                    + "            ?gene gene:Position ?pos . \n"
                    + "            ?pos pos:Scaffold ?sca . \n"
                    + "            ?gene gene:Description ?desc . \n"
                    + "            ?gene gene:FeatureName ?name . \n"
                    + "            ?pos pos:Start ?start . \n"
                    + "            ?pos pos:Stop ?stop . \n"
                    + "        } ORDER BY ?name \n";
            String[] keys = {"prot", "name", "sca", "start", "stop", "desc"};
//            System.out.println(querystring);
            matrix = this.remoteSelectQuery(querystring, matrix, keys);
            for (ArrayList<String> rows : matrix) {
                String[] tmp1 = rows.get(0).split("/");
                String prot_id = tmp1[tmp1.length - 1];

                HashMap<String, String> gene = new HashMap<String, String>();
                for (int cnt = 0; cnt < keys.length; cnt++) {
                    gene.put(keys[cnt], rows.get(cnt));
                }

                if (output.containsKey(prot_id)) {
                    ArrayList<HashMap<String, String>> tmp = output.get(prot_id);
                    tmp.add(gene);
                    output.put(prot_id, tmp);
                } else {
                    ArrayList<HashMap<String, String>> tmp =
                            new ArrayList<HashMap<String, String>>();
                    tmp.add(gene);
                    output.put(prot_id, tmp);
                }
            }
        }
        return output;
    }

    /**
     * Returns the all organism associated with the proteins.
     *
     * @param data a dictionary where the keys are reactions identifier and the
     * values lists of proteins.
     * @return a dictionary containing all the organism related with the
     * proteins specified. The data structure returned is like: {string:
     * [String]}, where the keys are the uniprot identifier and the values are
     * list of organisms associated with the protein.
     */
    public final HashMap<String, ArrayList<String>> getOrganismOfProteins(
            HashMap<String, ArrayList<String>> data) {
        HashMap<String, ArrayList<String>> output =
                new HashMap<String, ArrayList<String>>();
        for (Entry<String, ArrayList<String>> entry : data.entrySet()) {
            ArrayList<String> arrayList = entry.getValue();
            String proteins = arrayListToString(arrayList);

            ArrayList<ArrayList<String>> matrix =
                    new ArrayList<ArrayList<String>>();
            String querystring =
                    "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                    + "        SELECT DISTINCT ?prot ?name \n"
                    + uniprot
                    + "        WHERE { \n"
                    + "            ?prot uniprot:organism ?orga . \n"
                    + "            ?orga uniprot:scientificName ?name . \n"
                    + "            FILTER ( \n"
                    + "                ?prot IN ( \n"
                    + proteins
                    + "                ) \n"
                    + "            ) \n"
                    + "        }";
            String[] keys = {"prot", "name"};
            matrix = this.remoteSelectQuery(querystring, matrix, keys);
            for (ArrayList<String> rows : matrix) {
                String[] tmp1 = rows.get(0).split("/");
                String prot_id = tmp1[tmp1.length - 1];
                String orga = rows.get(1);

                if (output.containsKey(prot_id)) {
                    ArrayList<String> tmp = output.get(prot_id);
                    if (!tmp.contains(orga)) {
                        tmp.add(orga);
                    }
                    output.put(prot_id, tmp);
                } else {
                    ArrayList<String> tmp = new ArrayList<String>();
                    tmp.add(orga);
                    output.put(prot_id, tmp);
                }
            }
        }
        return output;
    }

    /**
     * Returns the pathways associated with proteins.
     *
     * @param data a dictionary where the keys are reactions identifier and the
     * values lists of proteins.
     * @return a dictionary containing all the pathways related with the
     * proteins specified. The data structure returned is like: {string:
     * [String]}, where the keys are the uniprot identifier and the values are
     * list of pathways associated with the protein.
     */
    public final HashMap<String, ArrayList<String>> getPathwaysOfProteins(
            HashMap<String, ArrayList<String>> data) {
        HashMap<String, ArrayList<String>> output =
                new HashMap<String, ArrayList<String>>();
        for (Entry<String, ArrayList<String>> entry : data.entrySet()) {
            ArrayList<String> arrayList = entry.getValue();
            String proteins = arrayListToString(arrayList);

            ArrayList<ArrayList<String>> matrix =
                    new ArrayList<ArrayList<String>>();
            String querystring =
                    "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                    + "        PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                    + "        PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
                    + "        SELECT DISTINCT ?prot ?desc \n"
                    + uniprot
                    + "        WHERE { \n"
                    + "            ?prot uniprot:annotation ?annot . \n"
                    + "            ?annot rdfs:seeAlso ?url . \n"
                    + "            ?annot rdfs:comment ?desc . \n"
                    + "            FILTER ( \n"
                    + "                ?prot IN ( \n"
                    + proteins
                    + "                ) \n"
                    + "            ) \n"
                    + "        }";
            String[] keys = {"prot", "desc"};
//            System.out.println(querystring);
            matrix = this.remoteSelectQuery(querystring, matrix, keys);
            for (ArrayList<String> rows : matrix) {
                String[] tmp1 = rows.get(0).split("/");
                String prot_id = tmp1[tmp1.length - 1];
                String desc = rows.get(1);

                if (output.containsKey(prot_id)) {
                    ArrayList<String> tmp = output.get(prot_id);
                    if (!tmp.contains(desc)) {
                        tmp.add(desc);
                    }
                    output.put(prot_id, tmp);
                } else {
                    ArrayList<String> tmp = new ArrayList<String>();
                    tmp.add(desc);
                    output.put(prot_id, tmp);
                }
            }
        }
        return output;
    }

    /**
     * Returns the all protein associated with a compound.
     *
     * @param chebi_id a string, identifier of a compound on chebi.
     * @return a dictionary containing all the proteins related with the
     * compound specified. The data structure returned is like: {string:
     * [String]}, where the keys are reaction identifiers and the values are
     * list of proteins associated with the reaction.
     */
    public final HashMap<String, ArrayList<String>> getProteinOfChebi(
            String chebi_id) {
        HashMap<String, ArrayList<String>> output =
                new HashMap<String, ArrayList<String>>();

        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "prefix bp: <http://www.biopax.org/release/biopax-level2.owl#> \n"
                + "    SELECT DISTINCT ?react ?xref \n"
                + rhea
                + "    WHERE { \n"
                + "      ?cmp bp:XREF <http://www.ebi.ac.uk/rhea#CHEBI:"
                + chebi_id + "> . \n"
                + "      ?dir ?p ?cmp . \n"
                + "      ?react ?p2 ?dir . \n"
                + "      ?react bp:XREF ?xref . \n"
                + "      FILTER ( \n"
                + "        regex(?xref, 'UNIPROT') \n"
                + "      ) \n"
                + "    } \n";
        String[] keys = {"react", "xref"};
//        System.out.println(querystring);
        matrix = this.remoteSelectQuery(querystring, matrix, keys);
        for (ArrayList<String> rows : matrix) {
            String[] tmp1 = rows.get(0).split("#");
            String react_id = tmp1[tmp1.length - 1];
            String[] tmp2 = rows.get(1).split("UNIPROT:");
            String prot = tmp2[tmp2.length - 1];

            if (output.containsKey(react_id)) {
                ArrayList<String> tmp = output.get(react_id);
                if (!tmp.contains(prot)) {
                    tmp.add(prot);
                }
                output.put(react_id, tmp);
            } else {
                ArrayList<String> tmp = new ArrayList<String>();
                tmp.add(prot);
                output.put(react_id, tmp);
            }
        }
        return output;
    }
}
