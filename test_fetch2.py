import urllib.request
import json
import ssl

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

req = urllib.request.Request("https://mercader-server.onrender.com/api/auth/login", data=b'{"nombre":"Zulma","contrasenna":"ZulmaKat"}', headers={'Content-Type': 'application/json'}, method='POST')
try:
    with urllib.request.urlopen(req, context=ctx) as response:
        body = response.read().decode('utf-8')
        token = json.loads(body)['data']['token']
        
        req2 = urllib.request.Request("https://mercader-server.onrender.com/api/juegos/sistema/visitados", data=b'{"allGames":false,"order":"descendente","amount":10}', headers={'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token}, method='GET')
        with urllib.request.urlopen(req2, context=ctx) as response2:
            body2 = response2.read().decode('utf-8')
            print(body2[:1500])
except Exception as e:
    print(e)
