package com.github.marcoral.inetutils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

//works with IPv4
public class InetAddresses {
    private static final int BYTE_BITS_SIZE = 8;
    private static final long BYTE_MASK = (1 << BYTE_BITS_SIZE) - 1;  //Made it long to prevent overflow errors

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
        return getByOffset(address, 1);
    }

    public static InetAddress getByOffset(InetAddress address, long offset) {
        Objects.requireNonNull(address, "address");
        return fromLong(toLong(address) + offset);
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
            int byteShift = ((lastOctetIndex - octetIndex) * BYTE_BITS_SIZE);
            result |= (addressArr[octetIndex] & BYTE_MASK) << byteShift;
        }
        return result;
    }

    public static InetAddress fromLong(long l) {
        if(l < 0)
            throw new IllegalArgumentException("Number must not be lower than 0!");
        byte[] bytes = new byte[4];
        for(int octetNum = bytes.length - 1; l != 0; --octetNum) {
            bytes[octetNum] = (byte) (l & BYTE_MASK);
            l >>= BYTE_BITS_SIZE;
        }
        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
