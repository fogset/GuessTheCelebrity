package com.example.tianhao.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    ImageView imageView;
    Button p1,p2,p3,p4;
    String[] answers = new String[4];
    int locationOfCorrectAnswer =0;
    int randNamesNumber;
    Random rand;

    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Wrong! It was" + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result =result+ current;
                    data = reader.read();
                }
                return result;

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView = findViewById(R.id.imageView);
        p1 = (Button)findViewById(R.id.button);
        p2 = (Button)findViewById(R.id.button2);
        p3 = (Button)findViewById(R.id.button3);
        p4 = (Button)findViewById(R.id.button4);


        DownloadTask task = new DownloadTask();
        String result = null;
        try{
            result =task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"listedArticles\">");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){
                celebURLs.add(m.group(1));
                System.out.println(m.group(1));
            }
            Pattern b = Pattern.compile(" alt=\"(.*?)\"/>");
            Matcher a = b.matcher(splitResult[0]);

            while(a.find()){
                celebNames.add(a.group(1));
                System.out.println(a.group(1));
            }

            newQuestion();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void newQuestion(){
        try {
            ImageDownloader imageTask = new ImageDownloader();
            rand = new Random();
            chosenCeleb = rand.nextInt(celebURLs.size());
            Bitmap celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();

            imageView.setImageBitmap(celebImage);
            locationOfCorrectAnswer = rand.nextInt(4);
            int incorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    incorrectAnswerLocation = rand.nextInt(celebURLs.size());
                    answers[i] = celebNames.get(incorrectAnswerLocation);
                    while (incorrectAnswerLocation == chosenCeleb) {
                        incorrectAnswerLocation = rand.nextInt(celebURLs.size());
                    }
                }
            }
            p1.setText(answers[0]);
            p2.setText(answers[1]);
            p3.setText(answers[2]);
            p4.setText(answers[3]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
