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
from server import StatusServer

PORT = 8888

server = StatusServer
httpd = SocketServer.TCPServer(("", PORT), StatusServer)

#print "serving at port", PORT
httpd.serve_forever()
'''
thread = threading.Thread(target = httpd.serve_forever)
try:
    thread.start()
except KeyboardInterrupt:
    server.shutdown()
    sys.exit(0)
'''

