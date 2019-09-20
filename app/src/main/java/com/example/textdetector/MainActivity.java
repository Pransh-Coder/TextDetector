package com.example.textdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button click,detectButton;
    ImageView imageView;
    TextView textView;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        click=findViewById(R.id.snapBtn);
        detectButton=findViewById(R.id.detectBtn);
        imageView=findViewById(R.id.imageView);
        textView=findViewById(R.id.txtView);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    detectTxt();
                }catch (Exception e)
                {
                    Toast.makeText(MainActivity.this,"Please Click the Photo please!",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    //you probably want to get the image back from the camera application and do something with it.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");           // the img that we captured with our camera converted into bitmap
            imageView.setImageBitmap(imageBitmap);
        }
    }
    private void detectTxt() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);        //created an instance of FirebaseVisionImage i.e image
        //FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();               // not working!!
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    // for extracting text in form of each line
    private void processTxt(FirebaseVisionText text) {
        String blockList = text.getText();                                 //we will make a list to hold FirebaseVisionText blocks
        if(blockList.length()==0)
        {
            Toast.makeText(MainActivity.this,"No Text Found!",Toast.LENGTH_SHORT).show();
        }
        String txt="";
       for (FirebaseVisionText.TextBlock block : text.getTextBlocks()){
           txt += block.getText();                                        //block- iterator
           textView.setTextSize(24);
           textView.setText(txt);                                               // from loop we are grtting value in txt variable
       }
    }
    //For extracting text in form of element
    /*private void processTxt(FirebaseVisionText text) {
        String blockList = text.getText();                                 //we will make a list to hold FirebaseVisionText blocks
        if(blockList.length()==0)
        {
            Toast.makeText(MainActivity.this,"No Text Found!",Toast.LENGTH_SHORT).show();
        }
        String txt="";
        for (FirebaseVisionText.TextBlock block :text.getTextBlocks()){
             txt = text+ block.getText();                                        //block- iterator
            textView.setTextSize(20);
            textView.setText(txt);                                               // from loop we are grtting value in txt variable
        }
    }*/

    //For extracting text in form of text blocks
   /* private void processTxt(FirebaseVisionText text) {
        List<FirebaseVisionText.TextBlock> blockList = text.getTextBlocks();                                 //we will make a list to hold FirebaseVisionText blocks
        if(blockList.size()==0)
        {
            Toast.makeText(MainActivity.this,"No Text Found!",Toast.LENGTH_SHORT).show();
        }
        for (FirebaseVisionText.TextBlock block :text.getTextBlocks()){
            String txt = block.getText();                                        //block- iterator
            textView.setTextSize(24);
            textView.setText(txt);                                               // from loop we are grtting value in txt variable
        }
    }*/
}
