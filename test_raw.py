import socket
import ssl

context = ssl.create_default_context()
with socket.create_connection(("mercader-server.onrender.com", 443)) as sock:
    with context.wrap_socket(sock, server_hostname="mercader-server.onrender.com") as ssock:
        req = b"GET /api/juegos/sistema/visitados HTTP/1.0\r\nHost: mercader-server.onrender.com\r\nConnection: close\r\nContent-Type: application/json\r\nContent-Length: 53\r\n\r\n{\"allGames\":false,\"order\":\"descendente\",\"amount\":10}"
        ssock.sendall(req)
        response = b""
        while True:
            data = ssock.recv(4096)
            if not data:
                break
            response += data
        
        print(response.decode('utf-8', errors='replace')[:1500])
