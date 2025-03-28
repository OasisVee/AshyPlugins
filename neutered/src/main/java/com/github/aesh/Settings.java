package com.github.aesh;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle; // Added import for Bundle
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
import com.discord.app.AppBottomSheet;
import com.discord.views.CheckedSetting;

import java.util.Locale;

public class Settings extends AppBottomSheet {
    SettingsAPI settings;
    neutered plugin;

    // Color Constants
    private static final int BACKGROUND_COLOR = Color.parseColor("#1E1E1E");
    private static final int TEXT_COLOR = Color.parseColor("#E0E0E0");
    private static final int ACCENT_COLOR = Color.parseColor("#4CAF50");
    private static final int DIVIDER_COLOR = Color.parseColor("#333333");

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
        Context context = layoutInflater.getContext();
        
        // Determine language for localization
        String[] phrases = getPhrases(context);

        // Main layout setup
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(BACKGROUND_COLOR);
        mainLayout.setPadding(dpToPx(context, 16), dpToPx(context, 16), dpToPx(context, 16), dpToPx(context, 16));

        // Title
        TextView titleView = new TextView(context);
        titleView.setText("Neutered Plugin Settings");
        titleView.setTextColor(ACCENT_COLOR);
        titleView.setTextSize(20);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setPadding(0, 0, 0, dpToPx(context, 16));
        mainLayout.addView(titleView);

        // Meow Randomization Section
        addSectionHeader(mainLayout, "Meow Randomization", phrases);

        // Chance Settings
        addChanceSettings(mainLayout, phrases, context);

        // Additional Meow Options
        addMeowOptions(mainLayout, phrases, context);

        // Silent Typing Section
        addSilentTypingSettings(mainLayout, phrases, context);

        // Emoji Customization
        addEmojiSettings(mainLayout, phrases, context);

        // Color Picker Button
        addColorPickerButton(mainLayout, phrases, context);

        return mainLayout;
    }

    private void addSectionHeader(LinearLayout layout, String title, String[] phrases) {
        TextView sectionHeader = new TextView(layout.getContext());
        sectionHeader.setText(title);
        sectionHeader.setTextColor(ACCENT_COLOR);
        sectionHeader.setTypeface(null, Typeface.BOLD);
        sectionHeader.setTextSize(16);
        sectionHeader.setPadding(0, dpToPx(layout.getContext(), 16), 0, dpToPx(layout.getContext(), 8));
        layout.addView(sectionHeader);
        
        View divider = new View(layout.getContext());
        divider.setBackgroundColor(DIVIDER_COLOR);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dpToPx(layout.getContext(), 1)
        );
        layout.addView(divider, dividerParams);
    }

    private void addChanceSettings(LinearLayout layout, String[] phrases, Context context) {
        String[] chanceKeys = {
            "chanceShortFace", "chanceExtraW", "chancePurr", 
            "chanceNyan", "chanceMiau", "chanceTilde", "chanceLongFace"
        };
        
        int[] defaultValues = {30, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < chanceKeys.length; i++) {
            TextView label = makeTextView(phrases[i]);
            label.setTextColor(TEXT_COLOR);
            layout.addView(label);

            EditText input = makeEditText();
            input.setTextColor(TEXT_COLOR);
            input.setHintTextColor(Color.GRAY);
            
            addTextWatcherForSettings(input, chanceKeys[i], defaultValues[i]);
            layout.addView(input);
        }

        // Allow Extra Phrases Toggle
        CheckedSetting extraPhrasesToggle = Utils.createCheckedSetting(
            context, 
            CheckedSetting.ViewType.SWITCH, 
            phrases[8], 
            phrases[9]
        );
        extraPhrasesToggle.setChecked(settings.getBool("extraMeows", false));
        extraPhrasesToggle.setOnCheckedListener(aBoolean -> 
            settings.setBool("extraMeows", aBoolean)
        );
        layout.addView(extraPhrasesToggle);
    }

    private void addMeowOptions(LinearLayout layout, String[] phrases, Context context) {
        CheckedSetting forceMiauToggle = Utils.createCheckedSetting(
            context, 
            CheckedSetting.ViewType.SWITCH, 
            phrases[10], 
            phrases[11]
        );
        forceMiauToggle.setChecked(settings.getBool("forceMiau", false));
        forceMiauToggle.setOnCheckedListener(aBoolean -> 
            settings.setBool("forceMiau", aBoolean)
        );
        layout.addView(forceMiauToggle);
    }

    private void addSilentTypingSettings(LinearLayout layout, String[] phrases, Context context) {
        addSectionHeader(layout, "Silent Typing", phrases);

        CheckedSetting showToastToggle = Utils.createCheckedSetting(
            context, 
            CheckedSetting.ViewType.SWITCH, 
            phrases[13], 
            ""
        );
        showToastToggle.setChecked(settings.getBool("showToast", false));
        showToastToggle.setOnCheckedListener(aBoolean -> 
            settings.setBool("showToast", aBoolean)
        );
        layout.addView(showToastToggle);

        CheckedSetting hideKeyboardToggle = Utils.createCheckedSetting(
            context, 
            CheckedSetting.ViewType.SWITCH, 
            phrases[14], 
            ""
        );
        hideKeyboardToggle.setChecked(settings.getBool("hideKeyboard", false));
        hideKeyboardToggle.setOnCheckedListener(aBoolean -> {
            settings.setBool("hideKeyboard", aBoolean);
            plugin.setHideKeyboard(aBoolean);
        });
        layout.addView(hideKeyboardToggle);
    }

    private void addEmojiSettings(LinearLayout layout, String[] phrases, Context context) {
        addSectionHeader(layout, "Emoji Customization", phrases);

        TextView emojiLabel = makeTextView(phrases[16]);
        emojiLabel.setTextColor(TEXT_COLOR);
        layout.addView(emojiLabel);

        EditText emojiInput = makeEditText();
        emojiInput.setInputType(InputType.TYPE_CLASS_TEXT);
        emojiInput.setHint("🐈");
        emojiInput.setTextColor(TEXT_COLOR);
        emojiInput.setHintTextColor(Color.GRAY);
        
        String currentEmoji = settings.getString("meowEmoji", "🐈");
        emojiInput.setText(currentEmoji);

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
                    Toast.makeText(context, "Meow emoji set to: " + emoji, Toast.LENGTH_SHORT).show();
                } else {
                    settings.remove("meowEmoji");
                    emojiInput.setHint("🐈");
                    Toast.makeText(context, "Meow emoji reset to default", Toast.LENGTH_SHORT).show();
                }
                Utils.promptRestart("Restart to apply changes");
            }
        });
        layout.addView(emojiInput);
    }

    private void addColorPickerButton(LinearLayout layout, String[] phrases, Context context) {
        Button colorPickerButton = new Button(context);
        colorPickerButton.setText(phrases[15]);
        colorPickerButton.setBackgroundColor(ACCENT_COLOR);
        colorPickerButton.setTextColor(Color.WHITE);
        colorPickerButton.setOnClickListener(f -> {
            com.github.aesh.ColorPicker picker = new com.github.aesh.ColorPicker();
            Utils.openPageWithProxy(context, picker);
        });
        layout.addView(colorPickerButton);
    }

    private String[] getPhrases(Context context) {
        return esLang(context) ? getSpanishPhrases() : getEnglishPhrases();
    }

    private String[] getEnglishPhrases() {
        return new String[] {
            "   Percent chance of :3",
            "   Percent chance of extra character\n   E.G. meoww, purrr, nyann, ect",
            "   Percent chance of replacing meow with *purr*",
            "   Percent chance of replacing meow and *purr* with nyan",
            "   Percent chance of overriding everything with miau",
            "   Percent chance of placing a tilde after the first word ~",
            "   Percent chance of using ≽^•⩊•^≼ instead of :3",
            "   Ignore everything and always use miau",
            "Allow more phrases",
            "E.G. *purr*, nyan, mreeowww, etc",
            "Always force miau",
            "miauu :3",
            "-- BetterSilentTyping Settings --",
            "Show Toast Message When Silent Typing is Toggled",
            "Hide Keyboard Icon",
            "Open Color Picker",
            "Custom Meow Emoji",
            "Set a custom emoji for the meow button"
        };
    }

    private String[] getSpanishPhrases() {
        return new String[] {
            "   Probabilidad de :3",
            "   Probabilidad de Más Caracteres\n   Ej. miauu, purrr, nyann, etc.",
            "   Probabilidad de sustituir meow con « *purr* »",
            "   Probabilidad de sustituir meow y *purr* por « nyan »",
            "   Probabilidad de sustituir Todo por « miau »",
            "   Probabilidad de Añadir una Virgulilla Después la Palabra Principal ~",
            "   Probabilidad de Usar ≽^•⩊•^≼ en Lugar de :3",
            "   Ignorar Todas las Otras y Siempre usar « miau »",
            "Permitir más Frases",
            "Ej. *purr*, nyan, mreeowww, etc.",
            "Siempre Forzar miau",
            "miau :3",
            "-- Opciones para BetterSilentTyping --",
            "Mostrar Mensaje Emergente Cuando se Conmute SilentTyping",
            "Ocultar Ícono del Teclado",
            "Abrir Selector de Color",
            "Emoji de Meow Personalizado",
            "Establece un emoji personalizado para el botón de meow"
        };
    }

    // Existing helper methods remain the same
    public void addTextWatcherForSettings(EditText editText, String settingsKey, int defVal) {
        int savedValue = settings.getInt(settingsKey, defVal);
        editText.setText(String.valueOf(savedValue));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
        return editText;
    }

    public TextView makeTextView(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
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
