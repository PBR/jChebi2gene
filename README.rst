jChebi2gene
==========

:Author: Pierre-Yves Chibon <pierre-yves.chibon@wur.nl>, <pingou@pingoured.fr>


Java library to link a chebi compound or identifier to pathways
or tomato genes.


Get this project:
-----------------
Source:  https://github.com/PBR/jChebi2gene


Dependencies:
-------------

.. _Jena: http://jena.apache.org/
.. _ARQ: http://jena.apache.org/documentation/query/index.html

- `Jena`_
- `ARQ`_


Build the project:
------------------

This project uses maven to manage its dependencies, you can therefore build the sources using:

    mvn clean install

The unit-tests of jChebi2gene are off by default. A valid SPARQL endpoint is required to run use these unit tests.

To build the project with the unit-test, you can run:

    mvn clean install -DskipTests=false


License:
--------

This project is licensed under the new BSD license.
