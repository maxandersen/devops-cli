package com.gerbenvis.opencli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "akamai info", description = "Prints akamai staging ip address of a host")
public class AkamaiInfoCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The host name")
    private String host;

    public static void main(String... args) {
        int exitCode = new CommandLine(new AkamaiInfoCommand()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        System.out.println("Akamai Staging Ip for : " + host + " = " + NSLookup.getAkamaiStagingAddress(host).getIp());
        return 0;
    }
}