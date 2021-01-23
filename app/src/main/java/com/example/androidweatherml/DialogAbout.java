package com.example.androidweatherml;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Luokka, jossa näytetään sovelluksen tiedot.
 * @see android.support.v4.app.DialogFragment Fragmentti, joka näyttää valintaikkunan, kelluen aktiviteetin ikkunan päällä.
 * @version 1.0
 * @author Matias Leinonen
 */
public class DialogAbout extends DialogFragment {

    /**Tekstikenttä joka toimii peruuta-nappina*/
    private TextView actionCancel;
    /**Tekstikenttä joka sisältää tietoa sovelluksesta*/
    private TextView about_text_data;
    /**Tekstikenttä joka sisältää tietoa sovelluksesta*/
    private TextView about_text_iconCredit;


    /**
     * Luo dialogin näkymän ja kutsuu tarvittavia metodeita sekä muuttujia.
     * @param savedInstanceState Hakee Activityn tilan.
     * @param container Näkymä, joka voi sisältää muita näkymiä.
     * @param inflater Asettelee näkymän pohjan.
     * @return Palauttaa näkymän.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //asettelee näkymän
        View view = inflater.inflate(R.layout.dialog_about, container,false);
        //haetaan id:t Activity:stä
        actionCancel = view.findViewById(R.id.action_cancel);
        about_text_data = view.findViewById(R.id.about_text_data);
        about_text_iconCredit = view.findViewById(R.id.about_text_iconCredit);
        //asetetaan tekstissä olevat hyperlinkit klikattaviksi
        about_text_iconCredit.setMovementMethod(LinkMovementMethod.getInstance());
        about_text_data.setMovementMethod(LinkMovementMethod.getInstance());

        //luodaan actionCancel-tekstikentälle painamiskuuntelija
        actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sulkee dialogin
                getDialog().dismiss();
            }
        });

        //luo näkymän
        return view;
    }

}
