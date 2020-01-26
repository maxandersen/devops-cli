//usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.1.4
//DEPS org.projectlombok:lombok:1.18.10

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Properties;

import lombok.Builder;
import lombok.Data;

final class StringUtils {

    public static String stripLatest(final String value) {
        if (value != null && value.length() >= 1) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}

@Data
@Builder
class LookupResponse {

    private String ip;
    private String cname;
}

class NSLookup {

    public static LookupResponse getAkamaiStagingAddress(final String hostname) {
        final LookupResponse response = NSLookup.lookup(hostname);
        final String stagingcName = response.getCname().replace(".edgekey.net", ".edgekey-staging.net");
        response.setCname(stagingcName);
        final Optional<String> ip = getAddress(response.getCname());
        return LookupResponse.builder().cname(stagingcName).ip(ip.isPresent() ? ip.get() : "").build();
    }

    private static LookupResponse lookup(final String host) {
        try {
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
            final InitialDirContext idc = new InitialDirContext(env);
            final Attributes attrs = idc.getAttributes(host, new String[]{"CNAME"});
            final Attribute attr = attrs.get("CNAME");
            final String cname = StringUtils.stripLatest((String) attr.get());
            return LookupResponse.builder().cname(cname).build();
        } catch (final NamingException e) {
            e.printStackTrace();
        }
        return LookupResponse.builder().build();

    }

    private static Optional<String> getAddress (final String host) {
        try {
            final InetAddress inetAddress = InetAddress.getByName(host);
            return Optional.of(inetAddress.getHostAddress());
        } catch (final UnknownHostException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}

@CommandLine.Command(name = "akamai info", description = "Prints akamai staging ip address of a host")
public class devopscli implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The host name")
    private String host;

    public static void main(String... args) {
        int exitCode = new CommandLine(new devopscli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        System.out.println("Akamai Staging Ip for : " + host + " = " + NSLookup.getAkamaiStagingAddress(host).getIp());
        return 0;
    }
}