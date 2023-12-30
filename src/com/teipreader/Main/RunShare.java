package com.teipreader.Main;

import java.io.IOException;


public class RunShare extends Thread implements Main {
    @Override
    public void run() {
        try {
            com.teipreader.share.Main.Start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void runShare() throws IOException {
        try {
            com.teipreader.share.Main.Start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void runServer() throws IOException {
        try {
            com.teipreader.share.Main.Start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}