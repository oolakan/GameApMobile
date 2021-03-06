package com.example.mygame.mygame.game;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygame.mygame.R;
import com.example.mygame.mygame.custom.GameTransactionsCustomList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import com.example.mygame.mygame.model.DBController;
import com.example.mygame.mygame.model.Transaction;
import com.example.mygame.mygame.utils.Constants;

public class GameSummaryActivity extends AppCompatActivity {

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    private ListView listView;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private JSONArray transactionLists;
    private DBController dbController;

    private ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    private GameTransactionsCustomList adapter;

    private ProgressBar progressBar;
    private Button cancelGameBtn, proceedToPayBtn, printGameTicket, openB;

    private double totalAmount;
    private TextView serialNoView, totalAmountView;

    private String printedData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        toolbar.setTitle(getString(R.string.game_transactions));
       // toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getViews();
        //get bluetooth
        try {
            findBT();
            closeBT();
            openBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dbController = new DBController(this);
        try {
            printedData = Constants.EMPTY; // reinitialize print data variable
            transactionLists = dbController.getTransactions();
            // set serial number
            serialNoView.setText(transactionLists.getJSONObject(0).getString(Constants.SERIAL_NO));
            printedData += String.format("\n          %s\n\n%s %s\n\n", getString(R.string.app_name), getString(R.string.serial_no),transactionLists.getJSONObject(0).getString(Constants.SERIAL_NO));
            Log.e("Transactions", transactionLists.toString());
            totalAmount = 0.0;
            for (int i = 0; i < transactionLists.length(); i++) {
                Transaction transaction = new Transaction();
                transaction.setTicketId(transactionLists.getJSONObject(i).getString(Constants.TICKET_ID));
                transaction.setUnitStake(transactionLists.getJSONObject(i).getString(Constants.UNIT_STAKE));
                transaction.setTotalAmount(transactionLists.getJSONObject(i).getString(Constants.TOTAL_AMOUNT));
                transaction.setGame_no_played(transactionLists.getJSONObject(i).getString(Constants.GAME_NO_PLAYED));
                transaction.setGame_names_id(transactionLists.getJSONObject(i).getString(Constants.GAME_NAMES_ID));
                transaction.setGame_types_id(transactionLists.getJSONObject(i).getString(Constants.GAME_TYPES_ID));
                transaction.setGame_type_options_id(transactionLists.getJSONObject(i).getString(Constants.GAME_TYPE_OPTIONS_ID));
                transaction.setDate_played(transactionLists.getJSONObject(i).getString(Constants.DATE_PLAYED));
                transaction.setTime_played(transactionLists.getJSONObject(i).getString(Constants.TIME_PLAYED));
                transaction.setSerialNo(transactionLists.getJSONObject(i).getString(Constants.SERIAL_NO));
                transaction.setGameName(transactionLists.getJSONObject(i).getString(Constants.GAME_NAME));
                transaction.setGameType(transactionLists.getJSONObject(i).getString(Constants.GAME_TYPE));
                transaction.setGameTypeOption(transactionLists.getJSONObject(i).getString(Constants.GAME_TYPE_OPTION));
                transaction.setPayment_option(transactionLists.getJSONObject(i).getString(Constants.PAYMENT_OPTION));
                transaction.setStatus(Constants.PENDING);
                transactionArrayList.add(transaction);
                totalAmount += Double.parseDouble(transactionLists.getJSONObject(i).getString(Constants.TOTAL_AMOUNT));

                String timePlayed[] =  transactionLists.getJSONObject(i).getString(Constants.TIME_PLAYED).split(":");
                int hr = Integer.parseInt(timePlayed[0]);
                String mm = timePlayed[1];
                String gameTimePlayed = "";
                if ( hr >= 12 ) {
                    gameTimePlayed = String.format("%s:%sPM",(hr - 12), mm );
                }
                else {
                    gameTimePlayed = String.format( "%s:%sAM", hr, mm );
                }
                // getData for printing
                printedData += String.format("%s: %s\n%s: %s\n%s: %s\n%s: %s\n%s %s\n%s %s\n%s %s\n\n",
                        getString(R.string.game_nos), transactionLists.getJSONObject(i).getString(Constants.GAME_NO_PLAYED),
                        getString(R.string.game_name), transactionLists.getJSONObject(i).getString(Constants.GAME_NAME),
                        getString(R.string.game_type_option), transactionLists.getJSONObject(i).getString(Constants.GAME_TYPE_OPTION),
                        getString(R.string.unit_stake), transactionLists.getJSONObject(i).getString(Constants.UNIT_STAKE),
                        getString(R.string.total_amount), transactionLists.getJSONObject(i).getString(Constants.TOTAL_AMOUNT),
                        getString(R.string.ticket_id), transactionLists.getJSONObject(i).getString(Constants.TICKET_ID),
                        getString(R.string.date_played), transactionLists.getJSONObject(i).getString(Constants.DATE_PLAYED) + ","
                                + gameTimePlayed);
            }
            printedData += String.format("%s: %s\n\n%s\n\n\n\n", getString(R.string.total_amount_paid), totalAmount,
                    getString(R.string.greetings));
            totalAmountView.setText(String.format("N%s", totalAmount));
            Log.e("TransactionList", transactionArrayList.toString());
            adapter = new GameTransactionsCustomList(GameSummaryActivity.this, transactionArrayList);
            listView.setAdapter(adapter);

        } catch (JSONException ex) {
        }

        getCancelGameBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(getString(R.string.are_you_sure_you_want_to_cancel));
            }
        });

        getProceedToPayBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    updateCreditBalance();
                    new SubmitGameTransaction().execute(Constants.GAME_TRANSACTION_URL);
                } else {
                    Snackbar.make(cancelGameBtn, getString(R.string.no_internet_connection), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        getPrintGameTicket().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printTicket();
            }
        });


    }

    private void getViews() {
        listView = findViewById(R.id.transactions);
        progressBar = findViewById(R.id.progressbar);
        cancelGameBtn = findViewById(R.id.cancel_game);
        proceedToPayBtn = findViewById(R.id.proceedToPay);
        printGameTicket = findViewById(R.id.printGame);
        serialNoView = findViewById(R.id.serial_no);
        totalAmountView = findViewById(R.id.total_amount);
    }

    public Button getPrintGameTicket() {
        return printGameTicket;
    }

    public Button getCancelGameBtn() {
        return cancelGameBtn;
    }

    public Button getProceedToPayBtn() {
        return proceedToPayBtn;
    }

    public String POST(String url) throws JSONException {
        InputStream inputStream = null;
        String result = "";
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            json = transactionLists.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamtoString(inputStream);
            } else
                result = "Did not work";
        } catch (Exception e) {
        }
        return result;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;

    }

    /**
     * Online Status AsyncTask
     */
    private class SubmitGameTransaction extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                return POST(urls[0]);
            } catch (JSONException ex) {
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showBar();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Result", result);
            super.onPostExecute(result);
            hideBar();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString(Constants.MESSAGE);
                String status = String.format("%s", jsonObject.getInt(Constants.STATUS));
                if (TextUtils.equals(message, getString(R.string.success))) {
                    dbController.deleteTransactions();
                    String msg = getString(R.string.game_transaction_successful);
                    hideBtns();
                    showPrintBtn();
                    showPrintDialog(msg);
                }
            } catch (Exception exception) {
                Log.e("Exception", exception.getMessage());
                exception.printStackTrace();
            }
        }
    }

    private void showPrintBtn() {
        printGameTicket.setVisibility(View.VISIBLE);
    }

    private void hideBtns() {
        cancelGameBtn.setVisibility(View.GONE);
        proceedToPayBtn.setVisibility(View.GONE);
    }

    /**
     * Convert input to stream
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public String convertInputStreamtoString(InputStream inputStream) throws IOException {
        String result = "";
        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            result += line;
        }
        reader.close();
        return result;
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSummaryActivity.this);
        builder.setMessage(message)
                .setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                dbController.deleteTransactions();
                startActivity(new Intent(GameSummaryActivity.this, GameHomeActivity.class));
                finish();
                dialogInterface.cancel();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPrintDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSummaryActivity.this);
        builder.setMessage(message)
                .setTitle(R.string.print_ticket);
        builder.setIcon(R.drawable.success_icon);
        builder.setPositiveButton(R.string.print_ticket, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                printTicket();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSummaryActivity.this);
        builder.setMessage(message)
                .setTitle(R.string.message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void printTicket() {
        try {
            sendData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideBar() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Post user data to the server
     */
    public String GETCREDIT(String url) throws JSONException {
        InputStream inputStream = null;
        String result = "";
        JSONObject params;
        params = new JSONObject();
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + dbController.getUser().get(Constants.USERS_ID) + "/" + totalAmount);
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpGet);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamtoString(inputStream);
            } else
                result = "Did not work";
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * Online Status AsyncTask
     */
    private class UpdateCreditBalance extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                return GETCREDIT(urls[0]);
            } catch (JSONException ex) {
                Log.e("JSONSException", ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(String result) {
            Log.e("Result", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                int status = jsonObject.getInt(Constants.STATUS);
            } catch (JSONException ex) {
                Log.e("JSONExcepion", ex.getMessage());
                String message = getString(R.string.poor_internet_connection);
            } catch (Exception ex) {
            }
        }
    }

    private void updateCreditBalance() {
        if (isConnected()) {
            new UpdateCreditBalance().execute(Constants.CREDIT_BALANCE_UPDATE_URL);
        }
    }

    @Override
    public void onBackPressed() {
        String message = getString(R.string.complete_cancel);
        showAlertDialog(message);
    }

    // bluetooth printer


    // This will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "No bluetooth adapter available", Toast.LENGTH_SHORT).show();
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // MP300 is the name of the bluetooth printer device
                    if (device.getName().equals(getString(R.string.gblackberry))) {
                        mmDevice = device;
                        Log.e("Printer name", device.getName());
                        break;
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString(Constants.SERIAL_UUID);
            mmSocket =  createBluetoothSocket(mmDevice, uuid);// mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();

            mmOutputStream = mmSocket.getOutputStream();

            mmInputStream = mmSocket.getInputStream();

            beginListenForData();
            Log.e( "Bluetooth status", "Bluetooth Opened");

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device, UUID uuid)
            throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, uuid);
            } catch (Exception e) {
                Log.e("Exception", "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(uuid);
    }
    // After opening a connection to bluetooth printer device,
// we have to listen and check if a data were sent to be printed.
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.e("data",data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData() throws IOException {
        try {
            // the text typed by the user
            String msg = printedData;
            msg += "\n";
            mmOutputStream.write(msg.getBytes());
            // tell the user data were sent
            Log.e("Data sent", "Data sent");
            startActivity(new Intent(GameSummaryActivity.this, PrintResultActivity.class));
            finish();
        }catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            if (mmOutputStream != null) {
                mmOutputStream.close();
                mmInputStream.close();
                mmSocket.close();
            }
           Log.e("Bluetooth status" , "Bluetooth Closed");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}