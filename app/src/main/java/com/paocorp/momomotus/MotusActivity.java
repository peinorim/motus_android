package com.paocorp.momomotus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.paocorp.models.Mot;
import com.paocorp.models.Motus;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class MotusActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText inputMot;
    Button sendMot;
    TextView l1c1, counter;
    Motus partie;
    Map<Integer, String> map = new HashMap<>();
    AdView adView;
    String loc;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    String fbMots;
    RadioGroup optGame;
    Button startGame;
    ProgressDialog dialog;
    PackageInfo pInfo;

    String TITLES[] = {"Home"};
    int ICONS[] = {R.drawable.ic_action_content_clear};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    int PROFILE = R.drawable.com_facebook_profile_picture_blank_portrait;

    private Toolbar toolbar;                              // Declaring the Toolbar Object
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;

    public void startGame(View v) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(this.getResources().getString(R.string.loading));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        startGame.setEnabled(false);
        startGame.setTextColor(this.getResources().getColor(R.color.white));
        startGame.setBackgroundResource(R.drawable.button_disabled);
        Intent intent = new Intent(MotusActivity.this, MotusActivity.class);
        Bundle b = new Bundle();
        if (optGame.getCheckedRadioButtonId() == R.id.radio_six) {
            b.putInt("nb", 6);
            intent.putExtras(b);
            startActivity(intent);
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_seven) {
            b.putInt("nb", 7);
            intent.putExtras(b);
            startActivity(intent);
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_eight) {
            b.putInt("nb", 8);
            intent.putExtras(b);
            startActivity(intent);
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_nine) {
            b.putInt("nb", 9);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_ten) {
            b.putInt("nb", 10);
            intent.putExtras(b);
            startActivity(intent);
        } else {
            b.putInt("nb", 6);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

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
                    //loadToast(partie.getMot(partie.getCurrent()).getMot(),false);
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

    public void shareFB(View v) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            String fbText = getResources().getString(R.string.fb_ContentDesc);
            if (fbMots != null) {
                fbText += fbMots;
            }
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(getResources().getString(R.string.store_url)))
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentDescription(fbText)
                    .setImageUrl(Uri.parse(getResources().getString(R.string.app_icon_url)))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle b = getIntent().getExtras();
        int nb = b.getInt("nb");
        partie = new Motus(nb);

        String game_layout = "game_" + partie.getNb();

        Resources res = getResources();
        int layId = res.getIdentifier(game_layout, "layout", getPackageName());

        RelativeLayout main_content = (RelativeLayout) findViewById(R.id.content_main_include);
        View child = getLayoutInflater().inflate(layId, null);
        main_content.removeAllViews();
        main_content.addView(child);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView);

        TextView txt_version = (TextView) findViewById(R.id.app_version);
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txt_version.setText(String.format("%s v%s", this.getResources().getString(R.string.app_name), pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

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
        counter.setText("0/" + partie.getNb());
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

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

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
                if (fbMots != null) {
                    fbMots += ",&#160;";
                }
                fbMots += partie.getMot(i).getMot();
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

    private void parseRes(HashMap verif) {

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
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        finish();
    }

    public boolean onRadioButtonClicked(View v) {
        return true;
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent = new Intent(this, MotusActivity.class);
        Bundle b = new Bundle();

        int id = item.getItemId();
        if (id == R.id.nav_share_fb) {
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                String fbText = getResources().getString(R.string.fb_ContentDesc);
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(getResources().getString(R.string.store_url)))
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentDescription(fbText)
                        .setImageUrl(Uri.parse(getResources().getString(R.string.app_icon_url)))
                        .build();

                shareDialog.show(linkContent);
            }
        } else if (id == R.id.new_6) {
            b.putInt("nb", 6);
        } else if (id == R.id.new_7) {
            b.putInt("nb", 7);
        } else if (id == R.id.new_8) {
            b.putInt("nb", 8);
        } else if (id == R.id.new_9) {
            b.putInt("nb", 9);
        } else if (id == R.id.new_10) {
            b.putInt("nb", 10);
        }
        intent.putExtras(b);
        finish();
        startActivity(intent);
        return true;
    }
}
