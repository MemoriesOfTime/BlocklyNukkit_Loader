package com.blocklynukkit.loader.other.control;

public interface IOHandler {
    IOHandler push(String... data);
    IOHandler push(int... data);
    IOHandler push(short... data);
    IOHandler push(long... data);
    IOHandler push(byte... data);
    IOHandler push(float... data);
    IOHandler push(double... data);

    String popString();
    int popInt();
    short popShort();
    long popLong();
    byte popByte();
    float popFloat();
    double popDouble();

    String getString();
    String getString(int toPos);
    String getString(int fromPos, int toPos);
    byte[] getBytes();
    byte[] getBytes(int toPos);
    byte[] getBytes(int fromPos, int toPos);
    int getInt(int pos);
    short getShort(int pos);
    long getLong(int pos);
    byte getByte(int pos);
    float getFloat(int pos);
    double getDouble(int pos);

    IOHandler clear();
    IOHandler clear(int fromPos, int toPos);

    IOHandler replace(int pos, byte[] toReplace);
    IOHandler replace(int pos, String toReplace);
    IOHandler replace(int pos, int toReplace);
    IOHandler replace(int pos, short toReplace);
    IOHandler replace(int pos, long toReplace);
    IOHandler replace(int pos, byte toReplace);
    IOHandler replace(int pos, float toReplace);
    IOHandler replace(int pos, double toReplace);

    int length();
    IOHandler handle();
}
