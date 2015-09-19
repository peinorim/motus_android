package com.paocorp.momomotus;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.paocorp.models.Mot;
import com.paocorp.models.Motus;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class MotusActivity extends AppCompatActivity {

    EditText inputMot;
    Button sendMot;
    TextView l1c1, counter;
    Motus partie;
    Map<Integer, String> map = new HashMap<>();
    AdView adView;
    String loc;
    AlertDialog dialog;

    public void checkMot(View v) {
        sendMot.setEnabled(false);
        inputMot = (EditText) findViewById(R.id.edit_message);
        Integer rang = partie.getCurrent();

        try {
            String sMot = stripAccents(inputMot.getText().toString().toLowerCase());
            if (sMot.length() == partie.getNb()) {
                boolean existe = existe(sMot);
                Mot mot = partie.verifMot(rang, sMot, existe);

                if (!partie.isComplete()) {

                    if (mot.isTrouve() && mot.isFini()) {

                        String game_layout = "game_" + partie.getNb();

                        Resources res = getResources();
                        int layId = res.getIdentifier(game_layout, "layout", getPackageName());

                        setContentView(layId);
                        l1c1 = (TextView) findViewById(R.id.l1c1);

                        l1c1.setText(String.valueOf(partie.getMot(partie.getCurrent()).getMot().toUpperCase().charAt(0)));
                        l1c1.setBackgroundResource(R.drawable.square_red);
                        map.clear();
                        loadToast(this.getResources().getString(R.string.the_word) + partie.getMot(partie.getCurrent() - 1).getMot() + this.getResources().getString(R.string.trouve), true);

                    } else if (!mot.isTrouve() && mot.isFini()) {

                        String game_layout = "game_" + partie.getNb();

                        Resources res = getResources();
                        int layId = res.getIdentifier(game_layout, "layout", getPackageName());

                        setContentView(layId);
                        l1c1 = (TextView) findViewById(R.id.l1c1);

                        l1c1.setText(String.valueOf(partie.getMot(partie.getCurrent()).getMot().toUpperCase().charAt(0)));
                        l1c1.setBackgroundResource(R.drawable.square_red);
                        map.clear();

                        if (existe) {
                            loadToast(this.getResources().getString(R.string.fallait_trouver) + partie.getMot(partie.getCurrent() - 1).getMot(), false);
                        } else {
                            loadToast(this.getResources().getString(R.string.the_word) + sMot + this.getResources().getString(R.string.existe_pas) + partie.getMot(partie.getCurrent() - 1).getMot(), false);
                        }
                    } else {
                        parseRes(partie.getMot(partie.getCurrent()).getVerif());
                    }
                    inputMot = (EditText) findViewById(R.id.edit_message);
                    inputMot.getText().clear();
                    sendMot.setEnabled(true);
                    counter = (TextView) findViewById(R.id.counter);
                    counter.setText("0/" + String.valueOf(partie.getNb()));
                    inputMot.addTextChangedListener(mTextEditorWatcher);
                } else {
                    setContentView(R.layout.ending);
                    loadEnding(partie);
                }
            }
            loadBanner();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backHome(View v) {
        finish();
        System.exit(0);
        this.startActivity(new Intent(this, MainActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        int nb = b.getInt("nb");
        partie = new Motus(nb);

        String game_layout = "game_" + partie.getNb();

        Resources res = this.getResources();
        int layId = res.getIdentifier(game_layout, "layout", this.getPackageName());

        setContentView(layId);

        l1c1 = (TextView) findViewById(R.id.l1c1);
        counter = (TextView) findViewById(R.id.counter);
        inputMot = (EditText) findViewById(R.id.edit_message);

        try {
            fillPartieMots();
        } catch (Exception ex) {
            Toast.makeText(this, "Exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        sendMot = (Button) findViewById(R.id.motsend);

        l1c1.setText(String.valueOf(partie.getMots().get(0).getMot().toUpperCase().charAt(0)));
        l1c1.setBackgroundResource(R.drawable.square_red);
        counter.setText("0/" + String.valueOf(partie.getNb()));
        inputMot.addTextChangedListener(mTextEditorWatcher);
        loadBanner();

        inputMot.setInputType(InputType.TYPE_CLASS_TEXT);
        inputMot.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    checkMot(v);
                    return false;
                }

                return false;
            }
        });
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            counter.setText(String.valueOf(s.length()) + "/" + String.valueOf(partie.getNb()));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private void loadToast(String mess, boolean trouve) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        if (trouve) {
            layout.setBackground(ContextCompat.getDrawable(this, R.drawable.toast_done));
        } else {
            layout.setBackground(ContextCompat.getDrawable(this, R.drawable.toast_fail));
        }

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(mess);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void loadEnding(Motus partie) {
        for (int i = 1; i <= partie.getMots().size(); i++) {
            String idmot = "mot" + i;
            int resID = getResources().getIdentifier(idmot, "id", getPackageName());
            TextView tview = ((TextView) findViewById(resID));

            if (partie.getMot(i).isTrouve()) {
                if (partie.getMot(i).getLigne() == 1) {
                    tview.setText(partie.getMot(i).getMot() + this.getResources().getString(R.string.premier_coup));
                } else {
                    tview.setText(partie.getMot(i).getMot() + this.getResources().getString(R.string.x_coup) + partie.getMot(i).getLigne() + this.getResources().getString(R.string.coups));
                }
                tview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_action_done, 0, 0, 0);
                tview.setTextColor(this.getResources().getColor(R.color.green));
            } else {
                tview.setText(partie.getMot(i).getMot() + this.getResources().getString(R.string.nontrouve));
                tview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_content_clear, 0, 0, 0);
                tview.setTextColor(this.getResources().getColor(R.color.red_darken1));
            }
        }
    }

    private void parseRes(ArrayList verif) {

        int prev = partie.getLigne() - 1;
        int next = partie.getLigne();

        for (int i = 1; i < verif.size(); i++) {
            String[] arr = (String[]) verif.get(i);
            for (int j = 0; j < arr.length; j++) {

                String lprev = "l" + prev + "c" + i;
                int resID = getResources().getIdentifier(lprev, "id", getPackageName());

                String colorstr = "square_" + arr[1];
                int colorID = getResources().getIdentifier(colorstr, "drawable", getPackageName());

                TextView tview = ((TextView) findViewById(resID));
                tview.setText(arr[0].toUpperCase());
                tview.setBackgroundResource(colorID);
                if (arr[1] == "red") {
                    if (map.size() < 1) {
                        map.put(0, "");
                    }
                    map.put(i, arr[0].toUpperCase());
                }
            }
        }
        String[] arr = (String[]) verif.get(1);
        if (arr[1] != "red") {
            String lnext = "l" + next + "c" + 1;
            int resID = getResources().getIdentifier(lnext, "id", getPackageName());
            TextView tview = ((TextView) findViewById(resID));

            tview.setText(String.valueOf(partie.getMot(partie.getCurrent()).getMot().toUpperCase().charAt(0)));
            tview.setBackgroundResource(R.drawable.square_red);
        }
        if (map.size() > 1) {
            for (Map.Entry<Integer, String> entry : map.entrySet()) {
                Integer key = entry.getKey();
                if (key != 0) {
                    String value = entry.getValue();
                    String lnext = "l" + next + "c" + key;
                    int resID = getResources().getIdentifier(lnext, "id", getPackageName());
                    TextView tview = ((TextView) findViewById(resID));
                    tview.setText(value);
                    tview.setBackgroundResource(R.drawable.square_red);
                }
            }
        }

    }

    private void fillPartieMots() throws Exception {

        InputSource inputSrc;
        loc = Locale.getDefault().getLanguage();
        String en = "";
        if (loc.equals("en")) {
            en = "_en";
        }

        String xmlraw = "xml_" + partie.getNb() + en;

        Resources res = this.getResources();
        int rawId = res.getIdentifier(xmlraw, "raw", this.getPackageName());

        inputSrc = new InputSource(getResources().openRawResource(rawId));

        // query XPath instance, this is the parser
        XPath xpath = XPathFactory.newInstance().newXPath();
        // specify the xpath expression
        String expression = "//d/m[@f>=5]";
        // list of nodes queried
        NodeList nodes = (NodeList) xpath.evaluate(expression, inputSrc, XPathConstants.NODESET);

        // if node found
        if (nodes != null && nodes.getLength() > 0) {
            while (partie.getNbmots() > partie.getMots().size()) {
                // query value
                int rnd = new Random().nextInt(nodes.getLength());
                Node node = nodes.item(rnd);
                if (node.getTextContent().indexOf("-") == -1) {
                    partie.getMots().add(new Mot(node.getTextContent()));
                }
            }
        }
    }

    private boolean existe(String mot) throws Exception {
        InputSource inputSrc;
        loc = Locale.getDefault().getLanguage();
        String en = "";
        if (loc.equals("en")) {
            en = "_en";
        }

        String xmlraw = "xml_" + partie.getNb() + en;

        Resources res = this.getResources();
        int rawId = res.getIdentifier(xmlraw, "raw", this.getPackageName());

        inputSrc = new InputSource(getResources().openRawResource(rawId));

        // query XPath instance, this is the parser
        XPath xpath = XPathFactory.newInstance().newXPath();
        // specify the xpath expression
        String expression = "/d/m[text()='" + mot + "']";
        // list of nodes queried
        NodeList nodes = (NodeList) xpath.evaluate(expression, inputSrc, XPathConstants.NODESET);

        // if node found
        if (nodes != null && nodes.getLength() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    private void loadBanner() {
        adView = (AdView) this.findViewById(R.id.banner_bottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_motus, menu);
        menu.getItem(0).setIcon(R.drawable.ic_more_vert_white_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            finish();
            System.exit(0);
            this.startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.action_help) {
            createHelpDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        finish();
        System.exit(0);
        this.startActivity(new Intent(this, MainActivity.class));
        return;
    }

    public void createHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.help_dialog, null))
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
