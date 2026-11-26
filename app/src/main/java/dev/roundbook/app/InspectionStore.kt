package dev.roundbook.app

import android.content.Context

data class Inspection(
    val id: String,
    val title: String,
    val location: String,
    val instructions: String,
    val checkpoints: List<String>,
    val completed: Boolean = false,
)

/** Rounds and their completion state. The activities call this store on the main thread. */
class InspectionStore private constructor(context: Context) {
    private val preferences = context.getSharedPreferences("round_status", Context.MODE_PRIVATE)
    private val listeners = mutableSetOf<(String) -> Unit>()

    fun all(): List<Inspection> = route.map(::current)

    fun find(id: String): Inspection? = route.firstOrNull { it.id == id }?.let(::current)

    fun setCompleted(id: String, completed: Boolean) {
        val inspection = find(id) ?: return
        if (inspection.completed == completed) return

        preferences.edit().putBoolean(id, completed).apply()
        listeners.toList().forEach { it(id) }
    }

    fun observe(listener: (String) -> Unit) {
        listeners.add(listener)
    }

    fun removeObserver(listener: (String) -> Unit) {
        listeners.remove(listener)
    }

    private fun current(inspection: Inspection): Inspection =
        inspection.copy(completed = preferences.getBoolean(inspection.id, inspection.completed))

    companion object {
        private var instance: InspectionStore? = null

        @Synchronized
        fun get(context: Context): InspectionStore =
            instance ?: InspectionStore(context.applicationContext).also { instance = it }
    }
}

private val route = listOf(
    Inspection(
        id = "vestibule",
        title = "Entrance mats",
        location = "Ground floor · East vestibule",
        instructions = "Walk the route from the outer doors to reception before visitors arrive.",
        checkpoints = listOf(
            "Check that both mats sit flat in their recesses.",
            "Open each door fully and check that its bottom edge clears the mat.",
            "Look for tracked-in water on the tile beyond the inner mat.",
        ),
    ),
    Inspection(
        id = "water-station",
        title = "Bottle refill station",
        location = "Ground floor · Atrium",
        instructions = "Check the refill point between the two meeting rooms while the atrium is quiet.",
        checkpoints = listOf(
            "Fill a bottle and check that the stream stops when the bottle is removed.",
            "Check that the drip tray empties without pooling.",
            "Read the filter indicator and flag a replacement if it shows red.",
        ),
    ),
    Inspection(
        id = "supply-shelf",
        title = "Supply shelf count",
        location = "Ground floor · Service pantry",
        instructions = "Use the minimum quantities printed on the shelf labels for this round.",
        checkpoints = listOf(
            "Count the sealed hand-towel packs on the upper shelf.",
            "Count unopened soap refills against the marked minimum.",
            "Move the oldest sealed packs to the front for the next shift.",
        ),
        completed = true,
    ),
    Inspection(
        id = "meeting-room",
        title = "Meeting room blinds",
        location = "Level 1 · Cedar room",
        instructions = "Check all three bays before the room is set up for afternoon bookings.",
        checkpoints = listOf(
            "Lower and raise each blind once using the wall control.",
            "Look for fabric that catches on a sill or window handle.",
            "Leave each blind at the half-height mark.",
        ),
    ),
    Inspection(
        id = "archive",
        title = "Archive room reading",
        location = "Level 1 · Records room",
        instructions = "Read the wall display beside the archive door during the morning round.",
        checkpoints = listOf(
            "Check that the display is on and its battery indicator is clear.",
            "Compare the humidity reading with the range on the room card.",
            "Enter the reading on the paper log beside the display.",
        ),
    ),
    Inspection(
        id = "wayfinding",
        title = "Directory panels",
        location = "Level 2 · Lift lobby",
        instructions = "Read both directory panels from the spot where passengers leave the lifts.",
        checkpoints = listOf(
            "Match the room numbers on each panel to the corridor door labels.",
            "Check that the inserts sit straight behind the clear covers.",
            "Remove smudges that obscure the lettering.",
        ),
        completed = true,
    ),
    Inspection(
        id = "courtyard",
        title = "Courtyard benches",
        location = "Ground floor · West courtyard",
        instructions = "Follow the paved loop and check the three benches facing the planting beds.",
        checkpoints = listOf(
            "Inspect each seat for loose slats and raised fixings.",
            "Check the paving immediately around the bench feet for standing water.",
            "Use the bench number on the end plate when reporting a fault.",
        ),
    ),
)
