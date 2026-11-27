public class Vehicle {

    private int passengers;
    private double fuelCap;
    private double mpg;

    public Vehicle() {
        this(0, 0.0, 0.0);
    }

    public Vehicle(int passengers, double fuelCap, double mpg) {
        this.passengers = passengers;
        this.fuelCap = fuelCap;
        this.mpg = mpg;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        if (passengers >= 0) {
            this.passengers = passengers;
        }
    }

    public double getFuelCap() {
        return fuelCap;
    }

    public void setFuelCap(double fuelCap) {
        if (fuelCap >= 0) {
            this.fuelCap = fuelCap;
        }
    }

    public double getMpg() {
        return mpg;
    }

    public void setMpg(double mpg) {
        if (mpg >= 0) {
            this.mpg = mpg;
        }
    }

    public double autonomiaEstimada() {
        return fuelCap * mpg;
    }

    @Override
    public String toString() {
        return "Vehicle [passageiros=" + passengers
                + ", capacidadeTanque=" + fuelCap + " gal"
                + ", consumo=" + mpg + " mpg"
                + ", autonomia=" + autonomiaEstimada() + " milhas]";
    }
}
