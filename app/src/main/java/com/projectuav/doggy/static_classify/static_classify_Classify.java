package com.projectuav.doggy.static_classify;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.projectuav.doggy.MainActivity;
import com.projectuav.doggy.R;
import com.projectuav.doggy.ProfileActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class static_classify_Classify extends AppCompatActivity {

    // presets for rgb conversion
    private static final int RESULTS_TO_SHOW = 3;
    private static final float IMAGE_MEAN = 127.5f;
    private static final float IMAGE_STD = 127.5f;

    // options for model interpreter
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
    // tflite graph
    private Interpreter tflite;
    // holds all the possible labels for model
    private List<String> labelList;
    // holds the selected image data as bytes
    private ByteBuffer imgData = null;
    // holds the probabilities of each label for non-quantized graphs
    private float[][] labelProbArray = null;
    // holds the probabilities of each label for quantized graphs
    private byte[][] labelProbArrayB = null;
    // array that holds the labels with the highest probabilities
    private String[] topLabels = null;
    // array that holds the highest probabilities
    private String[] topConfidence = null;


    // selected classifier information received from extras
    private String chosen;
    private boolean quant;

    // input image dimensions for the Inception Model
    private int DIM_IMG_SIZE_X = 224;
    private int DIM_IMG_SIZE_Y = 224;
    private int DIM_PIXEL_SIZE = 3;

    // int array to hold image data
    private int[] intValues;
    private Uri imageUri;

    // for permission requests
    public static final int REQUEST_PERMISSION = 300;

    // request code for permission requests to the os for image
    public static final int REQUEST_IMAGE = 100;

    // activity elements
    private ImageView selected_image;
    private Button back_button,again_button,unlock_button;
    private TextView label1;
    private TextView label2;
    private TextView label3;
    private TextView Confidence1;
    private TextView Confidence2;
    private TextView Confidence3;
    private TextView upperText;
    private TextView changeableText;
    private LinearLayout textLayout;
    private LottieAnimationView notFoundAnim;
    Uri uri;


    public static final String PRODUCT_PHOTO = "photo";
    public static Bitmap product_image;


    // priority queue that will hold the top results from the CNN
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // get all selected classifier data from classifiers
        chosen = (String) getIntent().getStringExtra("chosen");
        quant = (boolean) getIntent().getBooleanExtra("quant", false);

        // initialize array that holds image data
        intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

        super.onCreate(savedInstanceState);

        //initilize graph and labels
        try{
            tflite = new Interpreter(loadModelFile(), tfliteOptions);
            labelList = loadLabelList();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        // initialize byte array. The size depends if the input data needs to be quantized or not
        if(quant){
            imgData =
                    ByteBuffer.allocateDirect(
                            DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        } else {
            imgData =
                    ByteBuffer.allocateDirect(
                            4 * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        }
        imgData.order(ByteOrder.nativeOrder());

        // initialize probabilities array. The datatypes that array holds depends if the input data needs to be quantized or not
        if(quant){
            labelProbArrayB= new byte[1][labelList.size()];
        } else {
            labelProbArray = new float[1][labelList.size()];
        }

        setContentView(R.layout.static_classify_activity_classify);

        // labels that hold top three results of CNN
        label1 = (TextView) findViewById(R.id.static_classify_label1);
        label2 = (TextView) findViewById(R.id.static_classify_label2);
        label3 = (TextView) findViewById(R.id.static_classify_label3);
        // displays the probabilities of top labels
        Confidence1 = (TextView) findViewById(R.id.static_classify_Confidence1);
        Confidence2 = (TextView) findViewById(R.id.static_classify_Confidence2);
        Confidence3 = (TextView) findViewById(R.id.static_classify_Confidence3);
        // initialize imageView that displays selected image to the user
        selected_image = (ImageView) findViewById(R.id.static_classify_selected_image);

        // initialize array to hold top labels
        topLabels = new String[RESULTS_TO_SHOW];
        // initialize array to hold top probabilities
        topConfidence = new String[RESULTS_TO_SHOW];

        // allows user to go back to activity to select a different image
        back_button = (Button)findViewById(R.id.static_classify_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(static_classify_Classify.this, MainActivity.class);
                startActivity(i);
            }
        });
        again_button = findViewById(R.id.static_again_back_button);
        again_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // filename in assets
                chosen = "slow.tflite";
                // model in not quantized
                quant = false;
                // open camera
                openCameraIntent();
            }
        });
        unlock_button = findViewById(R.id.static_unlock_button);
        unlock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(static_classify_Classify.this, ProfileActivity.class);
                startActivity(intent);
            }
        });



        new java.util.Timer().schedule( new java.util.TimerTask() {
            @Override public void run() {
                // get current bitmap from imageView
                Bitmap bitmap_orig = ((BitmapDrawable)selected_image.getDrawable()).getBitmap();
                // resize the bitmap to the required input size to the CNN
                Bitmap bitmap = getResizedBitmap(bitmap_orig, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y);
                // convert bitmap to byte array
                convertBitmapToByteBuffer(bitmap);
                // pass byte data to the graph
                if(quant){
                    tflite.run(imgData, labelProbArrayB);
                } else {
                    tflite.run(imgData, labelProbArray);
                }
                // display the results
                printTopKLabels();
            }},250);

        // get image from previous activity to show in the imageView
        uri = (Uri)getIntent().getParcelableExtra("resID_uri");
        try {
            Bitmap bitmapX = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            selected_image.setImageBitmap(bitmapX);
            // not sure why this happens, but without this the image appears on its side
//            selected_image.setRotation(selected_image.getRotation() + 90);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // loads tflite grab from file
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd(chosen);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // converts bitmap to byte array which is passed in the tflite graph
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // loop through all pixels
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                // get rgb values from intValues where each int holds the rgb values for a pixel.
                // if quantized, convert each rgb value to a byte, otherwise to a float
                if(quant){
                    imgData.put((byte) ((val >> 16) & 0xFF));
                    imgData.put((byte) ((val >> 8) & 0xFF));
                    imgData.put((byte) (val & 0xFF));
                } else {
                    imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                }

            }
        }
    }

    // loads the labels from the label txt file in assets into a string array
    private List<String> loadLabelList() throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("label.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    // print the top labels and respective confidences
    private void printTopKLabels() {
        // add all results to priority queue

        textLayout = (LinearLayout) findViewById(R.id.textLayout);
        upperText = (TextView) findViewById(R.id.upperText);
        changeableText = (TextView) findViewById(R.id.changeableText);
        notFoundAnim = (LottieAnimationView) findViewById(R.id.notFoundAnim);

        for (int i = 0; i < labelList.size(); ++i) {
            if(quant){
                sortedLabels.add(
                        new AbstractMap.SimpleEntry<>(labelList.get(i), (labelProbArrayB[0][i] & 0xff) / 255.0f));
            } else {
                sortedLabels.add(
                        new AbstractMap.SimpleEntry<>(labelList.get(i), labelProbArray[0][i]));
            }
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }

        // get top results from priority queue
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            topLabels[i] = label.getKey();
            topConfidence[i] = String.format("%.0f%%",label.getValue()*100);
        }

        SharedPreferences temp = getApplicationContext().getSharedPreferences("doggy",MODE_PRIVATE);
        SharedPreferences.Editor tempEditor = temp.edit();
        SharedPreferences imgStr = getApplicationContext().getSharedPreferences("imagesText",MODE_PRIVATE);
        SharedPreferences.Editor imgStrEditor = imgStr.edit();

        String accuracy = topConfidence[2].replace("%","");
        if (Integer.parseInt(accuracy)<40){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textLayout.setVisibility(View.GONE);
                    notFoundAnim.setVisibility(View.VISIBLE);
                    // set the corresponding textviews with the results
                    label1.setText(topLabels[2]);
//            label2.setVisibility(View.INVISIBLE);
//            label3.setVisibility(View.INVISIBLE);
                    Confidence1.setText(topConfidence[2] + " Match");
//            Confidence2.setVisibility(View.INVISIBLE);
//            Confidence3.setVisibility(View.INVISIBLE);
                    upperText.setText("Sorry, Your match wasn't high enough!");
                    changeableText.setText("Please use different or clear image!");
                }
            });
        }
        else{
            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                // set the corresponding textviews with the results
                  label1.setText(topLabels[2]);
                  label2.setText(topLabels[1]);
                  label3.setText(topLabels[0]);
                  Confidence1.setText(topConfidence[2] + " Match");
                  Confidence2.setText(topConfidence[1] + " Match");
                  Confidence3.setText(topConfidence[0] + " Match");
                  textLayout.setVisibility(View.VISIBLE);
                         }
            });

            tempEditor.putBoolean(topLabels[2], true);
            tempEditor.apply();
            imgStrEditor.putString(topLabels[2],uri.toString());
            imgStrEditor.apply();
        }
        tempEditor.putString("TotalSnaps", String.valueOf(Integer.parseInt(temp.getString("TotalSnaps","0"))+1));
        tempEditor.apply();
    }


    // resizes bitmap to given dimensions
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
    // opens camera for user
    private void openCameraIntent() {
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, "New Picture");
//        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//        // tell camera where to store the resulting picture
//        imageUri = getContentResolver().insert(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        // start camera, and wait for it to finish
//        startActivityForResult(intent, REQUEST_IMAGE);
        CropImage.startPickImageActivity(static_classify_Classify.this);
    }

    // checks that the user has allowed all the required permission of read and write and camera. If not, notify the user and close the application
    @SuppressLint("ShowToast")
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(static_classify_Classify.this, "This application needs read, write, and camera permissions to run. Application now closing.", Toast.LENGTH_LONG);
                System.exit(0);
            }
        }
    }

    // dictates what to do after the user takes an image, selects and image, or crops an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the camera activity is finished, obtained the uri, crop it to make it square, and send it to 'Classify' activity
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                Uri imageuri = CropImage.getPickImageResultUri(this,data);
                if(CropImage.isReadExternalStoragePermissionsRequired(this,imageuri)){
                    uri = imageuri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }else {
                    startCrop(imageuri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if cropping activty is finished, get the resulting cropped image uri and send it to 'Classify' activity
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
//            imageUri = Crop.getOutput(data);
            Intent i = new Intent(static_classify_Classify.this, static_classify_Classify.class);
            // put image data in extras to send
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                int nh = (int) ( bitmap.getHeight() * (1080.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1080, nh, true);
                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), scaled, "SnapDogg" + Calendar.getInstance().getTime(), null);
                resultUri = Uri.parse(path);

//                AMIT WALA
                i.putExtra("resID_uri", resultUri);
                // put filename in extras
                i.putExtra("chosen", chosen);
                // put model type in extras
                i.putExtra("quant", quant);
                // send other required data
                startActivity(i);
            }
            catch (Exception e) {
                Toast.makeText(this, "Please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void startCrop(Uri imageuri){
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }
}
