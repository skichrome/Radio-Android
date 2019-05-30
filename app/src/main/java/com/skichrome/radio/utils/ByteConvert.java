package com.skichrome.radio.utils;

public class ByteConvert
{
    private static String mHexString;

    public static byte[] decodeStr(String hexString)
    {
        mHexString = hexString;
        return decodeHexString();
    }

    private static byte[] decodeHexString()
    {
        if (mHexString.length() % 2 == 1)
        {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[mHexString.length() / 2];
        for (int i = 0; i < mHexString.length(); i += 2)
        {
            bytes[i / 2] = hexToByte(mHexString.substring(i, i + 2));
        }

        return bytes;
    }

    private static byte hexToByte(String hexString)
    {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));

        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar)
    {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1)
        {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }

        return digit;
    }
}
