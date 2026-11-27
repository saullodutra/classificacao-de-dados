/**
 * Grafos pequenos de exemplo, para rodar cada algoritmo e conferir o resultado à mão.
 * Os dois primeiros são os exemplos do Cormen, com os mesmos rótulos do livro.
 */
public final class Exemplos {

    private Exemplos() {
    }

    /** Grafo não dirigido e não ponderado da busca em largura do Cormen. Origem: s (id 1). */
    public static IGraph grafoBusca(GraphReader.Representacao rep) {
        IGraph g = novo(8, GraphType.UNDIRECTED, rep);
        rotular(g, "r", "s", "t", "u", "v", "w", "x", "y");
        int[][] arestas = {
            {0, 1}, {0, 4}, {1, 5}, {5, 2}, {5, 6},
            {2, 6}, {2, 3}, {6, 3}, {6, 7}, {3, 7}
        };
        for (int[] a : arestas) {
            g.addEdge(a[0], a[1]);
        }
        return g;
    }

    /** Dígrafo não ponderado da busca em profundidade do Cormen (tem ciclo e o laço z -> z). */
    public static IGraph digrafoProfundidade(GraphReader.Representacao rep) {
        IGraph g = novo(6, GraphType.DIRECTED, rep);
        rotular(g, "u", "v", "w", "x", "y", "z");
        int[][] arcos = {
            {0, 1}, {0, 3}, {3, 1}, {1, 4},
            {4, 3}, {2, 4}, {2, 5}, {5, 5}
        };
        for (int[] a : arcos) {
            g.addEdge(a[0], a[1]);
        }
        return g;
    }

    /** Dígrafo ponderado: o sample.gr do enunciado (5 vértices, 8 arcos), com rótulos de 1 a 5. */
    public static IGraph digrafoPonderado(GraphReader.Representacao rep) {
        IGraph g = novo(5, GraphType.DIRECTED, rep);
        rotular(g, "1", "2", "3", "4", "5");
        int[][] arcos = {
            {4, 3, 66}, {3, 4, 55}, {5, 4, 87}, {5, 3, 8},
            {3, 2, 78}, {2, 5, 12}, {4, 2, 65}, {5, 1, 4}
        };
        for (int[] a : arcos) {
            g.addEdge(a[0] - 1, a[1] - 1, a[2]);
        }
        return g;
    }

    /**
     * Dígrafo acíclico (DAG) da ordenação topológica do Cormen: as peças de roupa do
     * professor Bumstead. Aresta (u, v) = vestir u antes de v. O relógio não depende de nada.
     */
    public static IGraph dagRoupas(GraphReader.Representacao rep) {
        IGraph g = novo(9, GraphType.DIRECTED, rep);
        rotular(g, "cueca", "calca", "cinto", "camisa", "gravata", "paleto", "meias", "sapatos", "relogio");
        int[][] arcos = {
            {0, 1}, {0, 7}, {1, 2}, {1, 7}, {2, 5},
            {3, 2}, {3, 4}, {4, 5}, {6, 7}
        };
        for (int[] a : arcos) {
            g.addEdge(a[0], a[1]);
        }
        return g;
    }

    /**
     * Grafo não dirigido e ponderado da árvore geradora mínima do Cormen (vértices a até i).
     * A AGM tem peso total 37.
     */
    public static IGraph grafoAgm(GraphReader.Representacao rep) {
        IGraph g = novo(9, GraphType.UNDIRECTED, rep);
        rotular(g, "a", "b", "c", "d", "e", "f", "g", "h", "i");
        int[][] arestas = {
            {0, 1, 4}, {0, 7, 8}, {1, 2, 8}, {1, 7, 11}, {2, 3, 7}, {2, 5, 4}, {2, 8, 2},
            {3, 4, 9}, {3, 5, 14}, {4, 5, 10}, {5, 6, 2}, {6, 7, 1}, {6, 8, 6}, {7, 8, 7}
        };
        for (int[] a : arestas) {
            g.addEdge(a[0], a[1], a[2]);
        }
        return g;
    }

    /**
     * Dígrafo do Bellman-Ford do Cormen, COM PESOS NEGATIVOS (e sem ciclo negativo).
     * Origem s (id 0). Distâncias esperadas: t = 2, x = 4, y = 7, z = -2.
     */
    public static IGraph digrafoBellmanFord(GraphReader.Representacao rep) {
        IGraph g = novo(5, GraphType.DIRECTED, rep);
        rotular(g, "s", "t", "x", "y", "z");
        int[][] arcos = {
            {0, 1, 6}, {0, 3, 7}, {1, 2, 5}, {1, 3, 8}, {1, 4, -4},
            {2, 1, -2}, {3, 2, -3}, {3, 4, 9}, {4, 0, 2}, {4, 2, 7}
        };
        for (int[] a : arcos) {
            g.addEdge(a[0], a[1], a[2]);
        }
        return g;
    }

    /** O mesmo dígrafo do Bellman-Ford com o arco z -> x valendo -8: cria o ciclo negativo x -> t -> z -> x. */
    public static IGraph digrafoCicloNegativo(GraphReader.Representacao rep) {
        IGraph g = novo(5, GraphType.DIRECTED, rep);
        rotular(g, "s", "t", "x", "y", "z");
        int[][] arcos = {
            {0, 1, 6}, {0, 3, 7}, {1, 2, 5}, {1, 3, 8}, {1, 4, -4},
            {2, 1, -2}, {3, 2, -3}, {3, 4, 9}, {4, 0, 2}, {4, 2, -8}
        };
        for (int[] a : arcos) {
            g.addEdge(a[0], a[1], a[2]);
        }
        return g;
    }

    /**
     * Dígrafo do Dijkstra do Cormen (pesos não negativos). Origem s (id 0).
     * Distâncias esperadas: t = 8, x = 9, y = 5, z = 7.
     */
    public static IGraph digrafoDijkstra(GraphReader.Representacao rep) {
        IGraph g = novo(5, GraphType.DIRECTED, rep);
        rotular(g, "s", "t", "x", "y", "z");
        int[][] arcos = {
            {0, 1, 10}, {0, 3, 5}, {1, 2, 1}, {1, 3, 2}, {2, 4, 4},
            {3, 1, 3}, {3, 2, 9}, {3, 4, 2}, {4, 0, 7}, {4, 2, 6}
        };
        for (int[] a : arcos) {
            g.addEdge(a[0], a[1], a[2]);
        }
        return g;
    }

    private static IGraph novo(int n, GraphType tipo, GraphReader.Representacao rep) {
        return rep == GraphReader.Representacao.LA ? new AdjListGraph(n, tipo) : new AdjMatrixGraph(n, tipo);
    }

    private static void rotular(IGraph g, String... rotulos) {
        for (int i = 0; i < rotulos.length; i++) {
            g.vertices().get(i).setLabel(rotulos[i]);
        }
    }
}
