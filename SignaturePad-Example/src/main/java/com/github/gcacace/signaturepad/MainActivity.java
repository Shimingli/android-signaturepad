package com.github.gcacace.signaturepad;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.gcacace.signaturepad.utils.logger.KLog;
import com.github.gcacace.signaturepad.views.LinePathView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import it.gcacace.signaturepad.R;

public class MainActivity extends Activity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private LinePathView mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private DynamicAdapter mDynamicAdapter;
    private String mAbsolutePath;
    private EditText mEText;
    private Bitmap mBitmap;
    private static String full_name = "";
    private static final String LAST_NAME = "img_";
    private int first_name = 1;
    private File mPhoto;
    private Button mBitmapButton;
    private ImageView mImageView;

    public  void displayImage(String url, ImageView imageView, int defRes) {
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(defRes).error(defRes).into(imageView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        AppContext.init(this);
        setContentView(R.layout.activity_main);

        //        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rc_who_can_select);
         mClearButton = (Button) findViewById(R.id.clear);
        mSaveButton = (Button) findViewById(R.id.save);
        mBitmapButton = (Button) findViewById(R.id.bitmap);
        mImageView = (ImageView) findViewById(R.id.iamgeview);
        mBitmapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                    startActivity(intent);
//                if (mImageView.getVisibility()==View.GONE){
//                    mImageView.setVisibility(View.VISIBLE);
//                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
//                    startActivity(intent);
//                }else {
//                    mImageView.setVisibility(View.GONE);
//                }

            }
        });
        //        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5) {
//            @Override
//            public boolean canScrollVertically() {
//                return true;
//            }
//        };
//        recyclerView.setLayoutManager(gridLayoutManager);
//        mDynamicAdapter = new DynamicAdapter(null,this);
//        recyclerView.setAdapter(mDynamicAdapter);
        mEText = (EditText) findViewById(R.id.modify_edit_text_view);
//        mEText.setInputType(InputType.TYPE_NULL);
        //禁止输入文字，但是不能输入了
        mEText.setFocusable(false);
        mSignaturePad = (LinePathView) findViewById(R.id.signature_pad);

       mClearButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mSignaturePad.clear();
           }
       });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bitmap bitMap = mSignaturePad.getSignatureBitmap();
//                boolean b = addJpgSignatureToGallery(bitMap);
//                if (b){
//                    saveBitmap();
//                    mSignaturePad.clear();
//                }
            }
        });

     /*   mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Toast.makeText(MainActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignaturePad.clearView();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();

                if (addJpgSignatureToGallery(signatureBitmap)) {
                    saveBitmap();
//                    mDynamicAdapter.setNewBitmap(signatureBitmap);
//                    mDynamicAdapter.setNewPath(mAbsolutePath);
                    mSignaturePad.clearView();
                    Toast.makeText(MainActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
//                if (addSvgSignatureToGallery(mSignaturePad.getSignatureSvg())) {
//                    Toast.makeText(MainActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
//                }
            }
        });
       mBitmapButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (mImageView.getVisibility()==View.GONE) {
                   Bitmap bitmapByView = getBitmapByView(mEText);
                   addJpgSignatureToGallery(bitmapByView);

                   mImageView.setVisibility(View.VISIBLE);
                   mImageView.setImageBitmap(bitmapByView);
               }else {
                   mImageView.setVisibility(View.GONE);
               }
           }
       });*/

    }
    public static Bitmap getBitmapByView(EditText editText) {
        int h = 0;
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(editText.getWidth(), editText.getHeight(),
                Bitmap.Config.ARGB_8888);//565  没有
        final Canvas canvas = new Canvas(bitmap);
        editText.draw(canvas);
        return bitmap;
    }
    public void saveBitmap() {
        mBitmap = null;
        try {
            FileInputStream fis = new FileInputStream(mPhoto);
            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
            mBitmap = BitmapUtils.resizeImage(bitmap, 150,150);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (mBitmap != null) {
            //根据Bitmap对象创建ImageSpan对象
            ImageSpan imageSpan = new ImageSpan(MainActivity.this, mBitmap);
            //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
            full_name = LAST_NAME + first_name;
            String s = "[" + full_name + "]";
            first_name++;
            SpannableString spannableString = new SpannableString(s);
            //  用ImageSpan对象替换face
            spannableString.setSpan(imageSpan, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //将选择的图片追加到EditText中光标所在位置
            int index = mEText.getSelectionStart(); //获取光标所在位置
            Editable edit_text = mEText.getEditableText();
            if (index < 0 || index >= edit_text.length()) {
                edit_text.append(spannableString);
            } else {
                edit_text.insert(index, spannableString);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }


    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        //图片的质量
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            mPhoto = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, mPhoto);
            mAbsolutePath = mPhoto.getAbsolutePath();
            KLog.d("shiming path"+mAbsolutePath);
            //            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        MainActivity.this.sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity the activity from which permissions are checked
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}
