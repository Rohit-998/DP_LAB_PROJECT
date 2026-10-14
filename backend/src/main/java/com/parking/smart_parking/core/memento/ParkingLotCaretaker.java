package com.parking.smart_parking.core.memento;

import java.util.ArrayList;
import java.util.List;

// Caretaker — manages the history of ParkingLot mementos (snapshots)
// Supports save, undo, and history listing
public class ParkingLotCaretaker {

    private List<ParkingLotMemento> history;

    public ParkingLotCaretaker() {
        this.history = new ArrayList<>();
    }

    // Save a snapshot
    public void save(ParkingLotMemento memento) {
        history.add(memento);
        System.out.println("CARETAKER: Snapshot saved. History size: " + history.size() + " — " + memento);
    }

    // Get the most recent snapshot (for undo)
    public ParkingLotMemento undo() {
        if (history.isEmpty()) {
            System.out.println("CARETAKER: No snapshots to restore.");
            return null;
        }
        ParkingLotMemento memento = history.remove(history.size() - 1);
        System.out.println("CARETAKER: Restoring snapshot — " + memento);
        return memento;
    }

    // Get a specific snapshot by index
    public ParkingLotMemento getSnapshot(int index) {
        if (index < 0 || index >= history.size()) {
            System.out.println("CARETAKER: Invalid snapshot index: " + index);
            return null;
        }
        return history.get(index);
    }

    // List all snapshots
    public List<ParkingLotMemento> getHistory() {
        return new ArrayList<>(history);
    }

    public int getHistorySize() {
        return history.size();
    }
}
