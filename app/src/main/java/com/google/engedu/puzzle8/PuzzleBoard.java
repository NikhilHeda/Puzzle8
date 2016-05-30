package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Random;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };
    private Random random = new Random();

    private int steps;

    private ArrayList<PuzzleTile> tiles = new ArrayList<>();

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        int number = 0;
        int factor = parentWidth / NUM_TILES;
        for (int x = 0; x <= parentWidth - factor; x += factor) {
            for (int y = 0; y <= parentWidth - factor; y += factor) {
                Bitmap t = Bitmap.createBitmap(bitmap, y, x, factor, factor);
                PuzzleTile ti = new PuzzleTile(t, number);
                if (number != NUM_TILES * NUM_TILES - 1) {
                    tiles.add(ti);
                }
                number++;
            }
        }
        tiles.add(null);
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {

        ArrayList<PuzzleBoard> res = new ArrayList<>();
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
                PuzzleTile tile = tiles.get(i);
                if (tile != null) {
                    int tileX = i % NUM_TILES;
                    int tileY = i / NUM_TILES;
                    int r = random.nextInt(4);
                    int delta[] = NEIGHBOUR_COORDS[r];
                    int nullX = tileX + delta[0];
                    int nullY = tileY + delta[1];
                    if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES && tiles.get(XYtoIndex(nullX, nullY)) == null) {
                        swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                    }
                }
            }
            if (j >= 5)
                res.add(new PuzzleBoard(this));
        }
        return res;
    }

    public int priority() {
        return 0;
    }

}
