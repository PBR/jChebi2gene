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

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import junit.framework.TestCase;

/**
 *
 * @author pierrey
 */
public class QueryRdfTest extends TestCase {
    
    /** Endpoint to query during the tests. */
    private String endpoint = "http://sparql.plantbreeding.nl:8080/sparql/";
    /** The QueryRdf object used to run the query. */
    private final QueryRdf instance = new QueryRdf();
    /** Chebi identifier used for the tests. */
    private final String chebi_id = "17578";
    
    public QueryRdfTest(String testName) {
        super(testName);
    }
    
    /**
     * Check whether a given url is available or not.
     * @param url a string of the url to test
     * @return a boolean true if the url is reachable, false otherwise
     */
    private boolean checkUrl(final String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            return ( con.getResponseCode() == HttpURLConnection.HTTP_OK );
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Browse all the network interface, retrieve all the IPs and checks if
     * one of them is in the 137.224 or 10.73 range.
     * Returns true if it is, false otherwise.
     * @return a boolean showing if the run if perform in the allowed network
     * range.
     * @throws SocketException if something goes wrong while trying to retrieve
     * the IP address.
     */
    private boolean allowedNetwork() throws SocketException {
        boolean allowed = false;
        Enumeration e = NetworkInterface.getNetworkInterfaces();

        while (e.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) e.nextElement();

            Enumeration e2 = ni.getInetAddresses();

            while (e2.hasMoreElements()) {
                InetAddress ipad = (InetAddress) e2.nextElement();
                String ip = ipad.toString();
                if (ip.startsWith("/137.224") || ip.startsWith("/10.73") ||
                        ip.startsWith("/192.168.41")) {
                    allowed = true;
                }
            }
        }
        return allowed;
    }

    @Override
    public final void setUp() throws SocketException {
        
        if (this.allowedNetwork()) {
            System.out.println("Using endpoint: " + endpoint);
            if (!checkUrl(endpoint)) {
                endpoint = "http://sparql-r:8890/sparql/";
                System.out.println("Fallback to: " + endpoint);
                if (!checkUrl(endpoint)) {
                    endpoint = null;
                    System.out.println("Both urls are not reachable -- tests fail");
                }
            }
        } else {
            System.out.println("Ip not in the allowed range");
            endpoint = null;
        }
        if (endpoint == null){
            assertNotNull(endpoint);
        }
        instance.endpoint = endpoint;
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getExactChebiFromSearch method, of class QueryRdf.
     */
    public void testGetExactChebiFromSearch() {
        System.out.println("getExactChebiFromSearch");
        String name = "-beta-carotene";
        HashMap<String, HashMap<String, ArrayList<String>>> result =
                instance.getExactChebiFromSearch(name);
        assertEquals(1, result.size());

        ArrayList<String> expectedName = new ArrayList<String>();
        expectedName.add("(5S,6R)-beta-carotene 5,6-epoxide^^http://www.w3.org/2001/XMLSchema#string");
        assertEquals(expectedName, result.get("35309").get("name"));
        
        assertEquals(3, result.get("35309").get("syn").size());
    }

    /**
     * Test of getExtendedChebiFromSearch method, of class QueryRdf.
     */
    public void testGetExtendedChebiFromSearch() {
        System.out.println("getExtendedChebiFromSearch");
        String name = "trans-beta-carotene";
        HashMap<String, HashMap<String, ArrayList<String>>> result =
                instance.getExtendedChebiFromSearch(name);
        assertEquals(1, result.size());

        ArrayList<String> expectedName = new ArrayList<String>();
        expectedName.add("beta-carotene^^http://www.w3.org/2001/XMLSchema#string");
        assertEquals(expectedName, result.get("17579").get("name"));

        ArrayList<String> expectedSyn = new ArrayList<String>();
        expectedSyn.add("all-trans-beta-carotene^^http://www.w3.org/2001/XMLSchema#string");
        assertEquals(expectedSyn, result.get("17579").get("syn"));
    }

    /**
     * Test of getGenesOfProteins method, of class QueryRdf.
     */
    public void testGetGenesOfProteins() {
        System.out.println("getGenesOfProteins");
        HashMap<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
        ArrayList<String> prot = new ArrayList<String>();
        prot.add("Q38933");
        data.put("key", prot);
        HashMap<String, ArrayList<HashMap<String, String>>> result =
                instance.getGenesOfProteins(data);
        assertEquals(4, result.get("Q38933").size());
    }

    /**
     * Test of getOrganismOfProteins method, of class QueryRdf.
     */
    public void testGetOrganismOfProteins() {
        System.out.println("getOrganismOfProteins");
        HashMap<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
        ArrayList<String> prot = new ArrayList<String>();
        prot.add("Q38933");
        data.put("key", prot);
        HashMap<String, ArrayList<String>> result =
                instance.getOrganismOfProteins(data);
        
        ArrayList<String> expectedOrga = new ArrayList<String>();
        expectedOrga.add("Arabidopsis thaliana");
        assertEquals(expectedOrga, result.get("Q38933"));
    }

    /**
     * Test of getPathwaysOfProteins method, of class QueryRdf.
     */
    public void testGetPathwaysOfProteins() {
        System.out.println("getPathwaysOfProteins");
        HashMap<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
        ArrayList<String> prot = new ArrayList<String>();
        prot.add("Q38933");
        data.put("key", prot);
        HashMap<String, ArrayList<String>> result = instance.getPathwaysOfProteins(data);
        ArrayList<String> expectedPath = new ArrayList<String>();
        expectedPath.add("Carotenoid biosynthesis; beta-carotene biosynthesis.");
        expectedPath.add("Carotenoid biosynthesis; beta-zeacarotene biosynthesis.");
        assertEquals(expectedPath, result.get("Q38933"));
    }

    /**
     * Test of getProteinOfChebi method, of class QueryRdf.
     */
    public void testGetProteinOfChebi() {
        System.out.println("getProteinOfChebi");
        HashMap<String, ArrayList<String>> result = instance.getProteinOfChebi(chebi_id);
        assertEquals(4, result.get("16740").size());
        assertTrue(result.get("16740").contains("P0C618"));
    }
}
