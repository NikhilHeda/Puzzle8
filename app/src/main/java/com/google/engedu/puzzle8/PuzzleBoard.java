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

    private ArrayList<PuzzleTile> tiles = new ArrayList<>();

    private int steps = 0;
    private PuzzleBoard previousBoard = null;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        tiles = new ArrayList<>();
        PuzzleTile tile = null;
        int count = 0;
        Bitmap source = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, false);
        int tileSize = parentWidth / NUM_TILES;
        Bitmap chopped = null;
        for (int i = 0; i < NUM_TILES; i++) {
            for (int j = 0; j < NUM_TILES; j++) {
                if (count == NUM_TILES * NUM_TILES - 1) {
                    tiles.add(count, null);
                    break;
                }
                chopped = Bitmap.createBitmap(source, j * tileSize, i * tileSize, tileSize, tileSize);
                tile = new PuzzleTile(chopped, count + 1);
                tiles.add(count, tile);
                count++;
            }
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps + 1;
        previousBoard = otherBoard;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
        steps = 0;
        previousBoard = null;
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
            if (tile == null || tile.getNumber() != i + 1)
                return false;
        }
        return true;
    }

    /*
        private int XYtoIndex(int x, int y) {
            return y + x * NUM_TILES;
        }
        */
    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }


    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        int nullX = 0, nullY = 0;
        ArrayList<PuzzleBoard> result = new ArrayList<>();
        PuzzleBoard temp = null;
        for (int i = 0; i < NUM_TILES; i++) {
            for (int j = 0; j < NUM_TILES; j++) {
                if (tiles.get(XYtoIndex(i, j)) == null) {
                    nullX = i;
                    nullY = j;
                    break;
                }
            }
        }
        for (int[] delta : NEIGHBOUR_COORDS) {// loop is like using for(int i =0;i<array.size();i++)

            int tileX = nullX + delta[0];
            int tileY = nullY + delta[1];
            if (tileX >= 0 && tileX < NUM_TILES && tileY >= 0 && tileY < NUM_TILES) {
                temp = new PuzzleBoard(this);
                temp.swapTiles(XYtoIndex(tileX, tileY), XYtoIndex(nullX, nullY));
                if (!temp.equals(this)) {
                    result.add(temp);
                }
            }
        }

        return result;
    }

    public int priority() {
        int manhattan = 0;

        for (int i = 0; i < NUM_TILES; i++)
            for (int j = 0; j < NUM_TILES; j++) {
                PuzzleTile tile = tiles.get(i * NUM_TILES + j);//you can also use tiles.get(XYtoIndex(i,j));
                if (tile == null)
                    continue;
                int row = (tile.getNumber() - 1) / NUM_TILES;
                int col = (tile.getNumber() - 1) % NUM_TILES;
                manhattan += (Math.abs(i - row) + Math.abs(j - col));
            }
        return manhattan + steps;

    }

    public int dist() {
        return this.priority() - this.steps;
    }

    public int compMat(PuzzleBoard t) {
        if (tiles.equals(t.tiles)) return 0;
        else {
            if (this.priority() < t.priority())
                return -1;
            return 1;
        }
    }

    public boolean checkValidity() {
        boolean isValid = true;
        for (int i = 1; i < NUM_TILES * NUM_TILES && isValid; i++) {
            if (tiles.get(i - 1) != null)
                isValid = i == tiles.get(i - 1).getNumber();
        }
        return isValid;
    }

    public PuzzleBoard getPreviousBoard() {
        return previousBoard;
    }

}
