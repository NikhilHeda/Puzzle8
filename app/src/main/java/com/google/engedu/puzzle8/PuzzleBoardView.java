package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

class PuzzleBoardView extends View {
    private static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    void initialize(Bitmap imageBitmap) {
        int width = imageBitmap.getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
        invalidate();
    }

    void shuffle() {
        if (animation == null && puzzleBoard != null) {
            // Do something. Then:
            for (int i = 0; i < NUM_SHUFFLE_STEPS; i++) {

                ArrayList<PuzzleBoard> possible = puzzleBoard.neighbours();
                puzzleBoard = possible.get(random.nextInt(possible.size()));

            }
            invalidate();
            puzzleBoard.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    void solve() {
        PriorityQueue<PuzzleBoard> queue = new PriorityQueue<>(10,
                new Comparator<PuzzleBoard>() {
                    @Override
                    public int compare(PuzzleBoard lhs, PuzzleBoard rhs) {
                        if (lhs.dist() == rhs.dist()) {
                            return lhs.compMat(rhs);
                        } else if (lhs.priority() < rhs.priority())
                            return -1;
                        else
                            return 1;

                    }
                });
        //we keep track of visited states, and next possible states.
        ArrayList<PuzzleBoard> nextStates, prevStates = new ArrayList<>();
        //proceed to reset the current board, and create references to store states during the course of the algorithm
        puzzleBoard.reset();
        PuzzleBoard cur = new PuzzleBoard(puzzleBoard); //reference to current board
        cur.reset();
        queue.add(cur);//enqueue current state

        while (!queue.isEmpty()) {
            cur = queue.poll(); //dequeue element with lowest priority
            prevStates.add(cur); //add the current state to previousStates

            if (!cur.resolved())//if not goal state,
            {
                nextStates = cur.neighbours();//get all neighbours and insert into the queue, if state is previously unvisited.
                for (PuzzleBoard state : nextStates) {
                    if (prevStates.contains(state))
                        continue;
                    else {
                        queue.add(state);//enqueue the neighbouring state
                    }

                }
            } else //when we reach our goal
            {
                ArrayList<PuzzleBoard> sequence = new ArrayList<>(); //store sequence of steps so far to animate (code already written)
                sequence.add(cur);
                while (cur.getPreviousBoard() != null)//backtrack till initial state of the board.
                {
                    sequence.add(cur.getPreviousBoard());
                    cur = cur.getPreviousBoard();
                }
                sequence.add(cur);
                Collections.reverse(sequence); //reverse to start from current unsolved state and end at solved state

                animation = (ArrayList<PuzzleBoard>) sequence.clone();
                break;
            }
        }
        invalidate();//update the final UI

    }

}
