import os
import json
import math
from collections import defaultdict
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns

from app import process_file


def load_data(file_path):
    with open(file_path, "r") as f:
        return json.load(f)

def compute_features(session):
    accel = [s for s in session if s["sensorType"] == "accelerometer"]
    gyro = [s for s in session if s["sensorType"] == "gyroscope"]

    # Akcelerometer: Z vrednosti
    z_vals = [s["z"] for s in accel]
    avg_z = sum(z_vals) / len(z_vals)
    std_dev_z = math.sqrt(sum((z - avg_z) ** 2 for z in z_vals) / len(z_vals))

    # Žiroskop: magnituda
    gyro_mags = [math.sqrt(s["x"]**2 + s["y"]**2 + s["z"]**2) for s in gyro]
    avg_gyro = sum(gyro_mags) / len(gyro_mags)

    # Min/max za accelerometer
    accel_min = {
        "x": min(s["x"] for s in accel),
        "y": min(s["y"] for s in accel),
        "z": min(s["z"] for s in accel),
    }
    accel_max = {
        "x": max(s["x"] for s in accel),
        "y": max(s["y"] for s in accel),
        "z": max(s["z"] for s in accel),
    }

    # Min/max za gyroscope
    gyro_min = {
        "x": min(s["x"] for s in gyro),
        "y": min(s["y"] for s in gyro),
        "z": min(s["z"] for s in gyro),
    }
    gyro_max = {
        "x": max(s["x"] for s in gyro),
        "y": max(s["y"] for s in gyro),
        "z": max(s["z"] for s in gyro),
    }

    return std_dev_z, avg_gyro, accel_min, accel_max, gyro_min, gyro_max


def test_hypotheses(label, std_z, avg_gyro, accel_min, accel_max, gyro_min, gyro_max):
    print(f"\n--- {label.upper()} ---")
    print(f"STD Z = {std_z:.3f}, AVG GYRO = {avg_gyro:.3f}")

    print("Accelerometer:")
    print(f"  Min X/Y/Z = {accel_min['x']:.3f}, {accel_min['y']:.3f}, {accel_min['z']:.3f}")
    print(f"  Max X/Y/Z = {accel_max['x']:.3f}, {accel_max['y']:.3f}, {accel_max['z']:.3f}")

    print("Gyroscope:")
    print(f"  Min X/Y/Z = {gyro_min['x']:.3f}, {gyro_min['y']:.3f}, {gyro_min['z']:.3f}")
    print(f"  Max X/Y/Z = {gyro_max['x']:.3f}, {gyro_max['y']:.3f}, {gyro_max['z']:.3f}")

    if label == "Straight":
        h0 = std_z <= 0.20 and avg_gyro <= 0.03
    elif label == "Up":
        h0 = std_z <= 0.30 and avg_gyro <= 0.045
    elif label == "Down":
        h0 = std_z <= 0.22 and avg_gyro <= 0.035
    else:
        print("Neznana oznaka!")
        return

    if h0:
        print("✅ H0 (ničelna hipoteza) drži — značilnosti so skladne z:", label)
    else:
        print("❌ H0 zavrnjena — značilnosti presegajo pričakovane za:", label)



def calculate_metrics(results):
    labels = sorted(list(set(t for t, _ in results) | set(p for _, p in results)))
    label_to_index = {label: i for i, label in enumerate(labels)}

    # Ustvari prazno matriko
    matrix = np.zeros((len(labels), len(labels)), dtype=int)

    for true, pred in results:
        i = label_to_index[true]
        j = label_to_index[pred]
        matrix[i][j] += 1

    # Tekstovni prikaz
    print("\n--- Konfuzijska matrika ---")
    for i, true_label in enumerate(labels):
        row = "\t".join(str(matrix[i][j]) for j in range(len(labels)))
        print(f"{true_label:>7}: {row}")

    # Prikaz grafično
    plt.figure(figsize=(6, 5))
    sns.heatmap(matrix, annot=True, fmt="d", cmap="Blues",
                xticklabels=labels, yticklabels=labels)
    plt.xlabel("Napovedana oznaka")
    plt.ylabel("Prava oznaka")
    plt.title("Konfuzijska matrika")
    plt.tight_layout()
    plt.show()

    # Izračun točnosti
    correct = np.trace(matrix)
    total = np.sum(matrix)
    accuracy = correct / total * 100
    print(f"\n✅ Skupna točnost: {accuracy:.2f}% ({correct}/{total})")

    # Preciznost po razredih
    print("\n--- Preciznost po razredih ---")
    for i, label in enumerate(labels):
        tp = matrix[i][i]
        fp = sum(matrix[j][i] for j in range(len(labels)) if j != i)
        precision = tp / (tp + fp) if (tp + fp) > 0 else 0.0
        print(f"{label:>7}: {precision:.2f}")


def plot_sensor_data(session, title="Sensor Data"):
    # Filtriraj podatke za akcelerometer in žiroskop
    accel_data = [s for s in session if s["sensorType"] == "accelerometer"]
    gyro_data = [s for s in session if s["sensorType"] == "gyroscope"]

    # Pripravi časovne nize v sekundah (predpostavka: timestamp v ms)
    accel_time = [(s["timestamp"] - accel_data[0]["timestamp"]) / 1000 for s in accel_data]
    gyro_time = [(s["timestamp"] - gyro_data[0]["timestamp"]) / 1000 for s in gyro_data]

    import matplotlib.pyplot as plt  # preveri, da je matplotlib importan tukaj

    fig, axs = plt.subplots(2, 1, figsize=(12, 8), sharex=False)
    fig.suptitle(title)  # nastavi naslov na celotnem figure

    # Akcelerometer
    axs[0].plot(accel_time, [s["x"] for s in accel_data], label="Accel X")
    axs[0].plot(accel_time, [s["y"] for s in accel_data], label="Accel Y")
    axs[0].plot(accel_time, [s["z"] for s in accel_data], label="Accel Z")
    axs[0].set_title("Akcelerometer")
    axs[0].set_ylabel("Pospešek [m/s²]")
    axs[0].set_xlabel("Čas [s]")  # dodaj, če želiš x-os tudi zgoraj
    axs[0].legend()
    axs[0].grid(True)

    # Za Z-os akcelerometra
    axs[0].axhline(0.22, color='orange', linestyle='--', label='Z threshold (Down)')
    axs[0].axhline(0.30, color='red', linestyle='--', label='Z threshold (Up)')

    # Za žiroskop magnitudo
    axs[1].axhline(0.035, color='orange', linestyle='--', label='Gyro threshold (Down)')
    axs[1].axhline(0.045, color='red', linestyle='--', label='Gyro threshold (Up)')

    # Žiroskop
    axs[1].plot(gyro_time, [s["x"] for s in gyro_data], label="Gyro X")
    axs[1].plot(gyro_time, [s["y"] for s in gyro_data], label="Gyro Y")
    axs[1].plot(gyro_time, [s["z"] for s in gyro_data], label="Gyro Z")
    axs[1].set_title("Žiroskop")
    axs[1].set_xlabel("Čas [s]")
    axs[1].set_ylabel("Kotna hitrost [rad/s]")
    axs[1].legend()
    axs[1].grid(True)

    plt.tight_layout(rect=[0, 0, 1, 0.95])
    plt.show()


def main(show_metrics=True):
    base_dir = os.path.dirname(__file__)
    files = {
        "Straight": os.path.join(base_dir, "ex1_andrea_Straight_20250516_232111.json"),
        "Up": os.path.join(base_dir, "ex1_andrea_Up_20250516_231912.json"),
        "Down": os.path.join(base_dir, "ex1_andrea_Down_20250516_232014.json"),
    }

    all_results = []
    for label, path in files.items():
        results = process_file(path, label, verbose=True)
        all_results.extend(results)

        session = load_data(path)
        std_z, avg_gyro, accel_min, accel_max, gyro_min, gyro_max = compute_features(session)
        test_hypotheses(label, std_z, avg_gyro, accel_min, accel_max, gyro_min, gyro_max)

    print("\n--- Rezultati klasifikacije ---")
    for true_label, predicted_label in all_results:
        print(f"True: {true_label}, Predicted: {predicted_label}")

    if show_metrics:
        calculate_metrics(all_results)

def plot_all_files(files):
    for label, path in files.items():
        with open(path, "r") as f:
            data = json.load(f)
        plot_sensor_data(data, title=f"Tip hoje: {label}")


if __name__ == "__main__":
    main()
    base_dir = os.path.dirname(__file__)
    files = {
        "Straight": os.path.join(base_dir, "ex1_andrea_Straight_20250516_232111.json"),
        "Up": os.path.join(base_dir, "ex1_andrea_Up_20250516_231912.json"),
        "Down": os.path.join(base_dir, "ex1_andrea_Down_20250516_232014.json"),
    }

    plot_all_files(files)



# ✅ Hipoteza 1: Hoja po ravnem
# Hipoteza 1:
# Z uporabo pospeškometra in žiroskopa lahko pri vzorčevalni frekvenci 50 Hz z natančnostjo več kot 90 % določim, da gre za hojo po ravnem, če ima signal naslednje značilnosti:
#
# standardni odklon Z-komponente pospeškometra je manjši ali enak 0.20,
#
# povprečna magnituda žiroskopa je manjša ali enaka 0.03.
#
# ✅ Hipoteza 2: Hoja po stopnicah navzgor
# Hipoteza 2:
# Z uporabo pospeškometra in žiroskopa lahko pri vzorčevalni frekvenci 50 Hz z natančnostjo več kot 85 % določim, da gre za hojo po stopnicah navzgor, če ima signal naslednje značilnosti:
#
# standardni odklon Z-komponente pospeškometra je večji od 0.30,
#
# povprečna magnituda žiroskopa je večja od 0.045.
#
# ✅ Hipoteza 3: Hoja po stopnicah navzdol
# Hipoteza 3:
# Z uporabo pospeškometra in žiroskopa lahko pri vzorčevalni frekvenci 50 Hz z natančnostjo več kot 85 % določim, da gre za hojo po stopnicah navzdol, če ima signal naslednje značilnosti:
#
# standardni odklon Z-komponente pospeškometra je večji od 0.22,
#
# povprečna magnituda žiroskopa je večja od 0.035.
# ✅ Rezultati preverjanja hipotez
# 🔹 Hoja po ravnem
# Izmerjeno:
#
# STD Z = 0.153
#
# AVG GYRO = 0.024
#
# Hipoteza 1: Z uporabo pospeškometra in žiroskopa lahko pri frekvenci 50 Hz z natančnostjo več kot 90 % določim hojo po ravnem, če je STD Z ≤ 0.20 in AVG GYRO ≤ 0.03.
#
# Rezultat:
# ✅ Ničelna hipoteza drži – podatki ustrezajo pričakovanim značilnostim za hojo po ravnem.
# → Hipoteza je potrjena.
#
# 🔹 Hoja po stopnicah navzgor
# Izmerjeno:
#
# STD Z = 0.354
#
# AVG GYRO = 0.060
#
# Hipoteza 2: ... če je STD Z > 0.30 in AVG GYRO > 0.045.
#
# Rezultat:
# ❌ Ničelna hipoteza je zavrnjena – podatki presegajo pragove.
# → Hipoteza je potrjena – gibanje ustreza hoji po stopnicah navzgor.
#
# Opomba: Zavrnitev H0 pomeni, da podatki presegajo mirne vrednosti – kar je pričakovano za bolj dinamično gibanje navzgor.
#
# 🔹 Hoja po stopnicah navzdol
# Izmerjeno:
#
# STD Z = 0.254
#
# AVG GYRO = 0.044
#
# Hipoteza 3: ... če je STD Z > 0.22 in AVG GYRO > 0.035.
#
# Rezultat:
# ❌ Ničelna hipoteza je zavrnjena – podatki presegajo pričakovane meje za hojo po ravnem.
# → Hipoteza je potrjena – gibanje ustreza hoji po stopnicah navzdol.
#
# 📌 Povzetek
# Vse tri hipoteze so bile s pomočjo senzorjev uspešno potrjene – zavrnitev ničelne hipoteze za "Up" in "Down" pomeni, da zaznani podatki ustrezajo dinamičnejšim vzorcem, kar je bistvo tvoje metode detekcije različnih vrst hoje.
