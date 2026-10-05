package com.example.brazucalite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class CatalogAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<MediaItemModel> all = new ArrayList<>();
    private final List<MediaItemModel> shown = new ArrayList<>();

    public CatalogAdapter(Context context) { inflater = LayoutInflater.from(context); }

    public void setItems(List<MediaItemModel> items) {
        all.clear(); all.addAll(items);
        shown.clear(); shown.addAll(items);
        notifyDataSetChanged();
    }

    public void filter(String q) {
        String s = q == null ? "" : q.trim().toLowerCase();
        shown.clear();
        if (s.isEmpty()) shown.addAll(all);
        else for (MediaItemModel m : all) if (m.title.toLowerCase().contains(s) || m.category.toLowerCase().contains(s)) shown.add(m);
        notifyDataSetChanged();
    }

    public MediaItemModel getMediaItem(int position) { return shown.get(position); }
    @Override public int getCount() { return shown.size(); }
    @Override public Object getItem(int position) { return shown.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            h = new Holder();
            h.poster = convertView.findViewById(R.id.poster);
            h.title = convertView.findViewById(R.id.title);
            h.subtitle = convertView.findViewById(R.id.subtitle);
            convertView.setTag(h);
        } else h = (Holder) convertView.getTag();
        MediaItemModel m = shown.get(position);
        h.title.setText(m.title);
        h.subtitle.setText((m.year + "  " + m.category).trim());
        SimpleImageLoader.load(m.poster, h.poster);
        return convertView;
    }

    static class Holder { ImageView poster; TextView title, subtitle; }
}
