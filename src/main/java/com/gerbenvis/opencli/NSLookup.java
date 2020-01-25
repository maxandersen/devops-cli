package com.gerbenvis.opencli;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Properties;

public class NSLookup {

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
