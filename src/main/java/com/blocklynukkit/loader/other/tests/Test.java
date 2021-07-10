package com.blocklynukkit.loader.other.tests;

import oshi.SystemInfo;

import java.util.Arrays;

public class Test {
    public static void main(String[] args){
        System.out.println(
                Arrays.toString(new SystemInfo().getHardware().getProcessor().getSystemCpuLoadTicks())
        );
    }
}
