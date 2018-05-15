package at.ac.univie.hci.powercoin.functionality;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import at.ac.univie.hci.powercoin.R;

public class NotificationDialog extends AppCompatDialogFragment {

    private EditText value;
    private NotificationDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());



        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_notification, null);

        builder.setView(view)
                .setTitle("enter value:")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println(value);
                        if (Double.parseDouble(value.getText().toString()) == 0) {
                            Toast.makeText(getContext(),
                                    "please enter valid number!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            String val = value.getText().toString();
                            listener.applyText(val);
                        }

                    }
                });

        value = view.findViewById(R.id.enterValue);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (NotificationDialogListener) context;
        } catch (ClassCastException e) {
           throw new ClassCastException(context.toString() +
                   "must implement NotificationDialogListener");
        }
    }

    public interface NotificationDialogListener{
        void applyText(String value);
    }
}
