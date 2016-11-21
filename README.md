# PiMonitor

PiMonitor is a utility to monitor your Pi's performance over a local network.  By installing a simple HTTP Rest service onto your Pi, your Android device can get real time updates of CPU usage, temperature, RAM usage, WiFi strength, and overclocking settings.

PiMonitor is open source and welcomes any enhancements or PRs!

To set up PiMonitor, first download the app from the Google Play Store here:

Next, you have to download the three Python files located in the "server" folder here:
[https://github.com/gregoryfein/PiMonitor/server](https://github.com/gregoryfein/PiMonitor/server)

Move all three to a destination folder on your Pi via FTP.  Afterwards, SSH into your Pi and type the following:

###This will show your network configuration.  Assuming you're using WiFi, input the IP address on the "wlan0" into the PiMonitor application on your phone.  Do this by tapping the pencil on the bottom of the page and putting in the IP.  The default port of the script is 8888, so don't change that unless you actually change the Python script.
```
sudo ifconfig 
```

### This will open the built-in scheduler for Linus
```
sudo crontab -e 
```

###At the bottom of the opened file, add the following line:
```
@reboot python /path/to/server/script/status_http.py&
```

###Save and exit from crontab
```
sudo reboot -p # this reboot the system.
```

You're now good to go!  Opening your PiMonitor application should be reading in real-time the current stats of your Pi system!
