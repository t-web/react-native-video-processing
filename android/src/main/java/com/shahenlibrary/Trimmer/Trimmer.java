/*
 * MIT License
 *
 * Copyright (c) 2017 Shahen Hovhannisyan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.shahenlibrary.Trimmer;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Base64;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.shahenlibrary.Events.Events;

import java.io.ByteArrayOutputStream;

public class Trimmer {

    public static void getPreviewImages(String path, Promise promise) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);

        WritableArray images = Arguments.createArray();
        int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

        int aspectRatio = Math.round(width / height);
        int resizeWidth = 200;
        int resizeHeight = resizeWidth / aspectRatio;

        for (int i = 0; i < duration; i += duration / 10) {
            Bitmap currBmp = Bitmap.createScaledBitmap(retriever.getFrameAtTime(i * 1000), resizeWidth, resizeHeight, false);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            currBmp.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
            images.pushString(encoded);
        }

        WritableMap event = Arguments.createMap();

        event.putArray("images", images);

        promise.resolve(event);
        retriever.release();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void getVideoInfo(String path, Promise promise) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);

        int duration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        int width = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int height = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int orientation = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));

        WritableMap event = Arguments.createMap();
        event.putInt(Events.DURATION, duration);
        event.putInt(Events.WIDTH, width);
        event.putInt(Events.HEIGHT, height);
        event.putInt(Events.ORIENTATION, orientation);

        promise.resolve(event);
    }
}
