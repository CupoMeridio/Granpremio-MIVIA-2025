import socket
import joblib
import numpy as np
import pandas as pd
import os

# Get the directory where this script is located
script_dir = os.path.dirname(os.path.abspath(__file__))
model_path = os.path.join(script_dir, "best_mlp_model.pkl")

with open(model_path, "rb") as f:
    model = joblib.load(f)

# Configurazione UDP
UDP_IP = "127.0.0.1" # loopback
UDP_PORT = 35567 # porta in ascolto
BUFFER_SIZE = 1024

# Crea socket UDP
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind((UDP_IP, UDP_PORT))

print("========================================")
print("    MLP PYTHON SERVER STARTED")
print("========================================")
print(f"[INFO] Server listening on {UDP_IP}:{UDP_PORT}")
print(f"[INFO] MLP model loaded successfully: {model_path}")
print("[INFO] Waiting for Java client connections...")
print("[INFO] Press Ctrl+C to stop the server")
print("========================================")
print()
while True:
    try:
        # Riceve i dati dal client (Java)
        data, addr = sock.recvfrom(BUFFER_SIZE)
        msg = data.decode("utf-8").strip()

        msg = msg.strip("()")

        print(f"[DATA] Received sensor data from {addr[0]}:{addr[1]}")
        
        # Check for termination message
        if msg.lower() in ['chiudi', 'close', 'exit', 'quit']:
            print("Ricevuto comando di chiusura. Terminando il server...")
            sock.sendto("OK".encode("utf-8"), addr)
            break
            
        # Handle test connection message
        if msg.lower() == 'test':
            print(f"[CONN] Connection test from Java client {addr[0]}:{addr[1]} - OK")
            sock.sendto("OK".encode("utf-8"), addr)
            continue

        columns = ["track0", "track2", "track4", "track6", "track8",
                   "track10", "track12", "track14", "track16", "track18",
                   "speedX", "angleToTrackAxis", "trackPosition", "distanceFromStartLine"]

        # Parsing input (es: "0.1,0.2,0.3")
        try:
            features_array= np.array([float(x) for x in msg.split(",")]).reshape(1, -1)
        except ValueError:
            print(f"Errore nel parsing dei dati: {msg}")
            continue
        features_df = pd.DataFrame(features_array, columns=columns)
        # print(features_df)  # Uncomment for debugging

        # Predizione
        """Il modello è una pipeline composta da 2 step : 'scaler' e 'mlp'. Quando viene chiamato il metodo predict i valori
           delle features vengono quindi trasformati dallo scaler scelto"""
        prediction = model.predict(features_df)


        # Prepara la risposta (es: valori separati da virgole)
        # [0.12,0.51,0.32] -> ["0.12".. -> "0.12,0.51,..."
        response = ",".join(map(str, prediction[0].tolist()))
        print(f"[PRED] Sending prediction: {response}")

        # Invia i dati al client
        sock.sendto(response.encode("utf-8"), addr)

    except Exception as e:
        print(f"Errore: {e}")

# Chiusura del socket
sock.close()
print("Server MLP terminato correttamente.")