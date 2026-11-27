import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Menu do TP2: representações de grafo do modelo UML, leitura dos arquivos .gr do professor
 * e os algoritmos (buscas, ordenação topológica, Kruskal, Prim, Bellman-Ford e Dijkstra),
 * cada um com o grafo de entrada, o resultado e o tempo de execução.
 *
 * Compilar:  javac -d bin src/*.java
 * Executar:  java -cp bin Main      (rodar na pasta do projeto, onde fica a pasta grafos/)
 */
public class Main {

    private static final GraphReader.Representacao LA = GraphReader.Representacao.LA;
    private static final GraphReader.Representacao MA = GraphReader.Representacao.MA;
    private static final GraphReader.Representacao[] AMBAS = {LA, MA};

    /** Execuções descartadas antes de medir, para o JIT da JVM otimizar o código. */
    private static final int AQUECIMENTO = 5;

    private static final String PASTA_GRAFOS = "grafos";

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("================= TP2 - Grafos =================");
            System.out.println("1. Mostrar os grafos de exemplo (LA e MA)");
            System.out.println("2. Busca em largura (BFS)");
            System.out.println("3. Busca em profundidade (DFS)");
            System.out.println("4. Ordenacao topologica");
            System.out.println("5. Arvore geradora minima - Kruskal");
            System.out.println("6. Arvore geradora minima - Prim");
            System.out.println("7. Caminho minimo de origem unica - Bellman-Ford");
            System.out.println("8. Caminho minimo de origem unica - Dijkstra");
            System.out.println("9. Ler um grafo do professor (.gr) e rodar os algoritmos (LA e MA)");
            System.out.println("0. Sair");
            System.out.print("Opcao: ");
            if (!in.hasNextLine()) {
                return;
            }
            try {
                switch (in.nextLine().trim()) {
                    case "1" -> mostrarExemplos();
                    case "2" -> demonstrar("Busca em Largura (origem s)", Exemplos::grafoBusca,
                            g -> new BuscaEmLargura(g, 1), BuscaEmLargura::relatorio);
                    case "3" -> demonstrar("Busca em Profundidade", Exemplos::digrafoProfundidade,
                            BuscaEmProfundidade::new, BuscaEmProfundidade::relatorio);
                    case "4" -> {
                        demonstrar("Ordenacao Topologica (DAG das roupas do Cormen)", Exemplos::dagRoupas,
                                OrdenacaoTopologica::new, OrdenacaoTopologica::relatorio);
                        demonstrar("Ordenacao Topologica (digrafo da DFS, que tem ciclo)", Exemplos::digrafoProfundidade,
                                OrdenacaoTopologica::new, OrdenacaoTopologica::relatorio);
                    }
                    case "5" -> demonstrar("Kruskal", Exemplos::grafoAgm, Kruskal::new, Kruskal::relatorio);
                    case "6" -> demonstrar("Prim (raiz a)", Exemplos::grafoAgm, g -> new Prim(g, 0), Prim::relatorio);
                    case "7" -> {
                        demonstrar("Bellman-Ford (pesos negativos, origem s)", Exemplos::digrafoBellmanFord,
                                g -> new BellmanFord(g, 0), BellmanFord::relatorio);
                        demonstrar("Bellman-Ford (com ciclo negativo, origem s)", Exemplos::digrafoCicloNegativo,
                                g -> new BellmanFord(g, 0), BellmanFord::relatorio);
                    }
                    case "8" -> demonstrar("Dijkstra (origem s)", Exemplos::digrafoDijkstra,
                            g -> new Dijkstra(g, 0), Dijkstra::relatorio);
                    case "9" -> rodarArquivo(in);
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void mostrarExemplos() {
        titulo("Grafo nao dirigido da BFS (Cormen)");
        System.out.print(Exemplos.grafoBusca(LA));
        System.out.println("Mesmo grafo como matriz:");
        System.out.print(((AdjMatrixGraph) Exemplos.grafoBusca(MA)).imprimirMatriz());

        titulo("Digrafo da DFS (Cormen)");
        System.out.print(Exemplos.digrafoProfundidade(LA));

        titulo("DAG da ordenacao topologica (roupas, Cormen)");
        System.out.print(Exemplos.dagRoupas(LA));

        titulo("Grafo nao dirigido ponderado da arvore geradora minima (Cormen)");
        System.out.print(Exemplos.grafoAgm(LA));
        System.out.println("Mesmo grafo como matriz (pesos):");
        System.out.print(((AdjMatrixGraph) Exemplos.grafoAgm(MA)).imprimirMatriz());

        titulo("Digrafo do Bellman-Ford (Cormen, pesos negativos)");
        System.out.print(Exemplos.digrafoBellmanFord(LA));

        titulo("Digrafo do Dijkstra (Cormen)");
        System.out.print(Exemplos.digrafoDijkstra(LA));

        titulo("Digrafo ponderado (sample.gr do enunciado)");
        System.out.print(Exemplos.digrafoPonderado(LA));
        System.out.println("Mesmo digrafo como matriz (pesos):");
        System.out.print(((AdjMatrixGraph) Exemplos.digrafoPonderado(MA)).imprimirMatriz());
    }

    /**
     * Roda um algoritmo num grafo de exemplo, em LA e em MA: mostra o grafo de entrada,
     * o nome do algoritmo, o resultado e o tempo de execução.
     */
    private static <T> void demonstrar(String algoritmo, Function<GraphReader.Representacao, IGraph> criar,
                                       Function<IGraph, T> rodar, Function<T, String> relatorio) {
        for (GraphReader.Representacao rep : AMBAS) {
            IGraph g = criar.apply(rep);
            titulo("Algoritmo: " + algoritmo + " | " + GraphUtils.nomeRepresentacao(g));
            System.out.println("Grafo de entrada:");
            System.out.print(g);
            long ns = medir(() -> rodar.apply(g));
            System.out.print(relatorio.apply(rodar.apply(g)));
            imprimirTempo(ns);
        }
    }

    private static void rodarArquivo(Scanner in) {
        String caminho = escolherArquivo(in);
        if (caminho == null) {
            return;
        }
        for (GraphReader.Representacao rep : AMBAS) {
            GraphReader.Leitura leitura;
            try {
                leitura = GraphReader.ler(caminho, rep);
            } catch (IOException | RuntimeException e) {
                System.out.println("Erro ao ler o arquivo: " + e.getMessage());
                return;
            }
            IGraph g = leitura.grafo();
            titulo("Arquivo " + caminho + " | " + GraphUtils.nomeRepresentacao(g));
            System.out.printf("Arcos no arquivo: %d | pares repetidos juntados (menor peso): %d%n",
                    leitura.arcosNoArquivo(), leitura.paresRepetidos());
            System.out.println("(vertices de 0 a |V|-1: o vertice 1 do arquivo e o 0 aqui)");
            System.out.print(GraphUtils.imprimir(g, 8));

            long nsBfs = medir(() -> new BuscaEmLargura(g, 0));
            BuscaEmLargura bfs = new BuscaEmLargura(g, 0);
            System.out.println("-- Busca em Largura a partir do vertice 0: " + bfs.alcancados()
                    + " vertices alcancados, maior distancia = "
                    + Arrays.stream(bfs.getDist()).max().orElse(0));
            imprimirTempo(nsBfs);

            long nsDfs = medir(() -> new BuscaEmProfundidade(g));
            BuscaEmProfundidade dfs = new BuscaEmProfundidade(g);
            System.out.println("-- Busca em Profundidade: tem ciclo = " + (dfs.temCiclo() ? "sim" : "nao"));
            imprimirTempo(nsDfs);

            long nsTopo = medir(() -> new OrdenacaoTopologica(g));
            OrdenacaoTopologica topo = new OrdenacaoTopologica(g);
            System.out.println("-- Ordenacao Topologica: " + (topo.isDag()
                    ? "ordem com " + topo.getOrdem().size() + " vertices"
                    : "o digrafo tem ciclo, nao existe ordenacao topologica"));
            imprimirTempo(nsTopo);

            // Kruskal e Prim exigem grafo nao dirigido: (u, v) e (v, u) viram uma aresta, com o menor peso
            IGraph nd = GraphUtils.paraNaoDirigido(g);
            System.out.printf("-- Versao nao dirigida para a AGM: |E| = %d (o tempo da conversao nao entra)%n",
                    nd.numEdges());
            long nsKruskal = medir(() -> new Kruskal(nd));
            Kruskal kruskal = new Kruskal(nd);
            System.out.println("-- Kruskal: " + kruskal.getArestas().size() + " arestas, peso total = "
                    + GraphUtils.formatarPeso(kruskal.getPesoTotal()));
            imprimirTempo(nsKruskal);

            long nsPrim = medir(() -> new Prim(nd, 0));
            Prim prim = new Prim(nd, 0);
            System.out.println("-- Prim (raiz 0): " + prim.getArestas().size() + " arestas, peso total = "
                    + GraphUtils.formatarPeso(prim.getPesoTotal()));
            imprimirTempo(nsPrim);

            long nsBf = medir(() -> new BellmanFord(g, 0));
            BellmanFord bf = new BellmanFord(g, 0);
            System.out.println("-- Bellman-Ford a partir do vertice 0: " + bf.alcancados()
                    + " vertices alcancados, maior distancia = " + GraphUtils.formatarPeso(bf.maiorDistancia())
                    + (bf.temCicloNegativo() ? " (CICLO NEGATIVO)" : ""));
            imprimirTempo(nsBf);

            long nsDij = medir(() -> new Dijkstra(g, 0));
            Dijkstra dij = new Dijkstra(g, 0);
            System.out.println("-- Dijkstra a partir do vertice 0: " + dij.alcancados()
                    + " vertices alcancados, maior distancia = " + GraphUtils.formatarPeso(dij.maiorDistancia())
                    + (Arrays.equals(bf.getDist(), dij.getDist()) ? " (mesmas distancias do Bellman-Ford)"
                                                                  : " (DIFERENTE do Bellman-Ford!)"));
            imprimirTempo(nsDij);
        }
    }

    /** Lista os .gr da pasta grafos/ e devolve o escolhido (ou um caminho digitado). */
    private static String escolherArquivo(Scanner in) {
        File[] arquivos = new File(PASTA_GRAFOS).listFiles((dir, nome) -> nome.endsWith(".gr"));
        if (arquivos != null && arquivos.length > 0) {
            Arrays.sort(arquivos, (a, b) -> Long.compare(tamanho(a), tamanho(b)));
            System.out.println("Grafos em " + PASTA_GRAFOS + "/:");
            for (int i = 0; i < arquivos.length; i++) {
                System.out.printf("%3d. %s%n", i + 1, arquivos[i].getName());
            }
        } else {
            System.out.println("(pasta " + PASTA_GRAFOS + "/ nao encontrada; digite o caminho do arquivo)");
        }
        System.out.print("Numero ou caminho do arquivo: ");
        String resp = in.hasNextLine() ? in.nextLine().trim() : "";
        if (resp.isEmpty()) {
            return null;
        }
        if (arquivos != null && resp.matches("\\d+")) {
            int i = Integer.parseInt(resp) - 1;
            if (i < 0 || i >= arquivos.length) {
                System.out.println("Numero invalido.");
                return null;
            }
            return arquivos[i].getPath();
        }
        return resp;
    }

    /** Ordena os arquivos do GTgraph por |V| e depois por |E| (sample100-1980 antes de sample100-3960). */
    private static long tamanho(File f) {
        String[] p = f.getName().replaceAll("[^0-9-]", "").split("-");
        try {
            return Long.parseLong(p[0]) * 10_000_000L + Long.parseLong(p[1]);
        } catch (RuntimeException e) {
            return Long.MAX_VALUE;
        }
    }

    /** Roda o algoritmo AQUECIMENTO vezes sem medir e depois mede uma execução. */
    private static long medir(Supplier<Object> algoritmo) {
        for (int i = 0; i < AQUECIMENTO; i++) {
            algoritmo.get();
        }
        long inicio = System.nanoTime();
        algoritmo.get();
        return System.nanoTime() - inicio;
    }

    private static void imprimirTempo(long ns) {
        System.out.printf("Tempo de execucao: %.4f ms (%d ns)%n", ns / 1e6, ns);
    }

    private static void titulo(String t) {
        System.out.println();
        System.out.println("--- " + t + " ---");
    }
}
