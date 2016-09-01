package android.turhanoz.com.imageset;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    Button gallery, camera;
    final static int Camresultcode = 0;
    final static int Galresultcode = 100;
    ImageView cameraView, galleryView;
    CompressImage compressImage;
    private static final int VIDEO_CAPTURE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera = (Button) findViewById(R.id.btnCamera);
        compressImage = new CompressImage();
        cameraView = (ImageView) findViewById(R.id.imageCamera);
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        } else {
            setClicks();
        }

        // requestStoragePermission();
        //requestCaptureVideoOutput();
        //requestRe¬cordAudio();


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        ) {
                    setClicks();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                    Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
                break;
        }}
    public void setClicks() {
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, Camresultcode);
            }

        });


        camera.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                File mediaFile =
                        new File("/sdcard/CamCapturr/"
                                + "/myvideo.mp4");

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                Uri videoUri = Uri.fromFile(mediaFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10380902L);

                startActivityForResult(intent, VIDEO_CAPTURE);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true

        //If permission is not granted returning false
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri;
            if (requestCode == Camresultcode) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                selectedImageUri = getImageUri(getApplicationContext(), photo);
                Uri newuri = Uri.parse("file://" + compressImage.compressImage(selectedImageUri.toString(), this));
                Log.d("File saved ", "File saved");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), newuri);
                    createDirectoryAndSaveFile(bitmap, "sampleimage");

                } catch (Exception e) {
                    //handle exception
                }//new uri is the uri of new compressed image
            } else {
                Toast.makeText(MainActivity.this, "Video recorded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        Log.d("SF", "SF");
        File direct = new File(Environment.getExternalStorageDirectory() + "/CamCapturr");
        Log.d("SF", "SF");
        if (!direct.exists()) {
            Log.d("SF", "SFi");
            File wallpaperDirectory = new File("/sdcard/CamCapturr/");
            wallpaperDirectory.mkdirs();
        }
        File file = new File(new File("/sdcard/CamCapturr/"), fileName+".jpg");
        if (file.exists()) {
            file.delete();
            Log.d("SF", "SFi");
        }
        try {
            Log.d("SF", "SFt");
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.d("SF", "SFc");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
