package com.ucsoftworks.rondocalc;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class CalculatorActivity extends Activity {

    private static final int MAX_DIGITS = 10;

    private TextView display;
    private boolean isCurrentNumDouble = false;
    private boolean allowCompletion = false;
    private boolean isNaN = false;
    private CalculatorState calculatorState = CalculatorState.NOT_QUEUED;

    private boolean isLastNumDouble = false;

    private long lastNumLong = 0;
    private double lastNumDouble = 0;


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
            if (displayText.length() < MAX_DIGITS) {
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
        if (!isCurrentNumDouble) {
            isCurrentNumDouble = true;
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
        isCurrentNumDouble = false;
        isLastNumDouble = false;
        lastNumDouble = 0;
        lastNumLong = 0;
        allowCompletion = true;
        calculatorState = CalculatorState.NOT_QUEUED;
    }

    private void operatorKeyPressed(CalculatorState buttonType) {
        if (isNaN) {
            isNaN = false;
            setInitialValues();
            display.setText("0");
            return;
        }
        String displayText = display.getText().toString();
        double currentNumDouble = Double.valueOf(displayText);
        long currentNumLong = 0;
        isCurrentNumDouble = isCurrentNumDouble || isLastNumDouble;
        if (!isCurrentNumDouble)
            currentNumLong = Long.valueOf(displayText);
        allowCompletion = false;
        switch (calculatorState) {
            case NOT_QUEUED:
                break;
            case PLUS:
                if (!isCurrentNumDouble)
                    setDisplayText(lastNumLong + currentNumLong);
                else
                    setDisplayText(lastNumDouble + currentNumDouble);
                break;
            case MINUS:
                if (!isCurrentNumDouble)
                    setDisplayText(lastNumLong - currentNumLong);
                else
                    setDisplayText(lastNumDouble - currentNumDouble);
                break;
            case MUL:
                if (!isCurrentNumDouble)
                    setDisplayText(lastNumLong * currentNumLong);
                else
                    setDisplayText(lastNumDouble * currentNumDouble);
                break;
            case DIV:
                isLastNumDouble = true;
                isCurrentNumDouble = true;
                setDisplayText(lastNumDouble / currentNumDouble);
                if (currentNumDouble == 0) {
                    allowCompletion = false;
                    isNaN = true;
                    return;
                }
                break;
        }
        lastNumDouble = Double.valueOf(display.getText().toString());

        if (!isLastNumDouble)
            lastNumLong = Long.valueOf(display.getText().toString());

        calculatorState = buttonType;
    }

    private void setDisplayText(long number) {
        String result;
        result = String.format(Locale.US, "%d", number);
        displayText(number, result);
    }

    private void setDisplayText(double number) {
        String result;
        result = String.format(Locale.US, "%f", number);
        displayText(number, result);
    }


    private void displayText(double number, String result) {
        if (result.length() > MAX_DIGITS) {
            isLastNumDouble = true;
            isCurrentNumDouble = true;
            result = String.format(Locale.US, "%e", number);
        }
        if (result.length() > MAX_DIGITS)
            result = String.format(Locale.US, "%1.3E", number);
        display.setText(result);
        display.setText(result);
    }

}
