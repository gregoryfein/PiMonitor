import urlparse
import os
import psutil
import json
from subprocess import check_output
import subprocess
import time
import threading
import sys
import socket

class Stats:
    
    def __init__(self):
        print('Stat class made!')
        
    def summary(self):
        summary = { 'uptime' : time.time() - psutil.boot_time() }
        summary['cpu'] = psutil.cpu_times(percpu=True)
        summary['cpu_percent'] = self.get_cpu_percent()
        summary['temp'] = self.get_cpu_temperature()
        summary['ram'] = self.getRAMinfo()
        summary['volt'] = self.get_voltage()
        summary['disk'] = self.getDiskSpace()
        summary['wifi'] = self.getWifi()
        return json.dumps(summary)
        
    def get_current_config(self):
        summary = {}
        summary['clock'] = self.get_clock_vals()
        summary['config'] = self.get_config()
        summary['split'] = self.get_cpu_split()
        return summary

    def getWifi(self):
        ret = {}
        try:
            
            vals = {}
            process = os.popen("sudo iwconfig wlan0")
            preprocessed = process.read()
            process.close()
            splits = preprocessed.split('\n')
            
            for bigline in splits:
              pieces = bigline.split('  ')
              for line in pieces:
                if "Access Point: " in line:
                    ret['address'] = line.split('Access Point: ')[1]
                elif "Channel:" in line:
                    ret['channel'] = line.split('Channel:')[1]
                elif "Frequency:" in line:
                    ret['freq'] = line.split('Frequency:')[1]
                elif "Quality=" in line:
                    ret['quality'] = line.split('Quality=')[1].split(' ')[0]
                elif "Signal level=" in line:
                    ret['signal'] = line.split('Signal level=')[1]
                elif "Encryption key:" in line:
                    ret['encryption'] = line.split('Encryption key:')[1]
                elif "ESSID:" in line:
                    ret['essid'] = line.split('"')[1]
                elif "Mode:" in line:
                    ret['mode'] = line.split(':')[1]
                elif "IEEE" in line:
                    ret['ieee'] = line.split('IEEE ')[1]
                elif "Bit Rate" in line:
                    ret['bit-rate'] = line.split('Bit Rate=')[1]
                elif "Tx-Power" in line:
                    ret['tx'] = line.split('Tx-Power=')[1]
        except Exception as ex:
            ret['error'] = 'wlan0 error'
            template = "An exception of type {0} occured. Arguments:\n{1!r}"
            message = template.format(type(ex).__name__, ex.args)
            ret['error_msg'] = message
        return ret
        
        
    def get_voltage(self):
        vals = {}
        
        res = os.popen('vcgencmd measure_volts core').readline()
        vals['core'] = res.replace("volt=","").replace("\n","")
        
        res = os.popen('vcgencmd measure_volts sdram_c').readline()
        vals['sdram_c'] = res.replace("volt=","").replace("\n","")
        
        res = os.popen('vcgencmd measure_volts sdram_i').readline()
        vals['sdram_i'] = res.replace("volt=","").replace("\n","")
        
        res = os.popen('vcgencmd measure_volts sdram_p').readline()
        vals['sdram_p'] = res.replace("volt=","").replace("\n","")
        
        return vals
        
    def get_config(self):
        vals = {}
        process = os.popen("vcgencmd get_config int")
        preprocessed = process.read()
        process.close()
        
        for line in preprocessed.split('\n'):
            pieces = line.split("=")
            if(len(pieces) == 2):
                vals[pieces[0]] = pieces[1]
            
        return vals
        
    def get_clock_vals(self):
        vals = {}
        res = os.popen('vcgencmd measure_clock arm').readline()
        vals['arm'] = res.replace("frequency(45)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock core').readline()
        vals['core'] = res.replace("frequency(1)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock h264').readline()
        vals['h264'] = res.replace("frequency(28)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock isp').readline()
        vals['isp'] = res.replace("frequency(42)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock v3d').readline()
        vals['v3d'] = res.replace("frequency(43)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock uart').readline()
        vals['uart'] = res.replace("frequency(22)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock pwm').readline()
        vals['pwm'] = res.replace("frequency(25)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock emmc').readline()
        vals['emmc'] = res.replace("frequency(47)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock pixel').readline()
        vals['pixel'] = res.replace("frequency(29)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock vec').readline()
        vals['vec'] = res.replace("frequency(10)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock hdmi').readline()
        vals['hdmi'] = res.replace("frequency(9)=","").replace("\n", "")
        
        res = os.popen('vcgencmd measure_clock dpi').readline()
        vals['dpi'] = res.replace("frequency(4)=","").replace("\n", "")
        
        return vals
        
    def get_cpu_split(self):
        vals = {}
        res = os.popen('vcgencmd get_mem arm').readline()
        vals['cpu'] = res.replace("arm=","").replace("\n", "")
        res = os.popen('vcgencmd get_mem gpu').readline()
        vals['gpu'] = res.replace("gpu=","").replace("\n", "")
        return vals
        
    def get_cpu_percent(self):
        return psutil.cpu_percent(interval=0.1, percpu=True)
        
    # Return CPU temperature as a character string                                      
    def get_cpu_temperature(self):
        try:
            res = os.popen('vcgencmd measure_temp').readline()
            return(res.replace("temp=","").replace("'C\n",""))
        except:
            return 'Error'
        
        #process = popen(['vcgencmd', 'measure_temp'], stdout=PIPE)
        #output, _error = process.communicate()
        #return float(output[output.index('=') + 1:output.rindex("'")])

     # Return RAM information (unit=kb) in a list                                        
     # Index 0: total RAM                                                                
     # Index 1: used RAM                                                                 
     # Index 2: free RAM                                                                 
    def getRAMinfo(self):
        vals = {}
        vals['virtual'] = psutil.virtual_memory()
        vals['swap'] = psutil.swap_memory()
        return vals

     # Return % of CPU used by user as a character string                                
    def getCPUuse(self):
        return(str(os.popen("top -n1 | awk '/Cpu\(s\):/ {print $2}'").readline().strip(\
    )))

     # Return information about disk space as a list (unit included)                     
     # Index 0: total disk space                                                         
     # Index 1: used disk space                                                          
     # Index 2: remaining disk space                                                     
     # Index 3: percentage of disk used                                                  
    def getDiskSpace(self):
        p = os.popen("df -h /")
        i = 0
        while 1:
            i = i +1
            line = p.readline()
            if i==2:
                return(line.split()[1:5])