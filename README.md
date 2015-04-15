##Set Android DNS  
In Official Lollipop, DNS cannot be set to static IPv6 address  
This application appends `setprop` to `/system/etc/dhcpcd/dhcpcd-hooks/20-dns.conf` so DNS will be configured as static addresses after connect to Wi-Fi  
```Java
        String dns = mDns1View.getText().toString();
        InetAddress dnsAddr = null;
        if (TextUtils.isEmpty(dns)) {
            //If everything else is valid, provide hint as a default option
            mDns1View.setText(mConfigUi.getContext().getString(R.string.wifi_dns1_hint));
        } else {
            dnsAddr = getIPv4Address(dns);   <-- Google use getIPv4Address here so IPv6 address cannot be identified
            if (dnsAddr == null) {
                return R.string.wifi_ip_settings_invalid_dns;
            }
            staticIpConfiguration.dnsServers.add(dnsAddr);
        }
        if (mDns2View.length() > 0) {
            dns = mDns2View.getText().toString();
            dnsAddr = getIPv4Address(dns);
            if (dnsAddr == null) {
                return R.string.wifi_ip_settings_invalid_dns;
            }
            staticIpConfiguration.dnsServers.add(dnsAddr);
        }
        return 0;
```
![Screenshot](https://github.com/gncy2013/SetDNS/blob/master/screenshot.png)