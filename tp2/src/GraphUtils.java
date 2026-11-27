import java.util.List;

/**
 * Funções auxiliares usadas pelos grafos e pelos algoritmos, fora da interface IGraph
 * (para a interface ficar igual ao modelo UML).
 */
public final class GraphUtils {

    /** Na impressão, cada vértice mostra no máximo esse número de vizinhos. */
    private static final int MAX_VIZINHOS_IMPRESSOS = 15;

    private GraphUtils() {
    }

    /**
     * Dada uma aresta incidente a x, devolve o vértice do outro lado.
     * No dígrafo, para uma aresta de adj(x), é sempre o destino v.
     */
    public static int outroExtremo(Edge e, int x) {
        int u = e.getU().getId();
        return u == x ? e.getV().getId() : u;
    }

    /** "66" para pesos inteiros, "2.5" para os outros. */
    public static String formatarPeso(double w) {
        return w == Math.rint(w) ? String.valueOf((long) w) : String.valueOf(w);
    }

    public static String nomeRepresentacao(IGraph g) {
        return g instanceof AdjMatrixGraph ? "Matriz de Adjacencia (MA)" : "Lista de Adjacencia (LA)";
    }

    /**
     * Densidade D = |E| / (|V| (|V| - 1)) para dígrafos, como no enunciado.
     * No grafo não dirigido cada aresta conta nos dois sentidos: D = 2|E| / (|V| (|V| - 1)).
     */
    public static double densidade(IGraph g) {
        int n = g.numVertices();
        if (n < 2) {
            return 0.0;
        }
        double arestas = g.type() == GraphType.DIRECTED ? g.numEdges() : 2.0 * g.numEdges();
        return arestas / ((double) n * (n - 1));
    }

    /** true se alguma aresta tem peso diferente de 1. */
    public static boolean ponderado(IGraph g) {
        for (Edge e : g.edges()) {
            if (e.getWeight() != 1.0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Versão NÃO DIRIGIDA de um dígrafo, na mesma representação, para rodar Kruskal e Prim
     * (que exigem grafo não dirigido) nos arquivos do professor.
     * Os arcos (u, v) e (v, u) viram uma aresta só, com o MENOR dos dois pesos.
     * Laços (u, u) são descartados, porque nunca entram numa árvore geradora.
     */
    public static IGraph paraNaoDirigido(IGraph g) {
        int n = g.numVertices();
        java.util.Map<Long, Double> menorPeso = new java.util.LinkedHashMap<>();
        for (Edge e : g.edges()) {
            int u = e.getU().getId();
            int v = e.getV().getId();
            if (u == v) {
                continue;
            }
            long chave = (long) Math.min(u, v) * n + Math.max(u, v);
            menorPeso.merge(chave, e.getWeight(), Math::min);
        }
        IGraph h = g instanceof AdjMatrixGraph
                ? new AdjMatrixGraph(n, GraphType.UNDIRECTED)
                : new AdjListGraph(n, GraphType.UNDIRECTED);
        for (int i = 0; i < n; i++) {
            h.vertices().get(i).setLabel(g.vertices().get(i).getLabel());
        }
        for (java.util.Map.Entry<Long, Double> e : menorPeso.entrySet()) {
            h.addEdge((int) (e.getKey() / n), (int) (e.getKey() % n), e.getValue());
        }
        return h;
    }

    /** Linha de resumo: tipo, representação, |V|, |E| e densidade. */
    public static String resumo(IGraph g) {
        return String.format("%s, %s | |V| = %d, |E| = %d, densidade = %.3f",
                g.type() == GraphType.DIRECTED ? "Digrafo (DIRECTED)" : "Grafo nao dirigido (UNDIRECTED)",
                nomeRepresentacao(g), g.numVertices(), g.numEdges(), densidade(g));
    }

    /**
     * Mostra o grafo como lista de adjacência (vale para LA e MA), por exemplo
     *   s: r w           (não ponderado)
     *   2: 5(12)         (ponderado: vizinho(peso))
     * Mostra no máximo maxVertices vértices e 15 vizinhos por vértice.
     */
    public static String imprimir(IGraph g, int maxVertices) {
        boolean comPeso = ponderado(g);
        List<Vertex> vs = g.vertices();
        StringBuilder sb = new StringBuilder(resumo(g)).append('\n');
        int limite = Math.min(g.numVertices(), maxVertices);
        for (int u = 0; u < limite; u++) {
            sb.append(String.format("%5s:", vs.get(u).getLabel()));
            List<Edge> adj = g.adj(u);
            int mostrar = Math.min(adj.size(), MAX_VIZINHOS_IMPRESSOS);
            for (int i = 0; i < mostrar; i++) {
                Edge e = adj.get(i);
                sb.append(' ').append(vs.get(outroExtremo(e, u)).getLabel());
                if (comPeso) {
                    sb.append('(').append(formatarPeso(e.getWeight())).append(')');
                }
            }
            if (adj.size() > mostrar) {
                sb.append(" ... (+").append(adj.size() - mostrar).append(')');
            }
            sb.append('\n');
        }
        if (limite < g.numVertices()) {
            sb.append("  ... (mais ").append(g.numVertices() - limite).append(" vertices)\n");
        }
        return sb.toString();
    }
}
