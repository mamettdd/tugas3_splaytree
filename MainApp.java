package tugas3;

import java.util.Random;

/**
 * Representasi Node untuk Splay Tree dan Semi-Splay Tree.
 */
class Node {
    int key;
    Node left, right, parent;

    public Node(int key) {
        this.key = key;
        this.left = null;
        this.right = null;
        this.parent = null;
    }
}

/**
 * Kelas Utama Basis untuk Splay Tree Klasik (Classic Splay Tree).
 */
class SplayTree {
    protected Node root;
    protected long rotationCount;

    public SplayTree() {
        this.root = null;
        this.rotationCount = 0;
    }

    public long getRotationCount() {
        return rotationCount;
    }

    public void resetRotationCount() {
        this.rotationCount = 0;
    }

    protected void rotateLeft(Node x) {
        Node y = x.right;
        if (y == null) return;
        
        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
        rotationCount++;
    }

    protected void rotateRight(Node x) {
        Node y = x.left;
        if (y == null) return;

        x.left = y.right;
        if (y.right != null) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
        rotationCount++;
    }

    /**
     * Operasi Splaying Standar (Full Splay - Bottom Up).
     * Membawa node x secara agresif hingga menjadi root utama.
     */
    protected void splay(Node x) {
        if (x == null) return;
        while (x.parent != null) {
            Node p = x.parent;
            Node g = p.parent;
            if (g == null) {
                if (x == p.left) rotateRight(p);
                else rotateLeft(p);
            } else if (x == p.left && p == g.left) {
                rotateRight(g);
                rotateRight(p);
            } else if (x == p.right && p == g.right) {
                rotateLeft(g);
                rotateLeft(p);
            } else if (x == p.right && p == g.left) {
                rotateLeft(p);
                rotateRight(g);
            } else {
                rotateRight(p);
                rotateLeft(g);
            }
        }
    }

    public void insert(int key) {
        Node z = new Node(key);
        Node y = null;
        Node x = this.root;

        while (x != null) {
            y = x;
            if (z.key < x.key) x = x.left;
            else x = x.right;
        }

        z.parent = y;
        if (y == null) this.root = z;
        else if (z.key < y.key) y.left = z;
        else y.right = z;

        splay(z);
    }

    public boolean search(int key) {
        Node x = this.root;
        Node lastAccessed = null;
        while (x != null) {
            lastAccessed = x;
            if (key == x.key) {
                splay(x);
                return true;
            } else if (key < x.key) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        if (lastAccessed != null) {
            splay(lastAccessed);
        }
        return false;
    }
}

/**
 * Kelas Variasi Modifikasi: Semi-Splay Tree.
 */
class SemiSplayTree extends SplayTree {

    /**
     * Operasi Semi-Splaying (Sleator & Tarjan Variant).
     * Pada kasus Zig-Zig, fokus pencarian splay rekursif dialihkan ke parent (p),
     * bukan dilanjutkan langsung dari x. Ini menghemat operasi rotasi internal.
     */
    @Override
    protected void splay(Node x) {
        if (x == null) return;
        while (x.parent != null) {
            Node p = x.parent;
            Node g = p.parent;
            if (g == null) {
                if (x == p.left) rotateRight(p);
                else rotateLeft(p);
            } else if (x == p.left && p == g.left) {
                rotateRight(g);
                x = p; // Perubahan Utama: Lompat ke parent untuk splay langkah berikutnya
            } else if (x == p.right && p == g.right) {
                rotateLeft(g);
                x = p; // Perubahan Utama: Lompat ke parent untuk splay langkah berikutnya
            } else if (x == p.right && p == g.left) {
                rotateLeft(p);
                rotateRight(g);
            } else {
                rotateRight(p);
                rotateLeft(g);
            }
        }
    }
}

/**
 * Driver Benchmark Utama
 */
public class MainApp {
    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("UJI PERFORMA KOMPARATIF: SPLAY TREE VS SEMI-SPLAY TREE");
        System.out.println("======================================================");

        int DATA_SIZE = 50000;
        int ACCESS_COUNT = 100000;
        
        SplayTree classicTree = new SplayTree();
        SemiSplayTree semiTree = new SemiSplayTree();
        
        int[] dataset = new int[DATA_SIZE];
        Random rand = new Random(42); // Seed konstan agar adil
        for (int i = 0; i < DATA_SIZE; i++) {
            dataset[i] = rand.nextInt(1000000);
        }

        // 1. Pengujian Insersi Classic Splay
        long startClassicInsert = System.nanoTime();
        for (int val : dataset) classicTree.insert(val);
        long endClassicInsert = System.nanoTime();
        double timeClassicInsert = (endClassicInsert - startClassicInsert) / 1_000_000.0;
        long rotClassicInsert = classicTree.getRotationCount();

        // 2. Pengujian Insersi Semi-Splay
        long startSemiInsert = System.nanoTime();
        for (int val : dataset) semiTree.insert(val);
        long endSemiInsert = System.nanoTime();
        double timeSemiInsert = (endSemiInsert - startSemiInsert) / 1_000_000.0;
        long rotSemiInsert = semiTree.getRotationCount();

        classicTree.resetRotationCount();
        semiTree.resetRotationCount();

        // Skenario Temporal Locality (90% kueri menyerang 10% zona data terpopuler)
        int[] searchQueries = new int[ACCESS_COUNT];
        int hotZoneSize = DATA_SIZE / 10;
        for (int i = 0; i < ACCESS_COUNT; i++) {
            if (rand.nextDouble() < 0.90) {
                searchQueries[i] = dataset[rand.nextInt(hotZoneSize)];
            } else {
                searchQueries[i] = dataset[rand.nextInt(DATA_SIZE)];
            }
        }

        // 3. Pengujian Akses Classic Splay
        long startClassicSearch = System.nanoTime();
        for (int key : searchQueries) classicTree.search(key);
        long endClassicSearch = System.nanoTime();
        double timeClassicSearch = (endClassicSearch - startClassicSearch) / 1_000_000.0;
        long rotClassicSearch = classicTree.getRotationCount();

        // 4. Pengujian Akses Semi-Splay
        long startSemiSearch = System.nanoTime();
        for (int key : searchQueries) semiTree.search(key);
        long endSemiSearch = System.nanoTime();
        double timeSemiSearch = (endSemiSearch - startSemiSearch) / 1_000_000.0;
        long rotSemiSearch = semiTree.getRotationCount();

        System.out.printf("\n%-25s | %-20s | %-20s\n", "METRIK OPERASI", "CLASSIC SPLAY TREE", "SEMI-SPLAY TREE");
        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("%-25s | %-17.2f ms | %-17.2f ms\n", "Waktu Insersi", timeClassicInsert, timeSemiInsert);
        System.out.printf("%-25s | %-20d | %-20d\n", "Jumlah Rotasi Insersi", rotClassicInsert, rotSemiInsert);
        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("%-25s | %-17.2f ms | %-17.2f ms\n", "Waktu Akses Pencarian", timeClassicSearch, timeSemiSearch);
        System.out.printf("%-25s | %-20d | %-20d\n", "Jumlah Rotasi Akses", rotClassicSearch, rotSemiSearch);
        System.out.println("============================================================================");
    }
}
