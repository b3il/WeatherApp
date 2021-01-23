package com.example.androidweatherml;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Luokka, jossa käyttäjä valitsee mitä kaupunkia haluaa tarkastella.
 * @see android.support.v4.app.DialogFragment Fragmentti, joka näyttää valintaikkunan, kelluen aktiviteetin ikkunan päällä.
 * @version 1.0
 * @author Matias Leinonen
 */
public class DialogChangeCity extends DialogFragment {

    /** TAG jota voidaan kutsua konsoli komennoissa*/
    private static final String TAG = "DialogChangeCity";
    /** Kuuntelija käyttäjän syöttämälle tekstille*/
    public OnInputListener OnInputListener;

    /** Muokkausteksti johon käyttäjä syöttää kaupungin nimen.*/
    private EditText Input;
    /** Tekstikenttä, joka toimii nappulana varmistamisessa.*/
    private TextView actionOK;
    /** Tekstikenttä, joka toimii nappulana varmistamisessa.*/
    private TextView actionCancel;
    /** Valintaruutu oletuskaupungin varmistamiseen.*/
    private CheckBox default_checkbox;


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
        View view = inflater.inflate(R.layout.dialog_change_city, container,false);
        //haetaan id:t Activity:stä
        actionCancel = view.findViewById(R.id.action_cancel);
        actionOK = view.findViewById(R.id.action_ok);
        Input = view.findViewById(R.id.input);
        default_checkbox = view.findViewById(R.id.default_checkbox);

        //luodaan actionCancel-tekstikentälle painamiskuuntelija
        actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sulkee dialogin
                getDialog().dismiss();
            }
        });

        //luodaan actionOk-tekstikentälle painamiskuuntelija
        actionOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //muutetaan käyttäjän syöttämä kaupunki
                String input = Input.getText().toString();

                //haetaan näkymän konteksti toast:ia varten
                Context context = v.getContext();

                //tarkastetaan mitä käyttäjä on kenttään syöttänyt
                if(input.matches("")){
                    //jos käyttäjä ei ole syöttänyt kenttään mitään, esitetään toast
                    Toast.makeText(context,getString(R.string.enter_valid_city), Toast.LENGTH_SHORT).show();
                }
                //jos käyttäjä on syöttänyt kenttään jotakin
                else
                    //jos käyttäjä valitsee valintaruudun
                    if (default_checkbox.isChecked()) {
                        //lähetetään syöte saveData-metodille
                        ((WeatherActivity) getActivity()).saveData(input);
                        //esitetään toast
                        Toast.makeText(context,getString(R.string.default_city_changed) + input, Toast.LENGTH_LONG).show();
                        //lähetetään syöte FindWeather-metodille
                        ((WeatherActivity) getActivity()).FindWeather(input);
                        //lähetetään syöte sendInput-metodille
                        OnInputListener.sendInput(input);
                        //suljetaan dialog
                        getDialog().dismiss();
                    }
                    //jos käyttäjä ei valitse valintaruutua
                    else
                        //lähetetään syöte FindWeather-metodille
                        ((WeatherActivity) getActivity()).FindWeather(input);
                        //lähetetään syöte sendInput-metodille
                        OnInputListener.sendInput(input);
                        //suljetaan dialog
                        getDialog().dismiss();
            }
        });
        //luo näkymän
        return view;
    }


    /**
     * Kutsutaan, kun fragmentti liitetään ensimmäisen kerran kontekstiin.
     * @param context Fragmentin konteksti.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            //liittää onInputListenerin Activityyn
            OnInputListener = (OnInputListener)getActivity();
        }catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    /**
     * Kuuntelee dialogin tapahtumia ja toimii linkkinä WeatherActivitylle.
     */
    public interface OnInputListener{
        void sendInput(String input);
    }


}
