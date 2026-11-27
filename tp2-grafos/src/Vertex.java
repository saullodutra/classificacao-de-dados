/**
 * Vértice do grafo (modelo UML): um id numérico e um rótulo opcional, como "s" ou "u".
 */
public class Vertex {

    private final int id;
    private String label;

    public Vertex(int id) {
        this(id, String.valueOf(id));
    }

    public Vertex(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
