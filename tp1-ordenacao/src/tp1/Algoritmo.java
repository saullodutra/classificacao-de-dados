package tp1;

import java.util.List;

/**
 * Catálogo dos algoritmos, para que testes e experimentos chamem todos da mesma
 * forma. Cada entrada recebe o vetor e o ordena por completo.
 * (Compatível com Java 11.)
 */
public final class Algoritmo {

    /** Algoritmo genérico sobre Integer[] (usa Comparable). */
    public interface Generico {
        void ordena(Integer[] a);
    }

    /** Algoritmo sobre int[] primitivo. */
    public interface Primitivo {
        void ordena(int[] a);
    }

    public static final class EntradaGenerica {
        private final String nome;
        private final boolean quadratico;
        private final Generico alg;

        EntradaGenerica(String nome, boolean quadratico, Generico alg) {
            this.nome = nome;
            this.quadratico = quadratico;
            this.alg = alg;
        }

        public String nome() { return nome; }
        public boolean quadratico() { return quadratico; }
        public Generico alg() { return alg; }
    }

    public static final class EntradaPrimitiva {
        private final String nome;
        private final boolean quadratico;
        private final boolean linear;
        private final Primitivo alg;

        EntradaPrimitiva(String nome, boolean quadratico, boolean linear, Primitivo alg) {
            this.nome = nome;
            this.quadratico = quadratico;
            this.linear = linear;
            this.alg = alg;
        }

        public String nome() { return nome; }
        public boolean quadratico() { return quadratico; }
        public boolean linear() { return linear; }
        public Primitivo alg() { return alg; }
    }

    private Algoritmo() { }

    /** Os 7 algoritmos de comparação, versão genérica (Comparable). */
    public static final List<EntradaGenerica> GENERICOS = List.of(
        new EntradaGenerica("Bubble",    true,  a -> Ordenadores.bubblesort(a, a.length)),
        new EntradaGenerica("Insertion", true,  a -> Ordenadores.insertionsort(a, a.length)),
        new EntradaGenerica("Selection", true,  a -> Ordenadores.selectionsort(a, a.length)),
        new EntradaGenerica("Shell",     false, a -> Ordenadores.shellsort(a, a.length)),
        new EntradaGenerica("Heap",      false, a -> Ordenadores.heapsort(a, a.length)),
        new EntradaGenerica("Merge",     false, a -> Ordenadores.mergesort(a, a.length)),
        new EntradaGenerica("Quick",     false, a -> Ordenadores.quicksort(a, 0, a.length - 1))
    );

    /** Os 7 algoritmos de comparação em int[] + os 3 lineares. */
    public static final List<EntradaPrimitiva> PRIMITIVOS = List.of(
        new EntradaPrimitiva("Bubble",    true,  false, a -> OrdenadoresInt.bubblesort(a, a.length)),
        new EntradaPrimitiva("Insertion", true,  false, a -> OrdenadoresInt.insertionsort(a, a.length)),
        new EntradaPrimitiva("Selection", true,  false, a -> OrdenadoresInt.selectionsort(a, a.length)),
        new EntradaPrimitiva("Shell",     false, false, a -> OrdenadoresInt.shellsort(a, a.length)),
        new EntradaPrimitiva("Heap",      false, false, a -> OrdenadoresInt.heapsort(a, a.length)),
        new EntradaPrimitiva("Merge",     false, false, a -> OrdenadoresInt.mergesort(a, a.length)),
        new EntradaPrimitiva("Quick",     false, false, a -> OrdenadoresInt.quicksort(a, 0, a.length - 1)),
        // Counting e Bucket precisam do maior valor; a busca do máximo (O(n)) faz parte do algoritmo
        new EntradaPrimitiva("Counting",  false, true,
            a -> { if (a.length > 0) OrdenadoresLineares.countingsort(a, a.length, OrdenadoresLineares.maximo(a)); }),
        new EntradaPrimitiva("Radix",     false, true,  a -> OrdenadoresLineares.radixsort(a, a.length)),
        new EntradaPrimitiva("Bucket",    false, true,
            a -> { if (a.length > 0) OrdenadoresLineares.bucketsort(a, a.length, OrdenadoresLineares.maximo(a)); })
    );
}
