package edu.temple.justeatit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class OptionsActivity extends AppCompatActivity {

    ListView optionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        optionsList = (ListView) findViewById(R.id.options_list);
        String[] options_list = getResources().getStringArray(R.array.options_array);
        OptionsAdapter<String> listAdapter = new OptionsAdapter<>(this, options_list);
        optionsList.setAdapter(listAdapter);
        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    TextView txtview = (TextView) view;
                    if (txtview.getText().toString().contains("Enable"))
                        txtview.setText(R.string.disable_gallery);
                    else
                        txtview.setText(R.string.enable_gallery);
                }
            }
        });
    }
}
