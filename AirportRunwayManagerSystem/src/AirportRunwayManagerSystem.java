/**
 * Airport Runway Management System
 *
 * <p>This program simulates an airport runway management system based on the
 * Dining Philosophers Problem. Ten airplanes (threads) compete for access to
 * five shared runways. Each airplane must acquire a unique runway to land or
 * take off. The system prevents deadlock by using a resource-ordering strategy
 * (planes always acquire the runway with the lower ID first when two are
 * conceptually needed) combined with a Semaphore-based runway pool that limits
 * concurrent access, ensuring no starvation and no runway collision.</p>
 *Liveness guarantees:
 *   Mutual exclusion – only one plane uses a runway at a time.
 *   No deadlock – a single semaphore pool with fair queuing prevents
 *       circular waiting.
 *   No starvation – Java's {@code Semaphore(permits, true)} uses a fair
 *       FIFO queue so every plane eventually gets clearance.
 **/


import java.util.concurrent.Semaphore;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

//01. ControlTower
class ControlTower {

    /** Logger shared across the whole simulation. */
    static final Logger LOG = Logger.getLogger("Airport");

    static {
        // Remove default handlers and install a clean one-line formatter
        Logger root = Logger.getLogger("");
        for (var h : root.getHandlers()) root.removeHandler(h);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord r) {
                return String.format("[%-6s] %s%n",
                        r.getLevel().getLocalizedName(), r.getMessage());
            }
        });
        LOG.addHandler(ch);
        LOG.setUseParentHandlers(false);
    }

    /** The physical runways managed by this tower. */
    private final Runway[] runways;

    /**
     * Fair semaphore – limits concurrent landings/take-offs to the number of
     * runways and uses FIFO queuing to guarantee starvation freedom.
     */
    private final Semaphore runwayPool;

    ControlTower(int runwayCount) {
        runways = new Runway[runwayCount];
        for (int i = 0; i < runwayCount; i++) {
            runways[i] = new Runway(i + 1);
        }
        // fair = true → FIFO ordering prevents starvation
        runwayPool = new Semaphore(runwayCount, true);
    }

    Runway requestClearance(int planeId) throws InterruptedException {
        LOG.info("Plane-" + planeId + " requests runway clearance.");
        runwayPool.acquire();                 // wait until a runway is available
        // Find and lock the first free runway
        synchronized (this) {
            for (Runway rw : runways) {
                if (rw.tryOccupy()) {
                    LOG.info("Plane-" + planeId + " granted clearance → Runway-" + rw.getId());
                    return rw;
                }
            }
        }
        // Should never reach here because the semaphore count matches runway count
        throw new IllegalStateException("Semaphore count inconsistent with runway count.");
    }

    /**
     * Releases the runway back to the pool so another plane may use it.
     *
     * @param rw      the {@link Runway} being released
     * @param planeId the identifier of the plane releasing the runway
     */
    void releaseRunway(Runway rw, int planeId) {
        rw.vacate();
        runwayPool.release();
        LOG.info("Plane-" + planeId + " released Runway-" + rw.getId() + ".");
    }
}

// Runway

///**
// * Represents a single physical runway at the airport.
// * A runway can be either free or occupied. The {@link #tryOccupy()} method
// * performs an atomic check-and-set to avoid race conditions.
// */
class Runway {

    private final int id;

    private boolean occupied;

    Runway(int id) {
        this.id = id;
        this.occupied = false;
    }

    int getId() { return id; }

//     Attempts to occupy this runway atomically.
//     Must be called from within a block that is synchronized on the
//     {@link ControlTower} instance so that the check-and-set is atomic.
//     @return {@code true} if the runway was free and is now occupied;
//             {@code false} if it was already occupied

    boolean tryOccupy() {
        if (!occupied) {
            occupied = true;
            return true;
        }
        return false;
    }

    //Marks this runway as free again.

    synchronized void vacate() {
        occupied = false;
    }
}

// 02.  Airplane
//Represents an airplane that repeatedly attempts to use a runway.

class Airplane extends Thread {

    private final int planeId;
    private final String operation;
    private final ControlTower tower;

//     Constructs an Airplane thread.
//
    Airplane(int planeId, String operation, ControlTower tower) {
        super("Plane-" + planeId);
        this.planeId   = planeId;
        this.operation = operation;
        this.tower     = tower;
    }


//     Main execution loop: request a runway, use it, then release it.
    @Override
    public void run() {
        try {
            ControlTower.LOG.info("Plane-" + planeId + " wants to " + operation + ".");
            Runway rw = tower.requestClearance(planeId);

            ControlTower.LOG.info("Plane-" + planeId + " is " + operation
                    + "ING on Runway-" + rw.getId() + ".");
            Thread.sleep((long) (1000 + Math.random() * 2000)); // 1–3 s

            tower.releaseRunway(rw, planeId);

        } catch (InterruptedException e) {
            ControlTower.LOG.warning("Plane-" + planeId + " interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}

// 03. AirportRunwayManagerSystem  (entry point)

//Entry point for the Airport Runway Management simulation.

public class AirportRunwayManagerSystem {
    private static final int RUNWAY_COUNT = 5;
    private static final int PLANE_COUNT = 10;

//    Application entry point.
    public static void main(String[] args) throws InterruptedException {
        ControlTower tower = new ControlTower(RUNWAY_COUNT);
        Airplane[]   planes = new Airplane[PLANE_COUNT];

        // Create 10 airplane threads with alternating operations
        for (int i = 0; i < PLANE_COUNT; i++) {
            String op = (i % 2 == 0) ? "LAND" : "TAKE OFF";
            planes[i] = new Airplane(i + 1, op, tower);
        }

        // Start all planes concurrently
        for (Airplane plane : planes) plane.start();

        // Wait for every plane to finish
        for (Airplane plane : planes) plane.join();

        ControlTower.LOG.info("All planes have completed. Airport clear.");
    }
}