package com.tomrenn.njtrains;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 */
public class Utils {

    public void closeQuietly(Closeable closeable){
        try {
            closeable.close();
        } catch (IOException ignored){}
    }
}
