package com.handscape.blesdk;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter {

    private Context context;
    private LayoutInflater inflater;

    private List<BluetoothDevice> devices;

    public Adapter(Context context, List<BluetoothDevice> devices) {
        this.context = context;
        this.devices = devices;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(inflater.inflate(R.layout.item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof Holder) {
            Holder holder = (Holder) viewHolder;
            holder.itemView.setTag(devices.get(i));
            holder.itemtext.setText(devices.get(i).getAddress());
        }
    }

    @Override
    public int getItemCount() {
        if (devices != null)
            return devices.size();
        return 0;
    }


    class Holder extends RecyclerView.ViewHolder {

        TextView itemtext;

        public Holder(@NonNull View itemView) {
            super(itemView);
            itemtext = itemView.findViewById(R.id.itemtext);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof MainActivity) {
                        MainActivity activity = (MainActivity) context;

                        activity.connect((BluetoothDevice) v.getTag());
                    }


                }
            });
        }
    }
}
