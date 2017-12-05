package edu.temple.justeatit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class OptionsActivity extends AppCompatActivity {

    ListView optionsList;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        if (getIntent() == null) {
            text = getString(R.string.enable_gallery);
        } else {
            text = getIntent().getStringExtra("textviewValue");
        }

        // getting reference to our options listview
        optionsList = (ListView) findViewById(R.id.options_list);

        String option = getIntent().getStringExtra("textviewValue");
        String[] options_list = new String[]{option};

        OptionsAdapter<String> listAdapter = new OptionsAdapter<>(this, options_list);
        optionsList.setAdapter(listAdapter);
        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    TextView txtview = (TextView) view;
                    if (txtview.getText().toString().contains("Enable")) {
                        txtview.setText(R.string.disable_gallery);
                        text = txtview.getText().toString();
                    }
                    else {
                        txtview.setText(R.string.enable_gallery);
                        text = txtview.getText().toString();
                    }
                }
            }
        });
    }

    private void sendDataOnFinish() {
        Intent result = new Intent();
        result.putExtra("optionsValue", text);
        setResult(RESULT_OK, result);
    }

    @Override
    public void finish() {
        sendDataOnFinish();
        super.finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
