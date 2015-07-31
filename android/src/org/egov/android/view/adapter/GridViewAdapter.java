package org.egov.android.view.adapter;

import java.io.File;
import java.util.ArrayList;

import org.egov.android.R;
import org.egov.android.model.ComplaintType;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ComplaintType> item;

    public GridViewAdapter(Context context, ArrayList<ComplaintType> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_list_item, parent, false);

            holder = new ViewHolder();
            holder.imageTitle = (TextView) convertView.findViewById(R.id.text);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ComplaintType cat = item.get(position);
        holder.imageTitle.setText(cat.getName());
        File file = new File(cat.getImagePath());

        if (file.exists()) {
            holder.image.setImageBitmap(_getBitmapImage(cat.getImagePath()));
        } else {
            holder.image.setImageResource(R.drawable.default_category);
        }
        return convertView;
    }

    private Bitmap _getBitmapImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}