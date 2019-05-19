package ro.pub.cs.systems.eim.practicaltest02;

public class ContainerClass {

    // This can be the temperature
    public String queryResponse1;
    // This can be the humidity
    public String queryResponse2;

    public ContainerClass(String queryResponse1, String queryResponse2)
    {
        this.queryResponse1 = queryResponse1;
        this.queryResponse2 = queryResponse2;
    }
}
