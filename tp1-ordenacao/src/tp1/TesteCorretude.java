package tp1;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Etapa 1 - verificação prática da corretude.
 *
 * Parte A: vetores pequenos (10 a 15 elementos, tamanhos pares e ímpares) nos
 * casos crescente, decrescente, aleatório e com muitas repetições. Os vetores
 * antes e depois da ordenação são impressos para inspeção.
 *
 * Parte B (complementar): 2000 vetores aleatórios de tamanhos 0 a 300,
 * comparados contra um oráculo. O oráculo (Arrays.sort) é usado APENAS para
 * conferir a saída; ele não substitui nenhuma das implementações.
 *
 * Uso: java tp1.TesteCorretude [arquivo_saida]
 */
public final class TesteCorretude {

    public static void main(String[] args) throws IOException {
        PrintStream out = System.out;
        if (args.length > 0) {
            Path p = Path.of(args[0]);
            if (p.getParent() != null) Files.createDirectories(p.getParent());
            out = new PrintStream(Files.newOutputStream(p), true, "UTF-8");
        }

        Map<String, int[]> casos = new LinkedHashMap<>();
        casos.put("Crescente (n=10)",         new int[]{1, 3, 4, 7, 9, 12, 15, 18, 21, 30});
        casos.put("Decrescente (n=11)",       new int[]{50, 44, 39, 31, 27, 20, 16, 11, 8, 3, 0});
        casos.put("Aleatorio (n=12)",         new int[]{23, 5, 41, 17, 0, 36, 9, 28, 14, 33, 2, 19});
        casos.put("Repetidos (n=13)",         new int[]{4, 1, 4, 2, 7, 1, 4, 2, 2, 7, 0, 4, 1});
        casos.put("Aleatorio (n=15)",         new int[]{88, 12, 57, 3, 99, 41, 26, 70, 8, 64, 35, 91, 19, 50, 77});
        casos.put("Repetidos, todos iguais (n=14)", new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6});

        int falhas = 0;
        out.println("=== PARTE A: vetores pequenos ===");
        for (Map.Entry<String, int[]> caso : casos.entrySet()) {
            int[] original = caso.getValue();
            int[] esperado = original.clone();
            Arrays.sort(esperado);
            out.println();
            out.println("Caso: " + caso.getKey());
            out.println("  entrada : " + Arrays.toString(original));
            out.println("  esperado: " + Arrays.toString(esperado));

            for (Algoritmo.EntradaGenerica e : Algoritmo.GENERICOS) {
                Integer[] v = caixa(original);
                e.alg().ordena(v);
                boolean ok = Arrays.equals(desembala(v), esperado);
                if (!ok) falhas++;
                out.printf("  %-10s (generico) %s %s%n", e.nome(), ok ? "OK   " : "FALHA", Arrays.toString(v));
            }
            for (Algoritmo.EntradaPrimitiva e : Algoritmo.PRIMITIVOS) {
                int[] v = original.clone();
                e.alg().ordena(v);
                boolean ok = Arrays.equals(v, esperado);
                if (!ok) falhas++;
                out.printf("  %-10s (int)      %s %s%n", e.nome(), ok ? "OK   " : "FALHA", Arrays.toString(v));
            }
        }

        out.println();
        out.println("=== PARTE B: 2000 vetores aleatorios (tamanhos 0..300) ===");
        Random rnd = new Random(2026);
        int testes = 0;
        for (int t = 0; t < 2000; t++) {
            int n = rnd.nextInt(301);
            int faixa = (t % 3 == 0) ? 5 : 1000; // 1/3 dos casos com muitas repetições
            int[] original = new int[n];
            for (int i = 0; i < n; i++) original[i] = rnd.nextInt(faixa);
            int[] esperado = original.clone();
            Arrays.sort(esperado);
            for (Algoritmo.EntradaGenerica e : Algoritmo.GENERICOS) {
                Integer[] v = caixa(original);
                e.alg().ordena(v);
                testes++;
                if (!Arrays.equals(desembala(v), esperado)) {
                    falhas++;
                    out.println("  FALHA " + e.nome() + " (generico) n=" + n);
                }
            }
            for (Algoritmo.EntradaPrimitiva e : Algoritmo.PRIMITIVOS) {
                int[] v = original.clone();
                e.alg().ordena(v);
                testes++;
                if (!Arrays.equals(v, esperado)) {
                    falhas++;
                    out.println("  FALHA " + e.nome() + " (int) n=" + n);
                }
            }
        }
        out.println("  execucoes verificadas: " + testes);
        out.println();
        out.println(falhas == 0 ? "RESULTADO: todos os testes passaram." : "RESULTADO: " + falhas + " falha(s).");
        if (out != System.out) {
            System.out.println(falhas == 0 ? "Todos os testes passaram." : falhas + " falha(s).");
            out.close();
        }
        if (falhas > 0) System.exit(1);
    }

    static Integer[] caixa(int[] a) {
        Integer[] r = new Integer[a.length];
        for (int i = 0; i < a.length; i++) r[i] = a[i];
        return r;
    }

    static int[] desembala(Integer[] a) {
        int[] r = new int[a.length];
        for (int i = 0; i < a.length; i++) r[i] = a[i];
        return r;
    }
}
