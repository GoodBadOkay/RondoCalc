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
    private boolean wasDividedByZero = false;
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
        if (wasDividedByZero) {
            wasDividedByZero = false;
            setInitialValues();
            display.setText(String.valueOf(num));
            return;
        }
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
            isCurrentNumDouble = false;
            display.setText(String.valueOf(num));
        }
    }

    private void dotKeyPressed() {
        if (wasDividedByZero) {
            wasDividedByZero = false;
            setInitialValues();
            initDecimalState();
            return;
        }
        if (allowCompletion) {
            String displayText = display.getText().toString();
            if (!isCurrentNumDouble) {
                isCurrentNumDouble = true;
                if (displayText.equals("0"))
                    display.setText("0.");
                else
                    display.setText(displayText + ".");
            }
        }
        else {
            initDecimalState();
        }
    }

    private void initDecimalState() {
        allowCompletion = true;
        isCurrentNumDouble = true;
        display.setText("0.");
    }

    private void clearKeyPressed() {
        display.setText("0");
        setInitialValues();
    }

    private void setInitialValues() {
        isCurrentNumDouble = false;
        lastNum = 0;
        allowCompletion = true;
        calculatorState = CalculatorState.NOT_QUEUED;
    }

    private void operatorKeyPressed(CalculatorState buttonType) {
        if (wasDividedByZero) {
            wasDividedByZero = false;
            setInitialValues();
            display.setText("0");
            return;
        }
        String displayText = display.getText().toString();
        double currentNumDouble = Double.valueOf(displayText);

        if (allowCompletion) {
            allowCompletion = false;
            switch (calculatorState) {
                case NOT_QUEUED:
                    break;
                case PLUS:
                    setDisplayText(lastNum + currentNumDouble);
                    break;
                case MINUS:
                    setDisplayText(lastNum - currentNumDouble);
                    break;
                case MUL:
                    setDisplayText(lastNum * currentNumDouble);
                    break;
                case DIV:
                    setDisplayText(lastNum / currentNumDouble);
                    if (currentNumDouble == 0) {
                        allowCompletion = false;
                        wasDividedByZero = true;
                        return;
                    }
                    break;
            }
            lastNum = Double.valueOf(display.getText().toString());
        }

        calculatorState = buttonType;
    }


    private void setDisplayText(double number) {
        String result;
        if (number == (long) number)
            result = String.format(Locale.US, "%d", (long) number);
        else {
            result = String.format(Locale.US, "%f", number);
            result = trimTrailingZeros(result);
        }
        displayText(number, result);
    }

    private static String trimTrailingZeros(String number) {
        if (!number.contains(".")) {
            return number;
        }

        return number.replaceAll("\\.?0*$", "");
    }

    private void displayText(double number, String result) {
        if (result.length() > MAX_DIGITS) {
            result = String.format(Locale.US, "%e", number);
        }
        if (result.length() > MAX_DIGITS)
            result = String.format(Locale.US, "%1.3E", number);
        display.setText(result);
    }

}
