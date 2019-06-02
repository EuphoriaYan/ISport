package com.isport.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.isport.Bean.GlobalValues;
import com.isport.Bean.LocalUser;
import com.isport.Bean.Pair;
import com.isport.Bean.ShareRecord;
import com.isport.Database.DatabaseManager;
import com.isport.R;
import com.isport.Utils.FileUtil;
import com.isport.Utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Euphoria on 2017/8/4.
 */

public class EasyPictureActivity extends Activity implements View.OnClickListener {

    public Bitmap photo;
    public String picPath;
    Pair<String, String> fileInfo;

    EditText etEasyPictureText;
    Button btSubmitshare;
    Button btdophoto;

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bt_submit_share:
                ToastUtil.setLongToast(getApplicationContext(), "正在上传……请等待");
                DatabaseManager.getInstance().UpdatePicture(picPath, fileInfo.getFirst());
                ShareRecord shareRecord = new ShareRecord();
                shareRecord.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                shareRecord.setUserId(LocalUser.getInstance().getUserid());
                shareRecord.setText(etEasyPictureText.getText().toString());
                shareRecord.setImgUrl(fileInfo.getFirst());
                DatabaseManager.getInstance().insertShareRecord(shareRecord);
                GlobalValues.MyShareRecord.add(0, shareRecord);
                ToastUtil.setLongToast(getApplicationContext(), "上传完毕");
                //EasyPictureActivity.this.finish();
                break;
            case R.id.bt_doPhoto:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int checkCallPhonePermission;
                    checkCallPhonePermission = ContextCompat.checkSelfPermission(EasyPictureActivity.this, Manifest.permission.CAMERA);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EasyPictureActivity.this,new String[]{Manifest.permission.CAMERA}, 222);
                        break;
                    }
                    checkCallPhonePermission = ContextCompat.checkSelfPermission(EasyPictureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EasyPictureActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);
                        break;
                    }
                    checkCallPhonePermission = ContextCompat.checkSelfPermission(EasyPictureActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EasyPictureActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 222);
                        break;
                    }
                    doPhoto(v);
                } else {
                    doPhoto(v);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_easypicture);
        etEasyPictureText = (EditText) findViewById(R.id.et_shareText);
        btSubmitshare = (Button) findViewById(R.id.bt_submit_share);
        btdophoto = (Button) findViewById(R.id.bt_doPhoto);
        btSubmitshare.setOnClickListener(this);
        btdophoto.setOnClickListener(this);
    }

    public void doPhoto(View view)
    {
        destoryBitmap();
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            //指派文件名&路径
            fileInfo = FileUtil.getImagesDirPath("EasyPicture");
            File EP = new File(fileInfo.getSecond());
            if (!EP.exists()) EP.mkdirs();
            picPath = fileInfo.getSecond() + File.separator + fileInfo.getFirst();
            File out = new File(picPath);
            Uri uri = Uri.fromFile(out);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(EasyPictureActivity.this, "没有SD卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        File pic = new File(picPath);
        if (!pic.exists()) return;
        btdophoto.setVisibility(View.GONE);
        btSubmitshare.setVisibility(View.VISIBLE);
        if (this.photo != null)
            destoryBitmap();
        this.photo = BitmapFactory.decodeFile(picPath);
        ImageView imageView = (ImageView) findViewById(R.id.iv_showPhoto);
        imageView.setImageBitmap(this.photo);
    }

    /**
     * 销毁图片文件
     */
    private void destoryBitmap()
    {
        if (photo != null && !photo.isRecycled()) {
            photo.recycle();
            photo = null;
        }
    }

}
