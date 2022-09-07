import java.util.concurrent.ArrayBlockingQueue;

class ParkingGarage {
	private ArrayBlockingQueue<Integer> places;

	public ParkingGarage(int places) {
		if (places < 0)
			places = 0;
		this.places = new ArrayBlockingQueue<>(places);
		for(int i = 0; i < places; i++) {
			this.places.add(i);
		}
	}

	public int enter() { // enter parking garage
		int ret = -1;
		try {
			ret = this.places.take();
		} catch (InterruptedException e) {
		}
		return ret;
	}

	public void leave(int emptied) { // leave parking garage
		try {
			this.places.put(emptied);
		}catch (InterruptedException e) {
		}
	}

	public int getPlaces() {
		return places.size();
	}
}

class Car extends Thread {
	private ParkingGarage parkingGarage;

	public Car(String name, ParkingGarage p) {
		super(name);
		this.parkingGarage = p;
		start();
	}

	private void tryingEnter() {
		System.out.println(getName() + ": trying to enter");
	}

	private void justEntered() {
		System.out.println(getName() + ": just entered");
	}

	private void aboutToLeave() {
		System.out.println(getName() + ":                                     about to leave");
	}

	private void Left() {
		System.out.println(getName() + ":                                     have been left");
	}

	public void run() {
		while (true) {
			try {
				sleep((int) (Math.random() * 10000)); // drive before parking
			} catch (InterruptedException e) {
			}

			tryingEnter();
			int curPlace = parkingGarage.enter();
			justEntered();

			try {
				sleep((int) (Math.random() * 20000)); // stay within the parking garage
			} catch (InterruptedException e) {
			}

			aboutToLeave();
			parkingGarage.leave(curPlace);
			Left();
		}
	}
}

public class ParkingBlockingQueue {
	public static void main(String[] args) {
		ParkingGarage parkingGarage = new ParkingGarage(7);
		for (int i = 1; i <= 10; i++) {
			Car c = new Car("Car " + i, parkingGarage);
		}
	}
}
