package pluradj.titan.peapod.example;

import com.thinkaurelius.titan.core.Cardinality;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import peapod.FramedGraph;
import pluradj.titan.peapod.frame.Person;

public class PeapodExample {
    // http://bayofmany.github.io/#framing

    // this value should match the Vertex annotation value for Person (@Vertex("person"))
    // if no value is supplied, this value should be Person.class.getSimpleName()
    public static final String LABEL_PERSON = "person";
    // this value should match the Property annotation in Person.name
    public static final String PROPERTY_NAME = "name";

    public static final String INDEX_NAME = "nameIndex";

    private TitanGraph titanGraph;
    private FramedGraph framedGraph;

    public PeapodExample() {
        createGraph();
        createSchema();
        loadGraph();
    }

    private void createGraph() {
        // create the Titan graph
        titanGraph = TitanFactory.open("inmemory");

        // create the Peapod framed graph
        framedGraph = new FramedGraph(titanGraph, Person.class.getPackage());
    }

    private void createSchema() {
        // create name property so that it can be indexed
        final TitanManagement mgmt = titanGraph.openManagement();
        final PropertyKey name = mgmt.makePropertyKey(PROPERTY_NAME).dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.buildIndex(INDEX_NAME, Vertex.class).addKey(name).buildCompositeIndex();
        mgmt.commit();
    }

    private void loadGraph() {
        // create a person using the Titan graph
        final Vertex madhatter = titanGraph.addVertex(T.label, LABEL_PERSON, PROPERTY_NAME, "mad hatter");

        // create a person using the Peapod framed graph
        final Person alice = framedGraph.addVertex(Person.class);
        alice.setName("alice");
    }

    public void dump() {
        // get the vertices directly from the Titan graph
        final GraphTraversalSource g = titanGraph.traversal();
        final Long hatterId = Long.valueOf(g.V().has(PROPERTY_NAME, "mad hatter").id().next().toString());
        final Long aliceId = Long.valueOf(g.V().has(PROPERTY_NAME, "alice").id().next().toString());

        // get the vertices from Peapod framed graph
        Person hatter = framedGraph.v(hatterId);
        Person alice = framedGraph.v(aliceId);
        System.out.println("id = " + hatterId + ", name = " + hatter.getName());
        System.out.println("id = " + aliceId + ", name = " + alice.getName());
    }

    public static void main(String[] args) {
        PeapodExample pe = new PeapodExample();
        pe.dump();
        System.exit(0);
    }
}
