package com.oropallo.assunta.recipes;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.oropallo.assunta.recipes.domain.IngredienteRicetta;
import com.oropallo.assunta.recipes.domain.Ricetta;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.uk.rushorm.android.RushBitmapFile;
import co.uk.rushorm.core.RushCallback;
import dmax.dialog.SpotsDialog;

public class ParserRicetta extends AppCompatActivity {
    private Context context = this;
    private SpotsDialog dialog;
    private final int MY_PERMISSIONS_REQUEST_INTERNET = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser_ricetta);
        final EditText editUrl = (EditText) findViewById(R.id.editTextURL);
        final RadioGroup gruop=(RadioGroup) findViewById(R.id.radioGroup);
        RadioButton radioButtonGialloZafferano= (RadioButton) findViewById(R.id.radioButtonGialloZafferano);
        radioButtonGialloZafferano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUrl.setHint("http://ricette.giallozafferano.it/Acai-bowl.html");
            }
        });
        RadioButton radioButtonRicette= (RadioButton) findViewById(R.id.radioButtonRicetteCom);
        radioButtonRicette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUrl.setHint("http://www.ricette.com/paccheri-fritti");
            }
        });
        //controllo se ho il permesso di internet
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
        }

        final Button button = (Button) findViewById(R.id.buttonaURL);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id=gruop.getCheckedRadioButtonId();
                String siteUrl = editUrl.getText().toString();
                dialog = new SpotsDialog(v.getContext());
                dialog.show();
                if(id==R.id.radioButtonGialloZafferano){
                    new ParseURLGialloZafferano().execute(new String[]{siteUrl});
                }else if(id==R.id.radioButtonRicetteCom){
                    new ParseURLRicettePuntoCom().execute(new String[]{siteUrl});
                }
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
    private void startActivtyRicetta(Ricetta r) {
        Log.d("DEBUG", "ricetta inserita");
        Intent intent = new Intent(context, RicettaActivity.class);
        intent.putExtra("Ricetta", r.getId());
        startActivity(intent);
        Log.d("DEBUG", r.getId());
        ((ParserRicetta) context).finish();
    }
    private Bitmap loadImage(String urlString) {
        try {
            URL url = new URL(urlString);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return image;
        } catch (Exception e) {
            Log.e("DEBUG", e.getMessage());
            return null;
        }
    }

    private class ParseURLRicettePuntoCom extends AsyncTask<String, Void, Integer> {
        private Ricetta ricetta;

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                ricetta = new Ricetta();
                String imageURL = "";
                String nome = "";
                String porzioni = "";
                String procedimento="";
                Log.d("JSwa", "Connecting to [" + strings[0] + "]");
                Document doc = Jsoup.connect(strings[0]).get();
                Log.d("JSwa", "Connected to [" + strings[0] + "]");
                /*File input = new File("/storage/emulated/0/Paccheri Fritti.html");
                Document doc = Jsoup.parse(input, "UTF-8", "http://www.ricette.com/");*/
                // Get document (HTML page) title
                Elements metaInfoTitle = doc.select("meta[property=og:title]");
                if (metaInfoTitle != null) {
                    nome = metaInfoTitle.attr("content");
                    Log.d("DEBUG", nome);
                }
                //prendo l'indirizzo dell'immagine
                Elements metaInfoImage = doc.select("meta[property=og:image]");
                if (metaInfoTitle != null) {
                    imageURL = metaInfoImage.attr("content");
                    Log.d("DEBUG", imageURL);
                }
                //prendo tutti gli item span per cercare il numero di porzioni e gli ingredienti:
                Elements spanElements = doc.getElementsByTag("span");
                List<String> ingredientiStrings = new ArrayList<String>();
                for (Element e : spanElements) {
                    String ingredienteString;
                    //numero porzioni
                    if (e.attr("itemprop").equals("recipeYield")) {
                        porzioni = e.toString().replace("<span itemprop=\"recipeYield\">", "");
                        porzioni = porzioni.replace("</span>", "").trim();
                    } else if (e.attr("itemprop").equals("recipeIngredient")) {
                        ingredienteString = e.toString().replace("<span itemprop=\"recipeIngredient\">", "");
                        if (e.toString().contains("<a style=")) {
                            //c'è anche un link nel nome dell'ingrediente, bisogna rimuoverlo
                            String quantitaString = ingredienteString.substring(0, ingredienteString.indexOf("<a")).trim();
                            ingredienteString = ingredienteString.replace(quantitaString, "");
                            ingredienteString = ingredienteString.substring(ingredienteString.indexOf("\">"), ingredienteString.indexOf("</a>"));
                            ingredienteString = ingredienteString.replace("\">", "").trim();
                            ingredienteString = quantitaString + " " + ingredienteString;
                            //Log.d("***", quantitaString+ " "+ ingredienteString);
                        } else {
                            ingredienteString = ingredienteString.replace("</span>", "");
                            //Log.d("/////", ingredienteString);
                        }
                        ingredientiStrings.add(ingredienteString);
                    }else if(e.toString().contains("class=\"prep\"")){
                       //cerco il procedimento
                            Log.d("PREPARAZIONE", e.toString());
                            procedimento= parseProcedimento(e.toString());
                        }

                }
                List<IngredienteRicetta> ingredienti = parseListaIngredientiTrovati(ingredientiStrings);
                for(IngredienteRicetta i: ingredienti)
                    Log.d("Ingredienti", i.toString());
                //TODO non c'è modo di trovare la categoria?
                //creo la nuova ricetta da inserire
                final Ricetta r= new Ricetta();
                r.setNum_persone(porzioni);
                r.setNome(nome);
                final Bitmap image = loadImage(imageURL);
                if(imageURL.equals(""))
                    r.setHasImage(false);
                else
                    r.setHasImage(true);
                r.setProcedimento(procedimento);
                r.setIngredientiList(ingredienti);
                r.setCategoria("");
                r.setNota("Ricette.com");

                //salvo la ricetta
                r.save(new RushCallback() {
                    @Override
                    public void complete() {
                        //controllare se c'è un'immagine e salvarla
                        if (image != null) {

                            RushBitmapFile file = new RushBitmapFile(context.getFilesDir().getAbsolutePath().concat(r.getId()));
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
                        } else {
                            dialog.dismiss();
                            startActivtyRicetta(r);
                        }

                    }
                });
            } catch (Throwable t) {
                t.printStackTrace();
                return 404;
            }
            return 200;
        }


        @Override
        protected void onPostExecute(Integer r) {

        }

        private String parseProcedimento(String s){
            String procedimento= s.replaceAll("<p>|<strong>|</strong>|<em>|</em>|</p>|<span class=\"prep\">|</span>|&nbsp;", "");
            String[] items= procedimento.split("img");
            String returnString="";
            for(String item:items) {
                item=item.replaceAll("src=(.)*\">","");
                item=item.replace("<","").trim();
                returnString=returnString+item+"\n";
                //Log.d("PROCEDIMENTO", item);
            }
            Log.d("Procedimento", returnString);
            return returnString;
        }
        private List<IngredienteRicetta> parseListaIngredientiTrovati(List<String> strings) {
            List<IngredienteRicetta> ingredienti = new ArrayList<IngredienteRicetta>();
            for (String s : strings) {
                s=s.trim();
                if (s.contains("quanto basta di ")) {
                    IngredienteRicetta i = new IngredienteRicetta();
                    i.setQuantita(0);
                    i.setUnita("q.b.");
                    i.setNome(s.replace("quanto basta di ", "").trim());
                    //Log.d("q.b", i.toString());
                    ingredienti.add(i);
                } else {
                    Pattern patternNome = Pattern.compile("(\\d?)* ");
                    Matcher matcherNome = patternNome.matcher(s);
                    String quantita="0";
                    String unita="";
                    String nome="";
                    if (matcherNome.find()) {
                        IngredienteRicetta i = new IngredienteRicetta();
                        quantita = matcherNome.group().trim();
                        //Log.d("Quantita", quantita);
                        s = s.replace(quantita, "");
                        //cerco l'unità di misura
                        Pattern patternUnita = Pattern.compile("gramm?");
                        Matcher matcherUnita = patternUnita.matcher(s);
                        if (matcherUnita.find()) {
                            unita = "g";
                            nome= getNome(s);
                            //Log.d("Unita", "g");
                        } else {
                            patternUnita = Pattern.compile("kilogramm?");
                            matcherUnita = patternUnita.matcher(s);
                            if (matcherUnita.find()) {
                                unita = "kg";
                                nome= getNome(s);
                                //Log.d("Unita", "kg");
                            } else {
                                patternUnita = Pattern.compile("millilitr?");
                                matcherUnita = patternUnita.matcher(s);
                                if (matcherUnita.find()) {
                                    unita = "ml";
                                    nome= getNome(s);
                                   // Log.d("Unita", "ml");
                                } else {
                                    patternUnita = Pattern.compile("litr?");
                                    matcherUnita = patternUnita.matcher(s);
                                    if (matcherUnita.find()) {
                                        unita = "l";
                                        nome= getNome(s);
                                       //Log.d("Unita", "l");
                                    } else {
                                        //TODO eventualmente gestire unita di misura come manicata, pizzico, cucchiaio,...
                                        nome= s.trim();
                                    }
                                }
                            }
                        }
                    }
                    IngredienteRicetta i= new IngredienteRicetta();
                    i.setNome(nome);
                    i.setUnita(unita);
                    try {
                        i.setQuantita(Integer.parseInt(quantita));
                    }catch(NumberFormatException e){
                        i.setQuantita(0);
                    }
                    ingredienti.add(i);
                    //Log.d("DEBUG", i.toString());
                }
            }
            return ingredienti;
        }
    }

    private String getNome(String nomeDi){
        nomeDi=nomeDi.substring(nomeDi.indexOf("di "));
        return nomeDi.replace("di ","").trim();
    }
        //TODO da completare
        private class ParseURLCucchiaioDArgento extends AsyncTask<String, Void, Integer> {
            private Ricetta ricetta;

            @Override
            protected Integer doInBackground(String... strings) {
                try {
                    ricetta = new Ricetta();
                    String imageURL = "";
                    String nome = "";
                    String porzioni = "";
                /*Log.d("JSwa", "Connecting to [" + strings[0] + "]");
                Document doc = Jsoup.connect(strings[0]).get();
                Log.d("JSwa", "Connected to [" + strings[0] + "]");*/
                    File input = new File("/storage/emulated/0/RicettaMaritozzi.html");
                    Document doc = Jsoup.parse(input, "UTF-8", "http://www.cucchiaio.it/ricetta/");
                    // Get document (HTML page) title
                    String title = doc.title();
                    Log.d("JSwA", "Title [" + title + "]");
                    Elements metaInfoTitle = doc.select("meta[property=og:title]");
                    if (metaInfoTitle != null) {
                        nome = metaInfoTitle.attr("content");
                        Log.d("DEBUG", nome);
                    }
                    //prendo l'indirizzo dell'immagine
                    Elements elements = doc.getElementsByClass("auto");
                    Element element = elements.get(0);
                    imageURL = element.getElementsByTag("img").attr("src").toString();
                    Log.d("DEBUG", "Image " + imageURL);

                    elements = doc.getElementsByTag("div");
                    for (Element e : elements) {
                        //per cercare il numero di porzioni
                        if (e.attr("class").equalsIgnoreCase("parbase recipe_info info")) {
                            Elements metaPorzioni = e.getElementsByTag("meta");
                            for (Element element1 : metaPorzioni) {
                                String s = element1.toString();
                                if (s.contains("itemprop=\"recipeYield\""))
                                    porzioni = s.substring(s.indexOf("content=\"")).replace("content=\"", "").replace("porzioni\">", "");
                            }
                        }
                        //TODO aggiungere ricerca ingrdienti, vedere che si può fare con il link della pagina degli ingredienti
                        elements = doc.getElementsByTag("span");
                        Log.d("DEBUG", elements.size() + "");
                        for (Element e2 : elements) {
                            if (e2.className().equalsIgnoreCase("f_t3_14_lh_20"))
                                Log.d("DEBUG", e2.toString());
                        }
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                    return 404;
                }
                return 200;
            }

            @Override
            protected void onPostExecute(Integer r) {

            }
        }

        private class ParseURLGialloZafferano extends AsyncTask<String, Void, Integer> {
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
                                procedimento = procedimento.replace("\",\"", "\n");
                                String url = jsonObject.getString("image");
                                final Bitmap image = loadImage(url);
                                String nota = jsonObject.getString("author");
                                ricetta.setNome(nome);
                                ricetta.setIngredientiList(ingredienti);
                                ricetta.setProcedimento(procedimento);
                                ricetta.setNum_persone(porzioni);
                                ricetta.setNota(nota);
                                ricetta.setCategoria(categoria);
                                if (image == null) ricetta.setHasImage(false);
                                else ricetta.setHasImage(true);

                                ricetta.save(new RushCallback() {
                                    @Override
                                    public void complete() {
                                        //controllare se c'è un'immagine e salvarla
                                        if (image != null) {

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
                                        } else {
                                            dialog.dismiss();
                                            startActivtyRicetta(ricetta);
                                        }

                                    }
                                });
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


            @Override
            protected void onPostExecute(Integer r) {
                dialog.dismiss();
                if (r == 404) {
                    AlertDialog dialog = new AlertDialog.Builder((ParserRicetta) context).create();
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
                    IngredienteRicetta ingredienteRicetta = new IngredienteRicetta(nome.trim(), Integer.parseInt(quantita.trim()), ingr);
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
                    default:
                        result = "Altro";

                }
                return result;
            }

        }
    }





