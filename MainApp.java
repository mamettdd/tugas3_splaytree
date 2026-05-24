import java.util.Random;

// ==========================================
// 1. STRUKTUR NODE POHON BINER
// ==========================================
class TreeNode {
    int key;
    TreeNode left, right, parent;

    public TreeNode(int key) {
        this.key = key;
        this.left = this.right = this.parent = null;
    }
}

// ==========================================
// 2. IMPLEMENTASI CLASSIC SPLAY TREE
// ==========================================
class ClassicSplayTree {
    TreeNode root;
    long rotationCount = 0;

    private void rotateLeft(TreeNode x) {
        TreeNode y = x.right;
        if (y == null) return;
        
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        
        y.parent = x.parent;
        if (x.parent == null) this.root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        
        y.left = x;
        x.parent = y;
        rotationCount++;
    }

    private void rotateRight(TreeNode x) {
        TreeNode y = x.left;
        if (y == null) return;
        
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        
        y.parent = x.parent;
        if (x.parent == null) this.root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        
        y.right = x;
        x.parent = y;
        rotationCount++;
    }

    private void splay(TreeNode x) {
        if (x == null) return;
        
        while (x.parent != null) {
            TreeNode p = x.parent;
            TreeNode g = p.parent;
            
            if (g == null) {
                // Kasus Zig
                if (x == p.left) rotateRight(p);
                else rotateLeft(p);
            } else if (x == p.left && p == g.left) {
                // Kasus Zig-Zig Klasik: Rotasi Parent (p) dulu, baru Target (x)
                rotateRight(g);
                rotateRight(p);
            } else if (x == p.right && p == g.right) {
                // Kasus Zig-Zig Klasik: Rotasi Parent (p) dulu, baru Target (x)
                rotateLeft(g);
                rotateLeft(p);
            } else if (x == p.right && p == g.left) {
                // Kasus Zig-Zag
                rotateLeft(p);
                rotateRight(g);
            } else {
                // Kasus Zag-Zig
                rotateRight(p);
                rotateLeft(g);
            }
        }
    }

    public void insert(int key) {
        TreeNode z = new TreeNode(key);
        TreeNode y = null;
        TreeNode x = this.root;

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
        TreeNode x = this.root;
        while (x != null) {
            if (key == x.key) {
                splay(x);
                return true;
            } else if (key < x.key) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        return false;
    }
}

// ==========================================
// 3. IMPLEMENTASI VARIASI MODIFIKASI: SEMI-SPLAY TREE
// ==========================================
class SemiSplayTree {
    TreeNode root;
    long rotationCount = 0;

    private void rotateLeft(TreeNode x) {
        TreeNode y = x.right;
        if (y == null) return;
        
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        
        y.parent = x.parent;
        if (x.parent == null) this.root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        
        y.left = x;
        x.parent = y;
        rotationCount++;
    }

    private void rotateRight(TreeNode x) {
        TreeNode y = x.left;
        if (y == null) return;
        
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        
        y.parent = x.parent;
        if (x.parent == null) this.root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        
        y.right = x;
        x.parent = y;
        rotationCount++;
    }

    private void semiSplay(TreeNode x) {
        if (x == null) return;
        
        while (x.parent != null && x.parent.parent != null) {
            TreeNode p = x.parent;
            TreeNode g = p.parent;
            
            if (x == p.left && p == g.left) {
                // Kasus Semi-Zig-Zig: Hanya merotasi Kakek (g), 
                // lalu mengalihkan fokus langkah berikutnya langsung ke Parent (p)
                rotateRight(g);
                x = p; 
            } else if (x == p.right && p == g.right) {
                // Kasus Semi-Zig-Zig: Hanya merotasi Kakek (g), 
                // lalu mengalihkan fokus langkah berikutnya langsung ke Parent (p)
                rotateLeft(g);
                x = p;
            } else if (x == p.right && p == g.left) {
                // Kasus Zig-Zag tetap melakukan restrukturisasi penuh lokal
                rotateLeft(p);
                rotateRight(g);
            } else {
                // Kasus Zag-Zig tetap melakukan restrukturisasi penuh lokal
                rotateRight(p);
                rotateLeft(g);
            }
        }
        
        // Sisa Kasus Tunggal (Zig Step) di dekat permukaan Root utama
        if (x.parent != null) {
            TreeNode p = x.parent;
            if (x == p.left) rotateRight(p);
            else rotateLeft(p);
        }
    }

    public void insert(int key) {
        TreeNode z = new TreeNode(key);
        TreeNode y = null;
        TreeNode x = this.root;

        while (x != null) {
            y = x;
            if (z.key < x.key) x = x.left;
            else x = x.right;
        }

        z.parent = y;
        if (y == null) this.root = z;
        else if (z.key < y.key) y.left = z;
        else y.right = z;

        semiSplay(z);
    }

    public boolean search(int key) {
        TreeNode x = this.root;
        while (x != null) {
            if (key == x.key) {
                semiSplay(x);
                return true;
            } else if (key < x.key) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        return false;
    }
}

// ==========================================
// 4. MAIN DRIVER & AUTOMATED BENCHMARK
// ==========================================
public class MainApp {
    public static void main(String[] args) {
        int datasetSize = 50000;
        int queryCount = 100000;
        
        ClassicSplayTree classicTree = new ClassicSplayTree();
        SemiSplayTree semiTree = new SemiSplayTree();
        
        System.out.println("====== MEMULAI SIMULASI EVALUASI PERFORMA TREE ======");
        System.out.println("Ukuran Dataset  : " + datasetSize + " entitas data.");
        System.out.println("Jumlah Pencarian: " + queryCount + " kueri akses.");
        System.out.println("Karakteristik   : Lokalisasi Akses Bias Temporal 90/10\n");

        // --------------------------------------------------
        // BENCHMARK 1: OPERASI PENYISIPAN (INSERTION)
        // --------------------------------------------------
        long startClassicInsert = System.currentTimeMillis();
        for (int i = 1; i <= datasetSize; i++) {
            classicTree.insert(i); // Menggunakan data terurut untuk mensimulasikan skewed tree awal
        }
        long endClassicInsert = System.currentTimeMillis();

        long startSemiInsert = System.currentTimeMillis();
        for (int i = 1; i <= datasetSize; i++) {
            semiTree.insert(i);
        }
        long endSemiInsert = System.currentTimeMillis();

        // --------------------------------------------------
        // BENCHMARK 2: OPERASI PENCARIAN (SEARCH) DENGAN POLA TEMPORAL LOCALITY
        // --------------------------------------------------
        int[] searchQueries = new int[queryCount];
        Random rand = new Random(42); // Seed konstan agar pengujian adil
        
        int hotDataLimit = (int) (datasetSize * 0.10); // 10% dari data menjadi hot data
        
        for (int i = 0; i < queryCount; i++) {
            if (rand.nextDouble() < 0.90) {
                // 90% kueri mengakses 10% data terpopuler (Hot Spot)
                searchQueries[i] = rand.nextInt(hotDataLimit) + 1;
            } else {
                // 10% kueri mengakses sisa data lainnya (Cold Data)
                searchQueries[i] = rand.nextInt(datasetSize - hotDataLimit) + hotDataLimit + 1;
            }
        }

        long startClassicSearch = System.currentTimeMillis();
        for (int q : searchQueries) {
            classicTree.search(q);
        }
        long endClassicSearch = System.currentTimeMillis();

        long startSemiSearch = System.currentTimeMillis();
        for (int q : searchQueries) {
            semiTree.search(q);
        }
        long endSemiSearch = System.currentTimeMillis();

        // --------------------------------------------------
        // OUTPUT HASIL EVALUASI KOMPARATIF
        // --------------------------------------------------
        System.out.println("-----------------------------------------------------");
        System.out.println("HASIL PENCATATAN EMPIRIS:");
        System.out.println("-----------------------------------------------------");
        System.out.println("[Classic Splay Tree]");
        System.out.println("  > Waktu Insersi : " + (endClassicInsert - startClassicInsert) + " ms");
        System.out.println("  > Rotasi Insersi: " + classicTree.rotationCount + " kali");
        System.out.println("  > Waktu Search  : " + (endClassicSearch - startClassicSearch) + " ms");
        System.out.println("  > Rotasi Search : " + classicTree.rotationCount + " kali");
        System.out.println();
        System.out.println("[Semi-Splay Tree (Modifikasi)]");
        System.out.println("  > Waktu Insersi : " + (endSemiInsert - startSemiInsert) + " ms");
        System.out.println("  > Rotasi Insersi: " + semiTree.rotationCount + " kali");
        System.out.println("  > Waktu Search  : " + (endSemiSearch - startSemiSearch) + " ms");
        System.out.println("  > Rotasi Search : " + semiTree.rotationCount + " kali");
        System.out.println("-----------------------------------------------------");
    }
}
