package com.oropallo.assunta.recipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.oropallo.assunta.recipes.domain.Ricetta;

import java.io.IOException;

import co.uk.rushorm.android.RushBitmapFile;
import co.uk.rushorm.core.RushCallback;
import dmax.dialog.SpotsDialog;


public class AddRicettaFragment3 extends Fragment {


    public AddRicettaFragment3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_ricetta_3, container, false);
        //per ricevere i parametri
        //String par=getArguments().getString("PAR");

        //editText procedimento
        final EditText procedimento= (EditText) view.findViewById(R.id.editTextProcediemento);
        final EditText note= (EditText) view.findViewById(R.id.editTextAddNota);
        Button save=(Button) view.findViewById(R.id.buttonSaveRicetta);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collect data
                final SpotsDialog dialog = new SpotsDialog(v.getContext());
                dialog.show();
                ActivityPageSlidingAddRicetta activity=(ActivityPageSlidingAddRicetta) getActivity();
                final Ricetta r=activity.getRicetta();
                String procedimento_str= procedimento.getText().toString();
                r.setProcedimento(procedimento_str);
                String nota_str= note.getText().toString();
                r.setNota(nota_str);
               final Bitmap image=r.getImage();

                if(image==null)
                    r.setHasImage(false);
                else
                    r.setHasImage(true);
                r.save(new RushCallback() {
                    @Override
                    public void complete() {
                        //controllare se c'è un'immagine e salvarla
                        if(image!=null) {
                            RushBitmapFile file = new RushBitmapFile(getContext().getFilesDir().getAbsolutePath().concat(r.getId()));
                            try {
                                file.setImage(image);
                                file.save(new RushCallback() {
                                    @Override
                                    public void complete() {
                                        dialog.dismiss();
                                        startActivtyRicetta(r);
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            dialog.dismiss();
                            startActivtyRicetta(r);
                        }

                    }});

            }
        });

        return view;
    }

    private void startActivtyRicetta(Ricetta r){
        Snackbar.make(getView(), "Ricetta inserita", Snackbar.LENGTH_SHORT).show();
        Log.d("DEBUG", "ricetta inserita");
        Intent intent= new Intent(getView().getContext(), RicettaActivity.class);
        //TODO gestire pulizia dello stack per non tornare indietro con il pulante back
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //passo l'id della ricetta creata all'activity RicettaActivity
        intent.putExtra("Ricetta", r.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        this.getActivity().finish();

    }


}
