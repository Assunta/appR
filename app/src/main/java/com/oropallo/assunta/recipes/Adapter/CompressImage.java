package com.oropallo.assunta.recipes.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Assunta on 13/12/2016.
 */

public class CompressImage {
    public static boolean resizeImage(String originalFilePath, String compressedFilePath) {
        InputStream in = null;
        try {
            in = new FileInputStream(originalFilePath);
        } catch (FileNotFoundException e) {
            Log.e("DEBUG","originalFilePath is not valid", e);
        }

        if (in == null) {
            return false;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap preview_bitmap = BitmapFactory.decodeStream(in, null, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        preview_bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] byteArray = stream.toByteArray();

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(compressedFilePath);
            outStream.write(byteArray);
            outStream.close();
            Log.e("DEBUG","immagine compressa con successo");
        } catch (Exception e) {
            Log.e("DEBUG","could not save", e);
        }

        return true;
    }
}
