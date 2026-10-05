package com.example.brazucalite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final String PREFS = "prefs";
    private static final String KEY_CONFIG_URL = "config_url";
    private static final String KEY_VERSION = "catalog_version";
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private CatalogAdapter adapter;
    private TextView status;
    private File cacheFile;
    private GridView grid;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        cacheFile = new File(getFilesDir(), "catalog_cache.json");
        adapter = new CatalogAdapter(this);
        grid = findViewById(R.id.grid);
        grid.setAdapter(adapter);
        status = findViewById(R.id.status);

        EditText search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int before, int count) { adapter.filter(s.toString()); }
            public void afterTextChanged(Editable e) {}
        });

        grid.setOnItemClickListener((p, v, pos, id) -> {
            MediaItemModel m = adapter.getMediaItem(pos);
            Intent i = new Intent(this, PlayerActivity.class);
            i.putExtra("title", m.title);
            i.putExtra("url", m.streamUrl);
            startActivity(i);
        });

        Button refresh = findViewById(R.id.btnRefresh);
        refresh.setOnClickListener(v -> syncRemote(true));
        Button config = findViewById(R.id.btnConfig);
        config.setOnClickListener(v -> showSourceDialog());

        loadImmediately();
        syncRemote(false);
    }

    private void loadImmediately() {
        io.execute(() -> {
            try {
                String json = cacheFile.exists() ? readFile(cacheFile) : readAsset("catalog.json");
                List<MediaItemModel> list = JsonCatalog.parse(json);
                runOnUiThread(() -> {
                    adapter.setItems(list);
                    if (adapter.getCount() > 0) { grid.setSelection(0); grid.requestFocus(); }
                    int ver = getSharedPreferences(PREFS, MODE_PRIVATE).getInt(KEY_VERSION, 0);
                    status.setText("Catálogo local • versão " + ver + " • " + list.size() + " itens");
                });
            } catch (Exception e) {
                runOnUiThread(() -> status.setText("Erro ao abrir catálogo: " + e.getMessage()));
            }
        });
    }

    private void syncRemote(boolean manual) {
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        String configUrl = sp.getString(KEY_CONFIG_URL, "").trim();
        if (configUrl.isEmpty()) {
            if (manual) status.setText("Defina a URL em Fonte para ativar atualização online.");
            return;
        }
        if (manual) status.setText("Verificando atualização...");
        io.execute(() -> {
            try {
                JSONObject cfg = new JSONObject(Net.get(configUrl));
                int remoteVersion = cfg.getInt("catalogVersion");
                int minApp = cfg.optInt("minimumAppVersion", 1);
                if (minApp > 1) {
                    runOnUiThread(() -> status.setText("A fonte exige uma versão mais nova do aplicativo."));
                    return;
                }
                int localVersion = sp.getInt(KEY_VERSION, 0);
                if (remoteVersion > localVersion || !cacheFile.exists()) {
                    String catalogUrl = cfg.getString("catalogUrl");
                    String json = Net.get(catalogUrl);
                    List<MediaItemModel> parsed = JsonCatalog.parse(json); // valida antes de salvar
                    writeFile(cacheFile, json);
                    sp.edit().putInt(KEY_VERSION, remoteVersion).apply();
                    runOnUiThread(() -> {
                        adapter.setItems(parsed);
                        if (adapter.getCount() > 0) { grid.setSelection(0); grid.requestFocus(); }
                        status.setText("Atualizado • versão " + remoteVersion + " • " + parsed.size() + " itens");
                    });
                } else if (manual) {
                    runOnUiThread(() -> status.setText("Catálogo já está atualizado • versão " + localVersion));
                }
            } catch (Exception e) {
                if (manual) runOnUiThread(() -> status.setText("Falha na atualização; usando cache local. " + e.getMessage()));
            }
        });
    }

    private void showSourceDialog() {
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("https://.../config.json");
        input.setText(sp.getString(KEY_CONFIG_URL, ""));
        new AlertDialog.Builder(this)
                .setTitle("URL do config.json")
                .setMessage("Use somente uma fonte HTTPS que você controla ou tem autorização para acessar.")
                .setView(input)
                .setPositiveButton("Salvar", (d, w) -> {
                    sp.edit().putString(KEY_CONFIG_URL, input.getText().toString().trim()).apply();
                    syncRemote(true);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String readAsset(String name) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(name)));
        StringBuilder sb = new StringBuilder(); String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close(); return sb.toString();
    }

    private String readFile(File f) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        StringBuilder sb = new StringBuilder(); String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close(); return sb.toString();
    }

    private void writeFile(File f, String s) throws Exception {
        FileOutputStream out = new FileOutputStream(f);
        out.write(s.getBytes("UTF-8")); out.close();
    }
}
