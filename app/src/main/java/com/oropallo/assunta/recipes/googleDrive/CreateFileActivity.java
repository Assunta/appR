/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oropallo.assunta.recipes.googleDrive;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.oropallo.assunta.recipes.domain.DBManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * An activity to illustrate how to create a file.
 */
public class CreateFileActivity extends BaseDemoActivity {

    private static final String TAG = "CreateFileActivity";
    private DriveId mFolderDriveId;
    private int countImage=0;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        //create Folder
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(this.getPackageName()).build();
        Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                getGoogleApiClient(), changeSet).setResultCallback(callback);
        // create new contents resource: create file
        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(driveContentsCallback);

        //create image file for all images
        Map<String,Bitmap> images= DBManager.getAllImagesWithName();
        for(String name: images.keySet()){
            createFile(null, name, "image/bmp", images.get(name));
        }

    }

    /***********************************************************************
     * create file in GOODrive
     * @param pFldr parent's ID, (null for root)
     * @param titl  file name
     * @param mime  file mime type
     * @param file  java.io.File (with content) to create
     */
    void createFile(DriveFolder pFldr, final String titl, final String mime, final Bitmap file) {

            Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(new ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult driveContentsResult) {
                    DriveContents cont = driveContentsResult != null && driveContentsResult.getStatus().isSuccess() ?
                            driveContentsResult.getDriveContents() : null;
                    if (cont != null) try {
                        final DriveFolder folder = mFolderDriveId.asDriveFolder();
                        OutputStream outputStream = cont.getOutputStream();
                        file.compress(Bitmap.CompressFormat.PNG,90,outputStream);
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                //TODO settare il nome in modo che non si perda il riferimento con la ricetta
                                .setTitle(titl)
                                .setMimeType("image/bmp")
                                .setStarred(true).build();

                       folder.createFile(getGoogleApiClient(), changeSet, cont).setResultCallback(new ResultCallback<DriveFileResult>() {
                            @Override
                            public void onResult(DriveFileResult driveFileResult) {
                                DriveFile dFil = driveFileResult != null && driveFileResult.getStatus().isSuccess() ?
                                        driveFileResult.getDriveFile() : null;
                                if (dFil != null) {
                                    showMessage("Create file "+titl);
                                } else {
                                    showMessage("Error while trying to create the file");
                                }
                            }
                        });
                    } catch (Exception e)  {e.printStackTrace();}
                }
            });
        }

    final private ResultCallback<DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveContentsResult>() {
        @Override
        public void onResult(final DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to create new file contents");
                return;
            }
            //final DriveContents driveContents = result.getDriveContents();
            final DriveFolder folder = mFolderDriveId.asDriveFolder();
            // Perform I/O off the UI thread.
            new Thread() {
                @Override
                public void run() {
                    // write content to DriveContents
                    OutputStream outputStream = result.getDriveContents().getOutputStream();
                    Writer writer = new OutputStreamWriter(outputStream);
                    String jsonRicette= DBManager.exportRicette();
                    try {
                        writer.write(jsonRicette);
                        writer.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("dbRecipes")
                            .setMimeType("text/plain")
                            .setStarred(true).build();

                    // create a file on root folder
                    /*Drive.DriveApi.getRootFolder(getGoogleApiClient())
                            .createFile(getGoogleApiClient(), changeSet, driveContents)
                            .setResultCallback(fileCallback);*/

                    //create file in defined folder
                    folder.createFile(getGoogleApiClient(), changeSet, result.getDriveContents())
                            .setResultCallback(fileCallback);
                }
            }.start();
        }
    };

    final private ResultCallback<DriveFileResult> fileCallback = new
            ResultCallback<DriveFileResult>() {
        @Override
        public void onResult(DriveFileResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to create the file");
                return;
            }
            showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
            DriveId id= result.getDriveFile().getDriveId();
            Log.d(TAG, id+"");
        }
    };
    final ResultCallback<DriveFolder.DriveFolderResult> callback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to create the folder");
                return;
            }
            showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
            mFolderDriveId=result.getDriveFolder().getDriveId();
        }
    };


}