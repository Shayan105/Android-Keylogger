from http.server import BaseHTTPRequestHandler, HTTPServer
import os


class MyServer(BaseHTTPRequestHandler):

    def do_POST(self):
        content_length = int(self.headers['Content-Length'])
        post_data = self.rfile.read(content_length)

        # Decode the received data (adjust encoding as needed)
        try:
            post_data_str = post_data.decode('utf-8')
        except UnicodeDecodeError:
            post_data_str = post_data.decode('latin-1')  # Try a different encoding

        # Print the received data to console
        print(f"POST data received: {post_data_str}")

        # Append data to file
        try:
            with open("title.txt", "a", encoding='utf-8') as f:
                f.write(f"{post_data_str}\n")
        except Exception as e:
            print(f"Error writing to file: {e}")

        # Send a response
        self.send_response(200)
        self.send_header('Content-type', 'text/plain')
        self.end_headers()
        self.wfile.write(b'POST data received successfully!')

    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

        try:
            with open('title.txt', 'r', encoding='utf-8', errors='ignore') as f:
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
    server_port = 4444

    web_server = HTTPServer((host_name, server_port), MyServer)
    print("Server started http://%s:%s" % (host_name, server_port))

    try:
        web_server.serve_forever()
    except KeyboardInterrupt:
        pass

    web_server.server_close()
    print("Server stopped.")