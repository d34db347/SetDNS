package ml.xxxmmd.SetDNS;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;

public class MainActivity extends Activity {

//    public SharedPreferences sharedPreferences = getSharedPreferences("SavedDNS", 0);

    /**
     * Called when the activity is first created.
     * Save current DNS to SharedPreferences only run once
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedPreferences sharedPreferences = getSharedPreferences("SavedDNS", 0);


        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.contains("DNS1")) {
            String DNS1 = sharedPreferences.getString("DNS1", null);
            ((TextView) findViewById(R.id.editText)).setText(DNS1);
        } else {
            String DNS1 = android.os.SystemProperties.get("dhcp.wlan0.dns1");
            editor.putString("DNS1", DNS1);
            editor.commit();
            ((TextView) findViewById(R.id.editText)).setText(DNS1);
        }

        if (sharedPreferences.contains("DNS2")) {
            String DNS2 = sharedPreferences.getString("DNS2", null);
            ((TextView) findViewById(R.id.editText2)).setText(DNS2);
        } else {
            String DNS2 = android.os.SystemProperties.get("dhcp.wlan0.dns2");
            editor.putString("DNS2", DNS2);
            editor.commit();
            ((TextView) findViewById(R.id.editText2)).setText(DNS2);
        }

    }

    /**
     * This way seems not work on my phone
     */
    //public void setDNS(String setDNS) throws IOException {
    //    Process proc = Runtime.getRuntime().exec("su");
    //        DataOutputStream os = new DataOutputStream(proc.getOutputStream());
    //        os.writeBytes(setDNS);
    //        os.writeBytes("exit\n");
    //        os.flush();
    //}

    /**
     * So I modify the /system/etc/dhcpcd/dhcpcd-hooks/20-dns.conf directly
     */
    public void addSetDNS(String appendConf) throws IOException {

        if (fileIsExists()) {
            Process proc = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(proc.getOutputStream());
                os.writeBytes("mount -o remount rw /system/\n");
                os.writeBytes("cat /sdcard/SetDNS/20-dns.conforig > /sdcard/SetDNS/20-dns.confmod\n");
                os.writeBytes("echo \"\n" + appendConf + "\" >> /sdcard/SetDNS/20-dns.confmod\n");
                os.writeBytes("cat /sdcard/SetDNS/20-dns.confmod > /system/etc/dhcpcd/dhcpcd-hooks/20-dns.conf\n");
                os.writeBytes("mount -o remount ro /system/\n");
                os.writeBytes("exit\n");
                os.flush();
        }
    }

    /**
     * Check if 20-dns.conforig exist
     */
    public boolean fileIsExists(){
        try{
            File f = new File("/sdcard/SetDNS/20-dns.conforig");
            if(!f.exists()){
                Process proc = Runtime.getRuntime().exec("sh");
                DataOutputStream os = new DataOutputStream(proc.getOutputStream());
                    os.writeBytes("mkdir /sdcard/SetDNS\n");
                    os.writeBytes("cat /system/etc/dhcpcd/dhcpcd-hooks/20-dns.conf > /sdcard/SetDNS/20-dns.conforig\n");
                    os.writeBytes("exit\n");
                    os.flush();
                return true;
            } else
                return true;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot backup the config file", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void onOKClick(View view) throws IOException {
        //String DNS1 = "setprop dhcp.wlan0.dns1 " + ((EditText) findViewById(R.id.editText)).getText().toString() + "\n";
        //String DNS2 = "setprop dhcp.wlan0.dns2 " + ((EditText) findViewById(R.id.editText2)).getText().toString() + "\n";
        //String netDNS1 = "setprop net.dns1 " + ((EditText) findViewById(R.id.editText)).getText().toString() + "\n";
        //setDNS(DNS1);
        //setDNS(DNS2);
        String setDNS1 = "setprop dhcp.wlan0.dns1 " + ((EditText) findViewById(R.id.editText)).getText().toString() + "\n";

        String setDNS2;
        if (((EditText) findViewById(R.id.editText2)).getText().toString().trim().length() != 0) {
            setDNS2 = "setprop dhcp.wlan0.dns2 " + ((EditText) findViewById(R.id.editText2)).getText().toString() + "\n";
        } else
            setDNS2 = "";
        String appendConf = setDNS1 + setDNS2;
        System.out.println(appendConf);
        addSetDNS(appendConf);
    }

    public void onRestore(View view) throws IOException {
        if (fileIsExists()) {
            Process proc = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(proc.getOutputStream());
            os.writeBytes("mount -o remount rw /system/\n");
            os.writeBytes("cat /sdcard/SetDNS/20-dns.conforig > /system/etc/dhcpcd/dhcpcd-hooks/20-dns.conf\n");
            os.writeBytes("mount -o remount ro /system/\n");
            os.writeBytes("exit\n");
            os.flush();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("SavedDNS", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DNS1", ((EditText) findViewById(R.id.editText)).getText().toString());
        editor.putString("DNS2", ((EditText) findViewById(R.id.editText2)).getText().toString());
        editor.commit();
    }
}
