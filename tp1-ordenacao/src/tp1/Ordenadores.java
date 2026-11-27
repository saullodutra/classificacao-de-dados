package tp1;

/**
 * Algoritmos de ordenação baseados em comparação, implementados de forma
 * genérica sobre a interface {@link Comparable}.
 *
 * Todos os métodos ordenam o vetor em ordem crescente, in-place (o Merge Sort
 * usa um vetor auxiliar de tamanho n).
 *
 * TP1 - Algoritmos e Classificação de Dados - UNIPAMPA Bagé.
 */
public final class Ordenadores {

    private Ordenadores() { }

    // ------------------------------------------------------------------
    // Métodos auxiliares
    // ------------------------------------------------------------------

    private static <T extends Comparable<T>> boolean menor(T x, T y) {
        return x.compareTo(y) < 0;
    }

    private static <T> void troca(T[] a, int i, int j) {
        T t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    // ------------------------------------------------------------------
    // Bubble Sort - O(n^2); melhor caso O(n) com parada antecipada
    // ------------------------------------------------------------------

    public static <T extends Comparable<T>> void bubblesort(T[] a, int n) {
        for (int i = 0; i < n - 1; i++) {
            boolean trocou = false;
            // após a passada i, os i+1 maiores já estão no fim
            for (int j = 0; j < n - 1 - i; j++) {
                if (menor(a[j + 1], a[j])) {
                    troca(a, j, j + 1);
                    trocou = true;
                }
            }
            if (!trocou) return; // vetor já ordenado
        }
    }

    // ------------------------------------------------------------------
    // Insertion Sort - O(n^2); melhor caso O(n)
    // ------------------------------------------------------------------

    public static <T extends Comparable<T>> void insertionsort(T[] a, int n) {
        for (int i = 1; i < n; i++) {
            T chave = a[i];
            int j = i - 1;
            while (j >= 0 && menor(chave, a[j])) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = chave;
        }
    }

    // ------------------------------------------------------------------
    // Selection Sort - Theta(n^2) em todos os casos
    // ------------------------------------------------------------------

    public static <T extends Comparable<T>> void selectionsort(T[] a, int n) {
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (menor(a[j], a[min])) min = j;
            }
            troca(a, i, min);
        }
    }

    // ------------------------------------------------------------------
    // Shell Sort - sequência de Knuth (1, 4, 13, 40, ...), ~O(n^{3/2})
    // ------------------------------------------------------------------

    public static <T extends Comparable<T>> void shellsort(T[] a, int n) {
        int h = 1;
        while (h < n / 3) h = 3 * h + 1;
        while (h >= 1) {
            // insertion sort com salto h
            for (int i = h; i < n; i++) {
                T chave = a[i];
                int j = i;
                while (j >= h && menor(chave, a[j - h])) {
                    a[j] = a[j - h];
                    j -= h;
                }
                a[j] = chave;
            }
            h /= 3;
        }
    }

    // ------------------------------------------------------------------
    // Heap Sort - O(n log n) em todos os casos
    // ------------------------------------------------------------------

    public static <T extends Comparable<T>> void heapsort(T[] a, int n) {
        // constrói o heap de máximo (bottom-up), O(n)
        for (int i = n / 2 - 1; i >= 0; i--) {
            desce(a, i, n);
        }
        // extrai o máximo repetidamente
        for (int fim = n - 1; fim > 0; fim--) {
            troca(a, 0, fim);
            desce(a, 0, fim);
        }
    }

    /** Restaura a propriedade de heap a partir do nó i, considerando a[0..tam-1]. */
    private static <T extends Comparable<T>> void desce(T[] a, int i, int tam) {
        T x = a[i];
        while (2 * i + 1 < tam) {
            int filho = 2 * i + 1;
            if (filho + 1 < tam && menor(a[filho], a[filho + 1])) filho++;
            if (!menor(x, a[filho])) break;
            a[i] = a[filho];
            i = filho;
        }
        a[i] = x;
    }

    // ------------------------------------------------------------------
    // Merge Sort (top-down) - Theta(n log n), memória extra Theta(n)
    // ------------------------------------------------------------------

    public static <T extends Comparable<T>> void mergesort(T[] a, int n) {
        T[] aux = a.clone(); // aloca o auxiliar uma única vez
        mergesort(a, aux, 0, n - 1);
    }

    public static <T extends Comparable<T>> void mergesort(T[] a, T[] aux, int lo, int hi) {
        if (lo >= hi) return;
        int meio = lo + (hi - lo) / 2;
        mergesort(a, aux, lo, meio);
        mergesort(a, aux, meio + 1, hi);
        intercala(a, aux, lo, meio, hi);
    }

    private static <T extends Comparable<T>> void intercala(T[] a, T[] aux, int lo, int meio, int hi) {
        System.arraycopy(a, lo, aux, lo, hi - lo + 1);
        int i = lo, j = meio + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > meio)                    a[k] = aux[j++];
            else if (j > hi)                 a[k] = aux[i++];
            else if (menor(aux[j], aux[i]))  a[k] = aux[j++];
            else                             a[k] = aux[i++]; // estável
        }
    }

    // ------------------------------------------------------------------
    // Quick Sort - pivô por mediana de três; médio O(n log n), pior O(n^2)
    // ------------------------------------------------------------------

    public static <T extends Comparable<T>> void quicksort(T[] a, int n) {
        quicksort(a, 0, n - 1);
    }

    public static <T extends Comparable<T>> void quicksort(T[] a, int lo, int hi) {
        if (lo >= hi) return;
        int p = particiona(a, lo, hi);
        quicksort(a, lo, p - 1);
        quicksort(a, p + 1, hi);
    }

    private static <T extends Comparable<T>> int particiona(T[] a, int lo, int hi) {
        // mediana de três: coloca a mediana de a[lo], a[meio], a[hi] em a[lo]
        int meio = lo + (hi - lo) / 2;
        if (menor(a[meio], a[lo])) troca(a, meio, lo);
        if (menor(a[hi], a[lo]))   troca(a, hi, lo);
        if (menor(a[hi], a[meio])) troca(a, hi, meio);
        troca(a, lo, meio);

        T v = a[lo];
        int i = lo, j = hi + 1;
        while (true) {
            while (menor(a[++i], v)) if (i == hi) break;
            while (menor(v, a[--j])) if (j == lo) break;
            if (i >= j) break;
            troca(a, i, j);
        }
        troca(a, lo, j);
        return j;
    }

    // ------------------------------------------------------------------
    // Verificação
    // ------------------------------------------------------------------

    public static <T extends Comparable<T>> boolean estaOrdenado(T[] a) {
        for (int i = 1; i < a.length; i++) {
            if (menor(a[i], a[i - 1])) return false;
        }
        return true;
    }
}
