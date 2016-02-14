package com.stocksrus.stocksrus;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;

/**
 * Created by divy9677 on 2/13/16.
 */
public enum Form {
    TEN_K, TEN_Q, EIGHT_K, INVALID;

    @Override
    public String toString() {
        switch (this) {
            case TEN_K:
                return "10-K";
            case TEN_Q:
                return "10-Q";
            case EIGHT_K:
                return "8-K";
            default:
                return null;
        }
    }

    public static Form getForm(String formName) {
        switch (formName) {
            case "10-K":
                return TEN_K;
            case "10-Q":
                return TEN_Q;
            case "8-K":
                return EIGHT_K;
            default:
                return INVALID;
        }
    }
}
