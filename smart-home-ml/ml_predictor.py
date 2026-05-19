# -*- coding: utf-8 -*-
"""
Spyder Editor

"""

import requests
import pandas as pd
import numpy as np
from sklearn.linear_model import LinearRegression
import time
from datetime import datetime, timedelta

API_URL = "http://localhost:8080/api"

def fetch_sensor_data():
    """Récupère l'historique des températures depuis Spring Boot"""
    response = requests.get(f"{API_URL}/sensor-data")
    if response.status_code == 200:
        return response.json()
    return []

def train_and_predict(data):
    """Entraîne un modèle de régression linéaire et prédit la température future"""
    if len(data) < 10:
        print("Pas assez de données pour entraîner le modèle (min 10 points)")
        return None
    
    # Conversion en DataFrame Pandas
    df = pd.DataFrame(data)
    
    # On garde seulement la valeur et l'index (le temps) pour faire simple
    # L'index représente le temps (1 point = 5 secondes)
    X = np.arange(len(df)).reshape(-1,1) # Feature : le temps (0, 1, 2, 3...)
    y = df['sensorValue'].values         # Target: la température
    
    # Entrainement du modèle
    model = LinearRegression()
    model.fit(X, y)
    
    # On veut prédire la température dans 15 minutes
    # 15 mnutes = 900 secondes. Avec 1 point toutes les 5 secondes, ca fait 180 points dans le futur
    future_index = len(df) + 180
    predicted_temp = model.predict([[future_index]])[0]
    
    print(f"Prédiction pour dans 15 minutes : {predicted_temp:.2f}°C")
    return round(predicted_temp, 2)

def send_prediction(predicted_value):
    """Envoie la prédiction à Spring Boot"""
    target_time = (datetime.now() + timedelta(minutes = 15)).strftime("%H:%M:%S")
    
    payload = {
        "predictedValue": predicted_value,
        "targetTime" : f"dans 15 min ({target_time})"
    }
    
    response = requests.post(f"{API_URL}/predictions", json=payload)
    if response.status_code == 200:
        print("Prédiction envoyée au backend !")
        
if __name__ == "__main__":
    print("Démarrage du moteur IA Python...")
    while True:
        print("\n--- Cycle de prédiction ---")
        data = fetch_sensor_data()
        if data:
            prediction = train_and_predict(data)
            if prediction:
                send_prediction(prediction)
                
        # On relance la prédiction toutes les 60 secondes
        time.sleep(60)