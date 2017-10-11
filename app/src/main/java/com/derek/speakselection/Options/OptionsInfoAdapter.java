package com.derek.speakselection.Options;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class OptionsInfoAdapter extends ArrayAdapter<OptionsInfo>{
    private Context context;
    private List<OptionsInfo> options;

    public OptionsInfoAdapter(@NonNull Context context, @NonNull List<OptionsInfo> objects) {
        super(context, android.R.layout.simple_list_item_2, android.R.id.text1, objects);
        this.context = context;
        options = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final OptionsInfo option = options.get(position);
        View view = super.getView(position, convertView, parent);

        ((TextView) view.findViewById(android.R.id.text1)).setText(option.title);
        ((TextView) view.findViewById(android.R.id.text2)).setText(option.displayValue);
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                option.action.call(option);
            }
        });

        return view;
    }
}
