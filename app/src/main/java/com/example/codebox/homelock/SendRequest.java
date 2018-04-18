package com.example.codebox.homelock;

/**
 * Created by codebox on 27/6/17.
 */

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class SendRequest extends AsyncTask<String, Object, String> {
    private Exception exception;
    @Override
    protected String doInBackground(String... params) {
        try {
            try {
                Socket socket = new Socket(DomainName.IP,9000);
                PrintWriter toServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                toServer.print(params[0]);
                toServer.flush();

                String reply = fromServer.readLine();

                return reply;

            } catch (IOException e) {

            }

        } catch (Exception e) {
            this.exception = e;
            return "Server offline";
        }
        return "Server offline";
    }
}
