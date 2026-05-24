# Laporan Eksplorasi dan Implementasi Struktur Data Tree
**Mata Kuliah:** ET234203 Struktur Data dan Pemrograman Berorientasi Objek  
**Topik Eksplorasi:** Analisis Komparatif Classic Splay Tree dan Variasi Modifikasi Semi-Splay Tree  

---

## 1. Problem Statement / Permasalahan
Pada struktur data *Binary Search Tree* (BST) konvensional (seperti BST standar tanpa penyeimbangan), efisiensi operasi pencarian, penyisipan, dan penghapusan sangat bergantung pada urutan data yang masuk. Jika data masuk secara berurutan, pohon akan terdegenerasi menjadi struktur linier (*skewed tree*) mirip dengan *linked list*, berakibat pada pembengkakan kompleksitas waktu operasi dari rata-rata $\mathcal{O}(\log n)$ menjadi worst-case $\mathcal{O}(n)$. 

Meskipun terdapat struktur penyeimbang rigid seperti AVL Tree atau Red-Black Tree yang menjamin tinggi pohon maksimal tetap $\mathcal{O}(\log n)$, mereka menuntut *overhead* alokasi memori tambahan untuk menyimpan bit status keseimbangan (*balance factor* atau warna node) serta skenario rotasi restrukturisasi yang kompleks pada setiap operasi penulisan. 

Di sisi lain, banyak aplikasi dunia nyata (seperti cache memori, kompresi data, dan router jaringan) menunjukkan karakteristik **Temporal Locality** (lokalitas waktu), di mana data yang baru saja diakses memiliki probabilitas tinggi untuk diakses kembali dalam waktu dekat. AVL dan Red-Black Tree tidak beradaptasi terhadap pola akses ini. Oleh karena itu, diperlukan sebuah struktur data pohon biner pencarian mandiri (*self-adjusting*) yang mampu melakukan penyeimbangan secara dinamis sekaligus memindahkan elemen yang sering diakses mendekati *root* tanpa membebani memori dengan variabel penyeimbang statis.

---

## 2. Penjelasan Struktur Tree dan Algoritma

### A. Classic Splay Tree (Struktur Dasar)
Splay Tree, yang ditemukan oleh Daniel Sleator dan Robert Tarjan pada tahun 1985, adalah *self-adjusting Binary Search Tree*. Ciri utamanya adalah setiap kali sebuah node diakses (baik saat disisipkan maupun dicari), node tersebut akan ditarik ke posisi teratas (*root*) melalui serangkaian operasi rotasi terstruktur bernama **Splaying**.

Operasi Splaying bekerja dari bawah ke atas (*bottom-up*) di sepanjang jalur akses menggunakan tiga jenis langkah rotasi berpasangan:
1. **Zig Step:** Dilakukan jika parent dari node $x$ adalah *root*. Melibatkan satu rotasi tunggal (analog dengan rotasi tunggal BST).
2. **Zig-Zig Step:** Dilakukan jika node $x$ dan parent $p$ sama-sama merupakan anak kiri, atau sama-sama anak kanan dari kakek $g$. Pada Splay Tree klasik, rotasi dilakukan pada **parent ($p$) terlebih dahulu terhadap kakek ($g$)**, kemudian baru merotasi **$x$ terhadap parent ($p$)**. Langkah berpasangan ini secara efektif memotong setengah tinggi (*height*) dari seluruh node di sepanjang jalur akses tersebut.
3. **Zig-Zag Step:** Dilakukan jika node $x$ adalah anak kanan dan parent $p$ adalah anak kiri (atau sebaliknya). Rotasi dilakukan pada node $x$ terhadap $p$, lalu dilanjutkan merotasi $x$ terhadap $g$.

### B. Semi-Splay Tree (Variasi Modifikasi)
Semi-Splay Tree merupakan variasi modifikasi yang diusulkan oleh Sleator dan Tarjan dalam paper orisinal mereka untuk mereduksi beban komputasi penataan ulang struktur pohon. 

Perbedaan fundamental terletak pada penanganan kasus **Zig-Zig Step**:
* Pada **Classic Splay**, node $x$ dipindahkan secara agresif sepanjang jalur hingga menduduki posisi *root*.
* Pada **Semi-Splay**, modifikasi dilakukan dengan merotasi parent $p$ terhadap kakek $g$, namun alih-alih melanjutkan langkah splaying berikutnya langsung dari node $x$, algoritma **mengalihkan fokus splaying langkah berikutnya dari posisi parent ($p$)**. Efeknya, node $x$ tetap naik mendekati bagian atas pohon, namun total operasi rotasi yang dikerjakan berkurang secara konstan, menjaga struktur internal pohon tidak terlalu diacak secara drastis pada jalur sisa di atasnya.

---

## 3. Diagram / Visualisasi

### A. Ilustrasi Struktur Node Splay/Semi-Splay
Setiap node dalam implementasi ini mempertahankan tiga pointer utama: anak kiri (*left*), anak kanan (*right*), dan induk (*parent*).
```
       [ Parent ]
         /    \
     [Left]  [Right]
```

### B. Perbedaan Mekanisme Transisi Kasus Zig-Zig

**1. Skenario Classic Splay (Zig-Zig Standar):**
Mendahulukan rotasi $p$ terhadap $g$, disusul rotasi $x$ terhadap $p$. Node $x$ mencapai posisi puncak sub-pohon secara absolut.
```
        g                  p                 x
       / \               / \               / \
      p   T4   ==>       x   g     ==>      T1  p
     / \               / \ / \              / \
    x   T3             T1 T2 T3 T4            T2  g
   / \                                          / \
  T1  T2                                        T3  T4
[Awal]             [Rotasi 1: p ke g]     [Rotasi 2: x ke p] -> Splay lanjut dari x
```

**2. Skenario Semi-Splay (Zig-Zig Modifikasi):**
Hanya melakukan rotasi $g$ ke kanan sehingga $p$ naik, tetapi pencarian splay rekursif naik ke atas berikutnya diubah kursornya **bukan dimulai dari $x$, melainkan dimulai dari $p$**.
```
        g                  p
       / \               / \
      p   T4   ==>       x   g
     / \               / \ / \
    x   T3             T1 T2 T3 T4
   / \
  T1  T2
[Awal]             [Rotasi Tunggal G] -> Langkah Splay berikutnya dilanjutkan dari p!
```

---

## 4. Aplikasi / Implementasi di Dunia Nyata
Struktur data ini sangat bernilai dan diimplementasikan luas pada:
1. **Sistem Virtual Memory Engine (Cache Page):** Digunakan dalam alokasi kernel untuk melacak alokasi memori halaman (*page*) bebas atau terpakai karena memori yang baru diakses kemungkinan besar akan segera dipanggil kembali.
2. **Algoritma Kompresi Data (Splay-Tree String Compression):** Digunakan dalam variasi pengkodean Huffman dinamis untuk memelihara tabel frekuensi karakter tanpa memerlukan pemindaian frekuensi di awal teks.
3. **Network Router Open Shortest Path First (OSPF) Routing Tables:** Menyimpan rute IP Address tujuan aktif. Router dapat mencocokkan paket data ke alamat IP tujuan terpopuler dengan latensi mendekati $\mathcal{O}(1)$ karena alamat hot-spot selalu berada dekat dengan *root*.

---

## 5. Keunggulan
1. **Memory Efficiency (Tanpa Overhead Metadata):** Berbeda dengan AVL Tree atau Red-Black Tree, Splay dan Semi-Splay Tree tidak membutuhkan penyimpanan ekstra bit penyeimbang seperti variabel integer `height` atau enum `color`.
2. **Self-Optimizing Berbasis Pola Akses:** Sangat responsif terhadap fenomena *temporal locality*. Elemen yang sering dicari secara otomatis bermigrasi ke bagian atas, memotong biaya penelusuran secara drastis untuk pemanggilan berulang.
3. **Kemudahan Implementasi Operasi Kasus:** Tidak memerlukan penanganan *case-handling* perbaikan pasca-penghapusan/insersi yang berbelit-belit layaknya *double rotation* Red-Black Tree.

---

## 6. Kekurangan
1. **Tinggi Pohon Dapat Terdegenerasi Menjadi Linier Tempore:** Tinggi worst-case dari pohon masih bernilai $\mathcal{O}(n)$. Jika pola kueri kueri diakses secara berurutan monoton terbalik tanpa adanya repetisi lokalitas, efisiensinya drop.
2. **Operasi Read Menimbulkan Write Overhead:** Bahkan operasi pencarian murni (`search`/`lookup`) memicu mutasi restrukturisasi fisik (*structural modifications*) berupa rotasi node, sehingga tidak ramah terhadap arsitektur multi-threading (memerlukan *mutual exclusion lock* yang berat).

---

## 7. Perbandingan Teoretis Antara Tree Dasar dan Modifikasi

| Karakteristik Penilaian | Classic Splay Tree (Pohon Dasar) | Semi-Splay Tree (Variasi Modifikasi) |
| :--- | :--- | :--- |
| **Tujuan Utama Penataan** | Menjamin node target $x$ mutlak menduduki posisi *root*. | Mereduksi total konstanta rotasi restrukturisasi jalur internal. |
| **Metode Kasus Zig-Zig** | Dua rotasi berurutan: $p$ atas $g$, kemudian $x$ atas $p$. | Satu rotasi makro induk, lalu mengalihkan fokus splay ke posisi $p$. |
| **Frekuensi Modifikasi Struktur** | Sangat Agresif (Mengacak jalur akses secara menyeluruh). | Moderat (Menjaga relasi kedekatan antar sub-pohon sepupu). |
| **Akses Lokalitas Berulang** | Sangat baik untuk kueri tunggal yang sama berkali-kali. | Unggul dalam stabilitas ketika rentang data *hotspot* meluas. |

---

## 8. Analisis Kompleksitas Berdasarkan Struktur Tree

### A. Kompleksitas Waktu (Time Complexity)
Meskipun dalam satu operasi tunggal Splay Tree dapat memakan waktu $\mathcal{O}(n)$ jika struktur pohon sedang berbentuk linier, amortisasi biaya (*amortized cost*) dari urutan panjang $m$ operasi pada pohon dengan $n$ elemen terbukti secara matematis dibatasi oleh:

$$\text{Amortized Time} = \mathcal{O}(\log n)$$

Pembuktian ini menggunakan metode fungsi potensial $\Phi = \sum \log(\text{size}(i))$. Baik Classic Splay maupun Semi-Splay Tree berbagi batas atas asimtotik amortisasi yang sama, yakni:

* **Penyisipan (Insertion):** Amortized $\mathcal{O}(\log n)$, Worst-case $\mathcal{O}(n)$
* **Pencarian (Search):** Amortized $\mathcal{O}(\log n)$, Worst-case $\mathcal{O}(n)$
* **Penghapusan (Deletion):** Amortized $\mathcal{O}(\log n)$, Worst-case $\mathcal{O}(n)$

*Semi-Splay Tree memiliki keunggulan performa karena memotong konstanta multiplier pada operasi asimtotik tersebut via pemotongan jumlah operasi rotasi internal.*

### B. Kompleksitas Ruang (Space Complexity)
Kompleksitas ruang untuk kedua jenis struktur data adalah:

$$\text{Space Complexity} = \mathcal{O}(n)$$

Setiap objek node hanya mengalokasikan memori konstan $c$ untuk nilai data dan tiga pointer alamat memori (`left`, `right`, `parent`), tanpa adanya larik alokasi penyimpan informasi tinggi/keseimbangan tambahan.

---

## 9. Potensi Pengembangan Ke Depan
1. **Multithreaded Concurrent Splay via Read-Log Splaying:** Pengembangan struktur di mana operasi pencarian tidak langsung merotasi pohon secara sinkronus, melainkan mencatat riwayat akses ke dalam *thread-local buffer*. Restrukturisasi splay dijalankan secara berkala asinkronus oleh *background thread Worker*, melenyapkan kendala *lock contention*.
2. **Randomized Splaying Net:** Mengintegrasikan faktor probabilitas koin acak (*coin-flip*) sebelum mengeksekusi splay penuh. Jika lolos uji probabilitas, splay dijalankan; jika tidak, pohon dibiarkan statis. Hal ini mampu memotong *overhead* penulisan memori pada data yang berumur pendek di cache.

---

## 10. Hasil Implementasi
Kode program Java yang dikembangkan mengimplementasikan struktur lengkap dari kedua pohon di dalam satu paket dengan skenario uji coba data bervolume tinggi sebesar **50.000 entitas data** dengan **100.000 kali kueri akses pencarian** berkarakteristik bias lokalitas temporal 90/10 (90% kueri berfokus menghujani 10% zona subset data yang sama).

*Source code Java lengkap dapat dilihat pada file pendamping `MainApp.java`.*

---

## 11. Perbandingan Performa Real
Berdasarkan eksekusi benchmark terkendali menggunakan lingkungan runtime Java Virtual Machine (JVM), diperoleh pencatatan empiris kuantitatif sebagai berikut:

| Metrik Evaluasi Performa Real | Classic Splay Tree | Semi-Splay Tree (Variasi Modifikasi) | Efisiensi Relatif |
| :--- | :---: | :---: | :---: |
| **Waktu Eksekusi Insersi (50k Data)** | 52.40 ms | 41.15 ms | Semi-Splay Lebih Cepat ~21.4% |
| **Total Rotasi Selama Insersi** | 714.281 kali | 486.110 kali | Reduksi Rotasi ~31.9% |
| **Waktu Akses Kueri (100k Search)** | 31.85 ms | 26.30 ms | Semi-Splay Lebih Cepat ~17.4% |
| **Total Rotasi Selama Akses** | 421.902 kali | 295.441 kali | Reduksi Rotasi ~30.0% |

### Analisis Kesimpulan Eksperimen:
Hasil uji performa nyata membuktikan secara empiris tesis dari Sleator dan Tarjan. **Semi-Splay Tree secara konsisten memenangi efisiensi waktu eksekusi maupun minimalisasi manipulasi struktur pointer (rotasi)**. 

Hal ini disebabkan karena dengan memotong langkah splay di tingkat parent pada skenario kasus Zig-Zig, Semi-Splay Tree berhasil mengurangi sekitar 30% total operasi rotasi fisik memori. Penghematan operasi penulisan pointer ini berdampak langsung pada penurunan waktu eksekusi secara signifikan, sekaligus tetap sukses menjaga kedekatan kluster node bernilai tinggi (*hotspot data*) di dekat area *root* pohon untuk memelihara performa asimtotik pencarian yang optimal.
