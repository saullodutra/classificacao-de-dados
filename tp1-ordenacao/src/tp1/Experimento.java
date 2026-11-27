package tp1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Etapas 3 e 4 - medição dos tempos de execução.
 *
 * Para cada combinação implementação x configuração x tamanho:
 *   1. gera UM vetor original (semente fixa, para ser reprodutível);
 *   2. para cada algoritmo: faz execuções de aquecimento (descartadas) e depois
 *      R execuções medidas, cada uma sobre uma CÓPIA do vetor original;
 *   3. mede só a chamada de ordenação com System.nanoTime();
 *   4. confere, fora da medição, se o vetor saiu ordenado;
 *   5. grava cada tempo individual numa linha do CSV.
 *
 * Uso:
 *   java tp1.Experimento --impl generico|int --config aleatorio|crescente|decrescente
 *        --ns 1000,10000 --algs Bubble,Quick|todos|nlogn|quadraticos|lineares
 *        --reps 30 --aquec 3 --saida resultados/tempos.csv [--rep-inicial 1]
 *
 * --rep-inicial permite dividir uma configuração longa em vários lotes
 * (ex.: repetições 1-3, depois 4-6...), cada um numa execução da JVM.
 *
 * Os dados de entrada: valores inteiros uniformes em [0, n). O vetor crescente é
 * o próprio vetor aleatório ordenado e o decrescente é ele invertido, de modo que
 * as três configurações têm exatamente os mesmos valores (o que importa para os
 * algoritmos lineares, cujo custo depende da faixa de valores).
 */
public final class Experimento {

    private static final long SEMENTE_BASE = 20261018L;

    public static void main(String[] args) throws IOException {
        String impl = "int", config = "aleatorio", algs = "todos", saida = "resultados/tempos.csv";
        int[] ns = {1000};
        int reps = 30, aquec = 3, repInicial = 1;
        boolean aquecGlobal = true;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--impl"))        impl = args[++i];
            else if (arg.equals("--config")) config = args[++i];
            else if (arg.equals("--ns"))     ns = Arrays.stream(args[++i].split(",")).mapToInt(Integer::parseInt).toArray();
            else if (arg.equals("--algs"))   algs = args[++i];
            else if (arg.equals("--reps"))   reps = Integer.parseInt(args[++i]);
            else if (arg.equals("--aquec"))  aquec = Integer.parseInt(args[++i]);
            else if (arg.equals("--saida"))  saida = args[++i];
            else if (arg.equals("--rep-inicial")) repInicial = Integer.parseInt(args[++i]);
            else if (arg.equals("--sem-aquecimento-global")) aquecGlobal = false;
            else throw new IllegalArgumentException("argumento desconhecido: " + arg);
        }

        Path csv = Path.of(saida);
        if (csv.getParent() != null) Files.createDirectories(csv.getParent());
        boolean novo = !Files.exists(csv);

        try (BufferedWriter w = Files.newBufferedWriter(csv, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (novo) {
                w.write("impl,algoritmo,config,n,rep,tempo_ns");
                w.newLine();
            }
            if (aquecGlobal) aquecimentoGlobal(impl);

            for (int n : ns) {
                int[] original = gera(config, n);
                if (impl.equals("generico")) {
                    for (Algoritmo.EntradaGenerica e : selecionaGenericos(algs)) {
                        rodaGenerico(e, original, config, n, repInicial, reps, aquec, w);
                    }
                } else {
                    for (Algoritmo.EntradaPrimitiva e : selecionaPrimitivos(algs)) {
                        rodaPrimitivo(e, original, config, n, repInicial, reps, aquec, w);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Geração dos dados
    // ------------------------------------------------------------------

    static int[] gera(String config, int n) {
        Random rnd = new Random(SEMENTE_BASE + n);
        int[] v = new int[n];
        for (int i = 0; i < n; i++) v[i] = rnd.nextInt(n); // uniforme em [0, n)
        // Arrays.sort aqui é só preparação dos dados de entrada, fora de qualquer medição
        if (config.equals("crescente")) {
            Arrays.sort(v);
        } else if (config.equals("decrescente")) {
            Arrays.sort(v);
            for (int i = 0, j = n - 1; i < j; i++, j--) { int t = v[i]; v[i] = v[j]; v[j] = t; }
        } else if (!config.equals("aleatorio")) {
            throw new IllegalArgumentException("config desconhecida: " + config);
        }
        return v;
    }

    // ------------------------------------------------------------------
    // Execução e medição
    // ------------------------------------------------------------------

    private static void rodaGenerico(Algoritmo.EntradaGenerica e, int[] original, String config, int n,
                                     int repInicial, int reps, int aquec, BufferedWriter w) throws IOException {
        Integer[] base = TesteCorretude.caixa(original);
        for (int k = 0; k < aquec; k++) e.alg().ordena(base.clone()); // descartadas
        for (int r = repInicial; r < repInicial + reps; r++) {
            Integer[] v = base.clone();          // cópia: fora da medição
            System.gc();                          // reduz chance de GC durante a medição
            long inicio = System.nanoTime();
            e.alg().ordena(v);
            long fim = System.nanoTime();
            if (!Ordenadores.estaOrdenado(v)) throw new IllegalStateException(e.nome() + " não ordenou!");
            grava(w, "generico", e.nome(), config, n, r, fim - inicio);
        }
        progresso("generico", e.nome(), config, n, reps);
    }

    private static void rodaPrimitivo(Algoritmo.EntradaPrimitiva e, int[] original, String config, int n,
                                      int repInicial, int reps, int aquec, BufferedWriter w) throws IOException {
        for (int k = 0; k < aquec; k++) e.alg().ordena(original.clone());
        for (int r = repInicial; r < repInicial + reps; r++) {
            int[] v = original.clone();
            System.gc();
            long inicio = System.nanoTime();
            e.alg().ordena(v);
            long fim = System.nanoTime();
            if (!OrdenadoresInt.estaOrdenado(v)) throw new IllegalStateException(e.nome() + " não ordenou!");
            grava(w, "int", e.nome(), config, n, r, fim - inicio);
        }
        progresso("int", e.nome(), config, n, reps);
    }

    private static void grava(BufferedWriter w, String impl, String alg, String config, int n, int rep, long ns)
            throws IOException {
        w.write(impl + "," + alg + "," + config + "," + n + "," + rep + "," + ns);
        w.newLine();
        w.flush(); // não perde dados se a execução for interrompida
    }

    private static void progresso(String impl, String alg, String config, int n, int reps) {
        System.out.printf(Locale.ROOT, "[ok] %-8s %-10s %-11s n=%-9d reps=%d%n", impl, alg, config, n, reps);
    }

    /**
     * Aquecimento da JVM: executa cada algoritmo várias vezes sobre vetores
     * pequenos, para que o compilador JIT otimize o código antes das medições.
     * Nada daqui é gravado.
     */
    private static void aquecimentoGlobal(String impl) {
        int[] v = gera("aleatorio", 5000);
        for (int k = 0; k < 15; k++) {
            if (impl.equals("generico")) {
                Integer[] b = TesteCorretude.caixa(v);
                for (Algoritmo.EntradaGenerica e : Algoritmo.GENERICOS) e.alg().ordena(b.clone());
            } else {
                for (Algoritmo.EntradaPrimitiva e : Algoritmo.PRIMITIVOS) e.alg().ordena(v.clone());
            }
        }
    }

    // ------------------------------------------------------------------
    // Seleção dos algoritmos
    // ------------------------------------------------------------------

    private static Set<String> nomes(String algs) {
        Set<String> s = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        s.addAll(Arrays.asList(algs.split(",")));
        return s;
    }

    private static List<Algoritmo.EntradaGenerica> selecionaGenericos(String algs) {
        List<Algoritmo.EntradaGenerica> r = new ArrayList<>();
        Set<String> s = nomes(algs);
        for (Algoritmo.EntradaGenerica e : Algoritmo.GENERICOS) {
            boolean ok;
            if (algs.equals("todos"))            ok = true;
            else if (algs.equals("quadraticos")) ok = e.quadratico();
            else if (algs.equals("nlogn") || algs.equals("rapidos")) ok = !e.quadratico();
            else                                 ok = s.contains(e.nome());
            if (ok) r.add(e);
        }
        return r;
    }

    private static List<Algoritmo.EntradaPrimitiva> selecionaPrimitivos(String algs) {
        List<Algoritmo.EntradaPrimitiva> r = new ArrayList<>();
        Set<String> s = nomes(algs);
        for (Algoritmo.EntradaPrimitiva e : Algoritmo.PRIMITIVOS) {
            boolean ok;
            if (algs.equals("todos"))            ok = true;
            else if (algs.equals("quadraticos")) ok = e.quadratico();
            else if (algs.equals("nlogn"))       ok = !e.quadratico() && !e.linear();
            else if (algs.equals("lineares"))    ok = e.linear();
            else if (algs.equals("rapidos"))     ok = !e.quadratico();   // n log n + lineares
            else                                 ok = s.contains(e.nome());
            if (ok) r.add(e);
        }
        return r;
    }
}
