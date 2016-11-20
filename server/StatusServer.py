import SimpleHTTPServer, SocketServer
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
from socket import socket
import Stats
from Stats import Stats

class StatusServer(SimpleHTTPServer.SimpleHTTPRequestHandler):
        
   def do_GET(self):

       # Parse query data & params to find out what was passed
       parsedParams = urlparse.urlparse(self.path)
       print('Path: ' + str(self.path))
       queryParsed = urlparse.parse_qs(parsedParams.query)
       print('queryParsed: ' + str(queryParsed))
       # request is either for a file to be served up or our test
       if parsedParams.path == "/stats":
         print(' - Stats!')
         self.processStats() 
       elif parsedParams.path == "/config":
         print(' - Config!')
         self.processConfig()
       elif parsedParams.path == "/exit":
         print(' - Exit!')
         sys.exit(0)
       else: 
         self.processNotYourBusiness(queryParsed)

   def processStats(self):

       self.send_response(200)
       self.send_header('Content-Type', 'application/json')
       self.send_header('Accept', 'text/plain')
       self.end_headers()

       stat = Stat()
       summary = stat.summary()
       self.wfile.write(summary)
       self.wfile.close();
       
   def processConfig(self):
       self.send_response(200)
       self.send_header('Content-Type', 'application/json')
       self.send_header('Accept', 'text/plain')
       self.end_headers()

       stat = Stat()
       summary = stat.get_current_config()
       self.wfile.write(summary)
       self.wfile.close();

   def processNotYourBusiness(self, query):

       self.send_response(200)
       self.send_header('Content-Type', 'text/json')
       self.end_headers()

       self.wfile.write("<html>");
       self.wfile.write("<head><title>Button Server</title></head>");
       self.wfile.write("<body>");
       self.wfile.write("<form action=\"punchin\">");
       self.wfile.write("<input type=\"submit\" value=\"punch in\"/><br/>");
       self.wfile.write("</form>");
       self.wfile.write("<form action=\"punchout\">");
       self.wfile.write("<input type=\"submit\" value=\"punch out\"/>");
       self.wfile.write("</form>");
       self.wfile.write("</body>");
       self.wfile.write("</html>");
       self.wfile.close();