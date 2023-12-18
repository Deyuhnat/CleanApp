package com.example.cleanproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cleanproject.MarkerItem;

import java.util.List;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {

    private List<MarkerItem> markerList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView latitudeTextView;
        private final TextView longitudeTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.marker_title);
            descriptionTextView = view.findViewById(R.id.marker_description);
            latitudeTextView = view.findViewById(R.id.marker_latitude);
            longitudeTextView = view.findViewById(R.id.marker_longitude);
        }

        public void bind(MarkerItem markerItem) {
            titleTextView.setText(markerItem.getTitle());
            descriptionTextView.setText(markerItem.getDescription());
            latitudeTextView.setText("Latitude: " + markerItem.getLatitude());
            longitudeTextView.setText("Longitude: " + markerItem.getLongitude());
        }
    }

    public MarkerAdapter(List<MarkerItem> markerList) {
        this.markerList = markerList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.marker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(markerList.get(position));
    }

    @Override
    public int getItemCount() {
        return markerList.size();
    }
}