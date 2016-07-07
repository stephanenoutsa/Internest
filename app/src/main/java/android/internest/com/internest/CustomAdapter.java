package android.internest.com.internest;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class CustomAdapter extends ArrayAdapter<Scanned> {

    public CustomAdapter(Context context, List<Scanned> scans) {
        super(context, R.layout.custom_row, scans);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_row, parent, false);

        Scanned singleScannedItem = getItem(position);

        ImageView scanIcon = (ImageView) customView.findViewById(R.id.scanIcon);
        TextView scanType = (TextView) customView.findViewById(R.id.scanType);
        TextView scanDetails = (TextView) customView.findViewById(R.id.scanDetails);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Magnificent.ttf");
        scanType.setTypeface(font, Typeface.BOLD);
        scanDetails.setTypeface(font);

        scanType.setText(singleScannedItem.getStype());
        scanDetails.setText(singleScannedItem.getSdetails());

        if(singleScannedItem.getStype().equals("url")) {
            scanIcon.setImageResource(R.drawable.link);
        }
        else if(singleScannedItem.getStype().equals("text")) {
            scanIcon.setImageResource(R.drawable.text);
        }

        return customView;
    }
}