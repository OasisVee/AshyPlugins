package com.github.aesh;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.aliucord.views.Divider;
import com.discord.app.AppBottomSheet;
import com.discord.views.CheckedSetting;

import java.util.Locale;

public class Settings extends AppBottomSheet {

    SettingsAPI settings;
    neutered plugin;

    public Settings(SettingsAPI set, neutered plugin) {
        settings = set;
        this.plugin = plugin;
    }

    @Override
    public int getContentViewResId() {
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        String[] phrases = new String[0];

        if(!esLang(getContext())) {
            phrases = new String[] {
                    // TEXT VIEW PHRASES //
                    "   Percent chance of :3",
                    "   Percent chance of extra character\n   E.G. meoww, purrr, nyann, ect",
                    "   Percent chance of replacing meow with *purr*",
                    "   Percent chance of replacing meow and *purr* with nyan",
                    "   Percent chance of overriding everything with miau",
                    "   Percent chance of placing a tilde after the first word ~", // 5
                    "   Percent chance of using ≽^•⩊•^≼ instead of :3",
                    // OTHER //
                    "   Ignore everything and always use miau", // 7
                    "Allow more phrases", // 8
                    "E.G. *purr*, nyan, mreeowww, etc", // 9
                    "Always force miau", // 10
                    "miauu :3", // 11
                    "-- BetterSilentTyping Settings --", // 12
                    "Show Toast Message When Silent Typing is Toggled", // 13
                    "Hide Keyboard Icon", // 14
                    "Open Color Picker", // 15
                    "Custom Meow Emoji", // 16
                    "Set a custom emoji for the meow button" // 17
            };
        } else {
            phrases = new String[] {
                    // FRASES PA LA VISTA DE TEXTO //
                    "   Probabilidad de :3",
                    "   Probabilidad de Más Caracteres\n   Ej. miauu, purrr, nyann, etc.",
                    "   Probabilidad de sustituir meow con « *purr* »",
                    "   Probabilidad de sustituir meow y *purr* por « nyan »",
                    "   Probabilidad de sustituir Todo por « miau »",
                    "   Probabilidad de Añadir una Virgulilla Después la Palabra Principal ~", // 5
                    "   Probabilidad de Usar ≽^•⩊•^≼ en Lugar de :3",
                    // OTROS //
                    "   Ignorar Todas las Otras y Siempre usar « miau »", // 7
                    "Permitir más Frases", // 8
                    "Ej. *purr*, nyan, mreeowww, etc.", // 9
                    "Siempre Forzar miau", // 10
                    "miau :3", // diferencía intentional, 11
                    "-- Opciones para BetterSilentTyping --", // 12
                    "Mostrar Mensaje Emergente Cuando se Conmute SilentTyping", // 13
                    "Ocultar Ícono del Teclado", // 14
                    "Abrir Selector de Color", // 15
                    "Emoji de Meow Personalizado", // 16
                    "Establece un emoji personalizado para el botón de meow" // 17
            };
        }

        Context context = layoutInflater.getContext();
        LinearLayout lay = new LinearLayout(context);
        lay.setOrientation(LinearLayout.VERTICAL);

        View dragHandle = new View(context);
        dragHandle.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams handleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(context, 4)
        );
        handleParams.setMargins(dpToPx(context, 16), dpToPx(context, 8), dpToPx(context, 16), dpToPx(context, 8));
        dragHandle.setLayoutParams(handleParams);

        View divider = new View(context);
        divider.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(context, 1)
        );
        divider.setLayoutParams(dividerParams);

        View padding = new View(context);
        padding.setBackgroundColor(Color.DKGRAY);
        LinearLayout.LayoutParams paddingParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(context, 10)
        );
        padding.setLayoutParams(paddingParams);

        // Create CheckedSetting for the switch
        CheckedSetting a = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, phrases[8], phrases[9]);
        a.setChecked(settings.getBool("extraMeows", false));
        a.setOnCheckedListener(aBoolean -> {
            settings.setBool("extraMeows", aBoolean);
        });

        CheckedSetting z = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, phrases[10], phrases[11]);
        z.setChecked(settings.getBool("forceMiau", false));
        z.setOnCheckedListener(aBoolean -> {
            settings.setBool("forceMiau", aBoolean);
        });

        // SILENT TYPING //

        TextView st = new TextView(getContext());
        st.setText(phrases[12]);
        st.setTextColor(Color.WHITE);
        st.setTypeface(null, Typeface.BOLD);

        lay.setOrientation(LinearLayout.VERTICAL);
        CheckedSetting fo = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH,phrases[13],"");
        fo.setChecked(settings.getBool("showToast",false));
        fo.setOnCheckedListener(aBoolean -> {
            settings.setBool("showToast",aBoolean);
        });
        CheckedSetting b= Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH,phrases[14],"");
        b.setChecked(settings.getBool("hideKeyboard",false));
        b.setOnCheckedListener(aBoolean -> {
            settings.setBool("hideKeyboard",aBoolean);
            plugin.setHideKeyboard(aBoolean);
        });

        Button button = new Button(context);
        button.setText(phrases[15]);
        button.setOnClickListener(f -> {
            com.github.aesh.ColorPicker picker = new com.github.aesh.ColorPicker();
            Utils.openPageWithProxy(context,picker);
        });

        // Add emoji setting
        TextView emojiLabel = makeTextView(phrases[16]);
        EditText emojiInput = makeEditText();
        emojiInput.setInputType(InputType.TYPE_CLASS_TEXT);
        emojiInput.setHint("🐈");
        
        // Get current emoji or use default
        String currentEmoji = settings.getString("meowEmoji", "🐈");
        emojiInput.setText(""); // Start with empty text

        emojiInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String emoji = s.toString().trim();
                if (!emoji.isEmpty()) {
                    settings.setString("meowEmoji", emoji);
                    
                    // Show a toast to confirm
                    Toast.makeText(context, "Meow emoji set to: " + emoji, Toast.LENGTH_SHORT).show();
                } else {
                    // If emoji is deleted, reset to default hint
                    settings.setString("meowEmoji", "🐈");
                    
                    Toast.makeText(context, "Meow emoji reset to default: 🐈", Toast.LENGTH_SHORT).show();
                }

                // Prompt restart like in BetterChatbox
                Utils.promptRestart("Restart to apply changes");
            }
        });

        // Every TextView label in order used for the EditText fields
        TextView lChanceShortFace = makeTextView(phrases[0]);
        TextView lChanceExtraW = makeTextView(phrases[1]);
        TextView lChancePurr = makeTextView(phrases[2]);
        TextView lChanceNyan = makeTextView(phrases[3]);
        TextView lChanceMiau = makeTextView(phrases[4]);
        TextView lChanceTilde = makeTextView(phrases[5]);
        TextView lChanceLongFace = makeTextView(phrases[6]);

        // Every EditText field in order
        EditText shortFace = makeEditText();
        EditText extraW = makeEditText();
        EditText purr = makeEditText();
        EditText nyan = makeEditText();
        EditText miau = makeEditText();
        EditText tilde = makeEditText();
        EditText longFace = makeEditText();

        // Every watcher in order
        addTextWatcherForSettings(shortFace, "chanceShortFace", 30);
        addTextWatcherForSettings(extraW, "chanceExtraW", 0);
        addTextWatcherForSettings(purr, "chancePurr", 0);
        addTextWatcherForSettings(nyan, "chanceNyan", 0);
        addTextWatcherForSettings(miau, "chanceMiau", 0);
        addTextWatcherForSettings(tilde, "chanceTilde", 0);
        addTextWatcherForSettings(longFace, "chanceLongFace", 0);

        // Add views to the layout
        lay.addView(dragHandle);
        lay.addView(divider);
        lay.addView(padding);

        lay.addView(lChanceShortFace);
        lay.addView(shortFace);
        // Allow extra //
        lay.addView(a);

        lay.addView(lChanceExtraW);
        lay.addView(extraW);

        lay.addView(lChancePurr);
        lay.addView(purr);

        lay.addView(lChanceNyan);
        lay.addView(nyan);

        lay.addView(lChanceMiau);
        lay.addView(miau);

        lay.addView(lChanceTilde);
        lay.addView(tilde);

        lay.addView(lChanceLongFace);
        lay.addView(longFace);

        lay.addView(z);

        lay.addView(st);

        lay.addView(fo);
        lay.addView(b);
        lay.addView(button);

        // Add emoji settings
        lay.addView(emojiLabel);
        lay.addView(emojiInput);

        return lay;
    }

    public void addTextWatcherForSettings(EditText editText, String settingsKey, int defVal) {
        int savedValue = settings.getInt(settingsKey, defVal);
        editText.setText(String.valueOf(savedValue));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(s.toString());
                    settings.setInt(settingsKey, value);
                } catch (NumberFormatException e) {
                    settings.setInt(settingsKey, 0);
                }
            }
        });
    }

    public EditText makeEditText() {
        EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("0 ~ 100");
        editText.setTextColor(Color.WHITE);
        editText.setHintTextColor(Color.GRAY);
        editText.setCursorVisible(false);
        return editText;
    }

    public TextView makeTextView(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    public Locale getPrimaryLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }

    public boolean esLang(Context context) {
        Locale primaryLocale = getPrimaryLocale(context);
        String languageCode = primaryLocale.getLanguage();
        return languageCode.equalsIgnoreCase("es");
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
