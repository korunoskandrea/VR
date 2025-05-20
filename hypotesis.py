import os
import json
import math
from math import sqrt
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns
from typing import Dict, List, Tuple, Optional

# Constants for thresholds
THRESHOLDS = {
    "Straight": {"std_z": 0.20, "avg_gyro": 0.03},
    "Up": {"std_z": 0.30, "avg_gyro": 0.045},
    "Down": {"std_z": 0.22, "avg_gyro": 0.035}
}


def load_data(file_path: str) -> List[Dict]:
    """Load JSON data from file."""
    with open(file_path, "r") as f:
        return json.load(f)


def filter_sensor_data(session: List[Dict], sensor_type: str) -> List[Dict]:
    """Filter session data by sensor type."""
    return [s for s in session if s["sensorType"] == sensor_type]


def calculate_std_dev(values: List[float]) -> float:
    """Calculate standard deviation of a list of values."""
    avg = sum(values) / len(values)
    return sqrt(sum((x - avg) ** 2 for x in values) / len(values))


def calculate_gyro_magnitudes(gyro_data: List[Dict]) -> List[float]:
    """Calculate magnitudes for gyroscope data."""
    return [sqrt(s["x"] ** 2 + s["y"] ** 2 + s["z"] ** 2) for s in gyro_data]


def analyze_session(session: List[Dict], verbose: bool = False) -> str:
    """Analyze sensor session and return movement classification."""
    accel_data = filter_sensor_data(session, "accelerometer")
    gyro_data = filter_sensor_data(session, "gyroscope")

    z_values = [s["z"] for s in accel_data]
    std_dev_z = calculate_std_dev(z_values)

    gyro_magnitudes = calculate_gyro_magnitudes(gyro_data)
    avg_gyro = sum(gyro_magnitudes) / len(gyro_magnitudes)

    if verbose:
        print(f"STD Z: {std_dev_z:.3f}, AVG GYRO: {avg_gyro:.3f}")

    if std_dev_z > THRESHOLDS["Up"]["std_z"] and avg_gyro > THRESHOLDS["Up"]["avg_gyro"]:
        return "Up"
    elif std_dev_z > THRESHOLDS["Down"]["std_z"] and avg_gyro > THRESHOLDS["Down"]["avg_gyro"]:
        return "Down"
    return "Straight"


def process_file(file_path: str, true_label: str, verbose: bool = False) -> List[Tuple[str, str]]:
    """Process a single data file and return classification results."""
    session = load_data(file_path)
    predicted = analyze_session(session, verbose)
    return [(true_label, predicted)]


def compute_session_metrics(session: List[Dict]) -> Tuple[float, float, List[float], List[float]]:
    """Compute key metrics from session data."""
    accel_data = filter_sensor_data(session, "accelerometer")
    gyro_data = filter_sensor_data(session, "gyroscope")

    z_values = [s["z"] for s in accel_data]
    std_dev_z = calculate_std_dev(z_values)

    gyro_magnitudes = calculate_gyro_magnitudes(gyro_data)
    avg_gyro = sum(gyro_magnitudes) / len(gyro_magnitudes)

    return std_dev_z, avg_gyro, z_values, gyro_magnitudes


def get_min_max_values(data: List[Dict], axes: List[str] = ["x", "y", "z"]) -> Dict[str, Dict[str, float]]:
    """Get minimum and maximum values for each axis in sensor data."""
    return {
        "min": {axis: min(s[axis] for s in data) for axis in axes},
        "max": {axis: max(s[axis] for s in data) for axis in axes}
    }


def plot_hypothesis(z_values: List[float], gyro_mags: List[float], std_z: float,
                    avg_gyro: float, label: str) -> None:
    """Plot sensor data with hypothesis thresholds."""
    fig, axs = plt.subplots(2, 1, figsize=(12, 6), sharex=True)
    fig.suptitle(f"Hipotetična analiza – {label}", fontsize=14)

    # Plot accelerometer data
    axs[0].plot(z_values, label="Accelerometer Z")
    threshold = THRESHOLDS[label]["std_z"]
    axs[0].axhline(threshold, color='red', linestyle='--', label=f"Meja STD Z = {threshold}")
    axs[0].set_ylabel("Z-pospešek [m/s²]")
    axs[0].legend()
    axs[0].grid(True)

    # Plot gyroscope data
    axs[1].plot(gyro_mags, label="Gyro Magnituda")
    gyro_th = THRESHOLDS[label]["avg_gyro"]
    axs[1].axhline(gyro_th, color='purple', linestyle='--', label=f"Meja GYRO = {gyro_th}")
    axs[1].set_ylabel("Kotna hitrost [rad/s]")
    axs[1].set_xlabel("Vzorec")
    axs[1].legend()
    axs[1].grid(True)

    plt.tight_layout(rect=[0, 0, 1, 0.95])
    plt.show()


def test_hypothesis(label: str, std_z: float, avg_gyro: float,
                    z_values: List[float], gyro_mags: List[float]) -> None:
    """Test classification hypothesis and plot results."""
    print(f"\n--- {label.upper()} ---")
    print(f"STD Z = {std_z:.3f}, AVG GYRO = {avg_gyro:.3f}")

    thresholds = THRESHOLDS[label]
    hypothesis_result = (std_z > thresholds["std_z"]) and (avg_gyro > thresholds["avg_gyro"])
    print(f"H{['1', '2', '3'][['Straight', 'Up', 'Down'].index(label)]}:",
          "potrjena" if hypothesis_result else "zavrnjena")

    plot_hypothesis(z_values, gyro_mags, std_z, avg_gyro, label)


def calculate_metrics(results: List[Tuple[str, str]]) -> None:
    """Calculate and display classification metrics."""
    labels = sorted({t for t, _ in results} | {p for _, p in results})
    label_to_index = {label: i for i, label in enumerate(labels)}

    # Build confusion matrix
    matrix = np.zeros((len(labels), len(labels)), dtype=int)
    for true, pred in results:
        matrix[label_to_index[true]][label_to_index[pred]] += 1

    print("\n--- Konfuzijska matrika ---")
    for i, true_label in enumerate(labels):
        row = "\t".join(str(matrix[i][j]) for j in range(len(labels)))
        print(f"{true_label:>7}: {row}")

    # Plot confusion matrix
    plt.figure(figsize=(6, 5))
    sns.heatmap(matrix, annot=True, fmt="d", cmap="Blues",
                xticklabels=labels, yticklabels=labels)
    plt.xlabel("Napovedana oznaka")
    plt.ylabel("Prava oznaka")
    plt.title("Konfuzijska matrika")
    plt.tight_layout()
    plt.show()

    # Calculate and display accuracy
    correct = np.trace(matrix)
    total = np.sum(matrix)
    accuracy = correct / total * 100
    print(f"\nSkupna točnost: {accuracy:.2f}% ({correct}/{total})")

    # Calculate and display precision per class
    print("\n--- Preciznost po razredih ---")
    for i, label in enumerate(labels):
        tp = matrix[i][i]
        fp = sum(matrix[j][i] for j in range(len(labels)) if j != i)
        precision = tp / (tp + fp) if (tp + fp) > 0 else 0.0
        print(f"{label:>7}: {precision:.2f}")


def duration_in_seconds(session: List[Dict]) -> float:
    """Calculate session duration in seconds."""
    timestamps = [s["timestamp"] for s in session]
    return (max(timestamps) - min(timestamps)) / 1000 if timestamps else 0


def estimate_sampling_frequency(session: List[Dict], sensor_type: str = "accelerometer") -> float:
    """Estimate sampling frequency for a given sensor type."""
    timestamps = [s["timestamp"] for s in session if s["sensorType"] == sensor_type]
    if len(timestamps) < 2:
        return 0
    return len(timestamps) / ((timestamps[-1] - timestamps[0]) / 1000)


def main():
    base_dir = os.path.dirname(__file__)
    files = {
        "Straight": os.path.join(base_dir, "ex1_andrea_Straight_20250516_232111.json"),
        "Up": os.path.join(base_dir, "ex1_andrea_Up_20250516_231912.json"),
        "Down": os.path.join(base_dir, "ex1_andrea_Down_20250516_232014.json"),
    }

    all_results = []

    for label, path in files.items():
        if not os.path.exists(path):
            print(f"Manjka datoteka za: {label}")
            continue

        # Process file and collect results
        results = process_file(path, label, verbose=True)
        all_results.extend(results)

        # Analyze session data
        session = load_data(path)
        std_z, avg_gyro, z_values, gyro_mags = compute_session_metrics(session)
        test_hypothesis(label, std_z, avg_gyro, z_values, gyro_mags)

        # Display min/max values
        accel_data = filter_sensor_data(session, "accelerometer")
        gyro_data = filter_sensor_data(session, "gyroscope")

        print("Pospeskometer MIN:", get_min_max_values(accel_data)["min"])
        print("Pospeskometer MAX:", get_min_max_values(accel_data)["max"])
        print("Ziroskop MIN:", get_min_max_values(gyro_data)["min"])
        print("Ziroskop MAX:", get_min_max_values(gyro_data)["max"])

    # Display classification results
    print("\n--- Rezultati klasifikacije ---")
    for true_label, predicted_label in all_results:
        print(f"True: {true_label}, Predicted: {predicted_label}")

    # Calculate and display metrics if we have results
    if all_results:
        calculate_metrics(all_results)


if __name__ == "__main__":
    main()