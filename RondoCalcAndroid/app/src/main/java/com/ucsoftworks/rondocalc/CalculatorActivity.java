package com.ucsoftworks.rondocalc;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalculatorActivity extends Activity {

    private TextView display;
    private boolean dotWasPressed = false;
    private boolean allowCompletion = false;
    private CalculatorState calculatorState = CalculatorState.NOT_QUEUED;
    private double lastNum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                display = (TextView) stub.findViewById(R.id.display_text);

                setExtraKeys(stub);

                setOperatorKeys(stub);

                setNumKeysOnClickListeners(stub);
            }

            private void setExtraKeys(WatchViewStub stub) {
                TextView key;
                key = (TextView) stub.findViewById(R.id.keyDot);
                key.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dotKeyPressed();
                    }
                });

                key = (TextView) stub.findViewById(R.id.keyC);
                key.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearKeyPressed();
                    }
                });
            }

            private void setOperatorKeys(WatchViewStub stub) {
                TextView key;
                Button button = (Button) stub.findViewById(R.id.key_equal);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        operatorKeyPressed(CalculatorState.NOT_QUEUED);
                    }
                });
                key = (TextView) stub.findViewById(R.id.key_plus);
                key.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        operatorKeyPressed(CalculatorState.PLUS);
                    }
                });
                key = (TextView) stub.findViewById(R.id.key_minus);
                key.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        operatorKeyPressed(CalculatorState.MINUS);
                    }
                });
                key = (TextView) stub.findViewById(R.id.key_mul);
                key.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        operatorKeyPressed(CalculatorState.MUL);
                    }
                });
                key = (TextView) stub.findViewById(R.id.key_divide);
                key.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        operatorKeyPressed(CalculatorState.DIV);
                    }
                });

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
        if (allowCompletion) {
            String displayText = display.getText().toString();
            if (displayText.length() < 11) {
                if (displayText.equals("0")) {
                    display.setText(String.valueOf(num));
                } else
                    display.setText(displayText + String.valueOf(num));
            }
        } else {
            allowCompletion = true;
            display.setText(String.valueOf(num));
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

    private void clearKeyPressed() {
        display.setText("0");
        setInitialValues();
    }

    private void setInitialValues() {
        dotWasPressed = false;
        lastNum = 0;
        allowCompletion = true;
        calculatorState = CalculatorState.NOT_QUEUED;
    }

    private void operatorKeyPressed(CalculatorState buttonType) {
        String displayText = display.getText().toString();
        float currentNum = Float.valueOf(displayText);
        allowCompletion = false;
        switch (calculatorState) {
            case NOT_QUEUED:
                break;
            case PLUS:
                setDisplayText(lastNum + currentNum);
                break;
            case MINUS:
                setDisplayText(lastNum - currentNum);
                break;
            case MUL:
                setDisplayText(lastNum * currentNum);
                break;
            case DIV:
                setDisplayText(lastNum / currentNum);
                if (currentNum == 0) {
                    setInitialValues();
                    return;
                }
                break;
        }
        lastNum = Float.valueOf(display.getText().toString());
        calculatorState = buttonType;
    }

    private void setDisplayText(double number) {
        String result;
        long longNum = (long) number;
        if (number == longNum)
            result = String.format("%d", longNum);
        else
            result = String.format("%s", number);
        if (result.length() > 11)
            result = String.format("%e", number);
        if (result.length() > 11)
            result = String.format("%1.3E", number);
        display.setText(result);
    }

}
