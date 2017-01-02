package com.oropallo.assunta.recipes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.oropallo.assunta.recipes.Adapter.AdapterAddIngrediente;
import com.oropallo.assunta.recipes.Adapter.AdapterIngredienti;
import com.oropallo.assunta.recipes.Adapter.AdapterProcedimento;

import com.oropallo.assunta.recipes.Constant.Ingredienti;
import com.oropallo.assunta.recipes.domain.DBManager;
import com.oropallo.assunta.recipes.domain.IngredienteRicetta;
import com.oropallo.assunta.recipes.domain.Ricetta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.RushBitmapFile;

/**
 * A placeholder fragment containing a simple view.
 */
public class RicettaActivityFragment extends Fragment {

    private Ricetta r;
    private Bitmap image;
    private View rootView;
    public RicettaActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_ricetta, container, false);
        String id=getArguments().getString("Ricetta");
        Log.d("Debug","Visualizzo ricetta: "+id);
        //TODO controllo su id exception
        r= DBManager.getRicetta(id);
        Log.d("DEBUG", r.toString());

        //Boookmark
        ImageView bookmark =(ImageView) rootView.findViewById(R.id.imageViewBookmarkRicetta);
        if(DBManager.isBookmarked(r.getId()))
            bookmark.setVisibility(View.VISIBLE);
        else
            bookmark.setVisibility(View.INVISIBLE);

        ArrayList<String> procedimento=new ArrayList<String>();
        TextView nome= (TextView)rootView.findViewById(R.id.name_ricetta);
        nome.requestFocus();
        TextView categoria= (TextView) rootView.findViewById(R.id.name_category);
        TextView numPersone= (TextView) rootView.findViewById(R.id.num_persone_tetx_view);
        TextView nota= (TextView) rootView.findViewById(R.id.nota_text_view);
        if(r!=null){
            nome.setText(r.getNome());
            categoria.setText("Categoria: "+r.getCategoria());
            numPersone.setText("Numero persone/porzioni: "+ r.getNum_persone());
            nota.setText("Nota: "+ r.getNota());
            String[] itemProcedimento= r.getProcedimento().split("\n");
            for(String item: itemProcedimento)
            procedimento.add(item);
           // procedimento.add(r.getProcedimento());
            //Log.d("DEBUG", itemProcedimento.length+"");
        }
        else Log.d("Debug", "Ricetta nulla");

        //recyclerView ingredienti
        RecyclerView recylerIngredienti= (RecyclerView) rootView.findViewById(R.id.recycler_ingredienti);
        recylerIngredienti.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        recylerIngredienti.setLayoutManager(llm);
        AdapterIngredienti adapter= new AdapterIngredienti(r.getIngredientiList());
        recylerIngredienti.setAdapter(adapter);

        //recyclerView procediemnto
        RecyclerView recylerProcediemnto= (RecyclerView) rootView.findViewById(R.id.recycler_procedimento);
        recylerProcediemnto.setHasFixedSize(true);
        LinearLayoutManager llm2 = new LinearLayoutManager(rootView.getContext());
        recylerProcediemnto.setLayoutManager(llm2);
        AdapterProcedimento adapter2= new AdapterProcedimento(procedimento);
        recylerProcediemnto.setAdapter(adapter2);

        //imageview ricetta
        ImageView image_view= (ImageView) rootView.findViewById(R.id.imageView_ricetta);
        if(r.isHasImage()) {
            image= DBManager.getImage(getContext().getFilesDir().getAbsolutePath().concat(r.getId()));
            image_view.setImageBitmap(image);
            }
        else {
            Log.d("Debug", "nessuna immagine trovata");
            image_view.setVisibility(View.INVISIBLE);
            image_view.getLayoutParams().height=5;
        }

        ImageButton editNome= (ImageButton) rootView.findViewById(R.id.imageButtonEditNome);
        editNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlert1();
            }
        });
        ImageButton editIngredienti= (ImageButton) rootView.findViewById(R.id.imageButton_edit_ingredienti);
        editIngredienti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlert2();
            }
        });
        ImageButton editProcedimento= (ImageButton) rootView.findViewById(R.id.imageButton3edit_procedimento);
        editProcedimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlert3();
            }
        });
        return rootView;
    }

    private void createAlert1(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        LayoutInflater inflater = this.getActivity().getLayoutInflater();
        final View v_view= inflater.inflate(R.layout.fragment_add_ricetta_1, null);
        //set all values
       final EditText nome= (EditText) v_view.findViewById(R.id.editText_addRicetta) ;
        nome.setText(r.getNome());
       final Spinner spinner= (Spinner) v_view.findViewById(R.id.spinner_categoria);
        ArrayAdapter<CharSequence> spinner_adapter= ArrayAdapter.createFromResource(getContext(),R.array.category, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        if(r.getCategoria().equalsIgnoreCase("primo"))
        spinner.setSelection(0);
        else if(r.getCategoria().equalsIgnoreCase("secondo"))
            spinner.setSelection(1);
        else if(r.getCategoria().equalsIgnoreCase("dolce"))
            spinner.setSelection(2);
        else if(r.getCategoria().equalsIgnoreCase("aperitivo"))
            spinner.setSelection(3);
        else spinner.setSelection(4);
        final EditText numPersone=(EditText) v_view.findViewById(R.id.editText_numPersone) ;
        numPersone.setText(r.getNum_persone());
        final ImageView imageV= (ImageView) v_view.findViewById(R.id.imageView_add_ricetta);
        if(r.isHasImage()) {
            imageV.setImageBitmap(image);
        }
        //non permetto di cambiare immagine, troppo lungo...........
        Button addPhoto=(Button) v_view.findViewById(R.id.button_add_immagine_ricetta);
        addPhoto.setVisibility(View.INVISIBLE);
        //create dialog
        builder.setView(v_view)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String n= nome.getText().toString();
                        String num_p= numPersone.getText().toString();
                        String cat= spinner.getSelectedItem().toString();
                        r.setNome(n);
                        r.setCategoria(cat);
                        r.setNum_persone(num_p);
                        r.save();
                        dialog.dismiss();
                        restartActivty();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert=builder.create();
        alert.show();
    }

    private void createAlert2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        LayoutInflater inflater = this.getActivity().getLayoutInflater();
        final View v_view= inflater.inflate(R.layout.fragment_add_ricetta_2, null);

        final RecyclerView recyclerIngredienti= (RecyclerView) v_view.findViewById(R.id.recycler_view_addIngredienti);
        recyclerIngredienti.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(v_view.getContext());
        recyclerIngredienti.setLayoutManager(llm);
        final List<IngredienteRicetta> ingredienti= r.getIngredientiList();
        final AdapterAddIngrediente adapterAddIngrediente= new AdapterAddIngrediente(ingredienti);
        recyclerIngredienti.setAdapter(adapterAddIngrediente);

        //addButton
        ImageButton addButton= (ImageButton) v_view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final int position=ingredienti.size();
                //create Dialog
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View v_view= inflater.inflate(R.layout.dialog_add_ingrediente, null);
                //AutoCompleteTextView e set Adapter
                final AutoCompleteTextView text= (AutoCompleteTextView) v_view.findViewById(R.id.autoCompleteTextViewAddIngrediente);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(v_view.getContext(),android.R.layout.simple_dropdown_item_1line, new Ingredienti().getArrayIngredienti());
                text.setAdapter(adapter);
                //TextView quantità
                final TextView quantita= (TextView)v_view.findViewById(R.id.editText_quantita_dialog);
                quantita.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quantita.setText("");
                    }
                });
                //spinner unità di misura
                final Spinner unit= (Spinner) v_view.findViewById(R.id.spinner_dialog_addElemento);
                //adapter spinner
                ArrayAdapter<CharSequence> spinner_adapter_unit= ArrayAdapter.createFromResource(getContext(),R.array.units, android.R.layout.simple_spinner_item);
                spinner_adapter_unit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                unit.setAdapter(spinner_adapter_unit);

                //create dialog
                builder2.setView(v_view)
                        // Add action buttons
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                IngredienteRicetta ingr= new IngredienteRicetta( );
                                ingr.setNome(text.getText()+"");
                                int q=0;
                                try {
                                    q=Integer.parseInt("" + quantita.getText());
                                }catch(Exception e){

                                }
                                ingr.setQuantita(q);
                                ingr.setUnita(unit.getSelectedItem().toString());
                                ingredienti.add(ingr);
                                adapterAddIngrediente.notifyItemInserted(position);
                                recyclerIngredienti.scrollToPosition(position);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert=builder2.create();
                alert.show();
            }
        });

        builder.setView(v_view)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        r.setIngredientiList(ingredienti);
                        r.save();
                        dialog.dismiss();
                        restartActivty();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert=builder.create();
        alert.show();

    }

    private void createAlert3(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        LayoutInflater inflater = this.getActivity().getLayoutInflater();
        final View v_view= inflater.inflate(R.layout.fragment_add_ricetta_3, null);
        final EditText procedimento= (EditText) v_view.findViewById(R.id.editTextProcediemento);
        final EditText nota= (EditText)v_view.findViewById(R.id.editTextAddNota);
        procedimento.setText(r.getProcedimento());
        nota.setText(r.getNota());
        Button button= (Button) v_view.findViewById(R.id.buttonSaveRicetta);
        button.setVisibility(View.INVISIBLE);
        builder.setView(v_view)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        r.setProcedimento(procedimento.getText().toString());
                        r.setNota(nota.getText().toString());
                        r.save();
                        dialog.dismiss();
                        restartActivty();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert=builder.create();
        alert.show();

    }

    //TODO gestire Refresh MainActivity.....
    public void restartActivty(){
        Intent intent= new Intent(this.getContext(), RicettaActivity.class);
        intent.putExtra("Ricetta", r.getId());
        startActivity(intent);
        getActivity().finish();
    }

    public Ricetta getRicetta(){
        return r;
    }
}
