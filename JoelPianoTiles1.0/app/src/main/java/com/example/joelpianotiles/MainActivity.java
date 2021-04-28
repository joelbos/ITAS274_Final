package com.example.joelpianotiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ImageView
            //This ImageView reflects the placement of the grid squares made in grid.xml used to put images in each grid
            iv_11, iv_12, iv_13,
            iv_21, iv_22, iv_23,
            iv_31, iv_32, iv_33,
            iv_41, iv_42, iv_43,
            iv_51, iv_52, iv_53;
    //creates a variable for start button
    Button b_play;
    //variables to score time, score and top score
    TextView tv_time, tv_score, tv_best;
    //variable created for random, allows us to place our images in random grid squares
    Random r;

    //Creates the following int variables
    int tileLocationRow1, tileLocationRow2, tileLocationRow3, tileLocationRow4, tileLocationRow5;
    //Variables for our tile states
    int toBeTapped, tapped, tap, noTap;
    //Int for the current score
    int currentScore = 0;
    //Int for the best score
    int bestScore;
    //Creates a CountDownTimer named timer
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Gets our highscore that our phone stored locally
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
       //Searches for the string highscore as the key for the highscore, has a default value of 0
        bestScore = preferences.getInt("highscore", 0);
        //Connects the variable to our various grid squares from grid.xml, (ImageView) is redundant but i'm keeping it in to help label since it does no harm.
        iv_11 = (ImageView) findViewById(R.id.iv_11);
        iv_12 = (ImageView) findViewById(R.id.iv_12);
        iv_13 = (ImageView) findViewById(R.id.iv_13);

        iv_21 = (ImageView) findViewById(R.id.iv_21);
        iv_22 = (ImageView) findViewById(R.id.iv_22);
        iv_23 = (ImageView) findViewById(R.id.iv_23);

        iv_31 = (ImageView) findViewById(R.id.iv_31);
        iv_32 = (ImageView) findViewById(R.id.iv_32);
        iv_33 = (ImageView) findViewById(R.id.iv_33);

        iv_41 = (ImageView) findViewById(R.id.iv_41);
        iv_42 = (ImageView) findViewById(R.id.iv_42);
        iv_43 = (ImageView) findViewById(R.id.iv_43);

        iv_51 = (ImageView) findViewById(R.id.iv_51);
        iv_52 = (ImageView) findViewById(R.id.iv_52);
        iv_53 = (ImageView) findViewById(R.id.iv_53);
        //Connects the variable for our Button from bottom_bar.xml, once again (Button) is redundant but i'm keeping it in to help label.
        b_play = (Button) findViewById(R.id.b_play);
        //Connects the variable for score referencing tv_score in top_bar.xml
        tv_score = (TextView) findViewById(R.id.tv_score);
        tv_score.setText("SCORE: " + currentScore);
        //Connects the variable for best score referencing tv_best in top_bar.xml
        tv_best = (TextView) findViewById(R.id.tv_best);
        tv_best.setText("BEST: " + bestScore);
        //Connects the variable for time referencing tv_time
        tv_time = (TextView) findViewById(R.id.tv_time);
        //Set default time to 15 seconds,
        tv_time.setText("TIME: " + millisToTime(1500000000));
        //adds a random number to our r variable
        r = new Random();

        loadImages();
        //Creates a timer, millsInFuture must match what we set the text to be,
        timer = new CountDownTimer(1500000000, 500) {
            @Override
            public void onTick (long millisUnitFinished) {
                //This initally had (millisUnitFished + 1) for some reason.. all it did was add a 1 to the countdown
                tv_time.setText("TIME: " + millisToTime(millisUnitFinished));
            }
            @Override
            public void onFinish() {
                //When the app is in the Finish state
                tv_time.setText("TIME: " + millisToTime(0));
                //Tapping gets disabled on rows
                iv_31.setEnabled(false);
                iv_32.setEnabled(false);
                iv_33.setEnabled(false);
                //The play button becomes visible again
                b_play.setVisibility(View.VISIBLE);
                //All tiles switch to the noTap state, resulting in them becoming the image attached to the state (which should be nothing)
                iv_11.setImageResource(noTap);
                iv_12.setImageResource(noTap);
                iv_13.setImageResource(noTap);

                iv_21.setImageResource(noTap);
                iv_22.setImageResource(noTap);
                iv_23.setImageResource(noTap);

                iv_31.setImageResource(noTap);
                iv_32.setImageResource(noTap);
                iv_33.setImageResource(noTap);

                iv_41.setImageResource(noTap);
                iv_42.setImageResource(noTap);
                iv_43.setImageResource(noTap);

                iv_51.setImageResource(noTap);
                iv_52.setImageResource(noTap);
                iv_53.setImageResource(noTap);
                //A toast comes up congratulating your success!
                Toast.makeText(MainActivity.this, "Nice job!", Toast.LENGTH_SHORT).show();
                //If your current score is > than the best score it becomes the best score!
                if(currentScore > bestScore){
                    bestScore = currentScore;
                    tv_best.setText("BEST: " + bestScore);
                    //Write the best score to phones storage
                    SharedPreferences preferences1 = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = preferences1.edit();
                    editor.putInt("highscore", bestScore);
                    editor.apply();

            }
            }
        };
        //These had new View.OnClickListener() to begin with, but android studio suggested I replace with a lambda
        //For some reason the tutorial decided to name this rockLocation
        //What these do are configured further below in setTileLocation, i'll explain what happens in each context
        //Set an on click listener for column 1 row 3
        iv_31.setOnClickListener(v -> {
            //If tileLocation for Row3 = Cases 1-3 which means that the incoming tile
            //existing in iv_31-33 (AKA columns 1, 2 and 3 in row 3) has the ImageResource
            //with a name of "tap" then call continueGame else call endGame
            //Because an improper tile was tapped
        if(tileLocationRow3 == 1) {
            continueGame();
        }else{
            endGame();
        }
    });

        iv_32.setOnClickListener(v -> {
            if(tileLocationRow3 == 2) {
                continueGame();
            }else{
                endGame();
            }
        });

        iv_33.setOnClickListener(v -> {
            if(tileLocationRow3 == 3) {
                continueGame();
            }else{
                endGame();
            }
        });

        b_play.setOnClickListener(v -> initGame());
    }
//This is the continue game function, each time a correct tile is tapped the following happens:
        private void continueGame (){
            //For context, row 1 is the top row and 5 is the bottom
            //row5
            //data in row 5 is now row 4
            tileLocationRow5 = tileLocationRow4;
            //set new row 5 data as row 5
            setTileLocation (tileLocationRow5, 5);
            //row4
            //data in row 4 is now row 3
            tileLocationRow4 = tileLocationRow3;
            //set new row 4 data as row 4
            setTileLocation (tileLocationRow4, 4);
            //row3
            //data in row 3 is now row 2
            tileLocationRow3 = tileLocationRow2;
            //set new row 3 data as row 3
            setTileLocation (tileLocationRow3, 3);
            //row2
            //data in row 2 is now row 1
            tileLocationRow2 = tileLocationRow1;
            //set new row 2 data as row 2
            setTileLocation (tileLocationRow2, 2);
            //row1
            //randomly generates a toBeTapped location between columns one and three in row one
            //Because java counts from 0 a plus 1 is added
            tileLocationRow1 = r.nextInt(3) + 1;
            //set new row 1 data as row 1
            setTileLocation (tileLocationRow1, 1);
            //Because we clicked a correct tile every time this runs our score increments up
            currentScore++;
            //Text is set to reflect this
            tv_score.setText("SCORE: " + currentScore);
        }
        //used to initialize the game
        private void initGame(){
            //Row we're allowed to tap in is set to true
            iv_31.setEnabled(true);
            iv_32.setEnabled(true);
            iv_33.setEnabled(true);
            //Sets the play button to invisible
            b_play.setVisibility(View.INVISIBLE);
            //Score is 0 by default
            currentScore = 0;
            //Resets the score to 0
            tv_score.setText("SCORE: " + currentScore);
            //Stars the timer
            timer.start();

            //row5 - nothing


            //row4
            //Row four populates row 2 (making the tiles noTap (invisible) tiles by default)
            tileLocationRow4 = 2;
            //Row 4 column 2 will always be populated as tapped, I intend to make this dynamic
            iv_42.setImageResource(tapped);
            //row3
            //Row three also populates row two for the same reason as Row four.. I understand why the tutorial did this it was just confusing to figure
            //out.. Row 2 is the only "Free" Row.
            tileLocationRow3 = 2;
            //Row 3 column 2 will always be the first tile to tap.. I intend to change this
            iv_32.setImageResource(tap);
            //row2
            //Randomly generates a toBeTapped tile between columns one and three on row two
            tileLocationRow2 = r.nextInt(3) + 1;
            setTileLocation(tileLocationRow2, 2);
            //row1
            //Randomly generates a ToBeTapped tile between columns one and three on row one
            tileLocationRow1 = r.nextInt(3) + 1;
            setTileLocation(tileLocationRow1, 1);
             }
            //Used to end the game, also known as the most hyped movie of 2019
             private void endGame(){
            //Stops the timer
            timer.cancel();
                //Disable onclick for the tap row
                 iv_31.setEnabled(false);
                 iv_32.setEnabled(false);
                 iv_33.setEnabled(false);
                 //The play button is now visible
                 b_play.setVisibility(View.VISIBLE);
                //set all tiles to noTap image
                 iv_11.setImageResource(noTap);
                 iv_12.setImageResource(noTap);
                 iv_13.setImageResource(noTap);

                 iv_21.setImageResource(noTap);
                 iv_22.setImageResource(noTap);
                 iv_23.setImageResource(noTap);

                 iv_31.setImageResource(noTap);
                 iv_32.setImageResource(noTap);
                 iv_33.setImageResource(noTap);

                 iv_41.setImageResource(noTap);
                 iv_42.setImageResource(noTap);
                 iv_43.setImageResource(noTap);

                 iv_51.setImageResource(noTap);
                 iv_52.setImageResource(noTap);
                 iv_53.setImageResource(noTap);

                 Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
             }

             //Sets defaults for rows and defines cases
             private void setTileLocation(int place, int row){
        //If the row number is 1
                if(row == 1){
                    //Set Image to noTap tile by default
                    iv_11.setImageResource(noTap);
                    iv_12.setImageResource(noTap);
                    iv_13.setImageResource(noTap);
                    //tile can also be a toBeTapped tile in any of its three columns
                        switch (place) {
                            case 1:
                                iv_11.setImageResource(toBeTapped);
                                break;
                            case 2:
                                iv_12.setImageResource(toBeTapped);
                                break;
                            case 3:
                                iv_13.setImageResource(toBeTapped);
                                break;

                        }
                }
                 //Identical to row 1
                 if(row == 2){
                     iv_21.setImageResource(noTap);
                     iv_22.setImageResource(noTap);
                     iv_23.setImageResource(noTap);

                     switch (place) {
                         case 1:
                             iv_21.setImageResource(toBeTapped);
                             break;
                         case 2:
                             iv_22.setImageResource(toBeTapped);
                             break;
                         case 3:
                             iv_23.setImageResource(toBeTapped);
                             break;

                     }
                 }
                 //Identical to row 1 aside from switch statements
                 if(row == 3){
                     iv_31.setImageResource(noTap);
                     iv_32.setImageResource(noTap);
                     iv_33.setImageResource(noTap);
                        //Tile can also be a tap tile
                     switch (place) {
                         case 1:
                             iv_31.setImageResource(tap);
                             break;
                         case 2:
                             iv_32.setImageResource(tap);
                             break;
                         case 3:
                             iv_33.setImageResource(tap);
                             break;

                     }
                 }
                 //Identical to row 1
                 if(row == 4){
                     iv_41.setImageResource(noTap);
                     iv_42.setImageResource(noTap);
                     iv_43.setImageResource(noTap);

                     switch (place) {
                         case 1:
                             iv_41.setImageResource(toBeTapped);
                             break;
                         case 2:
                             iv_42.setImageResource(toBeTapped);
                             break;
                         case 3:
                             iv_43.setImageResource(toBeTapped);
                             break;

                     }
                 }
                 //Identical to row 1 aside from switch statements
                 if(row == 5){
                     iv_51.setImageResource(noTap);
                     iv_52.setImageResource(noTap);
                     iv_53.setImageResource(noTap);
                        //Tile can also be a tapped tile
                     switch (place) {
                         case 1:
                             iv_51.setImageResource(tapped);
                             break;
                         case 2:
                             iv_52.setImageResource(tapped);
                             break;
                         case 3:
                             iv_53.setImageResource(tapped);
                             break;

                     }
                 }
             }

    private int millisToTime (long millis) {
        return (int) millis / 1000;
    }
    private void loadImages() {
//        The dude in the tutorial wrote some horrible, horrible variables that in no way reflect
//        what they actually do. I'm going to leave them in commented out cus they're kinda funny.
//        frameImage = R.drawable.ic_frame;
//        pawInFrameImage = R.drawable.ic_paw_frame;
//        tapImage = R.drawable.ic_tap;
//        emptyImage = R.drawable.ic_empty;
//          The image names were equally terrible, but I kept em in

        toBeTapped = R.drawable.ic_frame;
        tapped = R.drawable.ic_paw_frame;
        tap = R.drawable.ic_tap;
        noTap = R.drawable.ic_empty;
    }
}