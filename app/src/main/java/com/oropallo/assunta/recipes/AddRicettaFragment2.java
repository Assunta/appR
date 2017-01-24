package com.oropallo.assunta.recipes;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.oropallo.assunta.recipes.Adapter.AdapterAddIngrediente;
import com.oropallo.assunta.recipes.Constant.Ingredienti;
import com.oropallo.assunta.recipes.domain.IngredienteRicetta;
import com.oropallo.assunta.recipes.domain.Ricetta;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddRicettaFragment2 extends Fragment {


    public AddRicettaFragment2() {
        // Required empty public constructor
    }
        public static final String ARG_PAGE = "ARG_PAGE";
         final List<IngredienteRicetta> ingredienti= new ArrayList<IngredienteRicetta>();

        private int mPage;

        public static AddRicettaFragment2 newInstance(int page) {
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, page);
            AddRicettaFragment2 fragment = new AddRicettaFragment2();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPage = getArguments().getInt(ARG_PAGE);
        }

        // Inflate the fragment layout we defined above for this fragment
        // Set the associated text for the title
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_add_ricetta_2, container, false);
            //recyclerView

            final RecyclerView recyclerIngredienti= (RecyclerView) view.findViewById(R.id.recycler_view_addIngredienti);
            recyclerIngredienti.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
            recyclerIngredienti.setLayoutManager(llm);
            final AdapterAddIngrediente adapterAddIngrediente= new AdapterAddIngrediente(ingredienti);
            recyclerIngredienti.setAdapter(adapterAddIngrediente);
            final Context context= this.getContext();
            //addButton
            ImageButton addButton= (ImageButton) view.findViewById(R.id.addButton);
            addButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final int position=ingredienti.size();
                    //create Dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final View v_view= inflater.inflate(R.layout.dialog_add_ingrediente, null);
                    //AutoCompleteTextView e set Adapter
                    final AutoCompleteTextView text= (AutoCompleteTextView) v_view.findViewById(R.id.autoCompleteTextViewAddIngrediente);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(v_view.getContext(),android.R.layout.simple_dropdown_item_1line, new Ingredienti(context).getArrayIngredienti());
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
                    builder.setView(v_view)
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
                    AlertDialog alert=builder.create();
                    alert.show();
                }
            });
            return view;
        }

    public void getInfo(){
        ActivityPageSlidingAddRicetta activity=(ActivityPageSlidingAddRicetta) getActivity();
        Ricetta r=activity.getRicetta();
        r.setIngredientiList(ingredienti);
        activity.setRicetta(r);
    }
    }


