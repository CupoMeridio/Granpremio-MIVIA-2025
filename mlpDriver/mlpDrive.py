import socket
import joblib
import numpy as np
import pandas as pd
import os
from datetime import datetime

# Ottieni la directory dello script corrente
script_dir = os.path.dirname(os.path.abspath(__file__))
model_path = os.path.join(script_dir, "best_mlp_model.pkl")

print("[MLP SERVER] ========================================")
print("[MLP SERVER]     TORCS MLP Neural Network Server")
print("[MLP SERVER] ========================================")
print(f"[MLP SERVER] Caricamento modello da: {model_path}")

with open(model_path, "rb") as f:
    model = joblib.load(f)
    
print("[MLP SERVER] Modello MLP caricato con successo!")
print(f"[MLP SERVER] Tipo modello: {type(model).__name__}")

# Configurazione UDP
UDP_IP = "127.0.0.1" # loopback
UDP_PORT = 35567 # porta in ascolto
BUFFER_SIZE = 1024

# Crea socket UDP
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind((UDP_IP, UDP_PORT))

print(f"[MLP SERVER] Server avviato su {UDP_IP}:{UDP_PORT}")
print("[MLP SERVER] In attesa di connessioni dal client Java...")
print("[MLP SERVER] Premi Ctrl+C per terminare il server")
print("[MLP SERVER] ========================================\n")
while True:
    try:
        # Riceve i dati dal client (Java)
        data, addr = sock.recvfrom(BUFFER_SIZE)
        msg = data.decode("utf-8").strip()

        msg = msg.strip("()")

        timestamp = datetime.now().strftime("%H:%M:%S")
        print(f"[{timestamp}] Ricevuto da {addr[0]}:{addr[1]} -> {msg}")
        
        # Gestione messaggio di test per connessione iniziale
        if msg.lower() == "test":
            print(f"[{timestamp}] Test di connessione - Risposta: OK")
            sock.sendto("test_ok".encode("utf-8"), addr)
            continue
        
        # Gestione comando di chiusura
        if msg.lower() == "chiudi":
            print(f"[{timestamp}] Comando di shutdown ricevuto")
            print("[MLP SERVER] Chiusura server in corso...")
            sock.sendto("closing".encode("utf-8"), addr)
            break

        columns = ["track0", "track2", "track4", "track6", "track8",
                   "track10", "track12", "track14", "track16", "track18",
                   "speedX", "angleToTrackAxis", "trackPosition", "distanceFromStartLine"]

        # Parsing input (es: "0.1,0.2,0.3")
        features_array= np.array([float(x) for x in msg.split(",")]).reshape(1, -1)
        features_df = pd.DataFrame(features_array, columns=columns)
        print(f"[{timestamp}] Dati sensori elaborati: {len(features_array[0])} features")

        # Predizione
        """Il modello è una pipeline composta da 2 step : 'scaler' e 'mlp'. Quando viene chiamato il metodo predict i valori
           delle features vengono quindi trasformati dallo scaler scelto"""
        prediction = model.predict(features_df)


        # Prepara la risposta (es: valori separati da virgole)
        # [0.12,0.51,0.32] -> ["0.12".. -> "0.12,0.51,..."
        response = ",".join(map(str, prediction[0].tolist()))
        steering, acceleration, brake = prediction[0]
        print(f"[{timestamp}] Predizione -> Sterzo: {steering:.3f}, Accel: {acceleration:.3f}, Freno: {brake:.3f}")
        print(f"[{timestamp}] Invio risposta: {response}\n")

        # Invia i dati al client
        sock.sendto(response.encode("utf-8"), addr)

    except Exception as e:
        timestamp = datetime.now().strftime("%H:%M:%S")
        print(f"[{timestamp}] ERRORE: {e}")
        print(f"[{timestamp}] Verifica formato dati: dovrebbero essere 14 valori numerici separati da virgola\n")