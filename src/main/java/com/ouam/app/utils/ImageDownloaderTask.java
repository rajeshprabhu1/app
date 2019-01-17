package com.ouam.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import com.ouam.app.chat.SendBirdUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... URL) {

        String imageURL = URL[0];

        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        File file = getDownloadFile("OUAM_PROFILE_IMAGE");

        if (file == null)
            return;

        try {
            FileOutputStream out = new FileOutputStream(file);
            result.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            // here you can get file in "file" variable.

            SendBirdUtils.updateCurrentUserProfileImage(file, AppConstants.USER_NAME);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Function for create a file where file download.
    public static File getDownloadFile(String placeHolder) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/OUAM");

        if (!myDir.exists()) {
            if (!myDir.mkdirs()) {
                return null;
            }
        }

        String fname = "profile" + placeHolder + ".jpg";
        File file = new File(myDir, fname);

        if (file.exists()) file.delete();

        return file;
    }


}
