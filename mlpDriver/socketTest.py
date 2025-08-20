import socket
import joblib
import numpy as np

# Configurazione UDP
UDP_IP = "127.0.0.1" # loopback
UDP_PORT = 5005 # porta in ascolto
BUFFER_SIZE = 1024

# Crea socket UDP
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind((UDP_IP, UDP_PORT))

print("Adessp aspetto")
while True:
    try:
        # Riceve i dati dal client (Java)
        data, addr = sock.recvfrom(BUFFER_SIZE)
        msg = data.decode("utf-8").strip()

        msg = msg.strip("()")

        # Parsing input (es: "0.1,0.2,0.3")
        features = np.array([float(x) for x in msg.split(",")]).reshape(1, -1)

        print(features)

    except Exception as e:
        print(f"Errore: {e}")