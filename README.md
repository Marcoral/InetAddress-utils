# InetAddress utils

This tiny library allows you to operate InetAddress objects as if they were numbers. All features are in InetAddresses class.


## Examples

    public void test() throws UnknownHostException {
        InetAddress start = InetAddress.getByName("127.0.0.5");
        InetAddress end = InetAddress.getByName("127.0.24.8");

        System.out.println(InetAddresses.compare(start, end)); //Prints negative number
        System.out.println(InetAddresses.COMPARATOR.compare(start, end)); //Also prints negative number

        System.out.println(InetAddresses.between(start, end)); //Prints "6147"
        System.out.println(InetAddresses.getByOffset(start, 6147)); //Prints "127.0.24.8" (this is kind of inverse of "between" method)
        System.out.println(InetAddresses.getNext(start)); //Prints "/127.0.0.6"

	    InetAddresses.streamOfRange(start, end).parallel().forEach(this::broadcastSomething);
    }

    private void broadcastSomething(InetAddress address) {
        //Whatever
    }
