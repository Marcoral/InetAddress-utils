package com.github.marcoral.inetutils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class InetAddresses {
    public static final Comparator<InetAddress> COMPARATOR = Comparator.comparingLong(InetAddresses::toLong);

    public static int compare(InetAddress first, InetAddress second) {
        return COMPARATOR.compare(first, second);
    }

    public static Stream<InetAddress> streamOfRange(InetAddress start, InetAddress end) {
        Stream.Builder<InetAddress> builder = Stream.builder();
        InetAddress currentAddress = start;
        for(long l = 0; l < between(start, end); ++l) {
            builder.add(currentAddress);
            currentAddress = getNext(currentAddress);
        }
        return builder.build();
    }

    public static InetAddress getNext(InetAddress address) {
        Objects.requireNonNull(address, "address");
        byte[] addressArray = address.getAddress();
        final int lastOctet = addressArray.length - 1;
        try {
            for(int octetIndex = lastOctet; octetIndex >= 0; --octetIndex) {
                byte octetValue = addressArray[octetIndex];
                boolean octetFinished = octetValue == (byte) -1;    //-1 means "255" (due to byte overflow)
                if(!octetFinished) {
                    //Modify address array and setup new address
                    addressArray[octetIndex] = ++octetValue;
                    reduceLowerOctets(addressArray, octetIndex);
                    return InetAddress.getByAddress(addressArray);
                }
            }
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }

    private static void reduceLowerOctets(byte[] addressArray, int octetIndex) {
        while(++octetIndex < addressArray.length)
            addressArray[octetIndex] = 0;
    }

    public static long between(InetAddress start, InetAddress end) {
        return toLong(end) - toLong(start);
    }

    public static long toLong(InetAddress address) {
        Objects.requireNonNull(address);
        byte[] addressArr = address.getAddress();
        long result = 0;
        final int lastOctetIndex = addressArr.length - 1;
        for(int octetIndex = lastOctetIndex; octetIndex >= 0; --octetIndex) {
            int byteShift = ((lastOctetIndex - octetIndex) * 8);    //8 because of the length of the byte
            result |= (addressArr[octetIndex] & 0xFFL) << byteShift;
        }
        return result;
    }
}
