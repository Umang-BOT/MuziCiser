package com.example.music_player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class soundscreen extends AppCompatActivity {
    Button b1,b2,b3;
    String mode="OFF";
    TextView name,start,end;
    ImageView imag;
    SeekBar seek;
    String songname;
    public static final String Extra_name="song_name";
    static MediaPlayer mediaplayer;
    int position;
    ImageButton imgbut,voicerec;
    ArrayList<File> song;
    Thread updateseekbar;
    SpeechRecognizer speechRecognizer;
    Intent speechintent;
    String target ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundscreen);
        getSupportActionBar().hide();
        imgbut=findViewById(R.id.imgb);
        b1=findViewById(R.id.previous);
        b2=findViewById(R.id.pause);

        b3=findViewById(R.id.next);
        name=findViewById(R.id.textView6);
        start=findViewById(R.id.text00);
        end=findViewById(R.id.text11);
        seek=findViewById(R.id.seek);

        voice_command();
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(soundscreen.this);
        speechintent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matchesfound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesfound != null) {
                    if (mode.equals("ON")) {
                        target = matchesfound.get(0);
                        if (target.equals("pause the song")) {
                            if (mediaplayer.isPlaying()) {
                                b2.setBackgroundResource(R.drawable.play);
                                mediaplayer.pause();
                            }
                        }
                        if (target.equals("play the song")) {
                            b2.setBackgroundResource(R.drawable.pause);
                            mediaplayer.start();
                        }
                        if (target.equals("play next song")) {
                            mediaplayer.stop();
                            mediaplayer.release();
                            position = (position + 1) % song.size();
                            Uri uri = Uri.parse(song.get(position).toString());
                            songname = song.get(position).getName();
                            name.setText(songname);
                            mediaplayer = MediaPlayer.create(getApplicationContext(), uri);
                            mediaplayer.start();
                        }
                        if (target.equals("play previous song")) {
                            mediaplayer.stop();
                            mediaplayer.release();
                            if ((position - 1) < 0) {
                                position = song.size() - 1;
                            } else {
                                position -= 1;
                            }
                            Uri uri = Uri.parse(song.get(position).toString());
                            songname = song.get(position).getName();
                            name.setText(songname);
                            mediaplayer = MediaPlayer.create(getApplicationContext(), uri);
                            mediaplayer.start();
                        }
                    }
                    Toast.makeText(soundscreen.this, target + "", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        imgbut.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechintent);
                        target="";

                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });
        if(mediaplayer!=null){
            mediaplayer.start();
            mediaplayer.release();
        }
        Intent intent=getIntent();
        Bundle bundle =intent.getExtras();
        song=(ArrayList)bundle.getParcelableArrayList("song");
        String names=intent.getStringExtra("name");
        position=intent.getIntExtra("pos",0);
        name.setSelected(true);
        Uri uri=Uri.parse(song.get(position).toString());
        songname=song.get(position).getName();
        name.setText(songname);
        mediaplayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaplayer.start();
        updateseekbar=new Thread(){
            int duration=mediaplayer.getDuration();
            int curr=0;
            @Override
            public void run() {
                while(curr<duration){
                    try{
                        sleep(500);
                        curr=mediaplayer.getCurrentPosition();
                        seek.setProgress(curr);
                    }
                    catch(Exception e){
                        e.getStackTrace();
                    }
                }
                super.run();
            }
        };
        seek.setMax(mediaplayer.getDuration());
        updateseekbar.start();
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaplayer.seekTo(seekBar.getProgress());
            }
        });
        String entime=time(mediaplayer.getDuration());
        end.setText(entime);
        final Handler handler=new Handler();
        final int delay=1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currtime=time(mediaplayer.getCurrentPosition());
                start.setText(currtime);
                handler.postDelayed(this,delay);
            }
        },delay);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaplayer.isPlaying()){
                    b2.setBackgroundResource(R.drawable.play);
                    mediaplayer.pause();
                }
                else{
                    b2.setBackgroundResource(R.drawable.pause);
                    mediaplayer.start();
                }
            }
        });
        mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                b3.performClick();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaplayer.stop();
                mediaplayer.release();
                if((position-1)<0){
                    position=song.size()-1;
                }
                else{
                    position-=1;
                }
                Uri uri=Uri.parse(song.get(position).toString());
                songname=song.get(position).getName();
                name.setText(songname);
                mediaplayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaplayer.start();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaplayer.stop();
                mediaplayer.release();
                position=(position+1)%song.size();
                Uri uri=Uri.parse(song.get(position).toString());
                songname=song.get(position).getName();
                name.setText(songname);
                mediaplayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaplayer.start();
            }
        });
    }
    public String time(int duration){
        String time="";
        int min=duration/1000/60;
        int sec=duration/100/60;
        time=time+min+":";
        if(sec<10) {
            time+='0';
        }
        time+=sec;
        return time;
    }
    public void voice_command(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(soundscreen.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)){
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }
}