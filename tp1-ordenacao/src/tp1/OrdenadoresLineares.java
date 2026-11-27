package tp1;

/**
 * Algoritmos de ordenação de tempo linear (não baseados em comparação).
 *
 * Requisitos sobre a entrada (condições adotadas no TP1):
 *  - Counting Sort: inteiros no intervalo [0, k]; custo Theta(n + k).
 *  - Radix Sort (LSD): inteiros não negativos; d passadas de Counting Sort
 *    estável, uma por dígito na base b; custo Theta(d (n + b)).
 *  - Bucket Sort: inteiros em [0, max] distribuídos de forma aproximadamente
 *    uniforme; n baldes, cada um ordenado por Insertion Sort; custo médio
 *    Theta(n) sob a hipótese de uniformidade, pior caso Theta(n^2).
 */
public final class OrdenadoresLineares {

    private OrdenadoresLineares() { }

    /** Maior valor do vetor (assume n >= 1). */
    public static int maximo(int[] a) {
        int m = a[0];
        for (int x : a) if (x > m) m = x;
        return m;
    }

    // ------------------------------------------------------------------
    // Counting Sort (versão estável, como no Cormen)
    // ------------------------------------------------------------------

    /** Ordena a[0..n-1], com valores em [0, k]. */
    public static void countingsort(int[] a, int n, int k) {
        int[] c = new int[k + 1];
        for (int i = 0; i < n; i++) c[a[i]]++;           // contagem
        for (int v = 1; v <= k; v++) c[v] += c[v - 1];    // posições finais
        int[] b = new int[n];
        for (int i = n - 1; i >= 0; i--) b[--c[a[i]]] = a[i]; // de trás p/ frente: estável
        System.arraycopy(b, 0, a, 0, n);
    }

    // ------------------------------------------------------------------
    // Radix Sort LSD (dígito menos significativo primeiro)
    // ------------------------------------------------------------------

    /** Radix Sort na base 10 (versão didática). */
    public static void radixsort(int[] a, int n) {
        radixsort(a, n, 10);
    }

    /** Radix Sort numa base qualquer; usa Counting Sort estável por dígito. */
    public static void radixsort(int[] a, int n, int base) {
        if (n <= 1) return;
        int max = maximo(a);
        int[] b = new int[n];
        int[] c = new int[base];
        for (long exp = 1; max / exp > 0; exp *= base) {
            java.util.Arrays.fill(c, 0);
            for (int i = 0; i < n; i++) c[(int) ((a[i] / exp) % base)]++;
            for (int d = 1; d < base; d++) c[d] += c[d - 1];
            for (int i = n - 1; i >= 0; i--) {
                int dig = (int) ((a[i] / exp) % base);
                b[--c[dig]] = a[i];
            }
            System.arraycopy(b, 0, a, 0, n);
        }
    }

    // ------------------------------------------------------------------
    // Bucket Sort
    // ------------------------------------------------------------------

    /**
     * Bucket Sort com n baldes para inteiros em [0, max].
     * O elemento v vai para o balde floor(v * n / (max + 1)), o que equivale a
     * normalizar v para [0, 1) como no Cormen. Os baldes são representados
     * como segmentos contíguos de um vetor auxiliar (evita listas encadeadas)
     * e cada balde é ordenado por Insertion Sort.
     */
    public static void bucketsort(int[] a, int n, int max) {
        if (n <= 1) return;
        int nb = n;
        long divisor = (long) max + 1;
        int[] inicio = new int[nb + 1];

        // 1) tamanho de cada balde
        for (int i = 0; i < n; i++) inicio[balde(a[i], nb, divisor) + 1]++;
        // 2) posição inicial de cada balde
        for (int b = 1; b <= nb; b++) inicio[b] += inicio[b - 1];
        // 3) distribui os elementos nos baldes
        int[] pos = java.util.Arrays.copyOf(inicio, nb);
        int[] aux = new int[n];
        for (int i = 0; i < n; i++) aux[pos[balde(a[i], nb, divisor)]++] = a[i];
        // 4) ordena cada balde com Insertion Sort
        for (int b = 0; b < nb; b++) insercao(aux, inicio[b], inicio[b + 1]);
        // 5) concatena
        System.arraycopy(aux, 0, a, 0, n);
    }

    private static int balde(int v, int nb, long divisor) {
        return (int) ((long) v * nb / divisor);
    }

    /** Insertion Sort em a[lo..hi-1]. */
    private static void insercao(int[] a, int lo, int hi) {
        for (int i = lo + 1; i < hi; i++) {
            int chave = a[i];
            int j = i - 1;
            while (j >= lo && a[j] > chave) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = chave;
        }
    }
}
