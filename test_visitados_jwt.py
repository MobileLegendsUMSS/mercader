import urllib.request
import json
import ssl
import jwt

token = jwt.encode({'id_usuario': '6a2209023024cd7a56388bed', 'rol': 'admin'}, 'uyuyuyuy', algorithm='HS256')

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

req2 = urllib.request.Request("https://mercader-server.onrender.com/api/juegos/sistema/visitados", data=b'{"allGames":false,"order":"descendente","amount":10}', headers={'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token}, method='GET')
try:
    with urllib.request.urlopen(req2, context=ctx) as response2:
        body2 = response2.read().decode('utf-8')
        print(body2[:1500])
except Exception as e:
    if hasattr(e, 'read'):
        print(e.read().decode())
    print(e)
