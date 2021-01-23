package com.example.androidweatherml;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Luokka, joka määrittelee sovelluksesta poistumisdialogin.
 * @see android.support.v4.app.DialogFragment Fragmentti, joka näyttää valintaikkunan, kelluen aktiviteetin ikkunan päällä.
 * @version 1.0
 * @author Matias Leinonen
 */
public class DialogExit extends DialogFragment {

    /**
     * Luo dialogin näkymän ja muotoilee sen.
     * @param bundle Kartoitus merkkijonopainikkeista erilaisiin Parcelable-arvoihin.
     */
    public Dialog onCreateDialog(Bundle bundle) {
        //luodaan alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //asetetaan otsikko ja ikoni
        builder.setTitle(R.string.exit_confirm);
        builder.setIcon(R.drawable.ic_warning_red_24dp);

        //luodaan painamiskuuntelija alertdialogin positiiviselle napille
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //sulkee koko sovelluksen
                        System.exit(0);
                    }
                }
        );
        //luodaan painamiskuuntelija alertdialogin negatiiviselle napille
        builder.setNegativeButton(R.string.cancel,null);
        //luo alertdialogin
        return builder.create();
    }
}
