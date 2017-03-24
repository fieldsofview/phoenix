import module.ontology.OntologyModule;

import java.io.FileNotFoundException;

/**
 * Created by onkar on 10/8/14.
 */
public class OntoTest {
	public static void main(String args[]) throws FileNotFoundException {
		OntologyModule ontologyModule=new OntologyModule();
		ontologyModule.setupOntologyWithFile("inst.nq", OntologyModule.NTRIPLE);
	}
}
