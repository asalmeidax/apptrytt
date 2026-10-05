package com.example.brazucalite;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public final class JsonCatalog {
    private JsonCatalog() {}

    public static List<MediaItemModel> parse(String json) throws Exception {
        JSONObject root = new JSONObject(json);
        JSONArray items = root.getJSONArray("items");
        List<MediaItemModel> out = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject o = items.getJSONObject(i);
            out.add(new MediaItemModel(
                    o.optString("id", String.valueOf(i)),
                    o.optString("title", "Sem título"),
                    o.optString("year", ""),
                    o.optString("category", ""),
                    o.optString("poster", ""),
                    o.getString("streamUrl")
            ));
        }
        return out;
    }
}
