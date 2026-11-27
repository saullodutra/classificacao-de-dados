public class TestVehicles {

    public static void main(String[] args) {

        Vehicle carro = new Vehicle(5, 15.0, 30.0);

        Truck caminhao = new Truck(3, 40.0, 12.0, 5000.0);

        OffRoad jipe = new OffRoad(4, 20.0, 18.0, 10.5);

        System.out.println(carro);
        System.out.println(caminhao);
        System.out.println(jipe);

        System.out.println();

        Vehicle[] frota = { carro, caminhao, jipe };

        System.out.println("Autonomia estimada de cada veículo da frota:");
        for (Vehicle v : frota) {
            System.out.println(v.getClass().getSimpleName()
                    + " -> " + v.autonomiaEstimada() + " milhas");
        }

        System.out.println();

        caminhao.setCargoCap(6000.0);
        caminhao.setMpg(11.5);
        System.out.println("Caminhão atualizado: " + caminhao);
    }
}
