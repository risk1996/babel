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

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
