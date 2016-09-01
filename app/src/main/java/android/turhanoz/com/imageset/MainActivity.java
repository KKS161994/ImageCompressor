package android.turhanoz.com.imageset;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gallery = (Button) findViewById(R.id.btnGallery);
        camera = (Button) findViewById(R.id.btnCamera);
        compressImage = new CompressImage();
        cameraView = (ImageView) findViewById(R.id.imageCamera);
        galleryView = (ImageView) findViewById(R.id.imageGallery);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/jpeg");
                startActivityForResult(intent, Galresultcode);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, Camresultcode);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri;
            if (requestCode == Camresultcode) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                selectedImageUri = getImageUri(getApplicationContext(), photo);
                Uri newuri = Uri.parse("file://"+compressImage.compressImage(selectedImageUri.toString(), this));
                Log.d("File saved ","File saved");
                try
                {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver() , newuri);
                    createDirectoryAndSaveFile(bitmap,"hellobitches");

                }
                catch (Exception e)
                {
                    //handle exception
                }//new uri is the uri of new compressed image
              } else {
                Uri uri = data.getData();
                String[] filepathcolumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, filepathcolumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filepathcolumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                    Uri newuri = Uri.parse("file://"+compressImage.compressImage(uri.toString(), this));
              //newuri is the uri of new compressed image

            }
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
Log.d("SF","SF");
        File direct = new File(Environment.getExternalStorageDirectory() + "/Directory");
        Log.d("SF","SF");
        if (!direct.exists()) {
            Log.d("SF","SFi");
            File wallpaperDirectory = new File("/sdcard/DirName/");
            wallpaperDirectory.mkdirs();
        }
        File file = new File(new File("/sdcard/DirName/"), fileName);
        if (file.exists()) {
            file.delete();
            Log.d("SF","SFi");}
        try {
            Log.d("SF","SFt");
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.d("SF","SFc");
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
