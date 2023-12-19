package com.example.cleanproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cleanproject.MarkerItem;
import android.widget.Button;

import java.util.List;
import java.util.ArrayList;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {

    private List<MarkerItem> markerList;
    private MarkerAdapterListener listener;
    private MarkerDeleteListener deleteListener;

    public interface MarkerAdapterListener {
        void onNavigateButtonClicked(MarkerItem markerItem);
    }

    public void setMarkerAdapterListener(MarkerAdapterListener listener) {
        this.listener = listener;
    }

    public interface MarkerDeleteListener {
        void onDeleteButtonClicked(MarkerItem markerItem);
    }

    public void setMarkerDeleteListener(MarkerDeleteListener listener) {
        this.deleteListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView latitudeTextView;
        private final TextView longitudeTextView;
        private final Button navigateButton;
        private final Button deleteButton;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.marker_title);
            descriptionTextView = view.findViewById(R.id.marker_description);
            latitudeTextView = view.findViewById(R.id.marker_latitude);
            longitudeTextView = view.findViewById(R.id.marker_longitude);
            navigateButton = view.findViewById(R.id.marker_navigate_button);
            deleteButton = view.findViewById(R.id.marker_delete_button);
        }

        public void bind(MarkerItem markerItem, MarkerAdapterListener listener, MarkerDeleteListener deleteListener) {
            titleTextView.setText(markerItem.getTitle());
            descriptionTextView.setText(markerItem.getDescription());
            latitudeTextView.setText("Latitude: " + markerItem.getLatitude());
            longitudeTextView.setText("Longitude: " + markerItem.getLongitude());

            navigateButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNavigateButtonClicked(markerItem);
                }
            });
            deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteButtonClicked(markerItem);
                }
            });
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
        MarkerItem markerItem = markerList.get(position);
        holder.bind(markerItem, listener, deleteListener);
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteButtonClicked(markerItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return markerList.size();
    }

}