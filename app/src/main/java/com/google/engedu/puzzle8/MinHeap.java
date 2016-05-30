package com.google.engedu.puzzle8;

public class MinHeap {
    private PuzzleBoard[] H;
    private int curr;
    private int size;

    MinHeap(int size) {
        this.size = size;
        this.curr = -1;
        H = new PuzzleBoard[size];
    }

    void swap(int i, int j) {
        PuzzleBoard temp = H[i];
        H[i] = H[j];
        H[j] = temp;
    }

    void heapify(int l, int r, int k) {
        int n = r - l;
        if ((2 * k) > n) return;

        int j = 2 * k;
        if ((j + 1) <= n) {
            if (compare(H[j + 1], H[j]) < 0)
                j = j + 1;
        }

        if (compare(H[j], H[k]) < 0) {
            swap(j, k);
            heapify(l, r, j);
        }
    }

    void insert(PuzzleBoard item) {
        if (curr < size - 1) {
            H[++curr] = item;
            heap_bottom_up(curr);
            //display();
        }
    }

    void heap_bottom_up(int n) {
        int i;
        if (n <= 1)
            return;
        for (i = (n - 1) / 2; i > -1; i--)
            heapify(0, n - 1, i);
    }

    PuzzleBoard remove() {
        PuzzleBoard temp = H[0];
        if (curr > 0) {
            swap(0, curr);
            H[curr--] = null;
            heapify(0, curr, 0);
        }
        return temp;
    }

    boolean isEmpty() {
        return curr <= -1;
    }

    /*

    void display() {
        for (int i = 0; i < curr; i++) {
            if (H[i] != null)
                System.out.print(H[i].priority() + " ");
        }
        System.out.println();
    }

    */

    public int compare(PuzzleBoard lhs, PuzzleBoard rhs) {
        if (lhs.dist() == rhs.dist()) {
            return lhs.compMat(rhs);
        } else if (lhs.priority() < rhs.priority())
            return -1;
        else
            return 1;

    }

}