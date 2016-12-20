package com.oropallo.assunta.recipes;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oropallo.assunta.recipes.domain.DBManager;
import com.oropallo.assunta.recipes.domain.IngredienteRicetta;
import com.oropallo.assunta.recipes.domain.Ricetta;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.uk.rushorm.android.RushBitmapFile;
import co.uk.rushorm.core.RushCallback;
import dmax.dialog.SpotsDialog;

import static android.R.attr.data;

public class ParserRicetta extends AppCompatActivity {
    private Context context=this;
    private SpotsDialog dialog;
    private final int MY_PERMISSIONS_REQUEST_INTERNET = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser_ricetta);
        //controllo se ho il permesso di internet
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
        }
        final EditText editUrl = (EditText) findViewById(R.id.editTextURL);
        final Button button = (Button) findViewById(R.id.buttonaURL);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String siteUrl = editUrl.getText().toString();
                dialog = new SpotsDialog(v.getContext());
                dialog.show();
                new ParseURL().execute(new String[]{siteUrl});
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(ParserRicetta.this,
                            "Impossibile accedere a internet", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private class ParseURL extends AsyncTask<String, Void, Integer> {
        private Ricetta ricetta;

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                ricetta = new Ricetta();
                Log.d("JSwa", "Connecting to [" + strings[0] + "]");
                Document doc = Jsoup.connect(strings[0]).get();
                Log.d("JSwa", "Connected to [" + strings[0] + "]");
                // Get document (HTML page) title
                String title = doc.title();
                Log.d("JSwA", "Title [" + title + "]");


                Elements topicList = doc.getElementsByTag("script");
                int i = 0;
                String categoria = "";
                for (Element topic : topicList) {
                    for (DataNode node : topic.dataNodes()) {
                        //TODO gestire diversamente il get specifico nodo
                        if (i == 3) {
                            //get categira
                            String cat = node.getWholeData();
                            int index = cat.indexOf("'cat':");
                            int indexF = cat.indexOf("'caturl':");
                            cat = cat.substring(index, indexF);
                            Log.d("DEBUG", cat);
                            categoria = getCategoria(cat.trim());
                            Log.d("DEBUG", categoria);
                        }
                        if (i == 4) {
                            //buffer.append( "[" + node.getWholeData() + "]\n");
                            String result = node.getWholeData();
                            Log.d("DEBUG", result);
                            JSONObject jsonObject = new JSONObject(result);
                            String nome = jsonObject.getString("name");
                            String porzioni = jsonObject.getString("recipeYield");
                            String ingr = jsonObject.getString("recipeIngredient");
                            List<IngredienteRicetta> ingredienti = splitIngredienti(ingr);
                            String procedimento = jsonObject.getString("recipeInstructions");
                            procedimento = procedimento.replace("[\"", "");
                            procedimento = procedimento.replace("\"]", "");
                            procedimento=procedimento.replace("\",\"", "\n");
                            String url = jsonObject.getString("image");
                            final Bitmap image=loadImage(url);
                            String nota = jsonObject.getString("author");
                            ricetta.setNome(nome);
                            ricetta.setIngredientiList(ingredienti);
                            ricetta.setProcedimento(procedimento);
                            ricetta.setNum_persone(porzioni);
                            ricetta.setNota(nota);
                            ricetta.setCategoria(categoria);
                            if(image==null) ricetta.setHasImage(false);
                            else ricetta.setHasImage(true);

                            ricetta.save(new RushCallback() {
                                @Override
                                public void complete() {
                                    //controllare se c'è un'immagine e salvarla
                                    if(image!=null) {

                                        RushBitmapFile file = new RushBitmapFile(context.getFilesDir().getAbsolutePath().concat(ricetta.getId()));
                                        try {
                                            file.setImage(image);
                                            file.save(new RushCallback() {
                                                @Override
                                                public void complete() {
                                                    dialog.dismiss();
                                                    startActivtyRicetta(ricetta);
                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        dialog.dismiss();
                                        startActivtyRicetta(ricetta);
                                    }

                                }});
                        }
                        i++;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
                return 404;
            }
            return 200;
        }

        private void startActivtyRicetta(Ricetta r){
            Log.d("DEBUG", "ricetta inserita");
            Intent intent= new Intent(context, RicettaActivity.class);
            intent.putExtra("Ricetta", ricetta.getId());
            startActivity(intent);
            Log.d("DEBUG", ricetta.getId());
            ((ParserRicetta)context).finish();

        }
        @Override
        protected void onPostExecute(Integer r) {
            dialog.dismiss();
            if(r==404){
                AlertDialog dialog = new AlertDialog.Builder((ParserRicetta)context).create();
                dialog.setTitle("Ricetta non trovata");
                dialog.setMessage("Indirizzo non valido, impossibile inserire la ricetta");
                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                //Snackbar.make(((ParserRicetta)context).getCurrentFocus(), "Impossibile inserire la ricetta, indirizzo non trovato", Snackbar.LENGTH_SHORT).show();
            }
        }

        private List<IngredienteRicetta> splitIngredienti(String r) {
            List<IngredienteRicetta> ingredienteRicettaList = new ArrayList<IngredienteRicetta>();
            String[] res = r.split(",");
            for (int i = 0; i < res.length; i++) {
                String ingr = res[i];
                String nome = "";
                String quantita = "0";
                ingr = ingr.substring(ingr.indexOf("\"") + 1, ingr.lastIndexOf("\""));
                Log.d("INGREDIENTE", ingr);
                Pattern patternNome = Pattern.compile("(\\D|0)*");
                Matcher matcherNome = patternNome.matcher(ingr);
                if (matcherNome.find()) {
                    nome = matcherNome.group();
                    Log.d("Nome=", nome);
                }
                //gestisco il caso q.b.
                if (nome.contains("q.b.")) {
                    nome = nome.replace("q.b.", "");
                    ingr = "q.b.";
                } else {
                    ingr = ingr.replace(nome, "");
                    //gestisco il caso in cui compaiono ( X xxx )
                    if (nome.contains("(") && !nome.contains(")")) {
                        nome = nome.replace("(", "");
                        ingr = ingr.substring(ingr.indexOf(")"));
                        ingr = (ingr.replace(")", "")).trim();
                    }
                    Pattern patternQ = Pattern.compile("(\\d?)*");
                    Matcher matcherQ = patternQ.matcher(ingr);
                    if (matcherQ.find()) {
                        quantita = matcherQ.group();
                        Log.d("Quantità=", quantita);
                    }
                    ingr = ingr.replace(quantita, "").trim();
                }
                IngredienteRicetta ingredienteRicetta = new IngredienteRicetta(nome, Integer.parseInt(quantita.trim()), ingr);
                ingredienteRicettaList.add(ingredienteRicetta);
            }
            return ingredienteRicettaList;
        }
        //TODO gestire meglio le categorie
        private String getCategoria(String cat) {
            String result = "";
            switch (cat) {
                case "'cat':'Dolci e Desserts',":
                    result = "Dolce";
                    break;
                case "'cat':'Primi piatti',":
                    result = "Primo";
                    break;
                case "'cat':'Contorni',":
                    result = "Secondo";
                    break;
                case "'cat':'Secondi piatti',":
                    result = "Secondo";
                    break;
                case "'cat':'Insalate',":
                    result = "Secondo";
                    break;
                case "'cat':'Antipasti',":
                    result = "Aperitivo";
                    break;
                default: result = "Altro";

            }
            return result;
        }

        private Bitmap loadImage(String urlString){
            try {
                URL url = new URL(urlString);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return image;
            }
            catch (Exception e) {
                Log.e("DEBUG", e.getMessage());
                return null;
            }
        }
    }
}




