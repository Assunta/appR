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

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.oropallo.assunta.recipes.R;
import com.oropallo.assunta.recipes.domain.DBManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import dmax.dialog.SpotsDialog;

/**
 * An activity to illustrate how to create a file.
 */
public class CreateFileActivity extends BaseDemoActivity {

    private static final String TAG = "CreateFileActivity";
    private static final String TITLE="recipesDB";
    private DriveId mFolderDriveId;
    private int countFileDaSalvare=0;
    private SpotsDialog dialog;
    private Activity context= this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new SpotsDialog(this);
        dialog.show();
        dialog.setMessage(getResources().getString(R.string.sic_in_corso));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        //TODO controllare se esiste già la directory e i file
        //controllo se i file e la directory esistono gia'
        /*Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.contains(SearchableField.TITLE, this.getPackageName())))
                .build();
        Drive.DriveApi.query(getGoogleApiClient(), query)
                .setResultCallback(metadataCallback);*/
        saveFirstTime();
    }

    private void saveFirstTime(){
        //create Folder
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(this.getPackageName()).build();
        Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                getGoogleApiClient(), changeSet).setResultCallback(callback);
        // create new contents resource: create file
        //create file with db information
        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(driveContentsCallback);
        //create image file for all images
        Map<String, Bitmap> images = DBManager.getAllImagesWithName();
        countFileDaSalvare = images.size();
        for (String name : images.keySet()) {
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
                                .setTitle(titl)
                                .setMimeType("image/bmp")
                                .setStarred(true).build();

                       folder.createFile(getGoogleApiClient(), changeSet, cont).setResultCallback(new ResultCallback<DriveFileResult>() {
                            @Override
                            public void onResult(DriveFileResult driveFileResult) {
                                DriveFile dFil = driveFileResult != null && driveFileResult.getStatus().isSuccess() ?
                                        driveFileResult.getDriveFile() : null;
                                if (dFil != null) {
                                    countFileDaSalvare--;
                                    if(countFileDaSalvare==0){
                                        dialog.dismiss();
                                        context.finish();
                                    }
                                    //showMessage("Create file "+titl);
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
                            .setTitle(TITLE)
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
            //showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
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
            //showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
            mFolderDriveId=result.getDriveFolder().getDriveId();
        }
    };

    //result calback query file dbRecipes exsist
    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving results");
                        return;
                    }
                    Iterator<Metadata> i=result.getMetadataBuffer().iterator();
                    while(i.hasNext()){
                        Metadata m= i.next();
                        Log.d("DEBUG", m.getTitle()+" "+m.isTrashable());
                    }
                    //TODO risolvere problema file eliminati ancora trushable
                    Log.d("DEBUG", result.getMetadataBuffer().getCount()+"");
                     if(result.getMetadataBuffer().getCount()>0) {
                        dialog.dismiss();
                        context.finish();
                    }
                    else
                        saveFirstTime();


                }
            };



}
