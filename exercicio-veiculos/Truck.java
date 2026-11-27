public class Truck extends Vehicle {

    private double cargoCap;

    public Truck() {
        super();
        this.cargoCap = 0.0;
    }

    public Truck(int passengers, double fuelCap, double mpg, double cargoCap) {
        super(passengers, fuelCap, mpg);
        this.cargoCap = cargoCap;
    }

    public double getCargoCap() {
        return cargoCap;
    }

    public void setCargoCap(double cargoCap) {
        if (cargoCap >= 0) {
            this.cargoCap = cargoCap;
        }
    }

    @Override
    public String toString() {
        return "Truck [" + super.toString() + ", capacidadeCarga=" + cargoCap + "]";
    }
}
