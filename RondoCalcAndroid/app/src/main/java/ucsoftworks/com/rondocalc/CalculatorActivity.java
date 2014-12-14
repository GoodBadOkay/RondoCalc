package ucsoftworks.com.rondocalc;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CalculatorActivity extends Activity {

    TextView display;
    boolean dotWasPressed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                display = (TextView) stub.findViewById(R.id.display_text);

                TextView key;
                key = (TextView) stub.findViewById(R.id.keyDot);
                key.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dotKeyPressed();
                    }
                });


                setNumKeysOnClickListeners(stub);
            }

            private void setNumKeysOnClickListeners(WatchViewStub stub) {
                for (int i = R.id.key0; i <= R.id.key9; i++) {
                    TextView textView = (TextView) stub.findViewById(i);
                    final int key = i - R.id.key0;
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            numKeyPressed(key);
                        }
                    });
                }
            }
        });
    }

    private void numKeyPressed(int num) {
        String displayText = display.getText().toString();
        if (displayText.length() < 11) {
            if (displayText.equals("0")) {
                display.setText(String.valueOf(num));
            } else
                display.setText(displayText + String.valueOf(num));
        }
    }

    private void dotKeyPressed() {
        String displayText = display.getText().toString();
        if (!dotWasPressed) {
            dotWasPressed = true;
            if (displayText.equals("0"))
                display.setText("0.");
            else
                display.setText(displayText + ".");
        }
    }

}
