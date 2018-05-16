package id.ac.umn.mobile.babel;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class Test {
    void A(){
        EditText editText = new EditText(null);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//di detect oleh textwatcher (char sequence)
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//sedang dibaca oleh textwatcher (char sequence)
            }

            @Override
            public void afterTextChanged(Editable editable) {
// setelah berhasil dibaca
            }
        });
    }
}
