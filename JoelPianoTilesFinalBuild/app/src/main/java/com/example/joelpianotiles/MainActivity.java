package com.example.joelpianotiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewStructure;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    /** for firestore */
    private static final String KEY_TITLE = "name";
    private static final String KEY_DESCRIPTION = "score";
    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewData;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("scoreboard");
    private DocumentReference noteRef = db.document("Scoreboard/My First Note");
    ImageView
            //This ImageView reflects the placement of the grid squares made in grid.xml used to put images in each grid
            iv_11, iv_12, iv_13, iv_14,
            iv_21, iv_22, iv_23, iv_24,
            iv_31, iv_32, iv_33, iv_34,
            iv_41, iv_42, iv_43, iv_44;
    //creates a variable for start button
    Button b_play;

    LinearLayout splashscreen;
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
        //Connects the variable to our various grid squares (now rectangles) from grid.xml, (ImageView) is redundant but i'm keeping it in to help label since it does no harm.
        iv_11 = (ImageView) findViewById(R.id.iv_11);
        iv_12 = (ImageView) findViewById(R.id.iv_12);
        iv_13 = (ImageView) findViewById(R.id.iv_13);
        iv_14 = (ImageView) findViewById(R.id.iv_14);

        iv_21 = (ImageView) findViewById(R.id.iv_21);
        iv_22 = (ImageView) findViewById(R.id.iv_22);
        iv_23 = (ImageView) findViewById(R.id.iv_23);
        iv_24 = (ImageView) findViewById(R.id.iv_24);

        iv_31 = (ImageView) findViewById(R.id.iv_31);
        iv_32 = (ImageView) findViewById(R.id.iv_32);
        iv_33 = (ImageView) findViewById(R.id.iv_33);
        iv_34 = (ImageView) findViewById(R.id.iv_34);

        iv_41 = (ImageView) findViewById(R.id.iv_41);
        iv_42 = (ImageView) findViewById(R.id.iv_42);
        iv_43 = (ImageView) findViewById(R.id.iv_43);
        iv_44 = (ImageView) findViewById(R.id.iv_44);

        //Connects the variable for our Button from bottom_bar.xml, once again (Button) is redundant but i'm keeping it in to help label.
        b_play = (Button) findViewById(R.id.b_play);

        splashscreen = findViewById(R.id.include4);
        //Gives a variable for our leaderboard so we can hide it later
        LinearLayout one = findViewById(R.id.upload_score);
        //Connects the variable for score referencing tv_score in top_bar.xml
        tv_score = (TextView) findViewById(R.id.tv_score);
        tv_score.setText("SCORE: " + currentScore);
        //Connects the variable for best score referencing tv_best in top_bar.xml
        tv_best = (TextView) findViewById(R.id.tv_best);
        tv_best.setText("BEST: " + bestScore);
        //Connects the variable for time referencing tv_time
        tv_time = (TextView) findViewById(R.id.tv_time);
        //Set default time to 15 seconds,
        tv_time.setText("TIME: " + millisToTime(15000));
        //adds a random number to our r variable
        r = new Random();

        //for firestore
        editTextTitle = findViewById(R.id.edit_text_title);
        textViewData = findViewById(R.id.text_view_data);

        loadImages();
        //Creates a timer, millsInFuture must match what we set the text to be,
        timer = new CountDownTimer(15000, 500) {
            @Override
            public void onTick (long millisUnitFinished) {
                //This initially had (millisUnitFished + 1) for some reason.. all it did was add a 1 to the countdown
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
                iv_34.setEnabled(false);
                //The play button becomes visible again
                b_play.setVisibility(View.VISIBLE);
                splashscreen.setVisibility(View.VISIBLE);
                //All tiles switch to the noTap state, resulting in them becoming the image attached to the state (which should be nothing)
                iv_11.setImageResource(noTap);
                iv_12.setImageResource(noTap);
                iv_13.setImageResource(noTap);
                iv_14.setImageResource(noTap);

                iv_21.setImageResource(noTap);
                iv_22.setImageResource(noTap);
                iv_23.setImageResource(noTap);
                iv_24.setImageResource(noTap);

                iv_31.setImageResource(noTap);
                iv_32.setImageResource(noTap);
                iv_33.setImageResource(noTap);
                iv_34.setImageResource(noTap);

                iv_41.setImageResource(noTap);
                iv_42.setImageResource(noTap);
                iv_43.setImageResource(noTap);
                iv_44.setImageResource(noTap);

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
            //existing in iv_31-34 (AKA columns 1, 2, 3 and 4 in row 3) has the ImageResource
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

        iv_34.setOnClickListener(v -> {
            if(tileLocationRow3 == 4) {
                continueGame();
            }else{
                endGame();
            }
        });

        b_play.setOnClickListener(v -> initGame());
    }
//This is the continue game function, each time a correct tile is tapped the following happens:
        private void continueGame (){
            //For context, row 1 is the top row and 4 is the bottom, this is kinda backwards

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
            //randomly generates a toBeTapped location between columns one and four in row one
            //Because java counts from 0 a plus 1 is added
            tileLocationRow1 = r.nextInt(4) + 1;
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
            iv_34.setEnabled(true);
            //Sets the play button to invisible
            b_play.setVisibility(View.INVISIBLE);
            splashscreen.setVisibility(View.GONE);
            //Score is 0 by default
            currentScore = 0;
            //Resets the score to 0
            tv_score.setText("SCORE: " + currentScore);
            //Stars the timer
            timer.start();



            //row4

            /**
             * Defines weather the initial tapped tile starts in column 1 2 3 or 4.
             * The initial value doesn't matter really as i've removed setImageResource from the start for it
             */
            tileLocationRow4 = 2;
            //Randomly generates a tap file between columns 1 and 4 in row 3 (via case 1-4)
            tileLocationRow3 = r.nextInt(4) + 1;
            setTileLocation(tileLocationRow3, 3);
            //row2
            tileLocationRow2 = r.nextInt(4) + 1;
            setTileLocation(tileLocationRow2, 2);
            //row1
            //Randomly generates a ToBeTapped tile between columns one and four on row one (via case 1-4)
            tileLocationRow1 = r.nextInt(4) + 1;
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
                 iv_34.setEnabled(false);
                 //The play button is now visible
                 b_play.setVisibility(View.VISIBLE);
                 splashscreen.setVisibility(View.VISIBLE);
                //set all tiles to noTap image
                 iv_11.setImageResource(noTap);
                 iv_12.setImageResource(noTap);
                 iv_13.setImageResource(noTap);
                 iv_14.setImageResource(noTap);

                 iv_21.setImageResource(noTap);
                 iv_22.setImageResource(noTap);
                 iv_23.setImageResource(noTap);
                 iv_24.setImageResource(noTap);

                 iv_31.setImageResource(noTap);
                 iv_32.setImageResource(noTap);
                 iv_33.setImageResource(noTap);
                 iv_34.setImageResource(noTap);

                 iv_41.setImageResource(noTap);
                 iv_42.setImageResource(noTap);
                 iv_43.setImageResource(noTap);
                 iv_44.setImageResource(noTap);


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
                    iv_14.setImageResource(noTap);
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
                            case 4:
                                iv_14.setImageResource(toBeTapped);
                                break;

                        }
                }
                 //Identical to row 1
                 if(row == 2){
                     iv_21.setImageResource(noTap);
                     iv_22.setImageResource(noTap);
                     iv_23.setImageResource(noTap);
                     iv_24.setImageResource(noTap);

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
                         case 4:
                             iv_24.setImageResource(toBeTapped);
                             break;

                     }
                 }
                 //Identical to row 1 aside from switch statements
                 if(row == 3){
                     iv_31.setImageResource(noTap);
                     iv_32.setImageResource(noTap);
                     iv_33.setImageResource(noTap);
                     iv_34.setImageResource(noTap);
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
                         case 4:
                             iv_34.setImageResource(tap);
                             break;

                     }
                 }
                 //Identical to row 1
                 if(row == 4){
                     iv_41.setImageResource(noTap);
                     iv_42.setImageResource(noTap);
                     iv_43.setImageResource(noTap);
                     iv_44.setImageResource(noTap);

                     //Tile can also be a tapped tile
                     switch (place) {
                         case 1:
                             iv_41.setImageResource(tapped);
                             break;
                         case 2:
                             iv_42.setImageResource(tapped);
                             break;
                         case 3:
                             iv_43.setImageResource(tapped);
                             break;
                         case 4:
                             iv_44.setImageResource(tapped);
                             break;

                     }
                 }
             }

    private int millisToTime (long millis) {
        return (int) millis / 1000;
    }
    private void loadImages() {
/**       The dude in the tutorial wrote some horrible, horrible variables that in no way reflect
//        what they actually do. I'm going to leave them in commented out cus they're kinda funny.
//        frameImage = R.drawable.ic_frame;
//        pawInFrameImage = R.drawable.ic_paw_frame;
//        tapImage = R.drawable.ic_tap;
//        emptyImage = R.drawable.ic_empty;
          The image names were equally terrible, but I kept em in **/

        toBeTapped = R.drawable.ic_frame;
        tapped = R.drawable.ic_paw_frame;
        tap = R.drawable.ic_tap;
        noTap = R.drawable.ic_empty;
    }

    /**
     * The following is Firebase integration for the leaderboard
     */
    @Override
    protected void onStart() {
        super.onStart();
        //On start sort the field score from greatest to least
        notebookRef.whereGreaterThanOrEqualTo("score", 0)
                .orderBy("score", Query.Direction.DESCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            //Gets data and returns it
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());
                    String name = note.getName();
                    int score = note.getScore();
                    //Returns data with the following string
                    data += "\nname: " + name + "\nscore: " + score + "\n";
                }
                textViewData.setText(data);
            }
        });
    }
    public void addNote(View v) {
        //Takes the string you write in for the username and takes the saved bestScore variable to add
        String name = editTextTitle.getText().toString();
        int score = bestScore;
        Note note = new Note(name, score);
        notebookRef.add(note);
    }
    //Reloads the list manually instead of on start
    public void loadNotes(View v) {
        notebookRef.whereGreaterThanOrEqualTo("score", 0)
                .orderBy("score", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentId(documentSnapshot.getId());
                            String name = note.getName();
                            int score = note.getScore();
                            data += "\nname: " + name + "\nscore: " + score + "\n";
                        }
                        textViewData.setText(data);
                    }
                });
    }



}