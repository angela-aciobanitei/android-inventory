package com.basic.udacity.inventoryapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    // Source: https://stackoverflow.com/questions/11790104/how-to-storebitmap-image-and-retrieve-image-from-sqlite-database-in-android

    // Convert from bitmap to byte array
    public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        // The ByteArrayOutputStream class implements an output stream in which the data
        // is written into a byte array. The buffer automatically grows as data is written
        // to it. The data can be retrieved using toByteArray() and toString().
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // Use the method from Bitmap class
        // boolean compress (Bitmap.CompressFormat format, int quality, OutputStream stream)
        // to write a compressed version of the bitmap to the specified outputstream. Parameters:
        // format	Bitmap.CompressFormat: The format of the compressed image
        // quality	int: Hint to the compressor, 0-100.
        //          0 meaning compress for small size, 100 meaning compress for max quality.
        //          Some formats, like PNG which is lossless, will ignore the quality setting.
        // stream	OutputStream: The outputstream to write the compressed data.

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    // Convert from byte array to bitmap
    public static Bitmap getBitmapFromByteArray(byte[] image) {
        // Use the method Bitmap decodeByteArray (byte[] data,  int offset,  int length)
        // to decode an immutable bitmap from the specified byte array. Parameters:
        // data    byte: byte array of compressed image data
        // offset  int: offset into imageData for where the decoder should begin parsing.
        // length  int: the number of bytes, beginning at offset, to parse
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
