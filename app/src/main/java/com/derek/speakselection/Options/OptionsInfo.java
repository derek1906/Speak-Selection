package com.derek.speakselection.Options;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;

import com.derek.speakselection.R;
import com.derek.speakselection.Utils.Callback;

import java.util.List;

public class OptionsInfo {
    public String title;
    public String displayValue;
    public OptionsInfoAction action;

    // Custom action
    public OptionsInfo(String title, String displayValue, OptionsInfoAction action) {
        this.title = title;
        this.displayValue = displayValue;
        this.action = action;
    }

    // Select option from a list of values
    public OptionsInfo(final String title, final Context ctx,
                       final OptionsInfoAccessor<List<String>, Object> optionsAccessor,
                       final OptionsInfoAccessor<String, String> prefAccessor,
                       final Callback cb){
        this.title = title;

        String selectedValue = prefAccessor.get();
        if(optionsAccessor.get().contains(selectedValue)){
            this.displayValue = selectedValue;
        }else{
            this.displayValue = ctx.getResources().getString(R.string.invalid_selected_value, selectedValue);
        }

        this.action = new OptionsInfoAction() {
            @Override
            public void call(final OptionsInfo option) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(title);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> options = optionsAccessor.get();
                        ListView listview = ((AlertDialog) dialog).getListView();
                        String newSelectedValue = options.get(listview.getCheckedItemPosition());

                        // store new displayValue
                        prefAccessor.set(newSelectedValue);
                        option.displayValue = newSelectedValue;

                        dialog.dismiss();

                        // done callback
                        cb.call(OptionsInfo.this);
                    }
                });

                List<String> options = optionsAccessor.get();
                String selectedValue = prefAccessor.get();

                builder.setSingleChoiceItems(options.toArray(new String[0]), options.indexOf(selectedValue), null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
    }

    public static OptionsInfo createNoOp(String title, String displayValue) {
        return new OptionsInfo(title, displayValue, new OptionsInfoAction() {
            @Override
            public void call(OptionsInfo option) {}
        });
    }
}
