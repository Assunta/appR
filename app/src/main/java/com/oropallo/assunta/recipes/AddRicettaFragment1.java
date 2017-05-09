package com.oropallo.assunta.recipes;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.oropallo.assunta.recipes.Adapter.CompressImage;
import com.oropallo.assunta.recipes.domain.Ricetta;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddRicettaFragment1 extends Fragment {

    private static int RESULT_LOAD_IMAGE = 1;
    private static int REQUEST_CAMERA=0;
    private EditText name;
    private EditText num;
    private Spinner spinner_category;
    private ImageView image;
    private Bitmap imageLoad;
    public AddRicettaFragment1() {
    }

    @Override
    public void onResume() {
        super.onResume();
       /* Button button = (Button) getActivity().findViewById(R.id.button);
        button.setVisibility(View.VISIBLE);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_add_ricetta_1, container, false);


        //editText name
        name= (EditText) view.findViewById(R.id.editText_addRicetta);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText("");
            }
        });

        //spinner category
        spinner_category= (Spinner) view.findViewById(R.id.spinner_categoria);
        ArrayAdapter<CharSequence> spinner_adapter= ArrayAdapter.createFromResource(getContext(),R.array.category, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category.setAdapter(spinner_adapter);

        //Edit text num persone
        num= (EditText) view.findViewById(R.id.editText_numPersone);

        //Add immagine
        image= (ImageView) view.findViewById(R.id.imageView_add_ricetta);
        image.setVisibility(View.VISIBLE);
        imageLoad=null;
        final Button button= (Button) view.findViewById(R.id.button_add_immagine_ricetta);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  //OPEN GALLERIA
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                //image.setImageResource(R.drawable.ic_menu_gallery);*/
                selectImage();
                button.setText("Change foto");
            }
        });

        return view;
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            RESULT_LOAD_IMAGE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void onCaptureImageResult(Intent data){
        imageLoad = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageLoad.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        image.setImageBitmap(imageLoad);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = this.getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            try {
                String destFilePath = File.createTempFile("compressedFilePath", "jpg", this.getContext().getCacheDir()).getAbsolutePath();
                CompressImage.resizeImage(picturePath, destFilePath);
                picturePath=destFilePath;
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageLoad=BitmapFactory.decodeFile(picturePath);
            image.setImageBitmap(imageLoad);
        }else if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && null != data){
            onCaptureImageResult(data);
        }
    }
    public void getInfo(){
        String nomeRicetta= name.getText().toString();
        String catogory= spinner_category.getSelectedItem().toString();
        String num_P= num.getText().toString();
        ActivityPageSlidingAddRicetta activity=(ActivityPageSlidingAddRicetta) getActivity();
        Ricetta r=activity.getRicetta();
        r.setNome(nomeRicetta);
        r.setCategoria(catogory);
        r.setNum_persone(num_P);
        r.setImage(imageLoad);
        activity.setRicetta(r);
    }
}
