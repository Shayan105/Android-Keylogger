from http.server import BaseHTTPRequestHandler, HTTPServer
import os

class MyServer(BaseHTTPRequestHandler):

    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

        try:
            with open('title.txt', 'r') as f:
                lines = f.readlines()
                lines.reverse()
                html = "<html><body>"
                for line in lines:
                    html += "<p>" + line.strip() + "</p>" 
                html += "</body></html>"
                self.wfile.write(bytes(html, 'utf-8'))
        except FileNotFoundError:
            html = "<html><body><h1>File not found.</h1></body></html>"
            self.wfile.write(bytes(html, 'utf-8'))

if __name__ == '__main__':
    host_name = 'localhost'
    server_port = 4422
    web_server = HTTPServer((host_name, server_port), MyServer)
    print("Server started http://%s:%s" % (host_name, server_port))

    try:
        web_server.serve_forever()
    except KeyboardInterrupt:
        pass

    web_server.server_close()
    print("Server stopped.")