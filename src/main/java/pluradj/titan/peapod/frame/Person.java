package pluradj.titan.peapod.frame;

import peapod.annotations.Property;
import peapod.annotations.Vertex;

@Vertex("person")
public abstract class Person {
    @Property("name")
    public abstract String getName();
    public abstract void setName(final String name);
}
