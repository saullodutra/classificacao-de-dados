package tp1;

/**
 * Versões especializadas para o tipo primitivo int dos algoritmos baseados em
 * comparação (extensão do TP1: genérico com Comparable x tipo primitivo).
 *
 * A lógica é idêntica à de {@link Ordenadores}; muda apenas o tipo dos
 * elementos: aqui não há chamadas a compareTo() nem acesso indireto a objetos
 * Integer espalhados no heap.
 */
public final class OrdenadoresInt {

    private OrdenadoresInt() { }

    private static void troca(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static void bubblesort(int[] a, int n) {
        for (int i = 0; i < n - 1; i++) {
            boolean trocou = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (a[j + 1] < a[j]) {
                    troca(a, j, j + 1);
                    trocou = true;
                }
            }
            if (!trocou) return;
        }
    }

    public static void insertionsort(int[] a, int n) {
        for (int i = 1; i < n; i++) {
            int chave = a[i];
            int j = i - 1;
            while (j >= 0 && chave < a[j]) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = chave;
        }
    }

    public static void selectionsort(int[] a, int n) {
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (a[j] < a[min]) min = j;
            }
            troca(a, i, min);
        }
    }

    public static void shellsort(int[] a, int n) {
        int h = 1;
        while (h < n / 3) h = 3 * h + 1;
        while (h >= 1) {
            for (int i = h; i < n; i++) {
                int chave = a[i];
                int j = i;
                while (j >= h && chave < a[j - h]) {
                    a[j] = a[j - h];
                    j -= h;
                }
                a[j] = chave;
            }
            h /= 3;
        }
    }

    public static void heapsort(int[] a, int n) {
        for (int i = n / 2 - 1; i >= 0; i--) desce(a, i, n);
        for (int fim = n - 1; fim > 0; fim--) {
            troca(a, 0, fim);
            desce(a, 0, fim);
        }
    }

    private static void desce(int[] a, int i, int tam) {
        int x = a[i];
        while (2 * i + 1 < tam) {
            int filho = 2 * i + 1;
            if (filho + 1 < tam && a[filho] < a[filho + 1]) filho++;
            if (x >= a[filho]) break;
            a[i] = a[filho];
            i = filho;
        }
        a[i] = x;
    }

    public static void mergesort(int[] a, int n) {
        int[] aux = new int[n];
        mergesort(a, aux, 0, n - 1);
    }

    public static void mergesort(int[] a, int[] aux, int lo, int hi) {
        if (lo >= hi) return;
        int meio = lo + (hi - lo) / 2;
        mergesort(a, aux, lo, meio);
        mergesort(a, aux, meio + 1, hi);
        intercala(a, aux, lo, meio, hi);
    }

    private static void intercala(int[] a, int[] aux, int lo, int meio, int hi) {
        System.arraycopy(a, lo, aux, lo, hi - lo + 1);
        int i = lo, j = meio + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > meio)            a[k] = aux[j++];
            else if (j > hi)         a[k] = aux[i++];
            else if (aux[j] < aux[i]) a[k] = aux[j++];
            else                     a[k] = aux[i++];
        }
    }

    public static void quicksort(int[] a, int n) {
        quicksort(a, 0, n - 1);
    }

    public static void quicksort(int[] a, int lo, int hi) {
        if (lo >= hi) return;
        int p = particiona(a, lo, hi);
        quicksort(a, lo, p - 1);
        quicksort(a, p + 1, hi);
    }

    private static int particiona(int[] a, int lo, int hi) {
        int meio = lo + (hi - lo) / 2;
        if (a[meio] < a[lo]) troca(a, meio, lo);
        if (a[hi] < a[lo])   troca(a, hi, lo);
        if (a[hi] < a[meio]) troca(a, hi, meio);
        troca(a, lo, meio);

        int v = a[lo];
        int i = lo, j = hi + 1;
        while (true) {
            while (a[++i] < v) if (i == hi) break;
            while (v < a[--j]) if (j == lo) break;
            if (i >= j) break;
            troca(a, i, j);
        }
        troca(a, lo, j);
        return j;
    }

    public static boolean estaOrdenado(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i] < a[i - 1]) return false;
        }
        return true;
    }
}
