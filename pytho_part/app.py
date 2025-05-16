# Imam aplikacijo Android Kotlin Jet Composable, ki sluzi za analizp uporabnisko hojo in aplikacija omogoca zajemanje, berenje in analziranje podatke. Se uporabljata 2 senzora ziroskop in pospesko meter. Aplikacija dela pravilno in je v celoti implementirana. Ko se podatki posnemejo se shranijo v JSOn file in imam to izgleda priblizno tako "[
#   {
#     "sensorType": "accelerometer",
#     "timestamp": 1747266769793,
#     "x": -0.3854665,
#     "y": 2.1428106,
#     "z": 9.287108
#   },
#   {
#     "sensorType": "gyroscope",
#     "timestamp": 1747266769836,
#     "x": -0.061086524,
#     "y": -0.06658431,
#     "z": 0.010995574
#   },
#   {
#     "sensorType": "accelerometer",
#     "timestamp": 1747266769872,
#     "x": -0.81881696,
#     "y": 1.8435353,
#     "z": 9.378088
#   },
#   {
#     "sensorType": "gyroscope",
#     "timestamp": 1747266769912,
#     "x": -0.049480084,
#     "y": -0.037873644,
#     "z": 0.009162978
#   },
#   {
#     "sensorType": "accelerometer",
#     "timestamp": 1747266769952,
#     "x": -0.37828386,
#     "y": 1.9416976,
#     "z": 9.507375
#   },
#   {
#     "sensorType": "gyroscope",
#     "timestamp": 1747266769992,
#     "x": -0.10140363,
#     "y": -0.05009095,
#     "z": -0.0067195175
#   },
#   {
#     "sensorType": "accelerometer",
#     "timestamp": 1747266770032,
#     "x": 0.18195933,
#     "y": 1.8770541,
#     "z": 9.809045
#   },
# ....]"
# Moja naloga je da postavim jasne hipoteze za hoja po ravnem, hoja po stopnice gor in hoja po stopnica dol (v hipotezo dolocim vzorcevalno frekvenco od 50Hz, morem met neko natancnostjo ki bo se upostevala za dolocanje tip hoje). Tukaj imas mojo kodo ki uporabim za  analiza podatkov "package com.example.sensorsvr.utils
#
# import com.example.sensorsvr.model.PredictionResultData
# import com.example.sensorsvr.model.SensorData
# import kotlin.math.pow
# import kotlin.math.sqrt
#
# fun calculateConfusionMatrix(results: List<PredictionResultData>): Map<Pair<String, String>, Int> {
#     val matrix = mutableMapOf<Pair<String, String>, Int>()
#
#     for (res in results) {
#         val key = Pair(res.trueLabel, res.predictedLabel)
#         matrix[key] = matrix.getOrDefault(key, 0) + 1
#     }
#     return matrix
# }
#
# fun calculateAccuracy(results: List<PredictionResultData>): Double {
#     require(results.isNotEmpty()) { "Cannot calculate accuracy for empty results" }
#     return results.count { it.trueLabel == it.predictedLabel }.toDouble() / results.size
# }
#
# fun calculatePrecision(label: String, results: List<PredictionResultData>): Double {
#     val tp = results.count { it.predictedLabel == label && it.trueLabel == label }
#     val fp = results.count { it.predictedLabel == label && it.trueLabel != label }
#
#     return if (tp + fp == 0) 0.0 else tp.toDouble() / (tp + fp)
# }
#
# fun analyzeMovement(data: List<SensorData>): String {
#     if (data.size < 20) return "Insufficient data for analysis"
#
#     val accelData = data.filter { it.sensorType == "accelerometer" }
#     val gyroData = data.filter { it.sensorType == "gyroscope" }
#
#     val zValues = accelData.map { it.z }
#     val avgZ = zValues.average()
#     val stdDevZ = sqrt(zValues.map { (it - avgZ).pow(2) }.average())
#
#     val gyroMagnitude = gyroData.map { sqrt(it.x.pow(2) + it.y.pow(2) + it.z.pow(2)) }
#     val avgGyro = gyroMagnitude.average()
#
#     return when {
#         stdDevZ > 0.30 && avgGyro > 0.045 -> "Up"
#         stdDevZ > 0.22 && avgGyro > 0.035 -> "Down"
#         else -> "Straight"
#     }
# }"
#
# Najprej prosim mi izgeneriraj podatke za hoja po ravnem uporabljam pospeskometer in giroskop
# signal naj je dolg 10 sekund in upostevaj vzorcevalno frekvenco 50Hz in generiranemu signalu dodaj en procent suma
#



import json
import os
from math import sqrt

def load_session(file_path):
    with open(file_path, "r") as f:
        return json.load(f)

def analyze_session(session, verbose=False):
    accel_data = [s for s in session if s["sensorType"] == "accelerometer"]
    gyro_data = [s for s in session if s["sensorType"] == "gyroscope"]

    z_values = [s["z"] for s in accel_data]
    avg_z = sum(z_values) / len(z_values)
    std_dev_z = sqrt(sum((z - avg_z) ** 2 for z in z_values) / len(z_values))

    gyro_magnitudes = [sqrt(s["x"]**2 + s["y"]**2 + s["z"]**2) for s in gyro_data]
    avg_gyro = sum(gyro_magnitudes) / len(gyro_magnitudes)

    if verbose:
        print(f"STD Z: {std_dev_z:.3f}, AVG GYRO: {avg_gyro:.3f}")

    if std_dev_z > 0.30 and avg_gyro > 0.045:
        return "Up"
    elif std_dev_z > 0.22 and avg_gyro > 0.035:
        return "Down"
    else:
        return "Straight"


def process_file(file_path, true_label, verbose=False):
    session = load_session(file_path)
    predicted = analyze_session(session, verbose)
    return [(true_label, predicted)]

def main():
    files = {
        "Straight": "ex1_andrea_Straight_20250516_232111.json",
        "Up": "ex1_andrea_Up_20250516_231912.json",
        "Down": "ex1_andrea_Down_20250516_232014.json",
    }

    all_results = []
    for label, path in files.items():
        if os.path.exists(path):
            results = process_file(path, label, verbose=True)
            all_results.extend(results)
        else:
            print(f"⚠️ Datoteka ne obstaja: {path}")

    print("\n--- Rezultati klasifikacije ---")
    for true_label, predicted_label in all_results:
        print(f"True: {true_label}, Predicted: {predicted_label}")

    correct = sum(1 for t, p in all_results if t == p)
    total = len(all_results)
    accuracy = correct / total * 100

    print(f"\nSkupna točnost: {accuracy:.2f}% ({correct}/{total})")

if __name__ == "__main__":
    main()


